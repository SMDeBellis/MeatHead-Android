package com.sdrockstarstudios.meatheadandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeleteExerciseDialogFragment extends DialogFragment {

    private final int idToDelete;

    public int getIdToDelete() {
        return idToDelete;
    }

    public DeleteExerciseDialogFragment(int idToDelete){
        this.idToDelete = idToDelete;
    }

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    DeleteExerciseDialogFragment.NoticeDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DeleteExerciseDialogFragment.NoticeDialogListener) context;
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

        builder.setView(inflater.inflate(R.layout.dialog_delete_exercise, null))
                .setPositiveButton(R.string.delete, (dialog, id) ->
                        listener.onDialogPositiveClick(DeleteExerciseDialogFragment.this))
                .setNegativeButton(R.string.cancel, (dialog, which) ->
                        listener.onDialogNegativeClick(DeleteExerciseDialogFragment.this));

        return builder.create();
    }
}
