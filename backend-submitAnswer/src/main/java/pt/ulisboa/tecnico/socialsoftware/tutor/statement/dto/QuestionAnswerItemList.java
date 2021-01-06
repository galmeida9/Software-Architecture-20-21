package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuestionAnswerItem;

import java.io.Serializable;
import java.util.List;

public class QuestionAnswerItemList implements Serializable {
    private List<QuestionAnswerItem> answers;

    public QuestionAnswerItemList(List<QuestionAnswerItem> answers) { this.answers = answers; }

    public List<QuestionAnswerItem> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuestionAnswerItem> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "QuestionAnswerItemList{" +
                "answers=" + answers +
                '}';
    }
}
