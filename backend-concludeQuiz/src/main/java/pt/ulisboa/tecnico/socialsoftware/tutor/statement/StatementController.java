package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuizAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
public class StatementController {
    private static final Logger logger = LoggerFactory.getLogger(StatementController.class);
    @Autowired
    private StatementService statementService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/quizzes/write")
    public Set<Integer> findQuizzesToWrite() {
        return statementService.findQuizzesToWrite();
    }

    @DeleteMapping("/quizzes/{id}/delete")
    public void deleteQuizAnswerItemById(@PathVariable int id) {
        statementService.deleteQuizAnswerItemById(id);
    }

    @GetMapping("/quizzes/{quizId}")
    public List<QuizAnswerItem> findQuizAnswerItemsById(@PathVariable int quizId) {
        return statementService.findQuizAnswerItemsById(quizId);
    }

    @PostMapping("/quizzes/{quizId}/conclude")
    public void concludeQuiz(@PathVariable int quizId, @RequestBody StatementQuizDto statementQuizDto) {
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8080/quizzes/" + statementQuizDto.getQuizAnswerId() + "/concludeTimed", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

        if (response.getStatusCode() == HttpStatus.OK) {
                statementService.concludeQuiz(statementQuizDto);
        }
    }
}