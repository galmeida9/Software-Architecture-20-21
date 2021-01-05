package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuizAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class StatementService {

    @Autowired
    private QuizAnswerItemRepository quizAnswerItemRepository;


    @Autowired
    private RestTemplate restTemplate;

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void concludeQuiz(StatementQuizDto statementQuizDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m");

        if (statementQuizDto.getAvailableDate() != null &&
                LocalDateTime.parse(statementQuizDto.getAvailableDate(), formatter).isAfter(DateHandler.now())) {
            throw new TutorException(QUIZ_NOT_YET_AVAILABLE);
        }

        if (statementQuizDto.getConclusionDate() != null &&
                LocalDateTime.parse(statementQuizDto.getConclusionDate(), formatter).isBefore(DateHandler.now().minusMinutes(10))) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }

        QuizAnswerItem quizAnswerItem = new QuizAnswerItem(statementQuizDto);
        quizAnswerItemRepository.save(quizAnswerItem);
    }

    @Async("threadPoolTaskExecutor")
    public void completeQuiz(int quizAnswerId) {
        HttpEntity<Integer> request = new HttpEntity<>(quizAnswerId);

        restTemplate.postForEntity(
                "http://localhost:8080/quizzes/concludeTimed", request, Integer.class);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Set<Integer> findQuizzesToWrite() {
        return quizAnswerItemRepository.findQuizzesToWrite();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<QuizAnswerItem> findQuizAnswerItemsById(int quizId) {
        return quizAnswerItemRepository.findQuizAnswerItemsByQuizId(quizId);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteQuizAnswerItemById(int quizId) {
        quizAnswerItemRepository.deleteById(quizId);
    }
}
