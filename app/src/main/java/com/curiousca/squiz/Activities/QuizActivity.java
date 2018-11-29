package com.curiousca.squiz.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.curiousca.squiz.DataClasses.Question;
import com.curiousca.squiz.DataClasses.QuizDbHelper;
import com.curiousca.squiz.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {
    public static final String EXTRA_SCORE = "extraScore";
    private static final long COUNTDOWN_IN_MILLIS = 30000;
    private static final String KEY_SCORE = "keyScore";
    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    private static final String KEY_ANSWERED = "keyAnswered";
    private static final String KEY_QUESTION_LIST = "keyQuestionList";

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCategory;
    private TextView textViewCountDown;
    private RadioGroup radioGroup;
    private RadioButton rButton1;
    private RadioButton rButton2;
    private RadioButton rButton3;
    private RadioButton rButton4;
    private Button buttonConfirm;
    private TextView textViewanswer;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCd;
    private ArrayList<Question> questionList;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;
    private int score;
    private boolean answered;
    private long backPressedTime;
   private int correct=0;
    private int wrong=0;

    public static void start(Context context, String questionCount) {
        Intent intent = new Intent(context, QuizActivity.class);
        intent.putExtra("questionCount", questionCount);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewCategory = findViewById(R.id.text_view_category);
        textViewCountDown = findViewById(R.id.text_view_countdown);
        radioGroup = findViewById(R.id.radio_group);
        rButton1 = findViewById(R.id.radio_button1);
        rButton2 = findViewById(R.id.radio_button2);
        rButton3 = findViewById(R.id.radio_button3);
        rButton4 = findViewById(R.id.radio_button4);
        textViewanswer = findViewById(R.id.text_view_answer);
        buttonConfirm = findViewById(R.id.button_confirm_next);

        textViewanswer.setVisibility(textViewanswer.INVISIBLE);

        textColorDefaultRb = rButton1.getTextColors();
        textColorDefaultCd = textViewCountDown.getTextColors();

        final Intent intent = getIntent();
        int categoryID = intent.getIntExtra(MainActivity.EXTRA_CATEGORY_ID, 0);
        String categoryName = intent.getStringExtra(MainActivity.EXTRA_CATEGORY_NAME);
        questionCountTotal = intent.getIntExtra((MainActivity.EXTRA_QUESTION_COUNT), 0);

        textViewCategory.setText("Category: " + categoryName);


        if (savedInstanceState == null) {
            QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);
            questionList = dbHelper.getQuestions(categoryID);
            //questionCountTotal = questionList.size();
            Collections.shuffle(questionList);
            showNextQuestion();
        } else {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            if (questionList == null) {
                finishQuiz();
            }
            //questionCountTotal = questionList.size();
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currentQuestion = questionList.get(questionCounter - 1);
            score = savedInstanceState.getInt(KEY_SCORE);
            timeLeftInMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            answered = savedInstanceState.getBoolean(KEY_ANSWERED);

            if (!answered) {
                startCountDown();
            } else {
                updateCountDownText();
                showSolution();
            }
        }

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {
                    if (rButton1.isChecked() || rButton2.isChecked() || rButton3.isChecked() || rButton4.isChecked()) {
                        checkAnswer();
                    } else {
                        Toast.makeText(QuizActivity.this, "Please select an answer...",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNextQuestion();
               }

            }
        });
    }

    private void showNextQuestion() {
        rButton1.setTextColor(textColorDefaultRb);
        rButton2.setTextColor(textColorDefaultRb);
        rButton3.setTextColor(textColorDefaultRb);
        rButton4.setTextColor(textColorDefaultRb);
        radioGroup.clearCheck();

        if (questionCounter < questionCountTotal) {
            currentQuestion = questionList.get(questionCounter);

            textViewQuestion.setText(currentQuestion.getQuestion());
            rButton1.setText(currentQuestion.getOption1());
            rButton2.setText(currentQuestion.getOption2());
            rButton3.setText(currentQuestion.getOption3());
            rButton4.setText(currentQuestion.getOption4());
            textViewanswer.setVisibility(textViewanswer.INVISIBLE);

            questionCounter++;
            textViewQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
            answered = false;
            findViewById(R.id.button_confirm_next).setBackgroundResource(R.drawable.buttonsytle3);
         // int myColor=getResources().getColor(R.color.colorBrown);
           // buttonConfirm.setBackgroundColor(myColor);
            buttonConfirm.setText("Confirm!");

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();
        } else {
            SharedPreferences sharedPreferences=getSharedPreferences("userInfo",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt("score",score);
            editor.putInt("correct",correct);
            editor.putInt("wrong",wrong);
            editor.apply();

       Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
       startActivity(intent);
        finish();
        }
    }

    private void startCountDown() {

        countDownTimer = new CountDownTimer(timeLeftInMillis + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) ((timeLeftInMillis / 1000) / 60);
        int seconds = (int) ((timeLeftInMillis / 1000) % 60);

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        textViewCountDown.setText(timeFormatted);
        if (timeLeftInMillis < 30000) {
            textViewCountDown.setTextColor(Color.GREEN);

            if (timeLeftInMillis < 20000) {
                textViewCountDown.setTextColor(Color.YELLOW);

                if (timeLeftInMillis < 10000) {
                    textViewCountDown.setTextColor(Color.RED);
                }
            }
        } else {
            textViewCountDown.setTextColor(textColorDefaultCd);
        }
    }

    private void checkAnswer() {
        answered = true;
        countDownTimer.cancel();

        RadioButton rbSelected = findViewById(radioGroup.getCheckedRadioButtonId());
        int answerNr = radioGroup.indexOfChild(rbSelected) + 1;

        if (answerNr == currentQuestion.getAnswerNr()) {
            correct=correct +1;
            if (timeLeftInMillis < 30000 && timeLeftInMillis > 20000) {
                score = score + 5;
            } else if (timeLeftInMillis < 20000 && timeLeftInMillis > 10000) {
                score = score + 3;
            } else {
                score++;
            }
            MediaPlayer mediaPlayer =MediaPlayer.create(this,R.raw.correct);
            mediaPlayer.start();
            Toast.makeText(this, "you are Correct", Toast.LENGTH_SHORT).show();
            textViewScore.setText("Score: " + score);
            if (questionCounter < questionCountTotal) {
                findViewById(R.id.button_confirm_next).setBackgroundResource(R.drawable.buttonsytle4);
             //   buttonConfirm.setBackgroundColor(Color.GREEN);
                buttonConfirm.setText("NEXT");
            } else {
                buttonConfirm.setText("Show Result");
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            }

        } else {
            MediaPlayer mediaPlayer =MediaPlayer.create(this,R.raw.wrong);
            mediaPlayer.start();
            wrong=wrong+1;
            Toast.makeText(this, "you are wrong ", Toast.LENGTH_SHORT).show();
            textViewanswer.setVisibility(textViewanswer.VISIBLE);
            showSolution();
        }
    }
    private void showSolution() {
        rButton1.setTextColor(Color.RED);
        rButton2.setTextColor(Color.RED);
        rButton3.setTextColor(Color.RED);
        rButton4.setTextColor(Color.RED);
        textViewanswer.setVisibility(textViewanswer.VISIBLE);

        switch (currentQuestion.getAnswerNr()) {
            case 1:
                rButton1.setTextColor(Color.GREEN);
                textViewanswer.setText("Answer:" + rButton1.getText());
                break;
            case 2:
                rButton2.setTextColor(Color.GREEN);
                textViewanswer.setText("Answer:" + rButton2.getText());
                break;
            case 3:
                rButton3.setTextColor(Color.GREEN);
                textViewanswer.setText("Answer:" + rButton3.getText());
                break;
            case 4:
                rButton4.setTextColor(Color.GREEN);
                textViewanswer.setText("Answer:" + rButton4.getText());
                break;
        }
        if (questionCounter < questionCountTotal) {
            findViewById(R.id.button_confirm_next).setBackgroundResource(R.drawable.buttonsytle4);
            buttonConfirm.setText("NEXT");
        } else {
            buttonConfirm.setText("Finish");
        }
    }

    private void finishQuiz() {

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishQuiz();
        } else {
            Toast.makeText(this, "Please press the back button again to exit", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questionCounter);
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMillis);
        outState.putBoolean(KEY_ANSWERED, answered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList);
    }
}
