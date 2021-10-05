package org.l3ger0j.simpledimpledraw;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;

import org.l3ger0j.simpledimpledraw.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ColorPickerDialogListener, SeekBar.OnSeekBarChangeListener {

    // region Layouts
    SimpleDimpleDrawingView simpleDimpleDrawingView;
    // endregion

    // region Buttons
    FloatingActionButton floatingActionButton;
    Chip chip;
    ToggleButton toggleButton;
    BottomNavigationView bottomNavigationView;
    // endregion

    AlertDialog alertDialog;
    TextView textView;
    PopupWindowBuilder popupWindowBuilder = new PopupWindowBuilder();
    Uri uriBitmap = null;
    String path;

    int id;
    int turnOnMove;
    int[] posPopupWindow = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding activity_main => ActivityMainBinding
        org.l3ger0j.simpledimpledraw.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        simpleDimpleDrawingView = binding.simpleDrawingView;
        bottomNavigationView = binding.navigationToolbar;
        binding.navigationToolbar.setOnItemReselectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.appBarSave) {
                openCaptureMenu();
            } else if (itemId == R.id.appBarClear) {
                clearCanvas();
            } else if (itemId == R.id.appBarColor) {
                showColorSettingMenu(bottomNavigationView);
            } else if (itemId == R.id.appBarShapes) {
                alertDialog = DialogScreenBuilder.
                        getAlertDialog(this , DialogType.ShapeSelect);
                alertDialog.show();
            } else if (itemId == R.id.appBarAbout) {
                alertDialog = DialogScreenBuilder.
                        getAlertDialog(this , DialogType.MainMenu);
                alertDialog.show();
            }
        });

        floatingActionButton = binding.fab;
        binding.fab.setOnClickListener(v -> {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            floatingActionButton.setRippleColor(color);
            simpleDimpleDrawingView.id = 0;
            showMinSettingDrawWindow();
        });

        chip = binding.chip1;
        binding.chip1.setOnClickListener(v -> {
            bottomNavigationView.setVisibility(View.VISIBLE);
            floatingActionButton.show();
            chip.setVisibility(View.GONE);
        });

        toggleButton = binding.toggleButton;
        binding.toggleButton.setOnClickListener(v -> {
            RectF rectF = new RectF(
                    simpleDimpleDrawingView.points[0].x,
                    simpleDimpleDrawingView.points[1].y,
                    simpleDimpleDrawingView.points[2].x,
                    simpleDimpleDrawingView.points[3].y);
            simpleDimpleDrawingView.drawCanvas.drawRect(rectF, simpleDimpleDrawingView.drawPaint);
            simpleDimpleDrawingView.id = 0;
            bottomNavigationView.setVisibility(View.VISIBLE);
            floatingActionButton.show();
            toggleButton.setVisibility(View.GONE);
        });

        setContentView(binding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        floatingActionButton.show();
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (WindowType windowType : WindowType.values()) {
            popupWindowBuilder.createPopupWindow(windowType).dismiss();
        }
    }

    // region picSave
    public File getAppExternalFilesDir()  {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            // /storage/emulated/0/Android/data/files
            return this.getExternalFilesDir(null);
        } else {
            // @Deprecated in API 29.
            // /storage/emulated/0
            return Environment.getExternalStorageDirectory();
        }
    }

    private void openCaptureMenu() {
        View popupView = popupWindowBuilder.createPopupWindowView(this, WindowType.CaptureMenu);
        PopupWindow popupWindow = popupWindowBuilder.createPopupWindow(WindowType.CaptureMenu);
        popupWindow.showAsDropDown(bottomNavigationView.findViewById(R.id.appBarSave), 0, -370);

        popupView.findViewById(R.id.saveCanvas).setOnClickListener(v -> {
            try {
                File extStorage = getAppExternalFilesDir();

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault());

                String fileName = "PIC_"+sdf.format(date)+".png";

                path = extStorage.getAbsolutePath() + "/" + fileName;

                File myFile = new File(path);
                FileOutputStream fOut = new FileOutputStream(myFile);
                SimpleDimpleDrawingView.getCanvasBitmap().compress(Bitmap.CompressFormat.PNG,90,fOut);
                fOut.flush();
                fOut.close();
                uriBitmap = FileProvider.getUriForFile(
                        MainActivity.this,
                        "org.l3ger0j.simpledimpledraw.provider",
                        myFile
                );
                MediaStore.Images.Media.insertImage(getContentResolver(), myFile.getAbsolutePath(), myFile.getName(), myFile.getName());
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{myFile.toString()}, null, null);
                sendPic();
            } catch (IOException e) {
                e.printStackTrace();
            }
            popupWindow.dismiss();

            Toast.makeText(MainActivity.this, "Save", Toast.LENGTH_LONG).show();
            alertDialog = DialogScreenBuilder.
                    getAlertDialog(this, DialogType.CaptureDialog);
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

    // ClearCanvas
    private void clearCanvas() {
        View popupView = popupWindowBuilder.createPopupWindowView(this, WindowType.ClearCanvas);
        PopupWindow popupWindow = popupWindowBuilder.createPopupWindow(WindowType.ClearCanvas);
        popupWindow.showAsDropDown(bottomNavigationView.
                findViewById(R.id.appBarClear), -55, -370);

        popupView.findViewById(R.id.eraser).setOnClickListener(v -> {
            PorterDuffXfermode porterDuffXfermode =
                    new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
            if (simpleDimpleDrawingView.drawPaint.getXfermode() == null) {
                simpleDimpleDrawingView.drawPaint.setXfermode(porterDuffXfermode);
                simpleDimpleDrawingView.specialPath.reset();
                simpleDimpleDrawingView.clearPath.reset();
                simpleDimpleDrawingView.id = 4;
            } else {
                simpleDimpleDrawingView.drawPaint.setXfermode(null);
                simpleDimpleDrawingView.specialPath.reset();
                simpleDimpleDrawingView.clearPath.reset();
                simpleDimpleDrawingView.id = 0;
            }
        });

        popupView.findViewById(R.id.clearAll).setOnClickListener(v -> {
            simpleDimpleDrawingView.drawCanvas.drawColor(Color.TRANSPARENT , PorterDuff.Mode.CLEAR);
            simpleDimpleDrawingView.specialPath.reset();
            simpleDimpleDrawingView.clearPath.reset();
            simpleDimpleDrawingView.invalidate();
        });
    }

    // region ShapesSelect
    DialogInterface.OnClickListener onClickListener = (dialog , which) -> {
        ListView listView = ((AlertDialog) dialog).getListView();
        switch (id) {
            case 1:
                if (which == Dialog.BUTTON_POSITIVE) {
                    int pos = listView.getCheckedItemPosition();
                    if (pos == 0) {
                        alertDialog = DialogScreenBuilder.
                                getAlertDialog(this, DialogType.RoundSize);
                        alertDialog.show();
                        simpleDimpleDrawingView.id = 1;
                        break;
                    } else if (pos == 1) {
                        toggleButton.setChecked(true);
                        floatingActionButton.hide();
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                        toggleButton.setVisibility(View.VISIBLE);
                        simpleDimpleDrawingView.id = 2;
                        break;
                    } else if (pos == 2) {
                        simpleDimpleDrawingView.id = 3;
                        break;
                    }
                }
                break;
            case 2:
                if (which == Dialog.BUTTON_POSITIVE) {
                    try {
                        EditText editText = alertDialog.findViewById(R.id.sizeRadius);
                        String snd = editText.getText().toString();
                        simpleDimpleDrawingView.drawRadius = Integer.parseInt(snd);
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Dialog dismissed", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    };
    // endregion

    // region FAB popupWindow
    public void showMinSettingDrawWindow() {
        View popupView = popupWindowBuilder.createPopupWindowView(this, WindowType.MinimalSetting);
        PopupWindow popupWindow = popupWindowBuilder.createPopupWindow(WindowType.MinimalSetting);
        popupWindow.showAtLocation(findViewById(R.id.fab),
                Gravity.CENTER, posPopupWindow[0], posPopupWindow[1]);

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

        ImageButton undo = popupView.findViewById(R.id.undo);
        undo.setOnClickListener(v -> {
        });
        undo.setEnabled(false);

        ImageButton redo = popupView.findViewById(R.id.redo);
        redo.setOnClickListener(v -> {
        });
        redo.setEnabled(false);

        textView = popupView.findViewById(R.id.textView);
        textView.setText(String.valueOf(simpleDimpleDrawingView.stroke));
        seekBar.setProgress(simpleDimpleDrawingView.stroke);

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
        simpleDimpleDrawingView.stroke = seekBar.getProgress();
        simpleDimpleDrawingView.specialPath.rewind();
    }
    // endregion

    public void showColorSettingMenu (View v) {
        View popupView = popupWindowBuilder.createPopupWindowView(this, WindowType.ColorSetting);
        PopupWindow popupWindow = popupWindowBuilder.createPopupWindow(WindowType.ColorSetting);
        popupWindow.showAsDropDown(bottomNavigationView.findViewById(R.id.appBarColor), -55, -370);

        ImageButton drawColor = popupView.findViewById(R.id.drawColor);
        drawColor.setOnClickListener(v12 -> createColorPickerDialog(2));

        ImageButton backColor = popupView .findViewById(R.id.backColor);
        backColor.setOnClickListener(v1 -> createColorPickerDialog(1));
    }

    public int getBackgroundColor () {
        int color = Color.WHITE;
        Drawable background = simpleDimpleDrawingView.getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();

        return color;
    }

    private void createColorPickerDialog(int id) {
        if (id == 1) {
            ColorPickerDialog.newBuilder()
                    .setDialogId(id)
                    .setDialogTitle(R.string.color_background)
                    .setColor(getBackgroundColor())
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowCustom(true)
                    .setAllowPresets(true)
                    .setColorShape(ColorShape.SQUARE)
                    .show(this);
        } else if (id == 2) {
            ColorPickerDialog.newBuilder()
                    .setDialogId(id)
                    .setDialogTitle(R.string.color_drawing)
                    .setColor(simpleDimpleDrawingView.paintColor)
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
            simpleDimpleDrawingView.setBackgroundColor(color);
            simpleDimpleDrawingView.invalidate();
        } else if (dialogId == 2) {
            simpleDimpleDrawingView.paintColor = color;
            simpleDimpleDrawingView.specialPath = new SpecialPath();
            simpleDimpleDrawingView.drawPaint.setXfermode(null);
            simpleDimpleDrawingView.clearPath.reset();
            simpleDimpleDrawingView.id = 0;
            simpleDimpleDrawingView.invalidate();
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
    }
}
