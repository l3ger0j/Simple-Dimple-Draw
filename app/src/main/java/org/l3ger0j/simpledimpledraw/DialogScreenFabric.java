package org.l3ger0j.simpledimpledraw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;

public class DialogScreenFabric {

    public static final int MainMenu = 1;
    public static final int ShapeSelect = 2;
    public static final int RoundSize = 3;
    public static final String[] menu = {"Hide down bar", "Set single shapes", "Set multi shapes", "About"};
    public static final String[] shapes = {"circle", "rectangle", "not_oval"};

    public static AlertDialog getAlertDialog(Activity activity, int ID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        MainActivity mainActivity = (MainActivity) activity;

        switch (ID) {
            case MainMenu:
                builder.setTitle(R.string.menu);
                builder.setItems(menu, (dialog , which) -> {
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
                        builder.setMessage("Creator - WhoYouAndM3\n\nSupport&Help - Google and forum\n\nMy first pet-project:3");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.show();
                    }
                });
                builder.setNegativeButton(R.string.no,null);
                return builder.create();
            case ShapeSelect:
                builder.setTitle(R.string.shapesSelect);
                builder.setSingleChoiceItems(shapes, 0, mainActivity.onClickListener);
                mainActivity.id = 1;
                builder.setPositiveButton(android.R.string.ok, mainActivity.onClickListener);
                return builder.create();
            case RoundSize:
                builder.setTitle(R.string.roundSize);
                View view = activity.getLayoutInflater().inflate(R.layout.shapes_size_dialog, null);
                builder.setView(view);
                mainActivity.id = 2;
                builder.setPositiveButton(android.R.string.ok , mainActivity.onClickListener);
                return builder.create();
            default:
                return null;
        }

    }
}
