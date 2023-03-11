package org.l3ger0j.simpledimpledraw.view.dialogs;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableField;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.l3ger0j.simpledimpledraw.R;
import org.l3ger0j.simpledimpledraw.databinding.DialogImageBinding;

public class MainDialogFrags extends MainPatternDialogFrags {
    private MainDialogType dialogType;
    private DialogImageBinding imageBinding;

    public void setDialogType(MainDialogType dialogType) {
        this.dialogType = dialogType;
    }

    public ObservableField<Bitmap> imageBitmap = new ObservableField<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        var builder = new MaterialAlertDialogBuilder(requireContext());

        switch (dialogType) {
            case ABOUT_DIALOG:
                builder.setTitle(R.string.aboutMenu);
                builder.setMessage("Creator - WhoYouAndM3\n\nSupport&Help - Google and forum\n\nMy first pet-project");
                builder.setPositiveButton(android.R.string.ok , null);
                return builder.create();
            case CAPTURE_DIALOG:
                builder.setTitle(R.string.captureDialog);
                imageBinding = DialogImageBinding.inflate(getLayoutInflater());
                imageBinding.setDialogFragment(this);
                builder.setView(imageBinding.getRoot());
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setNegativeButton(R.string.share , (dialogInterface , i) ->
                        listener.onDialogNegativeClick(this));
                return builder.create();
        }
        return super.onCreateDialog(savedInstanceState);
    }
}
