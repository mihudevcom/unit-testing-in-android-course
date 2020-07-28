package com.techyourchance.unittesting.testdata;

import com.techyourchance.unittesting.questions.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionsTestData {

    public static List<Question> getQuestions() {
        List<Question> questionList = new ArrayList<>();
        questionList.add(new Question("id1", "title1"));
        questionList.add(new Question("id2", "title2"));
        return questionList;
    }

    public static Question getQuestion() {
        return new Question("id", "title");
    }
}
