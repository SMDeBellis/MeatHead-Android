package com.sdrockstarstudios.meatheadandroid;

import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;


import com.sdrockstarstudios.meatheadandroid.model.AppDatabase;
import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;

import java.util.*;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.provider.Settings.System.DATE_FORMAT;

public class WorkoutLogActivity extends AppCompatActivity
        implements AddExerciseDialogFragment.NoticeDialogListener, DeleteExerciseDialogFragment.NoticeDialogListener,
        DeleteSetDialogFragment.NoticeDialogListener {

    public static final String WORKOUT_NAME_KEY = "workout-name-key";
    public static final String WORKOUT_UUID_KEY = "workout-uuid-key";
    public static final String WORKOUT_START_DATE_KEY = "workout-start-date-key";

    Date startDate;
    String workoutUUID;
    String workoutName;
    LinearLayout viewToModify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_log);
        TextView workoutDateTextView = findViewById(R.id.dateTextView);
        Intent intent = getIntent();
        workoutUUID = intent.getStringExtra(WORKOUT_UUID_KEY);
        workoutName = intent.getStringExtra(WORKOUT_NAME_KEY);
        startDate = new Date(intent.getLongExtra(WORKOUT_START_DATE_KEY, -1));
        String date = DateFormat.getDateFormat(this).format(startDate);
        workoutDateTextView.setText(date);
    }

    public void add_exercise(View view){
        DialogFragment newFragment = new AddExerciseDialogFragment();
        newFragment.show(getSupportFragmentManager(), "addExercise");
    }

    public void delete_exercise(int idToDelete){
        DialogFragment newFragment = new DeleteExerciseDialogFragment(idToDelete);
        newFragment.show(getSupportFragmentManager(), "deleteExercise");
    }

    public void deleteSet(int containerId, int idToRemove){
        DialogFragment newFragment = new DeleteSetDialogFragment(containerId, idToRemove);
        newFragment.show(getSupportFragmentManager(), "deleteSet");
    }

    public void addSet(boolean repsOnly){
        DialogFragment newFragment = new AddSetDialogFragment(repsOnly, "place_holder");
        newFragment.show(getSupportFragmentManager(), "addSet");
    }

    private void handleDeleteExerciseDialogPositiveClick(DialogFragment dialog){
        LinearLayout workoutContentLinearLayout = findViewById(R.id.WorkoutContentLinearLayout);
        int idToDelete = ((DeleteExerciseDialogFragment) dialog).getIdToDelete();
        View viewToDelete = findViewById(idToDelete);
        workoutContentLinearLayout.removeView(viewToDelete);
        Toast.makeText(getApplicationContext(), "Exercise Deleted", Toast.LENGTH_SHORT).show();
    }

    private void handleAddExerciseDialogPositiveClick(DialogFragment dialog){
        EditText exerciseNameEditText = dialog.getDialog().findViewById(R.id.exercise_name_entry);
        String exerciseName = exerciseNameEditText.getText().toString();

        Checkable repsOnlyCheckbox = dialog.getDialog().findViewById(R.id.reps_only_checkbox);
        boolean repsOnly = repsOnlyCheckbox.isChecked();

        View newExercise = buildNewWeightExerciseView(exerciseName, repsOnly);
        Log.i("ID CHECK", String.valueOf(newExercise.getId()));
        newExercise.setTag(UUID.randomUUID());
        LinearLayout exerciseLayout = findViewById(R.id.WorkoutContentLinearLayout);
        exerciseLayout.addView(newExercise);
        ScrollView exerciseScrollView = findViewById(R.id.exerciseEntryScrollView);
        exerciseScrollView.postDelayed(() -> exerciseScrollView.fullScroll(ScrollView.FOCUS_DOWN), 100L);
        exerciseScrollView.fullScroll(ScrollView.FOCUS_AFTER_DESCENDANTS);

        //add exercise to DB
        Exercise exercise = new Exercise();
        exercise.exerciseName = exerciseName;
        exercise.exerciseUUID = newExercise.getTag().toString();
        exercise.parentWorkoutUUID = workoutUUID;
        AppDatabase.getInstance(getApplicationContext()).exerciseDoa().insert(exercise)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> Toast.makeText(getApplicationContext(), "Error inserting exercise: " + exerciseName + " in database.", Toast.LENGTH_SHORT))
                .subscribe();
    }

    private void handleAddSetDialogPositiveClick(DialogFragment dialog){
        addNewExerciseSetViewFromDialog(dialog);
        viewToModify = null;
    }

    private void handleDeleteSetDialogPositiveClick(DialogFragment dialog){
        int idOfContainer = ((DeleteSetDialogFragment) dialog).containerToRemoveFromId;
        int idToRemove = ((DeleteSetDialogFragment) dialog).idToRemove;
        LinearLayout container = findViewById(idOfContainer);
        LinearLayout toRemove = findViewById(idToRemove);
        container.removeView(toRemove);
        Toast.makeText(getApplicationContext(), "Set Deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(dialog instanceof DeleteExerciseDialogFragment)
            handleDeleteExerciseDialogPositiveClick(dialog);
        else if(dialog instanceof AddSetDialogFragment)
            handleAddSetDialogPositiveClick(dialog);
        else if(dialog instanceof DeleteSetDialogFragment)
            handleDeleteSetDialogPositiveClick(dialog);
        else
            handleAddExerciseDialogPositiveClick(dialog);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if(dialog instanceof AddSetDialogFragment){
            viewToModify = null;
        }
    }

    private View buildNewWeightExerciseView(String exerciseName, boolean repsOnly){
        TextView exerciseLabelTextView = new TextView(this);
        exerciseLabelTextView.setText(exerciseName);
        exerciseLabelTextView.setTextSize(25);
        exerciseLabelTextView.setPadding(exerciseLabelTextView.getPaddingLeft(),
                5,
                5,
                exerciseLabelTextView.getPaddingBottom());

        // container for [label, horScrollview[weightxreps, addbutton]]
        LinearLayout exerciseContainer = new LinearLayout(this);
        exerciseContainer.setOrientation(LinearLayout.HORIZONTAL);
        exerciseContainer.addView(exerciseLabelTextView);


        Button addSetButton = new Button(this);
        addSetButton.setText("+");

        // container for [LinearLayoutHor[weightxreps, add button]]
        HorizontalScrollView horScrollView = new HorizontalScrollView(this);
        horScrollView.postDelayed(() -> horScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT), 110L);

        LinearLayout horScrollViewLinearLayout = new LinearLayout(this);
        horScrollViewLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        horScrollViewLinearLayout.setId(View.generateViewId());

        horScrollViewLinearLayout.addView(addSetButton);
        addSetButton.setOnClickListener(v -> {
            viewToModify = horScrollViewLinearLayout;
            addSet(repsOnly);
        });

        horScrollView.addView(horScrollViewLinearLayout);

        exerciseContainer.addView(horScrollView);
        exerciseContainer.setId(View.generateViewId());
        exerciseLabelTextView.setOnLongClickListener(v -> {
            delete_exercise(exerciseContainer.getId());
            return false;
        });

        return exerciseContainer;
    }

    private void addNewExerciseSetViewFromDialog(DialogFragment dialog){

        boolean repsOnly = ((AddSetDialogFragment) dialog).repsOnly;

        LinearLayout setDataLayout = new LinearLayout(this);
        setDataLayout.setOrientation(LinearLayout.HORIZONTAL);
        setDataLayout.setId(View.generateViewId());

        TextView multiplierTextView = new TextView(this);
        multiplierTextView.setTextSize(30);
        multiplierTextView.setText("X");

        TextView repsTextView = new TextView(this);
        repsTextView.setTextSize(30);
        EditText repsEditText = dialog.getDialog().findViewById(R.id.repsEditText);
        repsTextView.setText(repsEditText.getText().toString());

        if(!repsOnly){
            TextView weightTextView = new TextView(this);
            weightTextView.setTextSize(30);
            EditText weightEditText = dialog.getDialog().findViewById(R.id.weightEditText);
            weightTextView.setText(weightEditText.getText().toString());
            setDataLayout.addView(weightTextView);
        }
        int idOfContainingView = viewToModify.getId();
        setDataLayout.setOnLongClickListener(v -> {
            deleteSet(idOfContainingView, setDataLayout.getId());
            return false;
        });
        setDataLayout.addView(multiplierTextView);
        setDataLayout.addView(repsTextView);

        viewToModify.addView(setDataLayout, viewToModify.getChildCount() - 1);
    }
}