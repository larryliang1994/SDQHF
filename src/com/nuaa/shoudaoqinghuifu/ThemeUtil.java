package com.nuaa.shoudaoqinghuifu;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.Window;

public class ThemeUtil {
    public static int Theme = 1;

    public final static int THEME_COLORFUL_LIGHT = 0;
    public final static int THEME_COLORFUL_DEEP = 1;
    public final static int THEME_BLUE = 2;
    public final static int THEME_DARK = 3;

    public static void onSetTheme(Activity activity, String type) {
        switch (Theme) {
            default:
            case THEME_COLORFUL_LIGHT:
                activity.setTheme(R.style.Theme_Colorful_Light);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = activity.getWindow();
                    switch (type) {
                        case "msg":
                            window.setStatusBarColor(activity.getResources().getColor(R.color.red_status));
                            break;

                        case "other":
                        case "group":
                            window.setStatusBarColor(activity.getResources().getColor(R.color.blue_status));
                            break;

                        case "memo":
                            window.setStatusBarColor(activity.getResources().getColor(R.color.purple_status));
                            break;

                        case "temp":
                            window.setStatusBarColor(activity.getResources().getColor(R.color.green_status));
                            break;
                    }
                }
                break;

            case THEME_COLORFUL_DEEP:
                activity.setTheme(R.style.Theme_Colorful_Deep);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = activity.getWindow();
                    switch (type) {
                        case "msg":
                            window.setStatusBarColor(activity.getResources().getColor(R.color.red_deep_status));
                            break;

                        case "other":
                        case "group":
                            window.setStatusBarColor(activity.getResources().getColor(R.color.blue_deep_status));
                            break;

                        case "memo":
                            window.setStatusBarColor(activity.getResources().getColor(R.color.purple_deep_status));
                            break;

                        case "temp":
                            window.setStatusBarColor(activity.getResources().getColor(R.color.green_deep_status));
                            break;
                    }

                }

                break;

            case THEME_BLUE:
                activity.setTheme(R.style.Theme_Blue);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    activity.getWindow().setStatusBarColor(Color.parseColor("#0288D1"));
                }
                break;

            case THEME_DARK:
                activity.setTheme(R.style.Theme_Dark);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    activity.getWindow().setStatusBarColor(Color.parseColor("#000000"));
                }
                break;
        }


    }
}

