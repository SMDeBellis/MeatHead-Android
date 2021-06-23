package com.sdrockstarstudios.meatheadandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.List;


public class LoadWorkoutDialogFragment extends DialogFragment {

    private List<String> availableWorkouts;
    private  String selectedWorkout;

    public String getSelectedWorkout(){ return selectedWorkout; }

    public LoadWorkoutDialogFragment(List<String> availableWorkouts) {
        this. availableWorkouts = availableWorkouts;
    }

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    LoadWorkoutDialogFragment.NoticeDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (LoadWorkoutDialogFragment.NoticeDialogListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "must implement NoticeListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_load_workout, null))
                .setPositiveButton(R.string.add_dialog_accept, (dialog, id) ->
                        listener.onDialogPositiveClick(LoadWorkoutDialogFragment.this))
                .setNegativeButton(R.string.cancel, (dialog, which) ->
                        listener.onDialogNegativeClick(LoadWorkoutDialogFragment.this));

        AlertDialog dialog = builder.create();

        // this will need to change to ensure spinner selection has been made.
        dialog.setOnShowListener(dialogInterface -> {
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
