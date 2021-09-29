package com.yxy.viewhelp.checkview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.AnyRes;

public class UIUtils {

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getWidthPixels(){
        return getMetrics().widthPixels;
    }

    public static int getHeightPixels(){
        return getMetrics().heightPixels;
    }

    private static DisplayMetrics getMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) ContextUtils.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public static Rect getViewRect(View view) {
        Rect rect = new Rect();
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        rect.left = locations[0];
        rect.top = locations[1];
        if (!checkStatusBarVisible(view.getContext())) {
            rect.top-= getStatusBarHeight(view.getContext());
        }
        rect.right = rect.left + view.getWidth();
        rect.bottom = rect.top + view.getHeight();
        return rect;
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static boolean checkStatusBarVisible(Context context){
        return checkFullScreenByTheme(context)|| checkFullScreenByCode(context)|| checkFullScreenByCode2(context);
    }

    public static boolean checkFullScreenByTheme(Context context){
        Resources.Theme theme=context.getTheme();
        if (theme!=null){
            TypedValue typedValue=new TypedValue();
            boolean result= theme.resolveAttribute(android.R.attr.windowFullscreen,typedValue,false);
            if (result){
                typedValue.coerceToString();
                if (typedValue.type== TypedValue.TYPE_INT_BOOLEAN){
                    return typedValue.data!=0;
                }
            }
        }
        return false;
    }

    public static boolean checkFullScreenByCode(Context context){
        if (context instanceof Activity) {
            Window window = ((Activity) context).getWindow();
            if (window != null) {
                View decorView = window.getDecorView();
                if (decorView != null) {
                    return  (decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) == View.SYSTEM_UI_FLAG_FULLSCREEN;
                }
            }
        }
        return false;
    }
    public static boolean checkFullScreenByCode2(Context context){
        if (context instanceof Activity){
            return (((Activity)context).getWindow().getAttributes().flags& WindowManager.LayoutParams.FLAG_FULLSCREEN)==WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        return false;
    }

    public static String getIdText(View view) {
        final int id = view.getId();
        StringBuilder out = new StringBuilder();
        if (id != View.NO_ID) {
            final Resources r = view.getResources();
            if (id > 0 && resourceHasPackage(id) && r != null) {
                try {
                    String pkgname;
                    switch (id&0xff000000) {
                        case 0x7f000000:
                            pkgname="app";
                            break;
                        case 0x01000000:
                            pkgname="android";
                            break;
                        default:
                            pkgname = r.getResourcePackageName(id);
                            break;
                    }
                    String typename = r.getResourceTypeName(id);
                    String entryname = r.getResourceEntryName(id);
                    out.append(" ");
                    out.append(pkgname);
                    out.append(":");
                    out.append(typename);
                    out.append("/");
                    out.append(entryname);
                } catch (Resources.NotFoundException e) {
                }
            }
        }
        return out.toString();
    }

    private static boolean resourceHasPackage(@AnyRes int resid) {
        return (resid >>> 24) != 0;
    }
}
