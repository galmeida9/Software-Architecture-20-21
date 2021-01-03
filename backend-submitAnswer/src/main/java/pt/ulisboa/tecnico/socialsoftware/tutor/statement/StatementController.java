package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class StatementController {
    private static final Logger logger = LoggerFactory.getLogger(StatementController.class);
    @Autowired
    private StatementService statementService;

    @PostMapping("/quizzes/{quizId}/submit")
    public void submitAnswer(@PathVariable int quizId, @Valid @RequestBody StatementAnswerDto answer) {
        String username = answer.getUsername();

        statementService.submitAnswer(username, quizId, answer);
    }

    @PostMapping("/quizzes/{quizId}/conclude")
    public void concludeQuiz(@PathVariable int quizId, @RequestBody StatementQuizDto statementQuizDto) {
        statementService.concludeQuiz(statementQuizDto);
    }
}