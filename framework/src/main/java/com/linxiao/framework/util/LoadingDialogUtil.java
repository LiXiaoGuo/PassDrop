package com.linxiao.framework.util;

import android.app.Dialog;
import android.content.Context;

import com.linxiao.framework.R;
import com.linxiao.framework.widget.CustomProgressDialog;

/**
 * @author chenli
 * @create 2018/6/7
 * @Describe
 */
public class LoadingDialogUtil {
    /**
     * 初始化进度条
     */
    public static CustomProgressDialog initCPD(Context context) {
        CustomProgressDialog pd = CustomProgressDialog.createDialog(context);
        pd.setCanceledOnTouchOutside(true);
        pd.closeOutSideWindow(true);
        pd.setMessage(context.getString(R.string.dl_waiting));
        return pd;
    }

    /**
     * 显示对话框
     */
    public static void showDialog(Dialog dialog) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 关闭对话框
     */
    public static void dimssDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
