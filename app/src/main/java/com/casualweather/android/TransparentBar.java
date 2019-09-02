package com.casualweather.android;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;

public class TransparentBar {
    private static TransparentBar transparentBar;

    // 构造函数私有化
    private TransparentBar() {

    }

    public static TransparentBar getInstance() {

        if (transparentBar == null) {
            // 加锁提高使用效率
            synchronized (TransparentBar.class) {
                if (transparentBar == null) {
                    transparentBar = new TransparentBar();
                }
            }
        }
        return transparentBar;

    }

    public void Immersive(Window window, ActionBar actionBar) {

        if (Build.VERSION.SDK_INT >= 21) {
            View view = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            view.setSystemUiVisibility(option);
            // 将状态栏设置成透明色
            window.setStatusBarColor(Color.TRANSPARENT);

        }

    }

}
