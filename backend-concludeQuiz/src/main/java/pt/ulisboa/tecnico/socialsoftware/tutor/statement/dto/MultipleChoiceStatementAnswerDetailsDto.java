package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

public class MultipleChoiceStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private Integer optionId;

    public MultipleChoiceStatementAnswerDetailsDto() {
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

   // private MultipleChoiceAnswer createdMultipleChoiceAnswer;

//    @Override
//    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
//        createdMultipleChoiceAnswer = new MultipleChoiceAnswer(questionAnswer);
//        questionAnswer.getQuestion().getQuestionDetails().update(this);
//        return createdMultipleChoiceAnswer;
//    }

    @Override
    public boolean emptyAnswer() {
        return optionId == null;
    }

//    @Override
//    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
//        return new MultipleChoiceAnswerItem(username, quizId, statementAnswerDto, this);
//    }

//    @Override
//    public void update(MultipleChoiceQuestion question) {
//        createdMultipleChoiceAnswer.setOption(question, this);
//    }

    @Override
    public String toString() {
        return "MultipleChoiceStatementAnswerDto{" +
                "optionId=" + optionId +
                '}';
    }
}
