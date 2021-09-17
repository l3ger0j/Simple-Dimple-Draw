package org.l3ger0j.simpledimpledraw;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

enum DialogType {
    MainMenu,
    ShapeSelect,
    RoundSize,
    CaptureDialog,
}

public class DialogScreenBuilder {
    private static final String[] menu = {"Hide down bar" , "Set single shapes" , "Set multi shapes" , "About"};
    private static final String[] shapes = {"circle" , "rectangle" , "not_oval"};

    @NonNull
    public static AlertDialog getAlertDialog(Activity activity , DialogType dialogType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        MainActivity mainActivity = (MainActivity) activity;

        switch (dialogType) {
            case MainMenu:
                builder.setTitle(R.string.menu);
                builder.setItems(menu , (dialog , which) -> {
                    if (which == 0) {
                        mainActivity.floatingActionButton.hide();
                        mainActivity.bottomNavigationView.setVisibility(View.INVISIBLE);
                        mainActivity.chip.setVisibility(View.VISIBLE);
                    } else if (which == 1) {
                        mainActivity.simpleDimpleDrawingView.active = which;
                    } else if (which == 2) {
                        mainActivity.simpleDimpleDrawingView.active = 0;
                    } else if (which == 3) {
                        builder.setTitle(R.string.aboutMenu);
                        builder.setMessage("Creator - WhoYouAndM3\n\nSupport&Help - Google and forum\n\nMy first pet-project");
                        builder.setPositiveButton(android.R.string.ok , null);
                        builder.show();
                    }
                });
                builder.setNegativeButton(android.R.string.no , null);
                return builder.create();
            case ShapeSelect:
                builder.setTitle(R.string.shapesSelect);
                builder.setSingleChoiceItems(shapes , 0 , mainActivity.onClickListener);
                mainActivity.id = 1;
                builder.setPositiveButton(android.R.string.ok , mainActivity.onClickListener);
                return builder.create();
            case RoundSize:
                builder.setTitle(R.string.roundSize);
                View view = activity.getLayoutInflater().inflate(R.layout.shapes_size_dialog , null);
                builder.setView(view);
                mainActivity.id = 2;
                builder.setPositiveButton(android.R.string.ok , mainActivity.onClickListener);
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
            default:
                return builder.create();
        }
    }
}
