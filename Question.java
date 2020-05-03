package quizGenerator;

public class Question {
	private String id;
	private String question;
	private String metaData ;

	public void toQuestion(String input) {
		int index = input.indexOf('|');
		this.id = input; 
		this.question = input.substring(0, index - 1);
		this.metaData = input.substring(index + 1);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String questionId) {
		this.id = questionId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}	
}
