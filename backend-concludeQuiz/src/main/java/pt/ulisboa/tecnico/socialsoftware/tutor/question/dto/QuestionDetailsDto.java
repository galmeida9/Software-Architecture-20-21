package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = MultipleChoiceQuestionDto.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceQuestionDto.class, name = "multiple_choice")
})
public abstract class QuestionDetailsDto implements Serializable {

//    public abstract QuestionDetails getQuestionDetails(Question question);
}
