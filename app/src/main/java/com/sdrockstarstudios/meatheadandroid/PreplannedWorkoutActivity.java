package com.sdrockstarstudios.meatheadandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.sdrockstarstudios.meatheadandroid.model.AppDatabase;
import com.sdrockstarstudios.meatheadandroid.model.relations.ExerciseAndSets;
import com.sdrockstarstudios.meatheadandroid.model.relations.WorkoutAndExercises;
import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;

import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PreplannedWorkoutActivity extends MeatheadBaseActivity implements AddExerciseDialogFragment.NoticeDialogListener,
        DeleteExerciseDialogFragment.NoticeDialogListener,
        ExerciseInfoDialogFragment.NoticeDialogListener {

    public static final String WORKOUT_UUID_KEY = "workout-uuid-key";
    public static final String EXERCISE_NAME_TEXT_VIEW_TAG = "exercise-name-text-view_key";

    private String workoutUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preplanned_workout_log);
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

    private void buildFromDatabase(WorkoutAndExercises workout){
        Log.i("WORKOUTLOGACTIVITY", "building new workout: " + workout.workout.workoutName);

        // set workout date label
        TextView workoutDateTextView = findViewById(R.id.dateTextView);
        String date = DateFormat.getDateFormat(this).format(workout.workout.startDate);
        workoutDateTextView.setText(date);

        Button addExerciseButton = findViewById(R.id.add_exercise_button);
        Button saveWorkoutButton = findViewById(R.id.save_workout_button);
        addExerciseButton.setOnClickListener(this::add_exercise);
        //saveWorkoutButton.setOnClickListener(v -> endWorkout(workout.workout.workoutUUID));


        if(!workout.exercisesAndSets.isEmpty()){
            LinearLayout exerciseLayout = findViewById(R.id.WorkoutContentLinearLayout);
            for (ExerciseAndSets exercise : workout.exercisesAndSets) {
                String exerciseName = exercise.exercise.exerciseName;
                ConstraintLayout exerciseNameLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.preplanned_exercise_list_row, null);
                exerciseNameLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.workout_list_border, null));
                exerciseNameLayout.setId(View.generateViewId());
                exerciseNameLayout.setTag(exercise.exercise.exerciseUUID);
                TextView exerciseLabelTextView = exerciseNameLayout.findViewById(R.id.preplannedExerciseNameTextView);
                exerciseLabelTextView.setOnClickListener(v -> showExerciseInfo(exerciseName));
                exerciseLabelTextView.setText(exerciseName);
                exerciseLayout.addView(exerciseNameLayout);
                exerciseLabelTextView.setOnLongClickListener(v -> {
                    delete_exercise(exerciseNameLayout.getId());
                    return false;
                });
            }
        }
    }

    private TextView buildExerciseLabelTextView(String exerciseName){
        TextView exerciseLabelTextView = new TextView(this);
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

    public void showExerciseInfo(String exerciseName){
        Disposable d = AppDatabase.getInstance(this).workoutDao().getAllWorkoutsWithExercise(exerciseName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workouts -> {
                    DialogFragment newFragment = new ExerciseInfoDialogFragment(exerciseName, workouts);
                    newFragment.show(getSupportFragmentManager(), "exerciseInfo");
                });
    }

    public void add_exercise(View view){
        DialogFragment newFragment = new AddExerciseDialogFragment();
        newFragment.show(getSupportFragmentManager(), "addExercise");
    }

    public void delete_exercise(int idToDelete){
        DialogFragment newFragment = new DeleteExerciseDialogFragment(idToDelete);
        newFragment.show(getSupportFragmentManager(), "deleteExercise");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(dialog instanceof DeleteExerciseDialogFragment)
            handleDeleteExerciseDialogPositiveClick(dialog);
//        else if(dialog instanceof AddSetDialogFragment)
//            handleAddSetDialogPositiveClick(dialog);
//        else if(dialog instanceof DeleteSetDialogFragment)
//            handleDeleteSetDialogPositiveClick(dialog);
//        else if(dialog instanceof EndWorkoutDialogFragment)
//            handleEndWorkoutDialogPositiveClick(dialog);
        else if(dialog instanceof AddExerciseDialogFragment)
            handleAddExerciseDialogPositiveClick(dialog);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {}

    private void handleDeleteExerciseDialogPositiveClick(DialogFragment dialog){
        LinearLayout workoutContentLinearLayout = findViewById(R.id.WorkoutContentLinearLayout);
        int idToDelete = ((DeleteExerciseDialogFragment) dialog).getIdToDelete();
        View viewToDelete = findViewById(idToDelete);

        Exercise exercise = new Exercise();
        exercise.parentWorkoutUUID = workoutUUID;
        exercise.exerciseUUID = viewToDelete.getTag().toString();
        exercise.exerciseName = ((TextView) viewToDelete.findViewById(R.id.preplannedExerciseNameTextView)).getText().toString();

        Disposable d = AppDatabase.getInstance(getApplicationContext()).exerciseDoa().delete(exercise)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> Toast.makeText(getApplicationContext(), "Error deleting exercise from database.", Toast.LENGTH_SHORT))
                .subscribe(() -> {
                    workoutContentLinearLayout.removeView(viewToDelete);
                    Toast.makeText(getApplicationContext(), "Exercise Deleted", Toast.LENGTH_SHORT).show();
                });
    }

    private void handleAddExerciseDialogPositiveClick(@NonNull DialogFragment dialog){
        EditText exerciseNameEditText = dialog.getDialog().findViewById(R.id.exercise_name_entry);
        String exerciseName = exerciseNameEditText.getText().toString().trim();

        Checkable repsOnlyCheckbox = dialog.getDialog().findViewById(R.id.reps_only_checkbox);
        boolean repsOnly = repsOnlyCheckbox.isChecked();

        //add exercise to DB
        Exercise exercise = new Exercise();
        exercise.exerciseName = exerciseName;
        exercise.exerciseUUID = UUID.randomUUID().toString();
        exercise.parentWorkoutUUID = workoutUUID;
        exercise.repsOnly = repsOnly;
        AppDatabase.getInstance(getApplicationContext()).exerciseDoa().insert(exercise)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> Toast.makeText(getApplicationContext(), "Error inserting exercise: " + exerciseName + " in database.", Toast.LENGTH_SHORT))
                .doOnComplete(() -> {
                    ConstraintLayout exerciseNameLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.preplanned_exercise_list_row, null);
                    exerciseNameLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.workout_list_border, null));
                    exerciseNameLayout.setTag(exercise.exerciseUUID);
                    exerciseNameLayout.setId(View.generateViewId());

                    TextView exerciseLabelTextView = exerciseNameLayout.findViewById(R.id.preplannedExerciseNameTextView);
                    exerciseLabelTextView.setText(exerciseName);
                    exerciseLabelTextView.setOnLongClickListener(v -> {
                        delete_exercise(exerciseNameLayout.getId());
                        return false;
                    });

                    LinearLayout exerciseLayout = findViewById(R.id.WorkoutContentLinearLayout);
                    exerciseLayout.addView(exerciseNameLayout);
                    ScrollView exerciseScrollView = findViewById(R.id.exerciseEntryScrollView);
                    exerciseScrollView.postDelayed(() -> exerciseScrollView.fullScroll(ScrollView.FOCUS_DOWN), 100L);
                })
                .subscribe();
    }
}
