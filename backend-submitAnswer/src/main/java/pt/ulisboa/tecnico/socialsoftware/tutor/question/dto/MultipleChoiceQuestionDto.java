package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleChoiceQuestionDto extends QuestionDetailsDto {
    private List<OptionDto> options = new ArrayList<>();

    public MultipleChoiceQuestionDto() {
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

//    @Override
//    public QuestionDetails getQuestionDetails(Question question) {
//        return new MultipleChoiceQuestion(question, this);
//    }
//
//    @Override
//    public void update(MultipleChoiceQuestion question) {
//        question.update(this);
//    }

    @Override
    public String toString() {
        return "MultipleChoiceQuestionDto{" +
                "options=" + options +
                '}';
    }

}
