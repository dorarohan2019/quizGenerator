package quizGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import quizGenerator.Enums.Level;
import quizGenerator.Enums.Tags;


public class GenerateQuizzes {
	public static Map<String, Integer> questionsCount = new HashMap<String,Integer>();
	public static Map<String, LinkedList<Question>> questionBank = new HashMap<String,LinkedList<Question>>();
	public static Map<String, LinkedList<Question>> processedQuestionBank = new HashMap<String,LinkedList<Question>>();
	public static Map<String, Integer> processedQuestionsCount = new HashMap<String,Integer>();
	public static Set<Set<Question>> quizzes = new HashSet<Set<Question>>();
	public static Integer totalQuestions, hardQuestions, mediumQuestions, easyQuestions;
	public Queue<String> unusedHardQuestions = new LinkedList<String>();
	public Queue<String> unusedMediumQuestions = new LinkedList<String>();
	public Queue<String> unusedEasyQuestions = new LinkedList<String>();
	public Integer recordsProcessed;
	public Integer[] TagWiseQuestionCount ;
	
	private void initializeState() {
		totalQuestions =0;
		hardQuestions = 0;
		mediumQuestions = 0;
		easyQuestions = 0;
		recordsProcessed = 0;
		TagWiseQuestionCount = new Integer[6];//No of Tags
	}
	
	public void readAndProcessQuestions(String inputFile) {
		try {
			initializeState();
			
			File f = new File(inputFile);
			
			BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()));
			String input = "";
			
			while ((input = br.readLine()) != null) {
				addToQuestionBank(input);
				updateQuestionsCount(input);
			}
			
			if (!haveSufficientQuestions()) {
				throw new Exception();
			}
			
			Iterator<Entry<String,LinkedList<Question>>> it = questionBank.entrySet().iterator();
			Entry<String,LinkedList<Question>> record = null ;
			while(recordsProcessed != 18 && it.hasNext()) {
				record = it.next();
				processRecord(record);
				recordsProcessed = recordsProcessed + 1 ;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void processRecord(Entry<String,LinkedList<Question>> entry) throws Exception {
		String key = entry.getKey();
		int index = key.indexOf('|');
		String tag = key.substring(index + 1);
		int difficultyLevel = -1, questionTopic = -1;
		
		if (key.contains("HARD")) {
			difficultyLevel = Level.HARD.getId();
			unusedHardQuestions.add(key);
		} else if (key.contains("MEDIUM")) {
			difficultyLevel = Level.MEDIUM.getId();
			unusedMediumQuestions.add(key);
		} else if (key.contains("EASY")) {
			difficultyLevel = Level.EASY.getId();
			unusedEasyQuestions.add(key);
		}
		
		questionTopic = Tags.toTags(tag);
		if (difficultyLevel >= 0 && questionTopic >= 0) {
				String qMeta = difficultyLevel+"|"+questionTopic;
				Integer count = questionsCount.get(key);
				processedQuestionsCount.put(qMeta , count);
				processedQuestionBank.put(qMeta , questionBank.get(key));//O(1) operation
		} else {
		//	throw new Exception();
			System.out.println("Bad QuestionLevelTopic Type");
		}
	}

	private boolean haveSufficientQuestions() {
		//return !(totalQuestions < 10 || hardQuestions < 2 || mediumQuestions < 2 || easyQuestions < 2);
		return !(hardQuestions < 2 || mediumQuestions < 2 || easyQuestions < 2 || getTotalQuestions() < 10) || hasMinimumQuestionsForEachTag() ;
	}
	
	private int getTotalQuestions() {
		return hardQuestions+mediumQuestions+easyQuestions;
	}
	private Integer hasMinimumQuestionsForEachTag() {
		for(int i = 0;i<TagWiseQuestionCount.length;i++) {
			if(TagWiseQuestionCount[i]<1) return false
		}
		return true;
	}

	private void addToQuestionBank(String input) {
		Question question = new Question();
		question.toQuestion(input);
		String qMeta = question.getMetaData();
		if (questionBank.containsKey(qMeta)) {
			questionBank.get(qMeta).addFirst(question); //addFirst add to head..O(1) operation
			questionsCount.put(qMeta,questionsCount.get(qMeta) + 1);
		} else {
			LinkedList<Question> ques = new LinkedList<Question>();
			ques.addFirst(question);
			questionBank.put(qMeta, ques);
			questionsCount.put(qMeta,1);
		}
	}
//why not TagWise Count..
	private void updateQuestionsCount(String input) {
		if (input.contains("HARD"))
			hardQuestions = hardQuestions + 1;
		else if (input.contains("MEDIUM"))
			mediumQuestions = mediumQuestions + 1;
		else if (input.contains("EASY"))
			easyQuestions = easyQuestions + 1;
		//totalQuestions = totalQuestions + 1;// can we remove this and use the above 3 variables to track the same
	}


	public Integer getValidQuizzesCount() {
//need to also check that each Tag should have minm. 1 question to make a quiz
		while(haveSufficientQuestions()) {
		Integer level = pickADifficultyLevel();
		Integer topic = pickATopic();
		Integer initialLevelChosen = level , initialTopicChosen = topic;
		Integer questionsInQuiz = 0;
		Set<Question> quiz = new HashSet<Question>();
		while(questionsInQuiz!=10) {
			
			String qId = level+"|"+topic ;
			addQuestionToQuiz(quiz,level, qId);
			questionsInQuiz = quiz.size();
			totalQuestions = totalQuestions - 1;
			
			if(questionsInQuiz<6)
			{
				level = getDifficultyLevelForNextQuestion(level);
				topic = getTopicForNextQuestion(topic,0);
			}
			else {
				level = getDifficultyLevel();
				Integer difference = 0;
				if(questionsInQuiz == 6) {
//below line very much dependent on questions condition...need generalisation
				difference = differenceOfInitialAndCurrentLevelChosen(level, initialLevelChosen);
				}
				else if(questionsInQuiz > 6) {
					initialTopicChosen = topic;
					difference = 0;
				}
				topic = getTopicForNextQuestion(topic,difference);
			}
		}
		quizzes.add(quiz);
	}
		return quizzes.size() ;
	}

	private Integer pickADifficultyLevel() {
		return new Random().nextInt(3);
	}

	private Integer differenceOfInitialAndCurrentLevelChosen(Integer level, Integer initialLevelChosen) {
		Integer gap = 0;
		while(initialLevelChosen != level ) {
			initialLevelChosen = getDifficultyLevelForNextQuestion(initialLevelChosen);
			gap = gap + 1;
		}
		return gap;
	}

	private void addQuestionToQuiz(Set<Question> quiz ,Integer level, String qId) {
		Integer value = processedQuestionsCount.get(qId);
		if(value != null && value > 0) {
			processedQuestionsCount.put(qId,value - 1);
			Question ques = processedQuestionBank.get(qId).removeFirst();
			quiz.add(ques);
			trackQuestionsByLevel(level);
		}
		else {
			getUnusedQuestion(level);
		}
	}

	private int getTopicForNextQuestion(Integer topic,Integer difference) {
		return (topic + difference + 1)%6;
	}

	private int getDifficultyLevelForNextQuestion(Integer level) {
		return (level + 1)%3;
	}

	private int pickATopic() {
		return new Random().nextInt(6);
	}

	private Integer getDifficultyLevel() {
		return hardQuestions > mediumQuestions ? hardQuestions > easyQuestions ? Level.HARD.getId() : Level.EASY.getId() : mediumQuestions > easyQuestions ? Level.MEDIUM.getId() : Level.EASY.getId();
	}
	

	//private void getUnusedQuestion(Set<Question> quiz, Integer difficultyLevel) {
		private Question getUnusedQuestion(Integer difficultyLevel) {
		Integer level = difficultyLevel;
		Question ques = null;
		try {	
			String key = getUnusedQuestionFromQueue(level);
			Integer index = key.indexOf('|');
			String tag = key.substring(index + 1);
			Integer topic = Tags.toTags(tag);
			String qMeta = level+"|"+topic;
			Integer questionsCount = processedQuestionsCount.get(qMeta);
			if ( questionsCount > 0) {
				processedQuestionsCount.put(qMeta,questionsCount - 1);
				ques = processedQuestionBank.get(qMeta).removeFirst();
				//quiz.add(ques);
				trackQuestionsByLevel(level);
				if(questionsCount > 1) {
					updateUnusedQuestions(key,level);
				}
			}
			else {
				return getUnusedQuestion(level);
			}
			}
		catch(Exception e) {
			System.out.println(e);
			if(totalQuestions > 0) {
				level = getDifficultyLevel();
			return getUnusedQuestion(level);
			}
		}
		return ques;
	}

	private void updateUnusedQuestions(String key, Integer group) {
		if(Level.HARD.getId() == group)
			unusedHardQuestions.add(key);
		if(Level.MEDIUM.getId() == group)
			unusedMediumQuestions.add(key);
		if(Level.EASY.getId() == group)
			unusedEasyQuestions.add(key);
	}

	private String getUnusedQuestionFromQueue(Integer group) {
		String key = "";
		if(Level.HARD.getId() == group)
			key = unusedHardQuestions.remove();
		if(Level.MEDIUM.getId() == group)
			key = unusedMediumQuestions.remove();
		if(Level.EASY.getId() == group)
			key = unusedEasyQuestions.remove();
		return key;
	}
	
	private void trackQuestionsByLevel(Integer group) {
		if(Level.HARD.getId() == group)
			hardQuestions = hardQuestions - 1 ;
		if(Level.MEDIUM.getId() == group)
			mediumQuestions = mediumQuestions - 1 ;
		if(Level.EASY.getId() == group)
			easyQuestions = easyQuestions - 1 ;

	}
}