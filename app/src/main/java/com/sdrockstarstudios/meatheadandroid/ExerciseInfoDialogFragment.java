package com.sdrockstarstudios.meatheadandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sdrockstarstudios.meatheadandroid.model.relations.ExerciseAndSets;
import com.sdrockstarstudios.meatheadandroid.model.relations.WorkoutAndExercises;
import com.sdrockstarstudios.meatheadandroid.model.tables.Sets;
import com.sdrockstarstudios.meatheadandroid.model.tables.Workout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.reactivex.Maybe;

public class ExerciseInfoDialogFragment extends DialogFragment {

    private static final int MAX_HEIGHT = 400;

    private List<WorkoutAndExercises> workoutsAndExercises;
    private String exerciseName;

    public ExerciseInfoDialogFragment(String exerciseName, List<WorkoutAndExercises> workoutsAndExercises){
        this.exerciseName = exerciseName;
        this.workoutsAndExercises = workoutsAndExercises;
    }

    public interface NoticeDialogListener {
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    AddExerciseDialogFragment.NoticeDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddExerciseDialogFragment.NoticeDialogListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "must implement NoticeListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), R.style.popup_dialogs);
        AlertDialog.Builder builder = new AlertDialog.Builder(newContext);
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_exercies_info, null))
                .setNegativeButton(R.string.cancel, (dialog, which) ->
                        listener.onDialogNegativeClick(ExerciseInfoDialogFragment.this));

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        
        dialog.setOnShowListener(dialogInterface -> {
            buildExerciseDisplayScrollView(dialog, exerciseName, workoutsAndExercises);
        });

        return dialog;
    }

    @Override
    public void onResume(){
        super.onResume();

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.height = MAX_HEIGHT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    private void buildExerciseDisplayScrollView(AlertDialog dialog, String exerciseName, List<WorkoutAndExercises> workouts){
        LinearLayout infoContainer = new LinearLayout(getContext());
        infoContainer.setOrientation(LinearLayout.VERTICAL);

        Collections.sort(workouts, (w1, w2) -> w2.workout.startDate.compareTo(w1.workout.startDate));
        ArrayList<String> exercisesToDisplay = new ArrayList<>();
        for(WorkoutAndExercises workout: workouts){
            String date = DateFormat.getDateFormat(getContext()).format(workout.workout.startDate);
            for(ExerciseAndSets e: workout.exercisesAndSets){
                if(e.exercise.exerciseName.equals(exerciseName)){
                    StringBuilder setsString = new StringBuilder();
                    setsString.append(date);
                    for(Sets set: e.setList){
                        if(e.exercise.repsOnly){
                            setsString.append(" X").append(set.reps);
                        }
                        else {
                            setsString.append(" ").append(set.weight).append("X").append(set.reps);
                        }
                    }
                    TextView infoTextView = new TextView(getContext());
                    infoTextView.setText(setsString.toString());
                    infoTextView.setTextSize(24);
                    infoContainer.addView(infoTextView);
                }
            }
        }
        ScrollView infoScrollView = dialog.findViewById(R.id.ExerciseInfoScrollView);
        infoScrollView.addView(infoContainer);
    }
}
