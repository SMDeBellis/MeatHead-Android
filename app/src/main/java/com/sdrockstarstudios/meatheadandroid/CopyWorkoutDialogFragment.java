package com.sdrockstarstudios.meatheadandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.List;

public class CopyWorkoutDialogFragment extends DialogFragment {

    private List<String> availableWorkouts;
    private  String selectedWorkout;

    public String getSelectedWorkout(){ return selectedWorkout; }


    public CopyWorkoutDialogFragment(List<String> availableWorkouts){
        this. availableWorkouts = availableWorkouts;
    }

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    CopyWorkoutDialogFragment.NoticeDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (CopyWorkoutDialogFragment.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement NoticeListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), R.style.popup_dialogs);
        AlertDialog.Builder builder = new AlertDialog.Builder(newContext);
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_copy_workout, null))
                .setPositiveButton(R.string.copy_workout_confirm, (dialog, id) ->
                        listener.onDialogPositiveClick(CopyWorkoutDialogFragment.this))
                .setNegativeButton(R.string.cancel, (dialog, which) ->
                        listener.onDialogNegativeClick(CopyWorkoutDialogFragment.this));

        AlertDialog dialog = builder.create();

        // this will need to change to ensure spinner selection has been made.
        dialog.setOnShowListener(dialogInterface -> {
            Log.i(this.getClass().toString(), "=========== in setOnShowListener for copy.");
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            Spinner availableWorkoutSpinner = (Spinner) dialog.findViewById(R.id.available_workouts);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, availableWorkouts);
            availableWorkoutSpinner.setAdapter(adapter);
            availableWorkoutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    selectedWorkout = (String) parent.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            });
        });

        return dialog;
    }
}
