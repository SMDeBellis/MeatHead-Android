package com.sdrockstarstudios.meatheadandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class AddExerciseDialogFragment extends DialogFragment{

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NoticeDialogListener) context;
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

        builder.setView(inflater.inflate(R.layout.dialog_add_exercise, null))
                .setPositiveButton(R.string.add_dialog_accept, (dialog, id) ->
                        listener.onDialogPositiveClick(AddExerciseDialogFragment.this))
                .setNegativeButton(R.string.cancel, (dialog, which) ->
                        listener.onDialogNegativeClick(AddExerciseDialogFragment.this));

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            EditText exerciseNameEditText = dialog.findViewById(R.id.exercise_name_entry);
            exerciseNameEditText.addTextChangedListener(new TextWatcher() {
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
