package com.sdrockstarstudios.meatheadandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddWorkoutDialogFragment  extends DialogFragment{

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    AddWorkoutDialogFragment.NoticeDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddWorkoutDialogFragment.NoticeDialogListener) context;
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

        builder.setView(inflater.inflate(R.layout.dialog_add_workout, null))
                .setPositiveButton(R.string.add_dialog_accept, (dialog, id) ->
                        listener.onDialogPositiveClick(AddWorkoutDialogFragment.this))
                .setNegativeButton(R.string.cancel, (dialog, which) ->
                        listener.onDialogNegativeClick(AddWorkoutDialogFragment.this));

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            EditText workoutNameEditText = dialog.findViewById(R.id.workout_name_entry);
            workoutNameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() != 0);
                }
            });
        });

        return dialog;
    }
}
