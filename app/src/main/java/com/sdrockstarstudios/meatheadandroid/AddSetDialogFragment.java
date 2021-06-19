package com.sdrockstarstudios.meatheadandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class AddSetDialogFragment extends DialogFragment {
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    AddExerciseDialogFragment.NoticeDialogListener listener;
    boolean repsOnly;
    String tagToAddTo;


    public AddSetDialogFragment(boolean repsOnly, String tagToAddTo){
        this.repsOnly = repsOnly;
        this.tagToAddTo = tagToAddTo;
    }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_add_set, null))
                .setPositiveButton(R.string.add_dialog_accept, (dialog, id) ->
                        listener.onDialogPositiveClick(AddSetDialogFragment.this))
                .setNegativeButton(R.string.cancel, (dialog, which) ->
                        listener.onDialogNegativeClick(AddSetDialogFragment.this));

        AlertDialog dialog = builder.create();

        if(repsOnly){
            EditText weightTextEdit = dialog.findViewById(R.id.weightEditText);
            LinearLayout addSetHorLinearLayout = dialog.findViewById(R.id.addSetHorLinearLayout);
            addSetHorLinearLayout.removeView(weightTextEdit);
        }

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            EditText repsEditText = dialog.findViewById(R.id.repsEditText);
            repsEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(repsOnly){
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() != 0);
                    }
                    else{
                        EditText weightEditText = dialog.findViewById(R.id.weightEditText);
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() != 0 && weightEditText.getText().length() != 0);
                    }
                }
            });

            if(!repsOnly){
                EditText weightEditText = dialog.findViewById(R.id.weightEditText);
                weightEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() != 0 && repsEditText.getText().length() != 0);
                    }
                });
            }
        });

        return dialog;
    }
}
