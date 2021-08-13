package org.l3ger0j.simpledimpledraw;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.l3ger0j.simpledimpledraw.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    SimpleDimpleDrawingView simpleDimpleDrawingView;
    FloatingActionButton floatingActionButton;
    Chip chip;
    BottomNavigationView bottomNavigationView;
    AlertDialog alertDialog;
    TextView textView;
    PopupWindow popupWindow;
    static SharedPreferences preferences;

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
                openCaptureDialog();
            } else if (itemId == R.id.appBarClear) {
                clearCanvas();
            } else if (itemId == R.id.appBarColor) {
                createColorSettingsDialog();
            } else if (itemId == R.id.appBarShapes) {
                alertDialog = DialogScreenFabric.getAlertDialog(this , DialogScreenFabric.ShapeSelect);
                assert alertDialog != null;
                alertDialog.show();
            } else if (itemId == R.id.appBarAbout) {
                alertDialog = DialogScreenFabric.getAlertDialog(this , DialogScreenFabric.MainMenu);
                assert alertDialog != null;
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

    private void openCaptureDialog() {
        /*
         * Save in file
         * String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
         * FileOutputStream outputStream;
         * File file = new File(extStorageDirectory, "test.jpg");
         */

        Bitmap bmMyView = SimpleDimpleDrawingView.getCanvasBitmap();

        try {
            File extStorage = this.getAppExternalFilesDir( );

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault());

            String fileName = "PIC_"+sdf.format(date)+".png";

            String path = extStorage.getAbsolutePath() + "/" + fileName;

            File myFile = new File(path);
            FileOutputStream fOut = new FileOutputStream(myFile);
            bmMyView.compress(Bitmap.CompressFormat.PNG,90,fOut);
            fOut.flush();
            fOut.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), myFile.getAbsolutePath(), myFile.getName(), myFile.getName());
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{myFile.toString()}, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(MainActivity.this, "Save", Toast.LENGTH_LONG).show();

        AlertDialog.Builder captureDialog = new AlertDialog.Builder(MainActivity.this);
        captureDialog.setTitle("Canvas captured");

        ImageView bmImage = new ImageView(MainActivity.this);

        bmImage.setImageBitmap(bmMyView);

        LayoutParams bmImageLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        bmImage.setLayoutParams(bmImageLayoutParams);

        LinearLayout dialogLayout = new LinearLayout(MainActivity.this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.addView(bmImage);
        captureDialog.setView(dialogLayout);

        captureDialog.setPositiveButton("OK", null);
        captureDialog.show();
    }

    // ClearCanvas
    private void clearCanvas() {
        try {
            simpleDimpleDrawingView.drawCanvas.drawColor(preferences.getInt("color_background", 0));
            simpleDimpleDrawingView.specialPath.reset();
        } catch (NullPointerException exception) {
            simpleDimpleDrawingView.drawCanvas.drawColor(Color.WHITE);
            simpleDimpleDrawingView.specialPath.reset();
        }
    }

    // ShapesSelect
    DialogInterface.OnClickListener onClickListener = (dialog , which) -> {
        ListView listView = ((AlertDialog) dialog).getListView();
        switch (id) {
            case 1:
                if (which == Dialog.BUTTON_POSITIVE) {
                    int pos = listView.getCheckedItemPosition();
                    if (pos == 0) {
                        alertDialog = DialogScreenFabric.getAlertDialog(this, DialogScreenFabric.RoundSize);
                        assert alertDialog != null;
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

    // ColorSetting
    private void createColorSettingsDialog() {
        startActivity(new Intent(this, ColorSettingsActivity.class));

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        preferences.registerOnSharedPreferenceChangeListener((sharedPreferences , key) -> {
            if (key.equals("color_background")) {
                simpleDimpleDrawingView.setBackgroundColor(sharedPreferences.getInt(key, Color.WHITE));
                simpleDimpleDrawingView.invalidateOutline();
            } else if (key.equals("color_drawing")) {
                simpleDimpleDrawingView.paintColor = sharedPreferences.getInt(key, Color.BLACK);
                simpleDimpleDrawingView.specialPath = new SpecialPath();
                simpleDimpleDrawingView.invalidateOutline();
                if (simpleDimpleDrawingView.paintColor != sharedPreferences.getInt(key, Color.BLACK)) {
                    Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // FAB popupWindow
    public void showMinSettingDrawWindow() {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_fab, null);
        popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(this.findViewById(R.id.fab), Gravity.CENTER, posPopupWindow[0], posPopupWindow[1]);

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
        ImageButton redo = popupView.findViewById(R.id.redo);
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

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
}
