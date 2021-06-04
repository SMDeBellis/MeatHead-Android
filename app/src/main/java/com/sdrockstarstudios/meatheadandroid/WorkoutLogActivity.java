package com.sdrockstarstudios.meatheadandroid;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import java.util.*;

import static android.provider.Settings.System.DATE_FORMAT;

public class WorkoutLogActivity extends AppCompatActivity
        implements AddExerciseDialogFragment.NoticeDialogListener, DeleteExerciseDialogFragment.NoticeDialogListener,
        DeleteSetDialogFragment.NoticeDialogListener {

    Date currentDateTime;
    LinearLayout viewToModify;


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
        ScrollView exerciseScrollView = findViewById(R.id.exerciseEntryScrollView);
        exerciseScrollView.postDelayed(() -> exerciseScrollView.fullScroll(ScrollView.FOCUS_DOWN), 100L);
        exerciseScrollView.fullScroll(ScrollView.FOCUS_AFTER_DESCENDANTS);
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
        exerciseLabelTextView.setOnLongClickListener(v -> {
            delete_exercise(exerciseContainer);
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