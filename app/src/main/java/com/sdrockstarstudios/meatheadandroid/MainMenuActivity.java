package com.sdrockstarstudios.meatheadandroid;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.ActionBar;


public class MainMenuActivity extends MeatheadBaseActivity {
    private final String MAIN_MENU_ACTIVITY_TAG = "MainMenuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        return true;
    }

    public void pressWorkoutsButton(View view){
        Intent workoutLogIntent = new Intent(this, CurrentWorkoutLogMenuActivity.class);
        startActivity(workoutLogIntent);
    }

    public void pressPlanningButton(View view){
        Intent preplannedWorkoutIntent = new Intent(this, PreplannedWorkoutLogMenuActivity.class);
        startActivity(preplannedWorkoutIntent);
    }
}