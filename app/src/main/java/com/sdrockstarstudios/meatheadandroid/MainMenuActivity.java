package com.sdrockstarstudios.meatheadandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sdrockstarstudios.meatheadandroid.model.AppDatabase;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void pressWorkoutsButton(View view){
        Intent workoutLogIntent = new Intent(this, WorkoutLogMenuActivity.class);
        startActivity(workoutLogIntent);
    }
}