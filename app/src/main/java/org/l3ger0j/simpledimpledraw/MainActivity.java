package org.l3ger0j.simpledimpledraw;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ColorPickerDialogListener, SeekBar.OnSeekBarChangeListener {

    SimpleDimpleDrawingView simpleDimpleDrawingView;
    FloatingActionButton floatingActionButton;
    Chip chip;
    BottomNavigationView bottomNavigationView;
    AlertDialog alertDialog;
    TextView textView;
    PopupWindow popupWindow;
    Uri uriBitmap = null;

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
                alertDialog = DialogScreenFabric.getAlertDialog(this , DialogScreenFabric.ShapeSelect);
                alertDialog.show();
            } else if (itemId == R.id.appBarAbout) {
                alertDialog = DialogScreenFabric.getAlertDialog(this , DialogScreenFabric.MainMenu);
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
        if (popupWindow != null)
            popupWindow.dismiss();
    }

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

    // region picSave
    private void openCaptureMenu() {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_capture_menu, null);
        popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(bottomNavigationView.findViewById(R.id.appBarSave), 0, -370);

        popupView.findViewById(R.id.saveCanvas).setOnClickListener(v -> {
            try {
                File extStorage = getAppExternalFilesDir();

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault());

                String fileName = "PIC_"+sdf.format(date)+".png";

                String path = extStorage.getAbsolutePath() + "/" + fileName;

                File myFile = new File(path);
                FileOutputStream fOut = new FileOutputStream(myFile);
                SimpleDimpleDrawingView.getCanvasBitmap().compress(Bitmap.CompressFormat.PNG,90,fOut);
                fOut.flush();
                fOut.close();
                uriBitmap = Uri.fromFile(myFile);
                MediaStore.Images.Media.insertImage(getContentResolver(), myFile.getAbsolutePath(), myFile.getName(), myFile.getName());
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{myFile.toString()}, null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            popupWindow.dismiss();

            Toast.makeText(MainActivity.this, "Save", Toast.LENGTH_LONG).show();
            alertDialog = DialogScreenFabric.getAlertDialog(MainActivity.this, 4);
            alertDialog.show();
        });

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        if (uriBitmap != null) {
            popupView.findViewById(R.id.shareCanvas).setOnClickListener(v -> {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uriBitmap);
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, "Send to"));
            });
        } else {
            popupView.findViewById(R.id.shareCanvas).setEnabled(false);
        }
    }
    // endregion

    // ClearCanvas
    private void clearCanvas() {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View clearPopupView = layoutInflater.inflate(R.layout.popup_clear, null);
        popupWindow = new PopupWindow(clearPopupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(bottomNavigationView.findViewById(R.id.appBarClear), -55, -370);

        clearPopupView.findViewById(R.id.eraser).setOnClickListener(v -> {
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

        clearPopupView.findViewById(R.id.clearAll).setOnClickListener(v -> {
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
                        alertDialog = DialogScreenFabric.getAlertDialog(this, DialogScreenFabric.RoundSize);
                        alertDialog.show();
                        simpleDimpleDrawingView.id = 1;
                    } else if (pos == 1) {
                        simpleDimpleDrawingView.id = 2;
                    } else if (pos == 2) {
                        simpleDimpleDrawingView.id = 3;
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
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_fab, null);
        popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(findViewById(R.id.fab), Gravity.CENTER, posPopupWindow[0], posPopupWindow[1]);

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
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        undo.setEnabled(false);

        ImageButton redo = popupView.findViewById(R.id.redo);
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
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
    public void onProgressChanged(SeekBar seekBar , int progress , boolean fromUser) {
        textView.setText(String.valueOf(seekBar.getProgress()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        simpleDimpleDrawingView.stroke = seekBar.getProgress();
        simpleDimpleDrawingView.specialPath.rewind();
    }
    // endregion

    public void showColorSettingMenu (View v) {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_color_setting, null);
        popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(bottomNavigationView.findViewById(R.id.appBarColor), -55, -370);

        ImageButton drawColor = popupView.findViewById(R.id.drawColor);
        drawColor.setOnClickListener(v12 -> createColorPickerDialog(2));

        ImageButton backColor = popupView.findViewById(R.id.backColor);
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
