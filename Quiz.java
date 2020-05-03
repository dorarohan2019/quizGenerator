package quizGenerator;

import java.util.List;

public class Quiz {
private String quizId;
private List<Question> questions;

public String getQuizId() {
	return quizId;
}
public void setQuizId(String quizId) {
	this.quizId = quizId;
}
public List<Question> getQuestions() {
	return questions;
}
public void setQuestions(List<Question> questions) {
	this.questions = questions;
}

}
