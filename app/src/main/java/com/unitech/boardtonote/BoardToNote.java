package com.unitech.boardtonote;

import android.app.Application;
import android.util.Log;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import com.unitech.boardtonote.helper.ThemeHelper;

public class BoardToNote extends Application {
    final String tag = "Application";

    public void onCreate() {
        super.onCreate();
        Log.i(tag, "onCreate");
        AppCenter.start(this, "f74746f2-abec-4479-b7cd-abeff0cc8ce5",
                Analytics.class, Crashes.class);
        String theme = ThemeHelper.loadTheme(this);
        ThemeHelper.applyTheme(theme);
        Log.v(tag, "Theme : " + theme);
    }
}
