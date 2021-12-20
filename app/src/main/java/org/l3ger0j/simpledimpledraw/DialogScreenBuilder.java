package org.l3ger0j.simpledimpledraw;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

enum DialogType {
    RoundSizeDialog,
    CaptureDialog,
    AboutDialog,
}

public class DialogScreenBuilder {
    public static int gID;

    public static AlertDialog createAlertDialog (Activity activity , @NonNull DialogType dialogType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        MainActivity mainActivity = (MainActivity) activity;

        switch (dialogType) {
            case CaptureDialog:
                builder.setTitle(R.string.captureDialog);
                gID = 1;
                ImageView imageView = new ImageView(mainActivity);
                imageView.setImageBitmap(SimpleDimpleDrawingView.getCanvasBitmap());
                ViewGroup.LayoutParams bmImageLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams(bmImageLayoutParams);
                LinearLayout dialogLayout = new LinearLayout(mainActivity);
                dialogLayout.setOrientation(LinearLayout.VERTICAL);
                dialogLayout.addView(imageView);
                builder.setView(dialogLayout);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setNegativeButton(R.string.share, mainActivity.dialogMainOnClickListener);
                return builder.create();
            case AboutDialog:
                builder.setTitle(R.string.aboutMenu);
                builder.setMessage("Creator - WhoYouAndM3\n\nSupport&Help - Google and forum\n\nMy first pet-project");
                builder.setPositiveButton(android.R.string.ok , null);
                return builder.create();
            default:
                return builder.create();
        }
    }

}
