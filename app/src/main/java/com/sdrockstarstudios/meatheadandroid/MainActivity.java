package com.sdrockstarstudios.meatheadandroid;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        launchWorkoutLogActivity(null);
    }

    public void launchWorkoutLogActivity(View view){
        Intent intent = new Intent(this, WorkoutLogActivity.class);
        startActivity(intent);
    }
}