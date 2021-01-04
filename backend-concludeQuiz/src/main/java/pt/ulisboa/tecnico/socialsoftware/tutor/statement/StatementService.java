package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuizAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Service
public class StatementService {

    @Autowired
    private QuizAnswerItemRepository quizAnswerItemRepository;

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void concludeQuiz(StatementQuizDto statementQuizDto) {
        QuizAnswerItem quizAnswerItem = new QuizAnswerItem(statementQuizDto);
        quizAnswerItemRepository.save(quizAnswerItem);
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
