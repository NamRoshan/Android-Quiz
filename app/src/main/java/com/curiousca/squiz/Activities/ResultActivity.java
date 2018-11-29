package com.curiousca.squiz.Activities;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.curiousca.squiz.R;
import com.curiousca.squiz.RecyclerView.RecyclerViewActivity;
import java.util.ArrayList;
import java.util.PriorityQueue;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

public class ResultActivity extends AppCompatActivity implements OnChartValueSelectedListener{
    private  TextView tv_wrong;
    private TextView tv_correct;
    private TextView textView_high_score;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_HIGHSCORE = "keyHighscore";
    private int highscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Button btn = (Button) findViewById(R.id.btn_Again);
        tv_correct=(TextView)findViewById(R.id.tv_correct_question);
        tv_wrong=(TextView)findViewById(R.id.tv_wrong_question) ;
        TextView scoreTxtView = (TextView) findViewById(R.id.score);
        textView_high_score=(TextView) findViewById(R.id.high_score);
       // ImageView img = (ImageView) findViewById(R.id.img1);


        SharedPreferences sp=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        int score=sp.getInt("score",0);
        int correct=sp.getInt("correct",0);
        int wrong =sp.getInt("wrong",0);

        tv_correct.setText("Correct : "+ correct);
        tv_wrong.setText("Wrong : "+ wrong);
        scoreTxtView.setText(String.valueOf("Current Score : "+score));
        loadHighscore();
        if (score > highscore) {

            updateHighscore(score);
        }






        PieChart pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        ArrayList<Entry> yvalues = new ArrayList<Entry>();
        yvalues.add(new Entry(correct, 0));
        yvalues.add(new Entry(wrong, 1));
        yvalues.add(new Entry(score, 2));

        PieDataSet dataSet = new PieDataSet(yvalues,"" );

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Correct");
        xVals.add("Wrong");
        xVals.add("Score");


        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);


        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(25f);
        pieChart.setHoleRadius(25f);

        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        pieChart.setOnChartValueSelectedListener(this);
        pieChart.animateXY(1400, 1400);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this, RecyclerViewActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                        + ", DataSet index: " + dataSetIndex);
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");

    }



    private void loadHighscore() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highscore = prefs.getInt(KEY_HIGHSCORE, 0);
        textView_high_score.setText("Highscore: " + highscore);
    }

    private void updateHighscore(int highscoreNew) {
        highscore = highscoreNew;
        textView_high_score.setText("Highscore: " + highscore);

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_HIGHSCORE, highscore);
        editor.apply();
    }
}
