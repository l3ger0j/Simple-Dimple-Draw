package org.l3ger0j.simpledimpledraw.view.dialogs;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public abstract class MainPatternDialogFrags extends DialogFragment {
    public interface MainPatternDialogList {
        void onDialogNegativeClick(MainDialogFrags mainDialogFrags);
    }

    public MainPatternDialogFrags.MainPatternDialogList listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (MainPatternDialogFrags.MainPatternDialogList) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context
                    + " must implement PatternDialogListener");
        }
    }
}