package com.sdrockstarstudios.meatheadandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sdrockstarstudios.meatheadandroid.model.AppDatabase;
import com.sdrockstarstudios.meatheadandroid.model.relations.ExerciseAndSets;
import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;
import com.sdrockstarstudios.meatheadandroid.model.tables.Workout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WorkoutLogMenuActivity extends AppCompatActivity
        implements AddWorkoutDialogFragment.NoticeDialogListener,
        LoadWorkoutDialogFragment.NoticeDialogListener,
        CopyWorkoutDialogFragment.NoticeDialogListener {

    private Map<String, Workout> availableWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_log_menu);
        Button loadWorkoutButton = findViewById(R.id.loadWorkoutButton);
        loadWorkoutButton.setOnClickListener(this::pressLoadWorkoutButton);
        Button copyWorkoutButton = findViewById(R.id.copyWorkoutButton);
        copyWorkoutButton.setOnClickListener(this::pressCopyWorkoutButton);
        Disposable d = AppDatabase.getInstance(getApplicationContext()).workoutDao().getAllWorkouts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(list -> {
                    availableWorkouts = createLabelToWorkoutMapping(list);
                    if(availableWorkouts.isEmpty()) {
                        loadWorkoutButton.setVisibility(View.GONE);
                        copyWorkoutButton.setVisibility(View.GONE);
                    }
                })
                .doOnError(error -> {
                    loadWorkoutButton.setEnabled(false);
                    copyWorkoutButton.setEnabled(false);
                })
                .subscribe();
    }

    public void pressNewWorkoutButton(View view){
        DialogFragment newFragment = new AddWorkoutDialogFragment();
        newFragment.show(getSupportFragmentManager(), "addWorkout");
    }

    public void pressLoadWorkoutButton(View view){
        Log.i("pressLoadWorkoutButton", "++++++++++ pressing load workout button");
        List<String> availableWorkoutList = new ArrayList<>(availableWorkouts.keySet());
        DialogFragment newFragment = new LoadWorkoutDialogFragment(availableWorkoutList);
        newFragment.show(getSupportFragmentManager(), "loadWorkout");
    }

    public void pressCopyWorkoutButton(View view){
        Log.i("pressCopyWorkoutButton", "++++++++++ pressing copy workout button");
        List<String> availableWorkoutList = new ArrayList<>(availableWorkouts.keySet());
        Log.i("Testing Copy Workout", "available workout: " + availableWorkoutList.toString());
        DialogFragment newFragment = new CopyWorkoutDialogFragment(availableWorkoutList);
        newFragment.show(getSupportFragmentManager(), "copyWorkout");
    }

    private void onAddWorkoutDialogPositiveClick(DialogFragment dialog){
        EditText workoutNameEditText = dialog.getDialog().findViewById(R.id.workout_name_entry);
        String workoutName = workoutNameEditText.getText().toString();
        String uuid = UUID.randomUUID().toString();

        // insert workout into database
        Workout workout = new Workout();
        workout.startDate = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()).getTime();
        workout.workoutUUID = uuid;
        workout.workoutName = workoutName;
        AppDatabase.getInstance(getApplicationContext()).workoutDao().insert(workout)
                .subscribeOn(Schedulers.io())
                .doOnError(error -> Toast.makeText(getApplicationContext(), "Error inserting workout: " + workoutName + " in database.", Toast.LENGTH_SHORT))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        Intent intent = new Intent(this, WorkoutLogActivity.class);
        intent.putExtra(WorkoutLogActivity.WORKOUT_NAME_KEY, workoutName);
        intent.putExtra(WorkoutLogActivity.WORKOUT_UUID_KEY, uuid);
        intent.putExtra(WorkoutLogActivity.WORKOUT_START_DATE_KEY, workout.startDate.getTime());
        startActivity(intent);
    }

    private void onLoadWorkoutDialogPositiveClick(DialogFragment dialog){
        String selectedWorkout = ((LoadWorkoutDialogFragment) dialog).getSelectedWorkout();
        Log.i(this.getClass().toString(), "onLoadWorkoutDialogPositiveClick - selectedWorkout: " + selectedWorkout);
        //Need to create an intent with the workout data and send to WorkoutLogActivity and have it build the workout from the database.
        Intent intent = new Intent(this, WorkoutLogActivity.class);
        Workout workout = availableWorkouts.get(selectedWorkout);
        assert workout != null;
        intent.putExtra(WorkoutLogActivity.WORKOUT_UUID_KEY, workout.workoutUUID);
        startActivity(intent);
    }

    private void onCopyWorkoutDialogPositiveClick(DialogFragment dialog){
        String selectedWorkout = ((CopyWorkoutDialogFragment) dialog).getSelectedWorkout();
        Workout workoutToCopy = availableWorkouts.get(selectedWorkout);
        String workoutName = stripDateFromWorkoutName(selectedWorkout);
        String uuid = UUID.randomUUID().toString();

        // insert workout into database
        Workout workout = new Workout();
        workout.startDate = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()).getTime();
        workout.workoutUUID = uuid;
        workout.workoutName = workoutName;
        AppDatabase.getInstance(getApplicationContext()).workoutDao().insert(workout)
                .subscribeOn(Schedulers.io())
                .doOnError(error -> Toast.makeText(getApplicationContext(), "Error inserting workout: " + workoutName + " in database.", Toast.LENGTH_SHORT))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        // insert a new exercise for each existing exercise with the new workout.
        AppDatabase.getInstance(getApplicationContext())
                .workoutDao()
                .getWorkout(workoutToCopy.workoutUUID)
                .subscribeOn(Schedulers.io())
                .doOnError(error -> Toast.makeText(getApplicationContext(), "Error inserting workout: " + workoutName + " in database.", Toast.LENGTH_SHORT))
                .doOnSuccess(x -> {
                    for(ExerciseAndSets eas : x.exercisesAndSets){
                        Exercise exercise = new Exercise();
                        exercise.exerciseName = eas.exercise.exerciseName;
                        exercise.exerciseUUID = UUID.randomUUID().toString();
                        exercise.parentWorkoutUUID = workout.workoutUUID;
                        exercise.repsOnly = eas.exercise.repsOnly;
                        AppDatabase.getInstance(getApplicationContext()).exerciseDoa().insert(exercise)
                                .subscribeOn(Schedulers.io())
                                .doOnError(error -> Toast.makeText(getApplicationContext(), "Error inserting exercise: " + exercise.exerciseName + " in database.", Toast.LENGTH_SHORT))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();


        Intent intent = new Intent(this, WorkoutLogActivity.class);
        intent.putExtra(WorkoutLogActivity.WORKOUT_NAME_KEY, workoutName);
        intent.putExtra(WorkoutLogActivity.WORKOUT_UUID_KEY, uuid);
        intent.putExtra(WorkoutLogActivity.WORKOUT_START_DATE_KEY, workout.startDate.getTime());
        startActivity(intent);
    }

    @Override // needs to handle the LoadWorkoutDialogFragment
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(dialog instanceof AddWorkoutDialogFragment)
            onAddWorkoutDialogPositiveClick(dialog);
        else if(dialog instanceof LoadWorkoutDialogFragment){
            onLoadWorkoutDialogPositiveClick(dialog);
        }
        else {
            onCopyWorkoutDialogPositiveClick(dialog);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {}

    private Map<String, Workout> createLabelToWorkoutMapping(List<Workout> workouts){
        HashMap<String, Workout> workoutMapping = new HashMap<>();
        for(Workout workout: workouts){
            String date = DateFormat.getDateFormat(this).format(workout.startDate);
            String label = workout.workoutName + " " + date;
            if(workout.endDate == null){
                label = "* " + label;
            }
            workoutMapping.put(label, workout);
        }
        return workoutMapping;
    }

    private String stripDateFromWorkoutName(String workoutName){
        Pattern pattern = Pattern.compile("\\**\\s*((\\d*|\\D*|\\s*)+)\\s\\d+/\\d+/\\d+");
        Matcher matcher = pattern.matcher(workoutName);
        matcher.find();
        return matcher.group(1);
    }
}