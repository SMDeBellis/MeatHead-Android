package com.sdrockstarstudios.meatheadandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.UUID;

public class WorkoutLogMenuActivity extends AppCompatActivity implements AddWorkoutDialogFragment.NoticeDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_log_menu);
    }

    public void pressNewWorkoutButton(View view){
        DialogFragment newFragment = new AddWorkoutDialogFragment();
        newFragment.show(getSupportFragmentManager(), "addWorkout");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText workoutNameEditText = dialog.getDialog().findViewById(R.id.workout_name_entry);
        String workoutName = workoutNameEditText.getText().toString();
        String uuid = UUID.randomUUID().toString();
        // create new workout uuid
        // insert workout into database

        Intent intent = new Intent(this, WorkoutLogActivity.class);
        intent.putExtra(WorkoutLogActivity.WORKOUT_NAME_KEY, workoutName);
        intent.putExtra(WorkoutLogActivity.WORKOUT_UUID_KEY, uuid);
        startActivity(intent);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {}
}