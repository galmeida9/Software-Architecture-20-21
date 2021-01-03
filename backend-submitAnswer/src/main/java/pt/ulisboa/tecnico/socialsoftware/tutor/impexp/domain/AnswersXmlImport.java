package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.AnswerDetailsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionDetailsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Component
public class AnswersXmlImport {
    public static final String SEQUENCE = "sequence";
    public static final String OPTION = "option";

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionDetailsRepository questionDetailsRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private AnswerDetailsRepository answerDetailsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    private Map<Integer, Map<Integer, Integer>> multipleChoiceQuestionMap;

    private void loadQuestionMap() {
        multipleChoiceQuestionMap = questionDetailsRepository.findMultipleChoiceQuestionDetails().stream()
                .collect(Collectors.toMap(questionDetails -> questionDetails.getQuestion().getKey(),
                        questionDetails -> questionDetails.getOptions().stream()
                                .collect(Collectors.toMap(Option::getSequence, Option::getId))));
    }

    private void importMultipleChoiceXmlImport(Element questionAnswerElement, QuestionAnswer questionAnswer) {
        Integer optionId = null;
        if (questionAnswerElement.getChild(OPTION) != null) {
            Integer questionKey = Integer.valueOf(questionAnswerElement.getChild(OPTION).getAttributeValue("questionKey"));
            Integer optionSequence = Integer.valueOf(questionAnswerElement.getChild(OPTION).getAttributeValue(SEQUENCE));
            optionId = multipleChoiceQuestionMap.get(questionKey).get(optionSequence);
        }

        if (optionId == null) {
            questionAnswer.setAnswerDetails((AnswerDetails) null);
        } else {
            MultipleChoiceAnswer answer
                    = new MultipleChoiceAnswer(questionAnswer, optionRepository.findById(optionId).orElse(null));
            questionAnswer.setAnswerDetails(answer);
            answerDetailsRepository.save(answer);
        }

    }
}
