package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import java.util.List;

public class MultipleChoiceStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private List<StatementOptionDto> options;

    public List<StatementOptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<StatementOptionDto> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "MultipleChoiceStatementQuestionDetailsDto{" +
                "options=" + options +
                '}';
    }
}