package com.linxiao.framework.dialog

import android.content.Context
import android.content.DialogInterface

import com.linxiao.framework.BaseApplication

/**
 * 应用内消息通知封装
 * Created by LinXiao on 2016-11-25.
 */
object AlertDialogManager {

    @JvmOverloads fun createAlertDialogBuilder(context: Context = BaseApplication.getAppContext()): AlertDialogBuilder {
        return AlertDialogBuilder(context)
    }

    /**
     * show an alert dialog with simple message and positive button
     */
    fun showAlertDialog(message: String) {
        createAlertDialogBuilder()
                .setMessage(message)
                .show()
    }

    /**
     * show alert dialog with simple message,
     * click event of positive button is configurable
     */
    fun showAlertDialog(message: String, positiveListener: DialogInterface.OnClickListener) {
        createAlertDialogBuilder()
                .setMessage(message)
                .setPositiveButton(positiveListener)
                .show()
    }

    /**
     * show alert dialog with simple message,
     * click event of positive button and negative button are configurable
     */
    fun showAlertDialog(message: String, positiveListener: DialogInterface.OnClickListener,
                        negativeListener: DialogInterface.OnClickListener) {
        createAlertDialogBuilder()
                .setMessage(message)
                .setPositiveButton(positiveListener)
                .setNegativeButton(negativeListener)
                .show()
    }

}
