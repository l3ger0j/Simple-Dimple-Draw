package org.l3ger0j.simpledimpledraw.view.dialogs

import android.content.Context
import androidx.fragment.app.DialogFragment

abstract class MainPatternDialogFrags : DialogFragment() {
    interface MainPatternDialogList {
        fun onDialogNegativeClick(mainDialogFrags: MainDialogFrags?)
    }

    var listener: MainPatternDialogList? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as MainPatternDialogList
        } catch (e: ClassCastException) {
            throw ClassCastException(context
                    .toString() + " must implement PatternDialogListener")
        }
    }
}