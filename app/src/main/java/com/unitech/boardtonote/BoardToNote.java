package com.unitech.boardtonote;

import android.app.Application;
import android.util.Log;

import com.unitech.boardtonote.helper.ThemeHelper;

public class BoardToNote extends Application {
    final String tag = "Application";

    public void onCreate() {
        super.onCreate();
        Log.i(tag, "onCreate");
        String theme = ThemeHelper.loadTheme(this);
        ThemeHelper.applyTheme(theme);
        Log.v(tag, "Theme : " + theme);
    }
}
