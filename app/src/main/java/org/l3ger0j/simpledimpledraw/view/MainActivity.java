package org.l3ger0j.simpledimpledraw.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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
import androidx.databinding.ObservableBoolean;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;

import org.l3ger0j.simpledimpledraw.DialogType;
import org.l3ger0j.simpledimpledraw.DrawPaint;
import org.l3ger0j.simpledimpledraw.R;
import org.l3ger0j.simpledimpledraw.ShapeBuilder;
import org.l3ger0j.simpledimpledraw.WindowType;
import org.l3ger0j.simpledimpledraw.model.DialogScreenBuilder;
import org.l3ger0j.simpledimpledraw.model.PopupWindowBuilder;
import org.l3ger0j.simpledimpledraw.model.ShakeDetector;
import org.l3ger0j.simpledimpledraw.presenter.MainContract;
import org.l3ger0j.simpledimpledraw.presenter.MainPresenter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MainContract.MainActivity, ColorPickerDialogListener,
        SeekBar.OnSeekBarChangeListener {

    // region Layouts
    private PaintActivity paintActivity;
    private ShapeBuilder shapeManager;
    // endregion

    // region Buttons
    private FloatingActionButton fabSave;
    private ToggleButton toggleButton;
    // endregion

    private final MainPresenter mainPresenter = new MainPresenter(this);

    public ObservableBoolean isClickOnCloseLMenu = new ObservableBoolean();
    public ObservableBoolean isClickOnCloseRMenu = new ObservableBoolean();
    public ObservableBoolean isClickOnCloseCenterMenu = new ObservableBoolean();

    private DrawPaint drawPaint;
    private AlertDialog alertDialog;
    private TextView textView;
    private final PopupWindowBuilder popupWindowBuilder = new PopupWindowBuilder();
    private Uri uriBitmap = null;
    private String path;
    private int turnOnMove;
    private final int[] posPopupWindow = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding activity_main => ActivityMainBinding
        org.l3ger0j.simpledimpledraw.databinding.ActivityMainBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMainActivity(this);
        shapeManager = binding.shapeManager;
        paintActivity = binding.simpleDrawingView;

        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        ShakeDetector mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(count -> {
            if (count == 3) {
                isClickOnCloseLMenu.set(false);
                isClickOnCloseRMenu.set(false);
                isClickOnCloseCenterMenu.set(false);
            }
        });

        drawPaint = new DrawPaint();
        paintActivity.setDrawPaint(drawPaint);

        fabSave = binding.fabSave;
        toggleButton = binding.toggleButton;

        binding.fabSetSize.setOnClickListener(mainViewOnClickList);
        binding.fabSetDrawColor.setOnClickListener(mainViewOnClickList);
        binding.fabSetBackgroundColor.setOnClickListener(mainViewOnClickList);
        binding.fabEraser.setOnClickListener(mainViewOnClickList);
        binding.fabClearCanvas.setOnClickListener(mainViewOnClickList);
        binding.fabSave.setOnClickListener(mainViewOnClickList);
        binding.fabShowMenu.setOnClickListener(mainViewOnClickList);
        binding.toggleButton.setOnClickListener(mainViewOnClickList);
        binding.undoButton.setOnClickListener(mainViewOnClickList);
        binding.redoButton.setOnClickListener(mainViewOnClickList);

        setContentView(binding.getRoot());
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
            }
        }
    };

    public void changeVisible(View v) {
        int id = v.getId();
        if (id == R.id.btnCloseLMenu) {
            isClickOnCloseLMenu.set(true);
        } else if (id == R.id.btnCloseRMenu) {
            isClickOnCloseRMenu.set(true);
        } else if (id == R.id.btnCloseCenterMenu) {
            isClickOnCloseCenterMenu.set(true);
        }
    }

    public void showAboutDialog() {
        mainPresenter.createAboutDialog().show();
    }

    public DialogInterface.OnClickListener mainDialogOnClickList (int dialogID) {
        return (dialog , which) -> {
            if (dialogID == 1) {
                // ListView listView = ((AlertDialog) dialog).getListView();
                if (which == Dialog.BUTTON_NEGATIVE) {
                    sendPic();
                }
            }
        };
    }

    // region picSave
    private void openCaptureMenu() {
        View popupView = popupWindowBuilder.createPopupWindowView(this, WindowType.CaptureMenu);
        PopupWindow popupWindow = popupWindowBuilder.createPopupWindow(WindowType.CaptureMenu);
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
            DialogScreenBuilder dialogScreenBuilder = new DialogScreenBuilder();
            dialogScreenBuilder.setImageBitmap(paintActivity.getCanvasBitmap());
            dialogScreenBuilder.setOnClickListener(mainDialogOnClickList(1));
            alertDialog = DialogScreenBuilder.createAlertDialog(this , DialogType.CaptureDialog);
            alertDialog.show();
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
        View popupView = popupWindowBuilder.createPopupWindowView(this, WindowType.MinimalSetting);
        PopupWindow popupWindow = popupWindowBuilder.createPopupWindow(WindowType.MinimalSetting);
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
}