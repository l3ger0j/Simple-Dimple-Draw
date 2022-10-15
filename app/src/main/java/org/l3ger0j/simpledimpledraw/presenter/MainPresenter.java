package org.l3ger0j.simpledimpledraw.presenter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableBoolean;

import org.l3ger0j.simpledimpledraw.DialogType;
import org.l3ger0j.simpledimpledraw.R;
import org.l3ger0j.simpledimpledraw.model.DialogScreenBuilder;

public class MainPresenter implements MainContract.MainPresenter {
    private final Context context;

    public MainPresenter (Context context) {
        this.context = context;
    }

    @Override
    public AlertDialog createAboutDialog() {
        return DialogScreenBuilder.createAlertDialog(context, DialogType.AboutDialog);
    }
}
