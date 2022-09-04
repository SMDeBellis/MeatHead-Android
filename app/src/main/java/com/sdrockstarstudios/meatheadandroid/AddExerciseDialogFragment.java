package com.sdrockstarstudios.meatheadandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sdrockstarstudios.meatheadandroid.model.AppDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class AddExerciseDialogFragment extends DialogFragment {

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

        Disposable d = AppDatabase.getInstance(getContext()).exerciseDoa().getExerciseNames()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> Toast.makeText(getContext(), "Error getting exercises from database. Manual entry only.", Toast.LENGTH_SHORT))
                .doOnSuccess(x -> {
                    AutoCompleteTextView exerciseOptions = dialog.findViewById(R.id.exercise_name_entry);
                    List<String> l = new ArrayList<>(new HashSet<>(x));
                    Collections.sort(l, String::compareTo);
                    ArrayAdapter<String> exercises = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, l);
                    exerciseOptions.setAdapter(exercises);
                    exerciseOptions.setOnClickListener(v -> exerciseOptions.showDropDown());
                    exerciseOptions.performClick();
                })
                .subscribe();


        dialog.setOnShowListener(dialogInterface -> {
            AutoCompleteTextView exerciseOptions = dialog.findViewById(R.id.exercise_name_entry);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            exerciseOptions.addTextChangedListener(new TextWatcher() {
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
