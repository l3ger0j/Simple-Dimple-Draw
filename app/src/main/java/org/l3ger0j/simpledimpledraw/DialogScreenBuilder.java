package org.l3ger0j.simpledimpledraw;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
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
            case RoundSizeDialog:
                builder.setTitle(R.string.roundSize);
                View view = mainActivity.getLayoutInflater().inflate(R.layout.shapes_size_dialog , null);
                builder.setView(view);
                gID = 1;
                builder.setPositiveButton(android.R.string.ok , mainActivity.dialogMainOnClickListener);
                return builder.create();
            case CaptureDialog:
                builder.setTitle(R.string.captureDialog);
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
