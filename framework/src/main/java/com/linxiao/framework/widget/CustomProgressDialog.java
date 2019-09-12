/**************************************************************************************
 * [Project]
 * MyProgressDialog
 * [Package]
 * com.lxd.widgets
 * [FileName]
 * CustomProgressDialog.java
 * [Copyright]
 * Copyright 2012 LXD All Rights Reserved.
 * [History]
 * Version          Date              Author                        Record
 * --------------------------------------------------------------------------------------
 * 1.0.0           2012-4-27         lxd (rohsuton@gmail.com)        Create
 **************************************************************************************/

package com.linxiao.framework.widget;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.linxiao.framework.R;
import com.linxiao.framework.util.StatusBarUtil;

import org.jetbrains.anko.ScreenSize;


public class CustomProgressDialog extends Dialog {
    private Context context = null;
    private static CustomProgressDialog customProgressDialog = null;

    public CustomProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static CustomProgressDialog createDialog(Context context) {
        customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog);
        customProgressDialog.setContentView(R.layout.dialog_custom_progress);
        Window window = customProgressDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;

        int mMaxHeight = context.getResources().getDisplayMetrics().heightPixels + StatusBarUtil.getStatusBarHeight(context);
        params.height = mMaxHeight;

        window.setAttributes(params);

        return customProgressDialog;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (customProgressDialog == null) {
            return;
        }

        ImageView imageView = customProgressDialog.findViewById(R.id.loadingImageView);
        if (imageView!=null){
            AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
            animationDrawable.start();
        }

    }

    /**
     * 设置点击外面是否可关闭
     */
    public void closeOutSideWindow(final boolean isClose) {
        LinearLayout llCostomProggress = customProgressDialog.findViewById(R.id.ll_costom_progress);
        if (llCostomProggress != null) {
            llCostomProggress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClose) {
                        dismiss();
                    }
                }
            });
        }

    }

    /**
     * [Summary] setTitile 标题
     *
     * @param strTitle
     * @return
     */
    public CustomProgressDialog setTitile(String strTitle) {
        return customProgressDialog;
    }

    /**
     * [Summary] setMessage 提示内容
     *
     * @param strMessage
     * @return
     */
    public CustomProgressDialog setMessage(String strMessage) {
        TextView tvMsg = customProgressDialog.findViewById(R.id.id_tv_loadingmsg);

        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }

        return customProgressDialog;
    }
}
