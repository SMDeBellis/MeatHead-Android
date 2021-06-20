package com.sdrockstarstudios.meatheadandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sdrockstarstudios.meatheadandroid.model.AppDatabase;
import com.sdrockstarstudios.meatheadandroid.model.tables.Workout;

import java.util.Date;
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

        // insert workout into database
        Workout workout = new Workout();
        workout.startDate = new Date();
        workout.workoutUUID = uuid;
        workout.workoutName = workoutName;
        AppDatabase.getInstance(getApplicationContext()).workoutDao().insert(workout);

        Intent intent = new Intent(this, WorkoutLogActivity.class);
        intent.putExtra(WorkoutLogActivity.WORKOUT_NAME_KEY, workoutName);
        intent.putExtra(WorkoutLogActivity.WORKOUT_UUID_KEY, uuid);
        intent.putExtra(WorkoutLogActivity.WORKOUT_START_DATE_KEY, workout.startDate);
        startActivity(intent);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {}
}