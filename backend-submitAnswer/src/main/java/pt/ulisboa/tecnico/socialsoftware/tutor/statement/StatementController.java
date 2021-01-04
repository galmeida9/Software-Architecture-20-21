package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;

import javax.validation.Valid;

@RestController
public class StatementController {
    private static final Logger logger = LoggerFactory.getLogger(StatementController.class);
    @Autowired
    private StatementService statementService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/quizzes/{quizId}/submit")
    public void submitAnswer(@PathVariable int quizId, @Valid @RequestBody StatementAnswerDto answer) {
        String username = answer.getUsername();

        statementService.submitAnswer(username, quizId, answer);
    }

    @PostMapping("/quizzes/{quizId}/conclude")
    public void concludeQuiz(@PathVariable int quizId, @RequestBody StatementQuizDto statementQuizDto) {
        ResponseEntity<Boolean> response = restTemplate.exchange(
                "http://localhost:8080/quizzes/" + statementQuizDto.getQuizAnswerId() + "/concludeTimed", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

        if (response.getBody() != null) {
            boolean result = response.getBody();

            if (result) {
                statementService.concludeQuiz(statementQuizDto);
            }
        }
    }
}