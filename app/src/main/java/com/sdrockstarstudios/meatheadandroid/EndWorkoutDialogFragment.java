package com.sdrockstarstudios.meatheadandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EndWorkoutDialogFragment  extends DialogFragment {

    private String workoutId;

    EndWorkoutDialogFragment(String uuid){
        workoutId = uuid;
    }

    public String getWorkoutId(){
        return workoutId;
    }

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    EndWorkoutDialogFragment.NoticeDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EndWorkoutDialogFragment.NoticeDialogListener) context;
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

        builder.setView(inflater.inflate(R.layout.dialog_end_workout, null))
                .setPositiveButton(R.string.end_workout_confirm, (dialog, id) ->
                        listener.onDialogPositiveClick(EndWorkoutDialogFragment.this))
                .setNegativeButton(R.string.cancel, (dialog, which) ->
                        listener.onDialogNegativeClick(EndWorkoutDialogFragment.this));

        return builder.create();
    }

}
