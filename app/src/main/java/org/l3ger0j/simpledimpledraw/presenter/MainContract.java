package org.l3ger0j.simpledimpledraw.presenter;

import android.app.AlertDialog;
import android.content.Context;

public interface MainContract {

    interface MainActivity {
    }

    interface PaintActivity {

    }

    interface MainPresenter {
        AlertDialog createAboutDialog ();
    }

    interface PaintPresenter {

    }
}
