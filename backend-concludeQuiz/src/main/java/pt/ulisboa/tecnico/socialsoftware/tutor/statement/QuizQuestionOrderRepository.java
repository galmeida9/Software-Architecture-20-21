package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuizAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuizAnswerItemOrder;

import java.util.List;
import java.util.Set;

@Repository
public interface QuizQuestionOrderRepository extends JpaRepository<QuizAnswerItemOrder, Integer> {
    @Query(value = "SELECT qaq FROM QuizAnswerItemOrder qaq WHERE qaq.quizId = :quizId AND qaq.username = :user")
    List<QuizAnswerItemOrder> findQuestionOrderByQuizIdAndUser(Integer quizId, String user);
}
