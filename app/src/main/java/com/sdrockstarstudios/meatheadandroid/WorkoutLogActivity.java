package com.sdrockstarstudios.meatheadandroid;


import android.text.InputFilter;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import java.util.*;

import static android.provider.Settings.System.DATE_FORMAT;

public class WorkoutLogActivity extends AppCompatActivity
        implements AddExerciseDialogFragment.NoticeDialogListener, DeleteExerciseDialogFragment.NoticeDialogListener{

    Date currentDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_log);
        TextView workoutDateTextView = findViewById(R.id.dateTextView);
        currentDateTime = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()).getTime();
        String date = DateFormat.getDateFormat(this).format(currentDateTime);
        workoutDateTextView.setText(date);
    }

    public void add_exercise(View view){
        DialogFragment newFragment = new AddExerciseDialogFragment();
        newFragment.show(getSupportFragmentManager(), "addExercise");
    }

    public void delete_exercise(View view){
        DialogFragment newFragment = new DeleteExerciseDialogFragment(view);
        newFragment.show(getSupportFragmentManager(), "deleteExercise");
    }

    private void handleDeleteExerciseDialogPositiveClick(DialogFragment dialog){
        LinearLayout workoutContentLinearLayout = findViewById(R.id.WorkoutContentLinearLayout);
        workoutContentLinearLayout.removeView(((DeleteExerciseDialogFragment) dialog).getViewToDelete());
        Toast.makeText(getApplicationContext(), "Exercise Deleted", Toast.LENGTH_SHORT).show();
    }

    private void handleAddExerciseDialogPositiveClick(DialogFragment dialog){
        EditText exerciseNameEditText = dialog.getDialog().findViewById(R.id.exercise_name_entry);
        String exerciseName = exerciseNameEditText.getText().toString();

        Checkable repsOnlyCheckbox = dialog.getDialog().findViewById(R.id.reps_only_checkbox);
        boolean repsOnly = repsOnlyCheckbox.isChecked();

        View newExercise = buildNewWeightExerciseView(exerciseName, repsOnly, UUID.randomUUID());
        LinearLayout exerciseLayout = findViewById(R.id.WorkoutContentLinearLayout);
        exerciseLayout.addView(newExercise);
        ScrollView exerciseScrollView = (ScrollView) findViewById(R.id.exerciseEntryScrollView);
        exerciseScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                exerciseScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 100L);
        exerciseScrollView.fullScroll(ScrollView.FOCUS_AFTER_DESCENDANTS);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(dialog instanceof DeleteExerciseDialogFragment){
            handleDeleteExerciseDialogPositiveClick(dialog);
        }
        else {
            handleAddExerciseDialogPositiveClick(dialog);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {}

    private View buildNewWeightExerciseView(String exerciseName, boolean repsOnly, UUID exerciseId){
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
//        exerciseContainer.addView(addSetButton);

        // container for [LinearLayoutHor[weightxreps, add button]]
        HorizontalScrollView horScrollView = new HorizontalScrollView(this);
//        horScrollView.addView(exerciseContainer);
        horScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                horScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 110L);

        LinearLayout horScrollViewLinearLayout = new LinearLayout(this);
        horScrollViewLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        horScrollViewLinearLayout.addView(addSetButton);
        addSetButton.setOnClickListener(v -> {
            // for now i'm just doing weighted sets but in the future I want a popup to appear with a selection
            // for either reps or weighted and then the user can add either the weight and reps or just reps depending
            // on the set type.
            View setView = buildNewExerciseSetView(repsOnly, UUID.randomUUID());
            horScrollViewLinearLayout.addView(setView, horScrollViewLinearLayout.getChildCount() - 1);
        });

        horScrollView.addView(horScrollViewLinearLayout);

        exerciseContainer.addView(horScrollView);
        exerciseLabelTextView.setOnLongClickListener(v -> {
            delete_exercise(exerciseContainer);
            return false;
        });

        return exerciseContainer;
    }

    private View buildNewExerciseSetView(boolean repsOnly, UUID setID){

        EditText repsEditText = new EditText(this);
        repsEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        repsEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
        repsEditText.setHint("reps");

        LinearLayout setDataLayout = new LinearLayout(this);
        setDataLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView multiplierTextView = new TextView(this);
        multiplierTextView.setText("X");

        if(!repsOnly){
            EditText weightEditText = new EditText(this);
            weightEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            weightEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
            weightEditText.setHint("lbs");

            setDataLayout.addView(weightEditText);
        }

        setDataLayout.addView(multiplierTextView);
        setDataLayout.addView(repsEditText);
        return setDataLayout;
    }

}