package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

public class MultipleChoiceAnswerDto extends AnswerDetailsDto {
    private OptionDto option;

    public MultipleChoiceAnswerDto() {
    }

    public OptionDto getOption() {
        return option;
    }

    public void setOption(OptionDto option) {
        this.option = option;
    }
}
