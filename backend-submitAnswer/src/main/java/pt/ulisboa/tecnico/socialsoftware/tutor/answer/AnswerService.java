package pt.ulisboa.tecnico.socialsoftware.tutor.answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.QuizAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuizAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;

import java.sql.SQLException;

@Service
public class AnswerService {

    @Autowired
    private QuizAnswerItemRepository quizAnswerItemRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void concludeQuiz(StatementQuizDto statementQuizDto) {
        ResponseEntity<Boolean> response = restTemplate.exchange(
                "http://localhost:8080/quizzes/" + statementQuizDto.getQuizAnswerId() + "/concludeTimed", HttpMethod.GET, null,
                new ParameterizedTypeReference<>(){});

        if (response.getBody() != null) {
            boolean result = response.getBody();

            if (result) {
                QuizAnswerItem quizAnswerItem = new QuizAnswerItem(statementQuizDto);
                quizAnswerItemRepository.save(quizAnswerItem);
            }
        }
    }
}
