package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuestionAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;

import javax.validation.Valid;
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

    @GetMapping("/questionAnswerItem/{id}")
    public List<QuestionAnswerItem> findQuestionAnswerItemsByQuizId(@PathVariable int id) {
        return statementService.findQuestionAnswerItemsByQuizId(id);
    }

    @PostMapping("/anonymize/{oldUsername}")
    public void anonymizeUser(@PathVariable String oldUsername, @Valid @RequestBody String newUsername) {
        statementService.anonymizeUser(oldUsername, newUsername);
    }
}