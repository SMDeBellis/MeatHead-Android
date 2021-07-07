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

public class DeleteSetDialogFragment extends DialogFragment {
    int containerToRemoveFromId;
    String tagToRemove;

    public DeleteSetDialogFragment(int containerId, String tagToRemove){
        this.containerToRemoveFromId = containerId;
        this.tagToRemove = tagToRemove;
    }

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    DeleteSetDialogFragment.NoticeDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DeleteSetDialogFragment.NoticeDialogListener) context;
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

        builder.setView(inflater.inflate(R.layout.dialog_delete_set, null))
                .setPositiveButton(R.string.delete, (dialog, id) ->
                        listener.onDialogPositiveClick(DeleteSetDialogFragment.this))
                .setNegativeButton(R.string.cancel, (dialog, which) ->
                        listener.onDialogNegativeClick(DeleteSetDialogFragment.this));

        return builder.create();
    }
}
