package org.l3ger0j.simpledimpledraw.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;

import org.l3ger0j.simpledimpledraw.view.dialogs.MainDialogFrags;
import org.l3ger0j.simpledimpledraw.view.dialogs.MainDialogType;
import org.l3ger0j.simpledimpledraw.utils.DrawPaint;
import org.l3ger0j.simpledimpledraw.R;
import org.l3ger0j.simpledimpledraw.utils.ShapeBuilder;
import org.l3ger0j.simpledimpledraw.view.window.WindowType;
import org.l3ger0j.simpledimpledraw.databinding.ActivityMainBinding;
import org.l3ger0j.simpledimpledraw.view.window.PopupWindowBuilder;
import org.l3ger0j.simpledimpledraw.view.dialogs.MainPatternDialogFrags;
import org.l3ger0j.simpledimpledraw.viewModel.MainViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MainPatternDialogFrags.MainPatternDialogList,
        ColorPickerDialogListener, SeekBar.OnSeekBarChangeListener {

    // region Layouts
    private PaintActivity paintActivity;
    private ShapeBuilder shapeManager;
    // endregion

    // region Buttons
    private FloatingActionButton fabSave;
    private ToggleButton toggleButton;
    // endregion

    private DrawPaint drawPaint;
    private TextView textView;
    private final PopupWindowBuilder popupWindowBuilder = new PopupWindowBuilder();
    private Uri uriBitmap = null;
    private String path;
    private int turnOnMove;
    private final int[] posPopupWindow = new int[2];

    private MainViewModel mainViewModel;
    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.mainActivityField.set(this);
        mainBinding.setMainViewModel(mainViewModel);

        shapeManager = mainBinding.shapeManager;
        paintActivity = mainBinding.simpleDrawingView;

        drawPaint = new DrawPaint();
        paintActivity.setDrawPaint(drawPaint);

        fabSave = mainBinding.fabSave;
        toggleButton = mainBinding.toggleButton;

        mainBinding.fabSetSize.setOnClickListener(mainViewOnClickList);
        mainBinding.fabSetDrawColor.setOnClickListener(mainViewOnClickList);
        mainBinding.fabSetBackgroundColor.setOnClickListener(mainViewOnClickList);
        mainBinding.fabEraser.setOnClickListener(mainViewOnClickList);
        mainBinding.fabClearCanvas.setOnClickListener(mainViewOnClickList);
        mainBinding.fabSave.setOnClickListener(mainViewOnClickList);
        mainBinding.fabShowMenu.setOnClickListener(mainViewOnClickList);
        mainBinding.toggleButton.setOnClickListener(mainViewOnClickList);
        mainBinding.undoButton.setOnClickListener(mainViewOnClickList);
        mainBinding.redoButton.setOnClickListener(mainViewOnClickList);

        setContentView(mainBinding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (WindowType windowType : WindowType.values()) {
            popupWindowBuilder.createPopupWindow(windowType).dismiss();
        }
    }

    View.OnClickListener mainViewOnClickList = new View.OnClickListener() {
        @Override
        public void onClick(@NonNull View v) {

            if (v.getId() == R.id.undoButton) {
                paintActivity.undoPath();
                paintActivity.invalidateView();
            } else if (v.getId() == R.id.redoButton) {
                paintActivity.redoPath();
                paintActivity.invalidateView();
            } else if (v.getId() == R.id.fabSetSize) {
                showMinSettingDrawWindow();
            } else if (v.getId() == R.id.fabSetDrawColor) {
                createColorPickerDialog(2);
            } else if (v.getId() == R.id.fabSetBackgroundColor) {
                createColorPickerDialog(1);
            } else if (v.getId() == R.id.fabEraser) {
                if (paintActivity.isEraserOn()) {
                    paintActivity.eraseCanvas(true);
                    v.setRotation(180);
                } else {
                    paintActivity.eraseCanvas(false);
                    v.setRotation(0);
                }
            } else if (v.getId() == R.id.fabSave) {
                openCaptureMenu();
            } else if (v.getId() == R.id.fabClearCanvas) {
                paintActivity.clearCanvas();
            } else if (v.getId() == R.id.toggleButton) {
                if (shapeManager.selectShape == 1) {
                    paintActivity.drawShape(0, shapeManager);
                    shapeManager.setVisibility(View.GONE);
                    toggleButton.setVisibility(View.GONE);
                } else if (shapeManager.selectShape == 2) {
                    paintActivity.drawShape(1, shapeManager);
                    shapeManager.setVisibility(View.GONE);
                    toggleButton.setVisibility(View.GONE);
                } else if (shapeManager.selectShape == 3) {
                    paintActivity.drawShape(2, shapeManager);
                    shapeManager.setVisibility(View.GONE);
                    toggleButton.setVisibility(View.GONE);
                }
                mainViewModel.showAllMenu();
            }
        }
    };

    public void addShape(View v) {
        int id = v.getId();
        if (id == R.id.cAddCircle) {
            shapeManager.selectShape = 1;
            toggleButton.setChecked(true);
            mainViewModel.hideAllMenu();
            shapeManager.setVisibility(View.VISIBLE);
            toggleButton.setVisibility(View.VISIBLE);
        } else if (id == R.id.cAddRect) {
            shapeManager.selectShape = 2;
            toggleButton.setChecked(true);
            shapeManager.setVisibility(View.VISIBLE);
            mainViewModel.hideAllMenu();
            toggleButton.setVisibility(View.VISIBLE);
        } else if (id == R.id.cAddOval) {
            shapeManager.selectShape = 3;
            toggleButton.setChecked(true);
            mainViewModel.hideAllMenu();
            shapeManager.setVisibility(View.VISIBLE);
            toggleButton.setVisibility(View.VISIBLE);
        }
    }

    public void showAboutDialog() {
        var dialogFrags = new MainDialogFrags();
        dialogFrags.setDialogType(MainDialogType.ABOUT_DIALOG);
        dialogFrags.show(getSupportFragmentManager() , "aboutDialogFragment");
    }

    // region picSave
    private void openCaptureMenu() {
        View popupView = popupWindowBuilder.createPopupWindowView(this, WindowType.CAPTURE_MENU);
        PopupWindow popupWindow = popupWindowBuilder.createPopupWindow(WindowType.CAPTURE_MENU);
        popupWindow.showAtLocation(fabSave, Gravity.START, 870, 140);

        popupView.findViewById(R.id.saveCanvas).setOnClickListener(v -> {
            try {
                File extStorage = getExternalFilesDir(null);
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault());
                String fileName = "PIC_"+sdf.format(date)+".png";
                path = extStorage.getAbsolutePath() + "/" + fileName;
                File myFile = new File(path);
                FileOutputStream fOut = new FileOutputStream(myFile);
                paintActivity.getCanvasBitmap().compress(Bitmap.CompressFormat.PNG,90,fOut);
                fOut.flush();
                fOut.close();
                uriBitmap = FileProvider.getUriForFile(
                        this,
                        "org.l3ger0j.simpledimpledraw.provider",
                        myFile
                );
                MediaStore.Images.Media.insertImage(getContentResolver(), myFile.getAbsolutePath(), myFile.getName(), myFile.getName());
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{myFile.toString()}, null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            popupWindow.dismiss();

            Toast.makeText(this, "Save", Toast.LENGTH_LONG).show();
            var dialogFrags = new MainDialogFrags();
            dialogFrags.setDialogType(MainDialogType.CAPTURE_DIALOG);
            dialogFrags.imageBitmap.set(paintActivity.getCanvasBitmap());
            dialogFrags.show(getSupportFragmentManager() , "captureDialogFragment");
        });

        if (uriBitmap != null & path != null) {
            popupView.findViewById(R.id.shareCanvas).setOnClickListener(v -> sendPic());
        } else {
            popupView.findViewById(R.id.shareCanvas).setEnabled(false);
        }
    }

    private void sendPic() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM , uriBitmap);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }
    // endregion

    // region FAB popupWindow
    public void showMinSettingDrawWindow() {
        View popupView = popupWindowBuilder.createPopupWindowView(this, WindowType.MINIMAL_SETTING);
        PopupWindow popupWindow = popupWindowBuilder.createPopupWindow(WindowType.MINIMAL_SETTING);
        popupWindow.showAtLocation(findViewById(R.id.fabSetSize),
                Gravity.CENTER, posPopupWindow[0], posPopupWindow[1]);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (turnOnMove == 1) {
            popupView.setOnTouchListener(new View.OnTouchListener() {
                private int wx = 0;
                private int wy = 0;

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v , MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            wx = (int) event.getX();
                            wy = (int) event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int xp = (int) event.getRawX();
                            int yp = (int) event.getRawY();
                            int sides = (xp - wx);
                            int topBot = (yp - wy);
                            popupWindow.update(sides , topBot , -1 , -1 , true);
                            posPopupWindow[0] = sides;
                            posPopupWindow[1] = topBot;
                            break;
                    }
                    return true;
                }
            });
        }

        SeekBar seekBar = popupView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        textView = popupView.findViewById(R.id.textView);
        textView.setText(String.valueOf(drawPaint.getStrokeWidth()));
        seekBar.setProgress((int) drawPaint.getStrokeWidth());

        ImageButton turnMoveDialog = popupView.findViewById(R.id.turnMoveDialog);
        turnMoveDialog.setOnClickListener(v -> {
            if (turnOnMove == 0) {
                turnOnMove = 1;
                popupWindow.dismiss();
                showMinSettingDrawWindow();
            } else if (turnOnMove == 1){
                turnOnMove = 0;
                popupWindow.dismiss();
                showMinSettingDrawWindow();
            }
        });
    }

    @Override
    public void onProgressChanged(@NonNull SeekBar seekBar , int progress , boolean fromUser) {
        textView.setText(String.valueOf(seekBar.getProgress()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(@NonNull SeekBar seekBar) {
        paintActivity.setStroke(seekBar.getProgress());
        shapeManager.mStrokeWidth = seekBar.getProgress();
    }
    // endregion

    private void createColorPickerDialog(int id) {
        if (id == 1) {
            ColorPickerDialog.newBuilder()
                    .setDialogId(id)
                    .setDialogTitle(R.string.color_background)
                    .setColor(paintActivity.getBackgroundColor())
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowCustom(true)
                    .setAllowPresets(true)
                    .setColorShape(ColorShape.SQUARE)
                    .show(this);
        } else if (id == 2) {
            ColorPickerDialog.newBuilder()
                    .setDialogId(id)
                    .setDialogTitle(R.string.color_drawing)
                    .setColor(drawPaint.getColor())
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowCustom(true)
                    .setAllowPresets(true)
                    .setColorShape(ColorShape.SQUARE)
                    .show(this);
        }
    }

    @Override
    public void onColorSelected(int dialogId , int color) {
        if (dialogId == 1) {
            paintActivity.setBackColor(color);
            popupWindowBuilder.setBackgroundColor(color);
        } else if (dialogId == 2) {
            paintActivity.setColor(color);
            shapeManager.mStrokeColor = color;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
    }

    @Override
    public void onDialogNegativeClick(MainDialogFrags mainDialogFrags) {
        sendPic();
    }
}