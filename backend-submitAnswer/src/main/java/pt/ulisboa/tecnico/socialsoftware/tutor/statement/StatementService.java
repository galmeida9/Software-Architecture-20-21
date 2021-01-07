package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuestionAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.QuestionAnswerItemList;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;

import java.sql.SQLException;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_ALREADY_ANSWERED;

@Service
public class StatementService {

    @Autowired
    private QuestionAnswerItemRepository questionAnswerItemRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void submitAnswer(String username, int quizId, StatementAnswerDto answer) {
        if (answer.getTimeToSubmission() == null) {
            answer.setTimeToSubmission(0);
        }

        if (answer.getIsFinal() && questionAnswerItemRepository.findAnswersByQuizQuestionIdAndUser(answer.getQuizQuestionId(), answer.getUsername()).size() > 0) {
            throw new TutorException(QUESTION_ALREADY_ANSWERED);
        }

        if (answer.emptyAnswer()) {
            questionAnswerItemRepository.insertQuestionAnswerItemOptionIdNull(username, quizId, answer.getQuizQuestionId(), DateHandler.now(),
                    answer.getTimeTaken(), answer.getTimeToSubmission());
        } else {
            questionAnswerItemRepository.save(answer.getQuestionAnswerItem(username, quizId));
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public List<QuestionAnswerItem> findQuestionAnswerItemsByQuizId(int id) {
        return questionAnswerItemRepository.findQuestionAnswerItemsByQuizId(id);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void anonymizeUser(String oldUsername, String newUsername) {
        questionAnswerItemRepository.updateQuestionAnswerItemUsername(oldUsername, newUsername);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public QuestionAnswerItemList getFinalAnswers(int quizId, String user) {
        return new QuestionAnswerItemList(questionAnswerItemRepository.findQuestionAnswerItemsByQuizAndUser(quizId, user));
    }
}
