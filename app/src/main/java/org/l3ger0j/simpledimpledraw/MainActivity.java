package org.l3ger0j.simpledimpledraw;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class MainActivity extends AppCompatActivity implements ColorPickerDialogListener,
        SeekBar.OnSeekBarChangeListener {

    // region Layouts
    private SimpleDimpleDrawingView simpleDimpleDrawingView;
    private ShapeManager shapeManager;
    // endregion

    // region Buttons
    private FloatingActionButton floatingActionButton , circleHiddenMenuButton , fabSetDrawColor ,
            fabCloseMenu , fabSetBackgroundColor , fabEraser , fabClearCanvas;
    private BottomNavigationView bottomNavigationView;
    private ToggleButton toggleButton;
    // endregion

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
                ActivityMainBinding.inflate(getLayoutInflater());

        simpleDimpleDrawingView = binding.simpleDrawingView;
        shapeManager = binding.shapeManager;
        bottomNavigationView = binding.navigationToolbar;
        binding.navigationToolbar.setOnItemReselectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.appBarSave) {
                openCaptureMenu();
            } else if (itemId == R.id.appBarClear) {
                clearCanvas();
            } else if (itemId == R.id.appBarColor) {
                showColorSettingMenu(bottomNavigationView);
            } else if (itemId == R.id.appBarMenu) {
                showPopupMenu();
            }
        });

        drawPaint = new DrawPaint();
        simpleDimpleDrawingView.setDrawPaint(drawPaint);

        floatingActionButton = binding.fab;
        circleHiddenMenuButton = binding.fabHiddenMenu;
        fabSetDrawColor = binding.fabSetDrawColor;
        fabSetBackgroundColor = binding.fabSetBackgroundColor;
        fabEraser = binding.fabEraser;
        fabClearCanvas = binding.fabClearCanvas;
        fabCloseMenu = binding.fabCloseMenu;
        toggleButton = binding.toggleButton;

        binding.fab.setOnClickListener(mainViewOnClickList);
        binding.fabHiddenMenu.setOnClickListener(mainViewOnClickList);
        binding.fabSetDrawColor.setOnClickListener(mainViewOnClickList);
        binding.fabSetBackgroundColor.setOnClickListener(mainViewOnClickList);
        binding.fabEraser.setOnClickListener(mainViewOnClickList);
        binding.fabClearCanvas.setOnClickListener(mainViewOnClickList);
        binding.fabCloseMenu.setOnClickListener(mainViewOnClickList);
        binding.toggleButton.setOnClickListener(mainViewOnClickList);

        setContentView(binding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toggleButton.getVisibility() == View.GONE &&
                circleHiddenMenuButton.getVisibility() == View.GONE) {
            floatingActionButton.show();
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (WindowType windowType : WindowType.values()) {
            popupWindowBuilder.createPopupWindow(windowType).dismiss();
        }
    }

    public void hideAppBarMenu (int id) {
        switch (id) {
            case 1:
                floatingActionButton.hide();
                bottomNavigationView.setVisibility(View.INVISIBLE);
                circleHiddenMenuButton.setVisibility(View.VISIBLE);
                break;
            case 2:
                floatingActionButton.hide();
                bottomNavigationView.setVisibility(View.INVISIBLE);
                break;
        }
    }

    View.OnClickListener mainViewOnClickList = new View.OnClickListener() {
        @Override
        public void onClick(@NonNull View v) {
            if (v.getId() == R.id.fabHiddenMenu) {
                if (fabCloseMenu.getVisibility() == View.GONE) {
                    fabSetDrawColor.setVisibility(View.VISIBLE);
                    fabSetBackgroundColor.setVisibility(View.VISIBLE);
                    fabEraser.setVisibility(View.VISIBLE);
                    fabClearCanvas.setVisibility(View.VISIBLE);
                    fabCloseMenu.setVisibility(View.VISIBLE);
                } else {
                    fabSetDrawColor.setVisibility(View.GONE);
                    fabSetBackgroundColor.setVisibility(View.GONE);
                    fabEraser.setVisibility(View.GONE);
                    fabClearCanvas.setVisibility(View.GONE);
                    fabCloseMenu.setVisibility(View.GONE);
                }
            } else if (v.getId() == R.id.fabSetDrawColor) {
                createColorPickerDialog(2);
            } else if (v.getId() == R.id.fabSetBackgroundColor) {
                createColorPickerDialog(1);
            } else if (v.getId() == R.id.fabEraser) {
                if (simpleDimpleDrawingView.isEraserOn()) {
                    simpleDimpleDrawingView.eraseCanvas(true);
                    v.setRotation(180);
                } else {
                    simpleDimpleDrawingView.eraseCanvas(false);
                    v.setRotation(0);
                }
            } else if (v.getId() == R.id.fabClearCanvas) {
                simpleDimpleDrawingView.clearCanvas();
            } else if (v.getId() == R.id.fabCloseMenu) {
                bottomNavigationView.setVisibility(View.VISIBLE);
                floatingActionButton.show();
                circleHiddenMenuButton.setVisibility(View.GONE);
                fabSetDrawColor.setVisibility(View.GONE);
                fabSetBackgroundColor.setVisibility(View.GONE);
                fabEraser.setVisibility(View.GONE);
                fabClearCanvas.setVisibility(View.GONE);
                fabCloseMenu.setVisibility(View.GONE);
            } else if (v.getId() == R.id.toggleButton) {
                if (shapeManager.selectShape == 1) {
                    simpleDimpleDrawingView.drawShape(0, shapeManager);
                    shapeManager.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    floatingActionButton.show();
                    toggleButton.setVisibility(View.GONE);
                } else if (shapeManager.selectShape == 2) {
                    simpleDimpleDrawingView.drawShape(1, shapeManager);
                    shapeManager.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    floatingActionButton.show();
                    toggleButton.setVisibility(View.GONE);
                } else if (shapeManager.selectShape == 3) {
                    simpleDimpleDrawingView.drawShape(2, shapeManager);
                    shapeManager.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    floatingActionButton.show();
                    toggleButton.setVisibility(View.GONE);
                }
            } else if (v.getId() == R.id.fab) {
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                floatingActionButton.setRippleColor(color);
                showMinSettingDrawWindow();
            }
        }
    };

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
        popupWindow.showAsDropDown(bottomNavigationView.findViewById(R.id.appBarSave), 99, -382);

        popupView.findViewById(R.id.saveCanvas).setOnClickListener(v -> {
            try {
                File extStorage = getExternalFilesDir(null);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            popupWindow.dismiss();

            Toast.makeText(MainActivity.this, "Save", Toast.LENGTH_LONG).show();
            DialogScreenBuilder dialogScreenBuilder = new DialogScreenBuilder();
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

    // region ClearCanvas
    private void clearCanvas() {
        View popupView = popupWindowBuilder.createPopupWindowView(this, WindowType.ClearCanvas);
        PopupWindow popupWindow = popupWindowBuilder.createPopupWindow(WindowType.ClearCanvas);
        popupWindow.showAsDropDown(bottomNavigationView.
                findViewById(R.id.appBarClear), 0, -370);

        popupView.findViewById(R.id.eraser).setOnClickListener(v -> {
            if (simpleDimpleDrawingView.isEraserOn()) {
                simpleDimpleDrawingView.eraseCanvas(true);
                v.setRotation(180);
            } else {
                simpleDimpleDrawingView.eraseCanvas(false);
                v.setRotation(0);
            }
        });

        popupView.findViewById(R.id.clearAll).setOnClickListener(v ->
                simpleDimpleDrawingView.clearCanvas());
    }
    // endregion

    // region Menu
    private void showPopupMenu () {
        PopupMenu popupMenu = new PopupMenu(this ,
                bottomNavigationView.findViewById(R.id.appBarMenu) , Gravity.CENTER);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            int groupId = item.getGroupId();
            if (groupId == R.id.groupShapes) {
                if (itemId == R.id.addCircle) {
                    shapeManager.selectShape = 1;
                    toggleButton.setChecked(true);
                    shapeManager.setVisibility(View.VISIBLE);
                    hideAppBarMenu(2);
                    toggleButton.setVisibility(View.VISIBLE);
                } else if (itemId == R.id.addRectangle) {
                    shapeManager.selectShape = 2;
                    toggleButton.setChecked(true);
                    shapeManager.setVisibility(View.VISIBLE);
                    hideAppBarMenu(2);
                    toggleButton.setVisibility(View.VISIBLE);
                } else if (itemId == R.id.addOval) {
                    shapeManager.selectShape = 3;
                    toggleButton.setChecked(true);
                    shapeManager.setVisibility(View.VISIBLE);
                    hideAppBarMenu(2);
                    toggleButton.setVisibility(View.VISIBLE);
                }
            } else if (groupId == R.id.groupSettings) {
                if (itemId == R.id.hide) {
                    hideAppBarMenu(1);
                } else if (itemId == R.id.about) {
                    alertDialog = DialogScreenBuilder.createAlertDialog(MainActivity.this ,
                            DialogType.AboutDialog);
                    alertDialog.show();
                }
            }
            return true;
        });
        popupMenu.show();
    }
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

        textView = popupView.findViewById(R.id.textView);
        textView.setText(String.valueOf(drawPaint.getStrokeWidth()));
        seekBar.setProgress((int) drawPaint.getStrokeWidth());

        ImageButton undoButton = popupView.findViewById(R.id.undoButton);
        undoButton.setOnClickListener(v -> {
            simpleDimpleDrawingView.undoPath();
            simpleDimpleDrawingView.invalidate();
        });

        ImageButton redoButton = popupView.findViewById(R.id.redoButton);
        redoButton.setOnClickListener(v -> {
            simpleDimpleDrawingView.redoPath();
            simpleDimpleDrawingView.invalidate();
        });

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
        simpleDimpleDrawingView.setStroke(seekBar.getProgress());
    }
    // endregion

    public void showColorSettingMenu (View v) {
        View popupView = popupWindowBuilder.createPopupWindowView(this, WindowType.ColorSetting);
        PopupWindow popupWindow = popupWindowBuilder.createPopupWindow(WindowType.ColorSetting);
        popupWindow.showAsDropDown(bottomNavigationView.findViewById(R.id.appBarColor), -80, -370);

        ImageButton drawColor = popupView.findViewById(R.id.drawColor);
        drawColor.setOnClickListener(v12 -> createColorPickerDialog(2));

        ImageButton backColor = popupView .findViewById(R.id.backColor);
        backColor.setOnClickListener(v1 -> createColorPickerDialog(1));
    }

    private void createColorPickerDialog(int id) {
        if (id == 1) {
            ColorPickerDialog.newBuilder()
                    .setDialogId(id)
                    .setDialogTitle(R.string.color_background)
                    .setColor(simpleDimpleDrawingView.getBackgroundColor())
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
            simpleDimpleDrawingView.setBackgroundColor(color);
        } else if (dialogId == 2) {
            simpleDimpleDrawingView.setColor(color);
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
    }
}