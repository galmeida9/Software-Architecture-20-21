package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

public class MultipleChoiceCorrectAnswerDto extends CorrectAnswerDetailsDto {
    private Integer correctOptionId;

    public Integer getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(Integer correctOptionId) {
        this.correctOptionId = correctOptionId;
    }

    @Override
    public String toString() {
        return "MultipleChoiceCorrectAnswerDto{" +
                "correctOptionId=" + correctOptionId +
                '}';
    }
}