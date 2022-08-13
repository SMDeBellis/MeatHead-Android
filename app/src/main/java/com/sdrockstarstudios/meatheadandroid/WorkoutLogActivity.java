package com.sdrockstarstudios.meatheadandroid;

import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.DialogFragment;


import com.sdrockstarstudios.meatheadandroid.model.AppDatabase;
import com.sdrockstarstudios.meatheadandroid.model.FinishWorkout;
import com.sdrockstarstudios.meatheadandroid.model.relations.ExerciseAndSets;
import com.sdrockstarstudios.meatheadandroid.model.relations.WorkoutAndExercises;
import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;
import com.sdrockstarstudios.meatheadandroid.model.tables.Sets;

import java.util.*;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WorkoutLogActivity extends AppCompatActivity
        implements AddExerciseDialogFragment.NoticeDialogListener, DeleteExerciseDialogFragment.NoticeDialogListener,
        DeleteSetDialogFragment.NoticeDialogListener, EndWorkoutDialogFragment.NoticeDialogListener,
        ExerciseInfoDialogFragment.NoticeDialogListener {

    public static final String WORKOUT_NAME_KEY = "workout-name-key";
    public static final String WORKOUT_UUID_KEY = "workout-uuid-key";
    public static final String WORKOUT_START_DATE_KEY = "workout-start-date-key";

    public static final String EXERCISE_NAME_TEXT_VIEW_TAG = "exercise-name-text-view_key";

    String workoutUUID;
    LinearLayout viewToModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_log);
        Intent intent = getIntent();
        workoutUUID = intent.getStringExtra(WORKOUT_UUID_KEY);
        Disposable d = AppDatabase.getInstance(this).workoutDao().getWorkout(workoutUUID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError( error -> {
                    Toast.makeText(this, "Error occurred getting workout data.", Toast.LENGTH_SHORT).show();
                    // Go back to WorkoutLogActivityMenu?
                })
                .doOnSuccess(this::buildFromDatabase)
                .subscribe();
    }


    public void add_exercise(View view){
        DialogFragment newFragment = new AddExerciseDialogFragment();
        newFragment.show(getSupportFragmentManager(), "addExercise");
    }

    public void delete_exercise(int idToDelete){
        DialogFragment newFragment = new DeleteExerciseDialogFragment(idToDelete);
        newFragment.show(getSupportFragmentManager(), "deleteExercise");
    }

    public void deleteSet(int containerId, String tagToRemove){
        DialogFragment newFragment = new DeleteSetDialogFragment(containerId, tagToRemove);
        newFragment.show(getSupportFragmentManager(), "deleteSet");
    }

    public void addSet(boolean repsOnly){
        DialogFragment newFragment = new AddSetDialogFragment(repsOnly, "place_holder");
        newFragment.show(getSupportFragmentManager(), "addSet");
    }

    public void endWorkout(String uuid){
        DialogFragment newFragment = new EndWorkoutDialogFragment(uuid);
        newFragment.show(getSupportFragmentManager(), "endWorkout");
    }

    public void showExerciseInfo(String exerciseName){
        Disposable d = AppDatabase.getInstance(this).workoutDao().getAllWorkoutsWithExercise(exerciseName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workouts -> {
                    DialogFragment newFragment = new ExerciseInfoDialogFragment(exerciseName, workouts);
                    newFragment.show(getSupportFragmentManager(), "exerciseInfo");
                });
    }

    private void handleEndWorkoutDialogPositiveClick(DialogFragment dialog){
        String workoutUUID = ((EndWorkoutDialogFragment) dialog).getWorkoutId();
        AppDatabase.getInstance(this).workoutDao().finishWorkout(new FinishWorkout(workoutUUID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> Toast.makeText(this, "Error ending workout.", Toast.LENGTH_SHORT))
                .doOnComplete(() -> {
                    Intent intent = new Intent(this, CurrentWorkoutLogMenuActivity.class);
                    startActivity(intent);
                })
                .subscribe();
    }

    private void handleDeleteExerciseDialogPositiveClick(DialogFragment dialog){
        LinearLayout workoutContentLinearLayout = findViewById(R.id.WorkoutContentLinearLayout);
        int idToDelete = ((DeleteExerciseDialogFragment) dialog).getIdToDelete();
        View viewToDelete = findViewById(idToDelete);

        Exercise exercise = new Exercise();
        exercise.parentWorkoutUUID = workoutUUID;
        exercise.exerciseUUID = viewToDelete.getTag().toString();
        exercise.exerciseName = ((TextView) viewToDelete.findViewWithTag(EXERCISE_NAME_TEXT_VIEW_TAG)).getText().toString();

        Disposable d = AppDatabase.getInstance(getApplicationContext()).exerciseDoa().delete(exercise)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> Toast.makeText(getApplicationContext(), "Error deleting exercise from database.", Toast.LENGTH_SHORT))
                .subscribe(() -> {
                    workoutContentLinearLayout.removeView(viewToDelete);
                    Toast.makeText(getApplicationContext(), "Exercise Deleted", Toast.LENGTH_SHORT).show();
                });
    }

    private void buildFromDatabase(WorkoutAndExercises workout){
        Log.i("WORKOUTLOGACTIVITY", "building new workout: " + workout.workout.workoutName);

        // set workout date label
        TextView workoutDateTextView = findViewById(R.id.dateTextView);
        String date = DateFormat.getDateFormat(this).format(workout.workout.startDate);
        workoutDateTextView.setText(date);

        boolean editable = workout.workout.endDate == null;

        if(!editable){
            removeAddExerciseAndEndWorkoutButtons();
        }
        else{
            Button addExerciseButton = findViewById(R.id.add_exercise_button);
            Button endWorkoutButton = findViewById(R.id.end_workout_button);
            addExerciseButton.setOnClickListener(this::add_exercise);
            endWorkoutButton.setOnClickListener(v -> endWorkout(workout.workout.workoutUUID));
        }

        if(!workout.exercisesAndSets.isEmpty()){
            LinearLayout exerciseLayout = findViewById(R.id.WorkoutContentLinearLayout);
            for (ExerciseAndSets exercise : workout.exercisesAndSets) {
                TextView exerciseLabelTextView = buildExerciseLabelTextView(exercise.exercise.exerciseName);
                LinearLayout exerciseContainer = new LinearLayout(this);
                exerciseContainer.setOrientation(LinearLayout.HORIZONTAL);
                exerciseContainer.addView(exerciseLabelTextView);

                View exerciseView = buildExistingExerciseView(exercise, editable);
                exerciseLayout.addView(exerciseView);
            }
        }
    }

    private void removeAddExerciseAndEndWorkoutButtons(){
        Button addExerciseButton = findViewById(R.id.add_exercise_button);
        Button endWorkoutButton = findViewById(R.id.end_workout_button);

        ConstraintLayout mainLayout = findViewById(R.id.WorkoutLogMainLayout);

        ConstraintSet constraints = new ConstraintSet();
        constraints.clone(mainLayout);
        constraints.clear(R.id.add_exercise_button, ConstraintSet.END);
        constraints.clear(R.id.add_exercise_button, ConstraintSet.BOTTOM);
        constraints.clear(R.id.end_workout_button, ConstraintSet.END);
        constraints.clear(R.id.end_workout_button, ConstraintSet.BOTTOM);
        constraints.clear(R.id.exerciseEntryScrollView, ConstraintSet.BOTTOM);
        constraints.connect(R.id.exerciseEntryScrollView, ConstraintSet.BOTTOM, mainLayout.getId(), ConstraintSet.BOTTOM);
        constraints.setMargin(R.id.exerciseEntryScrollView, ConstraintSet.BOTTOM, 20 + addExerciseButton.getHeight());
        constraints.applyTo(mainLayout);

        mainLayout.removeView(addExerciseButton);
        mainLayout.removeView(endWorkoutButton);
    }

    private void handleAddExerciseDialogPositiveClick(@NonNull DialogFragment dialog){
        EditText exerciseNameEditText = dialog.getDialog().findViewById(R.id.exercise_name_entry);
        String exerciseName = exerciseNameEditText.getText().toString();

        Checkable repsOnlyCheckbox = dialog.getDialog().findViewById(R.id.reps_only_checkbox);
        boolean repsOnly = repsOnlyCheckbox.isChecked();

        View newExercise = buildNewWeightExerciseView(exerciseName, repsOnly);
        Log.i("ID CHECK", String.valueOf(newExercise.getId()));

        LinearLayout exerciseLayout = findViewById(R.id.WorkoutContentLinearLayout);
        exerciseLayout.addView(newExercise);
        ScrollView exerciseScrollView = findViewById(R.id.exerciseEntryScrollView);
        exerciseScrollView.postDelayed(() -> exerciseScrollView.fullScroll(ScrollView.FOCUS_DOWN), 100L);

        //add exercise to DB
        Exercise exercise = new Exercise();
        exercise.exerciseName = exerciseName;
        exercise.exerciseUUID = newExercise.getTag().toString();
        exercise.parentWorkoutUUID = workoutUUID;
        exercise.repsOnly = repsOnly;
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
        String tagToRemove = ((DeleteSetDialogFragment) dialog).tagToRemove;
        LinearLayout container = findViewById(idOfContainer);
        LinearLayout toRemove = container.findViewWithTag(tagToRemove);
        Sets toRemoveFromDatabase = createSetToRemove(toRemove, container.getTag().toString(), container.indexOfChild(toRemove));
        Disposable d = AppDatabase.getInstance(getApplicationContext()).setsDao().delete(toRemoveFromDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> Toast.makeText(getApplicationContext(), "Error deleting set from database.", Toast.LENGTH_SHORT))
                .subscribe(() -> {
                    container.removeView(toRemove);
                    Toast.makeText(getApplicationContext(), "Set Deleted", Toast.LENGTH_SHORT).show();
                });
    }

    @NonNull
    private Sets createSetToRemove(@NonNull LinearLayout view, String parentUUID, int index){
        Sets set = new Sets();
        set.index = index;
        set.parentExerciseUUID = parentUUID;
        set.setUUID = view.getTag().toString();

        int childCount = view.getChildCount();
        set.reps = Integer.parseInt(((TextView) view.getChildAt(childCount - 1)).getText().toString());
        if(childCount == 3){
            set.weight = Integer.parseInt(((TextView) view.getChildAt(0)).getText().toString());
        }
        return set;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(dialog instanceof DeleteExerciseDialogFragment)
            handleDeleteExerciseDialogPositiveClick(dialog);
        else if(dialog instanceof AddSetDialogFragment)
            handleAddSetDialogPositiveClick(dialog);
        else if(dialog instanceof DeleteSetDialogFragment)
            handleDeleteSetDialogPositiveClick(dialog);
        else if(dialog instanceof EndWorkoutDialogFragment)
            handleEndWorkoutDialogPositiveClick(dialog);
        else
            handleAddExerciseDialogPositiveClick(dialog);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if(dialog instanceof AddSetDialogFragment){
            viewToModify = null;
        }
    }

    private View buildExistingExerciseView(ExerciseAndSets exerciseAndSets, boolean editable){
        TextView exerciseLabelTextView = buildExerciseLabelTextView(exerciseAndSets.exercise.exerciseName);

        // container for [label, horScrollview[weightxreps, addbutton]]
        LinearLayout exerciseContainer = new LinearLayout(this);
        exerciseContainer.setOrientation(LinearLayout.HORIZONTAL);
        exerciseContainer.addView(exerciseLabelTextView);

        // container for [LinearLayoutHor[weightxreps, add button]]
        HorizontalScrollView horScrollView = new HorizontalScrollView(this);
//        horScrollView.postDelayed(() -> horScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT), 110L);

        LinearLayout horScrollViewLinearLayout = new LinearLayout(this);
        horScrollViewLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        horScrollViewLinearLayout.setId(View.generateViewId());
        UUID exerciseUUID = UUID.fromString(exerciseAndSets.exercise.exerciseUUID);
        horScrollViewLinearLayout.setTag(exerciseUUID);

        Button addSetButton = (Button) LayoutInflater.from(this).inflate(R.layout.add_set_button, null);
        exerciseContainer.addView(addSetButton);

        if(editable){

            addSetButton.setOnClickListener(v -> {
                viewToModify = horScrollViewLinearLayout;
                addSet(exerciseAndSets.exercise.repsOnly);
            });

            exerciseLabelTextView.setOnLongClickListener(v -> {
                delete_exercise(exerciseContainer.getId());
                return false;
            });
        }
        else{
            addSetButton.setVisibility(View.INVISIBLE);
            exerciseLabelTextView.setOnLongClickListener(v -> {
                Toast.makeText(this, "Workout not editable", Toast.LENGTH_SHORT).show();
                return false;
            });
        }

        horScrollView.addView(horScrollViewLinearLayout);

        exerciseContainer.setTag(exerciseUUID);
        exerciseContainer.addView(horScrollView);
        exerciseContainer.setId(View.generateViewId());

        //need to build sets
        List<LinearLayout> sets = buildSetLayoutsFromSetsList(exerciseAndSets.setList, horScrollViewLinearLayout.getId(), editable, exerciseAndSets.exercise.repsOnly);
        Collections.reverse(sets);

        for(LinearLayout set: sets){
            horScrollViewLinearLayout.addView(set, 0, getSetLayoutParams(exerciseAndSets.exercise.repsOnly));
        }

        return exerciseContainer;
    }

    @NonNull
    private View buildNewWeightExerciseView(String exerciseName, boolean repsOnly){
        TextView exerciseLabelTextView = buildExerciseLabelTextView(exerciseName);

        // container for [label, horScrollview[weightxreps, addbutton]]
        LinearLayout exerciseContainer = new LinearLayout(this);
        exerciseContainer.setOrientation(LinearLayout.HORIZONTAL);
        exerciseContainer.addView(exerciseLabelTextView);


        Button addSetButton = (Button) LayoutInflater.from(this).inflate(R.layout.add_set_button, null);//new Button(this);
        addSetButton.setText("+");
        exerciseContainer.addView(addSetButton);

        // container for [LinearLayoutHor[weightxreps, add button]]
        HorizontalScrollView horScrollView = new HorizontalScrollView(this);
        //horScrollView.postDelayed(() -> horScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT), 110L);

        LinearLayout horScrollViewLinearLayout = new LinearLayout(this);
        horScrollViewLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        horScrollViewLinearLayout.setId(View.generateViewId());
        UUID exerciseUUID = UUID.randomUUID();
        horScrollViewLinearLayout.setTag(exerciseUUID);

        //horScrollViewLinearLayout.addView(addSetButton);
        addSetButton.setOnClickListener(v -> {
            viewToModify = horScrollViewLinearLayout;
            addSet(repsOnly);
        });

        horScrollView.addView(horScrollViewLinearLayout);

        exerciseContainer.setTag(exerciseUUID);
        exerciseContainer.addView(horScrollView);
        exerciseContainer.setId(View.generateViewId());
        exerciseLabelTextView.setOnLongClickListener(v -> {
            delete_exercise(exerciseContainer.getId());
            return false;
        });

        return exerciseContainer;
    }

    private TextView buildExerciseLabelTextView(String exerciseName){
        TextView exerciseLabelTextView = new TextView(this);
        exerciseLabelTextView.setTag(EXERCISE_NAME_TEXT_VIEW_TAG);
        exerciseLabelTextView.setText(exerciseName);
        exerciseLabelTextView.setTextSize(25);
        exerciseLabelTextView.setPadding(5,
                5,
                5,
                exerciseLabelTextView.getPaddingBottom());
        exerciseLabelTextView.setWidth(getResources().getDimensionPixelSize(R.dimen.exercise_name_text_edit_width));
        exerciseLabelTextView.setOnClickListener(v -> showExerciseInfo(exerciseName));
        return exerciseLabelTextView;
    }


    // need to set the setOnLongClickListener for each of these after return if editable with containing views id.
    private List<LinearLayout> buildSetLayoutsFromSetsList(List<Sets> sets, int containerId, boolean editable, boolean repsOnly){
        List<LinearLayout> setViews = new ArrayList<>();
        for(Sets set: sets){
            LinearLayout setDataLayout = new LinearLayout(this);
            setDataLayout.setOrientation(LinearLayout.HORIZONTAL);
            setDataLayout.setId(View.generateViewId());
            setDataLayout.setTag(set.setUUID);

            TextView multiplierTextView = new TextView(this);
            multiplierTextView.setTextSize(30);
            multiplierTextView.setText("X");

            TextView repsTextView = new TextView(this);
            repsTextView.setTextSize(30);
            repsTextView.setText(String.valueOf(set.reps));

            if(!repsOnly){
                TextView weightTextView = new TextView(this);
                weightTextView.setTextSize(30);
                weightTextView.setText(String.valueOf(set.weight));
                setDataLayout.addView(weightTextView);
            }
            setDataLayout.addView(multiplierTextView);
            setDataLayout.addView(repsTextView);

            if(editable){
                setDataLayout.setOnLongClickListener(v -> {
                    deleteSet(containerId, setDataLayout.getTag().toString());
                    return false;
                });
            }
            setViews.add(setDataLayout);
        }
        return setViews;
    }

    private void addNewExerciseSetViewFromDialog(@NonNull DialogFragment dialog){

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


        Sets set = new Sets();
        set.reps = Integer.parseInt(repsEditText.getText().toString());
        set.setUUID = UUID.randomUUID().toString();
        setDataLayout.setTag(set.setUUID);

        if(!repsOnly){
            TextView weightTextView = new TextView(this);
            weightTextView.setTextSize(30);
            EditText weightEditText = dialog.getDialog().findViewById(R.id.weightEditText);
            set.weight = Integer.parseInt(weightEditText.getText().toString());
            weightTextView.setText(weightEditText.getText().toString());
            setDataLayout.addView(weightTextView);
        }
        int idOfContainingView = viewToModify.getId();
        setDataLayout.setOnLongClickListener(v -> {
            deleteSet(idOfContainingView, setDataLayout.getTag().toString());
            return false;
        });
        setDataLayout.addView(multiplierTextView);
        setDataLayout.addView(repsTextView);

        int setIndex = viewToModify.getChildCount();
        viewToModify.addView(setDataLayout, setIndex, getSetLayoutParams(repsOnly));

        set.parentExerciseUUID = viewToModify.getTag().toString();
        set.index = setIndex;

        AppDatabase.getInstance(getApplicationContext()).setsDao().insert(set)
                .subscribeOn(Schedulers.io())
                .doOnError(error -> Toast.makeText(getApplicationContext(), "Error adding set to exercise.", Toast.LENGTH_SHORT))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private LinearLayout.LayoutParams getSetLayoutParams(boolean repsOnly){
        LinearLayout.LayoutParams setLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if(repsOnly)
            setLayoutParams.setMargins(15, 5, 0, 0);
        else
            setLayoutParams.setMargins(10, 5, 0, 0);
        return setLayoutParams;
    }
}