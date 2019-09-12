package com.linxiao.framework.fragment


import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.linxiao.framework.R
import com.linxiao.framework.dialog.BaseDialogFragment
import org.jetbrains.anko.sdk27.coroutines.onClick

class LoadingFragment : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mDialog = Dialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_loading, null)
        mDialog.setContentView(view)
        onInitView(mDialog, view)
        return mDialog
    }

    private fun onInitView(mDialog: Dialog, root: View?) {
        root?.onClick { }
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val win = dialog?.window ?: return
            win.setGravity(Gravity.CENTER) //可设置dialog的位置
//            dialog.setCancelable(false)
            dialog?.setCanceledOnTouchOutside(false)
        }

    }
}

