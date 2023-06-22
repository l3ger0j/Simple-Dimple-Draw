package org.l3ger0j.simpledimpledraw.view.adapters

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("imageLoad")
fun loadImage(view: ImageView, imageBitmap: Bitmap?) {
    if (imageBitmap != null) {
        view.setImageBitmap(imageBitmap)
    }
}