package com.example.quizzyapplication_v2.Offline;

import android.provider.BaseColumns;

public final class QuizContract {

    private QuizContract() {}

    public static class TopicsTable implements  BaseColumns {
        public static final String TABLE_NAME = "quiz_topics";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image";
    }

    public static class QuestionsTable implements BaseColumns {
        public static final String TABLE_NAME = "questions";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_OPTION1 = "option1";
        public static final String COLUMN_OPTION2 = "option2";
        public static final String COLUMN_OPTION3 = "option3";
        public static final String COLUMN_OPTION4 = "option4";
        public static final String COLUMN_ANSWER_NR = "answer_no";
        public static final String COLUMN_TOPIC_ID = "topic_id";
        public static final String COLUMN_DIFFICULTY = "difficulty";
        public static final String COLUMN_IMAGE = "image";
    }

    public static class RecordsTable implements BaseColumns {
        public static final String TABLE_NAME = "records";
        public static final String COLUMN_TOPIC_ID = "topic_id";
        public static final String COLUMN_POINT = "point";
        public static final String COLUMN_CORRECT_QUEST = "correct_quest";
        public static final String COLUMN_TOTAL_QUEST = "total_quest";
    }
}