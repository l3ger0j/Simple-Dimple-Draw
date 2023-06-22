package org.l3ger0j.simpledimpledraw.view.adapters;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

public class Bind {
    @BindingAdapter({"imageLoad"})
    public static void loadImage(ImageView view, Bitmap imageBitmap) {
        if (imageBitmap != null) {
            view.setImageBitmap(imageBitmap);
        }
    }
}
