package org.l3ger0j.simpledimpledraw.view.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import androidx.databinding.ObservableField
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.l3ger0j.simpledimpledraw.R
import org.l3ger0j.simpledimpledraw.databinding.DialogImageBinding

class MainDialogFrags : MainPatternDialogFrags() {
    private var dialogType: MainDialogType? = null
    private var imageBinding: DialogImageBinding? = null
    fun setDialogType(dialogType: MainDialogType?) {
        this.dialogType = dialogType
    }

    @JvmField
    var imageBitmap = ObservableField<Bitmap>()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext())
        when (dialogType) {
            MainDialogType.ABOUT_DIALOG -> {
                builder.setTitle(R.string.aboutMenu)
                builder.setMessage("Creator - WhoYouAndM3\n\nSupport&Help - Google and forum\n\nMy first pet-project")
                builder.setPositiveButton(android.R.string.ok, null)
                return builder.create()
            }

            MainDialogType.CAPTURE_DIALOG -> {
                builder.setTitle(R.string.captureDialog)
                imageBinding = DialogImageBinding.inflate(layoutInflater)
                imageBinding!!.dialogFragment = this
                builder.setView(imageBinding!!.root)
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setNegativeButton(R.string.share) { _: DialogInterface?, _: Int ->
                    listener?.onDialogNegativeClick(this)
                }
                return builder.create()
            }

            else -> {}
        }
        return super.onCreateDialog(savedInstanceState)
    }
}