package quizGenerator;

public class BootStrap {
	
	public static void main(String args[]) {
		String filename = args[0];
		GenerateQuizzes gq = new GenerateQuizzes();
		gq.readAndProcessQuestions(filename);
		Integer vq = gq.getValidQuizzesCount();
 		System.out.println("Valid Quizzes possible:"+vq);
	}
}
