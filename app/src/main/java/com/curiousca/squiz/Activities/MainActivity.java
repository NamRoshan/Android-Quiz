package com.curiousca.squiz.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.curiousca.squiz.ConnectionMngr;
import com.curiousca.squiz.DataClasses.Category;
import com.curiousca.squiz.DataClasses.Question;
import com.curiousca.squiz.DataClasses.QuizDbHelper;
import com.curiousca.squiz.GKDataSource;
import com.curiousca.squiz.R;
import com.curiousca.squiz.model.ApiObject;
import com.curiousca.squiz.retrofit.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    String[] listitems;

    ProgressDialog progressDialog;
    private GKDataSource gkDataSource;

    private static final int REQUEST_CODE_QUIZ = 1;
    public static final String EXTRA_CATEGORY_ID = "extraCategoryID";
    public static final String EXTRA_CATEGORY_NAME = "extraCategoryName";
    public static final String EXTRA_QUESTION_COUNT = "questionCount";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_HIGHSCORE = "keyHighscore";

    private TextView textViewHighscore;
    EditText etNoOfQuestion;
    private int highscore;
    String categoryName;
    int categoryId;
    String numberOfQuestions;

    public static void start(Context context, String categoryName, int categoryId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setClass(context, MainActivity.class);
        intent.putExtra("categoryName", categoryName);
        intent.putExtra("categoryId", categoryId);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listitems=new String[]{"5","10","15","20","30","50"};
        AlertDialog.Builder mbuilder= new AlertDialog.Builder(MainActivity.this);
        mbuilder.setTitle("Select Questions");
        mbuilder.setCancelable(false);
        mbuilder.setSingleChoiceItems(listitems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                etNoOfQuestion.setText(listitems[i]);
                dialogInterface.dismiss();
            }
        });
        AlertDialog mdialog=mbuilder.create();
        mdialog.show();



        textViewHighscore = findViewById(R.id.text_View_highscore);
        etNoOfQuestion = findViewById(R.id.et_no_of_question);
        gkDataSource = new GKDataSource(MainActivity.this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            categoryName = extras.getString("categoryName");
            categoryId = extras.getInt("categoryId");
        } else {
            return;
        }
        SharedPreferences sp=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        int score=sp.getInt("score",0);
        // int score = data.getIntExtra(QuizActivity.EXTRA_SCORE, 0);
        loadHighscore();
        if (score > highscore) {

            updateHighscore(score);
        }

        if (ConnectionMngr.hasConnection(MainActivity.this)) //returns true if internet available
        {

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("LOADING");
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
            ApiService.getServiceClass().getAllPost().enqueue(new Callback<List<ApiObject>>() {
                @Override
                public void onResponse(Call<List<ApiObject>> call, Response<List<ApiObject>> response) {
                    if (response.isSuccessful()) {
                        progressDialog.dismiss();
                        setQuestionsTable(response);
                    }
                }

                @Override
                public void onFailure(Call<List<ApiObject>> call, Throwable t) {
                    Log.d("", "Error msg is :::" + t.getMessage());
                }
            });

        }
        else{
            //no connection
            Toast toast = Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG);
            toast.show();
        }

        Button buttonStartQuiz = findViewById(R.id.button_start_quiz);
        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
    }

    public void setQuestionsTable(Response<List<ApiObject>> response) {
        gkDataSource.open();
        Question question = new Question();
        for (int i = 0; i < response.body().size(); i++) {
            int id = response.body().get(i).getId();
            question.setId(id);
            question.setAnswerNr(Integer.parseInt(response.body().get(i).getAnswer()));
            question.setOption1(response.body().get(i).getOpta());
            question.setOption2(response.body().get(i).getOptb());
            question.setOption3(response.body().get(i).getOptc());
            question.setOption4(response.body().get(i).getOptd());
            question.setQuestion(response.body().get(i).getQuestion());
            question.setCategoryID(response.body().get(i).getCategory());
            gkDataSource.insertOrUpdateGkQuestion(question);
        }
        gkDataSource.close();
    }

    private void startQuiz() {
        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        numberOfQuestions = etNoOfQuestion.getText().toString();

        intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
        intent.putExtra(EXTRA_CATEGORY_NAME, categoryName);
        intent.putExtra(EXTRA_QUESTION_COUNT, Integer.parseInt(numberOfQuestions));
        startActivityForResult(intent, REQUEST_CODE_QUIZ);

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_QUIZ) {
//            if (resultCode == RESULT_OK) {
//                int score = data.getIntExtra(QuizActivity.EXTRA_SCORE, 0);
//                if (score > highscore) {
//                    updateHighscore(score);
//                }
//            }
//        }
//    }


    private void loadHighscore() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highscore = prefs.getInt(KEY_HIGHSCORE, 0);
        textViewHighscore.setText("Highscore: " + highscore);
    }

    private void updateHighscore(int highscoreNew) {
        highscore = highscoreNew;
        textViewHighscore.setText("Highscore: " + highscore);

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_HIGHSCORE, highscore);
        editor.apply();
    }

}
