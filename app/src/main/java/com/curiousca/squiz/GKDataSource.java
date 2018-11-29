package com.curiousca.squiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.curiousca.squiz.DataClasses.Question;
import com.curiousca.squiz.DataClasses.QuizContract;
import com.curiousca.squiz.DataClasses.QuizDbHelper;


public class GKDataSource {
    private static final String LOGTAG = "GKDataSource";
    SQLiteOpenHelper dbhelper;
    SQLiteDatabase database;

    public GKDataSource(Context context) {
        dbhelper = new QuizDbHelper(context);
    }

    public void open() {
        Log.d(LOGTAG, "Database opened!");
        database = dbhelper.getWritableDatabase();
    }

    public void close() {
        Log.d(LOGTAG, "Database closed!");
        dbhelper.close();
    }

    public void insertOrUpdateGkQuestion(Question question) {
        long id = question.getId();
        Cursor c = database.rawQuery("SELECT * FROM " + QuizContract.QuestionsTable.TABLE_NAME + " WHERE " + QuizContract.QuestionsTable._ID + "=" + id, null);
        if (c.getCount() == 0) {
            //if the row is not present,then insert the row
            ContentValues values = new ContentValues();
            values.put(QuizContract.QuestionsTable._ID, id);
            values.put(QuizContract.QuestionsTable.COLUMN_QUESTION, question.getQuestion());
            values.put(QuizContract.QuestionsTable.COLUMN_OPTION1, question.getOption1());
            values.put(QuizContract.QuestionsTable.COLUMN_OPTION2, question.getOption2());
            values.put(QuizContract.QuestionsTable.COLUMN_OPTION3, question.getOption3());
            values.put(QuizContract.QuestionsTable.COLUMN_OPTION4, question.getOption4());
            values.put(QuizContract.QuestionsTable.COLUMN_ANSWER_NR, question.getAnswerNr());
            values.put(QuizContract.QuestionsTable.COLUMN_CATEGORY_ID, question.getCategoryID());
            database.insert(QuizContract.QuestionsTable.TABLE_NAME, null, values);
        } else {
            //else update the row
            ContentValues updatedValues = new ContentValues();
            updatedValues.put(QuizContract.QuestionsTable._ID, id);
            updatedValues.put(QuizContract.QuestionsTable.COLUMN_QUESTION, question.getQuestion());
            updatedValues.put(QuizContract.QuestionsTable.COLUMN_OPTION1, question.getOption1());
            updatedValues.put(QuizContract.QuestionsTable.COLUMN_OPTION2, question.getOption2());
            updatedValues.put(QuizContract.QuestionsTable.COLUMN_OPTION3, question.getOption3());
            updatedValues.put(QuizContract.QuestionsTable.COLUMN_OPTION4, question.getOption4());
            updatedValues.put(QuizContract.QuestionsTable.COLUMN_ANSWER_NR, question.getAnswerNr());

            database.update(QuizContract.QuestionsTable.TABLE_NAME, updatedValues, QuizContract.QuestionsTable._ID + "=" + id, null);
        }
    }


}
