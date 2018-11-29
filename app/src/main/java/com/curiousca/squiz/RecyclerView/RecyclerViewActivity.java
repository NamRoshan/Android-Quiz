package com.curiousca.squiz.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.curiousca.squiz.Activities.DevelopersProfile;
import com.curiousca.squiz.Activities.MainActivity;
import com.curiousca.squiz.QuizClickInterface;
import com.curiousca.squiz.R;
import com.curiousca.squiz.RecyclerViewAdapter;

import java.util.ArrayList;


public class RecyclerViewActivity extends AppCompatActivity implements QuizClickInterface {
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    private static final String TAG = "RecyclerViewActivity";
    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    public static void start(Context context) {
        Intent intent = new Intent(context, RecyclerViewActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_vertical_view);
        Log.d(TAG, "onCreate: started.");

        initImageBitmaps();
        //NavDrawer
        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);
        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.developer_Profile:
                        Intent intent=new Intent(RecyclerViewActivity.this, DevelopersProfile.class);
                        startActivity(intent);
                        finish();

                        break;
                    case R.id.settings:
                        Toast.makeText(RecyclerViewActivity.this, "Settings",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.account:

                    default:

                }
                return true;

            }
        });

    }
        private void initImageBitmaps() {
            Log.d(TAG, "initImageBitmaps: preparing bitmaps.");
            mNames.add("Computer Science");
            mNames.add("G.K");
            mNames.add("Sports");
        mNames.add("OS");
        mNames.add("DBMS");
        mNames.add("DS");
        mNames.add("Economics");
        mNames.add("DSP");

        initRecyclerView();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        //vertical recycler view
        RecyclerView recyclerView = findViewById(R.id.recycler_vertical_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onRecyclerItemClick(int adapterPosition, String categoryName) {
        MainActivity.start(this, categoryName, adapterPosition + 1);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}






















