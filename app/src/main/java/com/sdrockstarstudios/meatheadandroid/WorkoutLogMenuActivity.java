package com.sdrockstarstudios.meatheadandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdrockstarstudios.meatheadandroid.model.AppDatabase;
import com.sdrockstarstudios.meatheadandroid.model.relations.ExerciseAndSets;
import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;
import com.sdrockstarstudios.meatheadandroid.model.tables.Workout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WorkoutLogMenuActivity extends AppCompatActivity implements AddWorkoutDialogFragment.NoticeDialogListener{

    private Map<String, Workout> availableWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_log_menu);
        Button loadWorkoutButton = findViewById(R.id.loadWorkoutButton);
        loadWorkoutButton.setOnClickListener(this::pressLoadWorkoutButton);
        loadWorkoutButton.setEnabled(false);
        Button copyWorkoutButton = findViewById(R.id.copyWorkoutButton);
        copyWorkoutButton.setOnClickListener(this::pressCopyWorkoutButton);
        copyWorkoutButton.setEnabled(false);

        List<Button> toEnable = Arrays.asList(loadWorkoutButton, copyWorkoutButton);

        Disposable d = AppDatabase.getInstance(getApplicationContext()).workoutDao().getAllWorkouts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(list -> {
                    availableWorkouts = createUUIDToWorkoutMapping(list);
                    if(availableWorkouts.isEmpty()) {
                        loadWorkoutButton.setVisibility(View.GONE);
                        copyWorkoutButton.setVisibility(View.GONE);
                    }
                    Log.d(this.getClass().toString(), "creatingWorkoutViews");
                    List<ConstraintLayout> workoutViews = createWorkoutViews(availableWorkouts, toEnable);
                    LinearLayout workoutLayoutView = findViewById(R.id.workoutListLinearLayout);

                    for(ConstraintLayout v : workoutViews){
                        Log.d(this.getClass().toString(), "Adding workoutScrollview to workoutLayout view");
                        workoutLayoutView.addView(v);
                    }
                    workoutLayoutView.setOnFocusChangeListener((v, hasFocus) -> {
                        if(hasFocus){
                            Log.d(this.getClass().toString(), "workoutLayoutView has focus");
                        }
                        else {
                            Log.d(this.getClass().toString(), "workoutLayoutView does not have focus");
                        }
                    });
                })
                .doOnError(error -> {
                    loadWorkoutButton.setEnabled(false);
                    copyWorkoutButton.setEnabled(false);
                })
                .subscribe();
    }

    @SuppressLint("ClickableViewAccessibility")
    public List<ConstraintLayout> createWorkoutViews(Map<String, Workout> workoutsMap, List<Button> toReady){
        ArrayList<ConstraintLayout> views = new ArrayList<>();
        for(Map.Entry<String, Workout> entry: workoutsMap.entrySet()) {
            Workout workout = entry.getValue();

            ConstraintLayout workoutListRow = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.workout_list_row, null);
            workoutListRow.setFocusable(true);
            workoutListRow.setFocusableInTouchMode(true);
            workoutListRow.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.workout_list_border, null));

            TextView workoutNameTextView = (TextView) workoutListRow.getChildAt(0);
            workoutNameTextView.setText(workout.workoutName);
            workoutNameTextView.setTag(R.id.workout_name_key);
            workoutNameTextView.setTag(R.id.workout_uuid, workout.workoutUUID);
            workoutNameTextView.setFocusable(false);
            workoutNameTextView.setTextSize(getResources().getDimension(R.dimen.workout_list_row_text_size));

            TextView workoutStartDateTextView = (TextView) workoutListRow.getChildAt(1);
            workoutStartDateTextView.setText(DateFormat.getDateFormat(this).format(workout.startDate));
            workoutStartDateTextView.setFocusable(false);
            workoutStartDateTextView.setTextSize(getResources().getDimension(R.dimen.workout_list_row_text_size));

            if(workout.endDate != null) {
                Log.i("WorkoutDate", "+++++++++++++++++" + workout.endDate.toString());
                TextView workoutEndDateTextView = (TextView) workoutListRow.getChildAt(2);
                workoutEndDateTextView.setText(DateFormat.getDateFormat(this).format(workout.endDate));
                workoutEndDateTextView.setFocusable(false);
                workoutEndDateTextView.setTextSize(getResources().getDimension(R.dimen.workout_list_row_text_size));
            }

            workoutListRow.setOnTouchListener((v, ev) -> {
                Log.i(this.getClass().toString(), "workoutContainer touched......");
                workoutListRow.requestFocus();
                return true;
            });

            workoutListRow.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus){
                    v.setBackground((ResourcesCompat.getDrawable(getResources(), R.drawable.workout_list_selected_icon, null)));
                    for(Button b: toReady){
                        b.setEnabled(true);
                    }
                    Log.d(this.getClass().toString(), "workoutListRow has focus");
                }
                else {
                    v.setBackground((ResourcesCompat.getDrawable(getResources(), R.drawable.workout_list_border, null)));
                    Log.d(this.getClass().toString(), "workoutListRow doesn't have focus");
                }
            });
            workoutListRow.setOnClickListener(v -> {
                Log.d(this.getClass().toString(), "workoutListRow clicked.....");
            });

            views.add(workoutListRow);
        }
        return views;
    }

    public void pressNewWorkoutButton(View view){
        DialogFragment newFragment = new AddWorkoutDialogFragment();
        newFragment.show(getSupportFragmentManager(), "addWorkout");
    }

    public void pressLoadWorkoutButton(View view){
        Log.i("pressLoadWorkoutButton", "++++++++++ pressing load workout button");
        View v = getCurrentFocus();
        if(v != null) {
            Log.i("pressLoadButton", v.toString());
            String selectedWorkout = (String) (v.findViewWithTag(R.id.workout_name_key)).getTag(R.id.workout_uuid);
            Intent intent = new Intent(this, WorkoutLogActivity.class);
            Workout workout = availableWorkouts.get(selectedWorkout);
            assert workout != null;
            intent.putExtra(WorkoutLogActivity.WORKOUT_UUID_KEY, workout.workoutUUID);
            startActivity(intent);
        }
        else
            Log.i("pressLoadButton", "v is null");
    }

    public void pressCopyWorkoutButton(View view){
        Log.i("pressCopyWorkoutButton", "++++++++++ pressing copy workout button");
        View v = getCurrentFocus();
        if(v != null) {
            Log.i("pressCopyButton", v.toString());
            String selectedWorkout = (String) (v.findViewWithTag(R.id.workout_name_key)).getTag(R.id.workout_uuid);
            Log.i("pressCopyWorkoutButton", "selectedWorkout: " + selectedWorkout);
            Workout workoutToCopy = availableWorkouts.get(selectedWorkout);
            if(workoutToCopy != null) {
                String workoutName = workoutToCopy.workoutName;
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
                            for (ExerciseAndSets eas : x.exercisesAndSets) {
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
            else{
                Log.e("pressLoadButton", "workoutToCopy is null.");
            }
        }
        else
            Log.i("pressLoadButton", "v is null");
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

    @Override // needs to handle the LoadWorkoutDialogFragment
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(dialog instanceof AddWorkoutDialogFragment)
            onAddWorkoutDialogPositiveClick(dialog);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {}

    private Map<String, Workout> createUUIDToWorkoutMapping(List<Workout> workouts){
        HashMap<String, Workout> workoutMapping = new HashMap<>();
        for(Workout workout: workouts){
            workoutMapping.put(workout.workoutUUID, workout);
        }
        return workoutMapping;
    }
}