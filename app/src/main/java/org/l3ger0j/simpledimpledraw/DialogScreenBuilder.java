package org.l3ger0j.simpledimpledraw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

public class DialogScreenBuilder {
    private static DialogInterface.OnClickListener onClickListener;
    private static Bitmap imageBitmap = null;

    public void setOnClickListener (DialogInterface.OnClickListener onClickListener) {
        DialogScreenBuilder.onClickListener = onClickListener;
    }

    public void setImageBitmap (Bitmap bitmap) {
        imageBitmap = bitmap;
    }

    public static AlertDialog createAlertDialog (Activity activity , DialogType dialogType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        ImageView imageView = new ImageView(activity);
        LinearLayout dialogLayout = new LinearLayout(activity);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        switch (dialogType) {
            case AboutDialog:
                builder.setTitle(R.string.aboutMenu);
                builder.setMessage("Creator - WhoYouAndM3\n\nSupport&Help - Google and forum\n\nMy first pet-project");
                builder.setPositiveButton(android.R.string.ok , null);
                return builder.create();
            case CaptureDialog:
                builder.setTitle(R.string.captureDialog);
                imageView.setImageBitmap(imageBitmap);
                imageView.setLayoutParams(layoutParams);
                dialogLayout.setOrientation(LinearLayout.VERTICAL);
                dialogLayout.addView(imageView);
                builder.setView(dialogLayout);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setNegativeButton(R.string.share, onClickListener);
                return builder.create();
            default:
                return builder.create();
        }
    }
}
