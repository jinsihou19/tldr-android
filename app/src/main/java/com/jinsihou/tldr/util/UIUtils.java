package com.jinsihou.tldr.util;

import android.content.Context;
import android.content.res.Configuration;

public class UIUtils {
    private UIUtils() {
        throw new AssertionError("No com.jinsihou.tldr.util.UIUtils for you!");
    }

    public static boolean isDarkTheme(Context context) {
        return Configuration.UI_MODE_NIGHT_YES
                == (context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK);
    }
}
