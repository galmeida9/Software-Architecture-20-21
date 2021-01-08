package pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CANNOT_CONCLUDE_QUIZ;

@Entity
public class QuizAnswerItemOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer quizId;

    private String username;

    @Lob
    private String answers;

    public QuizAnswerItemOrder() {
    }

    public QuizAnswerItemOrder(StatementQuizDto statementQuizDto) {
        this.quizId = statementQuizDto.getId();
        this.username = statementQuizDto.getUsername();
        ObjectMapper obj = new ObjectMapper();
        try {
            this.answers = obj.writeValueAsString(statementQuizDto.getAnswers());
        } catch (JsonProcessingException e) {
            throw new TutorException(CANNOT_CONCLUDE_QUIZ);
        }
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<StatementAnswerDto> getAnswersList() {
        ObjectMapper obj = new ObjectMapper();
        try {
            return obj.readValue(this.getAnswers(), new TypeReference<ArrayList<StatementAnswerDto>>() {
            });
        } catch (JsonProcessingException e) {
            throw new TutorException(CANNOT_CONCLUDE_QUIZ);
        }
    }
}
