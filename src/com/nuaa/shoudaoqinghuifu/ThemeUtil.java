package com.nuaa.shoudaoqinghuifu;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

public class ThemeUtil {
    public static int Theme = 1;

    public final static int THEME_COLORFUL_LIGHT = 0;
    public final static int THEME_COLORFUL_DEEP = 1;
    public final static int THEME_BLUE = 2;
    public final static int THEME_DARK = 3;

    public static void onSetTheme(Activity activity, String type) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);

        }

        switch (Theme) {
            default:
            case THEME_COLORFUL_LIGHT:
                activity.setTheme(R.style.Theme_Colorful_Light);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                        && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                    switch (type) {
                        case "msg":
                            tintManager.setTintColor(Color.parseColor("#F36C60"));
                            break;

                        case "other":
                        case "group":
                            tintManager.setTintColor(Color.parseColor("#03A9F4"));
                            break;

                        case "memo":
                            tintManager.setTintColor(Color.parseColor("#9C27B0"));
                            break;

                        case "temp":
                            tintManager.setTintColor(Color.parseColor("#8bc34a"));
                            break;
                    }
                }
                break;

            case THEME_COLORFUL_DEEP:
                activity.setTheme(R.style.Theme_Colorful_Deep);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                        && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    switch (type) {
                        case "msg":
                            tintManager.setTintColor(Color.parseColor("#e91e63"));
                            break;

                        case "other":
                        case "group":
                            tintManager.setTintColor(Color.parseColor("#5677fc"));
                            break;

                        case "memo":
                            tintManager.setTintColor(Color.parseColor("#673ab7"));
                            break;

                        case "temp":
                            tintManager.setTintColor(Color.parseColor("#259b24"));
                            break;
                    }
                }

                break;

            case THEME_BLUE:
                activity.setTheme(R.style.Theme_Blue);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(Color.parseColor("#0288D1"));
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                        && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    tintManager.setTintColor(Color.parseColor("#03A9F4"));
                }

                break;

            case THEME_DARK:
                activity.setTheme(R.style.Theme_Dark);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(Color.parseColor("#000000"));
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                        && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    tintManager.setTintColor(Color.parseColor("#293238"));
                }

                break;
        }
    }
}

