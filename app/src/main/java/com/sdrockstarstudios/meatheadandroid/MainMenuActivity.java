package com.sdrockstarstudios.meatheadandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainMenuActivity extends MeatheadBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
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