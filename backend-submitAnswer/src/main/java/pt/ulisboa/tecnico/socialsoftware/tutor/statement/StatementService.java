package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuestionAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;

import java.sql.SQLException;
import java.util.List;

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
}
