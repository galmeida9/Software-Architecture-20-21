package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = MultipleChoiceStatementAnswerDetailsDto.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceStatementAnswerDetailsDto.class, name = "multiple_choice")
})
public abstract class StatementAnswerDetailsDto {
//    public abstract AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer);

    public abstract boolean emptyAnswer();

//    public abstract QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto);
}
