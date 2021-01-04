package pt.ulisboa.tecnico.socialsoftware.tutor.answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.AnswerDetailsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlExportVisitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuizAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class AnswerService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private AnswerDetailsRepository answerDetailsRepository;

    @Autowired
    private AnswersXmlImport xmlImporter;

    @Autowired
    private RestTemplate restTemplate;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizAnswerDto createQuizAnswer(Integer userId, Integer quizId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
        quizAnswerRepository.save(quizAnswer);

        return new QuizAnswerDto(quizAnswer);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<CorrectAnswerDto> concludeQuiz(StatementQuizDto statementQuizDto) {
        QuizAnswer quizAnswer = checkQuiz(statementQuizDto.getQuizAnswerId());

        if (!quizAnswer.isCompleted()) {
            quizAnswer.setCompleted(true);
            quizAnswer.setAnswerDate(DateHandler.now());

            for (QuestionAnswer questionAnswer : quizAnswer.getQuestionAnswers()) {
                writeQuestionAnswer(questionAnswer, statementQuizDto.getAnswers());
            }
            return quizAnswer.getQuestionAnswers().stream()
                    .sorted(Comparator.comparing(QuestionAnswer::getSequence))
                    .map(CorrectAnswerDto::new)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void concludeTimedQuiz(int quizAnswerId) {
        QuizAnswer quizAnswer = checkQuiz(quizAnswerId);

        if (!quizAnswer.isCompleted()) {
            quizAnswer.setCompleted(true);
        }
    }

    public QuizAnswer checkQuiz(int quizAnswerId) {
        QuizAnswer quizAnswer = quizAnswerRepository.findById(quizAnswerId)
                .orElseThrow(() -> new TutorException(QUIZ_ANSWER_NOT_FOUND, quizAnswerId));

        if (quizAnswer.getQuiz().getAvailableDate() != null && quizAnswer.getQuiz().getAvailableDate().isAfter(DateHandler.now())) {
            throw new TutorException(QUIZ_NOT_YET_AVAILABLE);
        }

        if (quizAnswer.getQuiz().getConclusionDate() != null && quizAnswer.getQuiz().getConclusionDate().isBefore(DateHandler.now().minusMinutes(10))) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }

        return quizAnswer;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void writeQuizAnswers(Integer quizId) {
        Quiz quiz = quizRepository.findQuizWithAnswersAndQuestionsById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
        Map<Integer, QuizAnswer> quizAnswersMap = quiz.getQuizAnswers().stream().collect(Collectors.toMap(QuizAnswer::getId, Function.identity()));

        //List<QuizAnswerItem> quizAnswerItems = quizAnswerItemRepository.findQuizAnswerItemsByQuizId(quizId);

        ResponseEntity<List<QuizAnswerItem>> response = restTemplate.exchange(
                "http://localhost:8078/quizzes/" + quizId, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

        List<QuizAnswerItem> quizAnswerItems = response.getBody();

        if (response.getStatusCode() == HttpStatus.OK && quizAnswerItems != null) {
            quizAnswerItems.forEach(quizAnswerItem -> {
                QuizAnswer quizAnswer = quizAnswersMap.get(quizAnswerItem.getQuizAnswerId());

                if (quizAnswer.getAnswerDate() == null) {
                    quizAnswer.setAnswerDate(quizAnswerItem.getAnswerDate());

                    for (QuestionAnswer questionAnswer : quizAnswer.getQuestionAnswers()) {
                        writeQuestionAnswer(questionAnswer, quizAnswerItem.getAnswersList());
                    }
                }
                //quizAnswerItemRepository.deleteById(quizAnswerItem.getId());
                ResponseEntity<String> deleteResponse = restTemplate.exchange(
                        "http://localhost:8078/quizzes/" + quizAnswerItem.getId() + "/delete", HttpMethod.DELETE, null,
                        new ParameterizedTypeReference<>() {
                        });
                if (deleteResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    throw new TutorException(CANNOT_DELETE_QUIZ_ANSWER_ITEM);
                }
            });
        }
    }

    private void writeQuestionAnswer(QuestionAnswer questionAnswer, List<StatementAnswerDto> statementAnswerDtoList) {
        StatementAnswerDto statementAnswerDto = statementAnswerDtoList.stream()
                .filter(statementAnswerDto1 -> statementAnswerDto1.getQuestionAnswerId().equals(questionAnswer.getId()))
                .findAny()
                .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswer.getId()));

        questionAnswer.setTimeTaken(statementAnswerDto.getTimeTaken());
        AnswerDetails answer = questionAnswer.setAnswerDetails(statementAnswerDto);
        if (answer != null) {
            answerDetailsRepository.save(answer);
        }
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String exportAnswers() {
        AnswersXmlExportVisitor xmlExport = new AnswersXmlExportVisitor();

        return xmlExport.export(quizAnswerRepository.findAll());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void importAnswers(String answersXml) {
        xmlImporter.importAnswers(answersXml);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteQuizAnswer(QuizAnswer quizAnswer) {
        quizAnswer.remove();
        quizAnswerRepository.delete(quizAnswer);
    }
}
