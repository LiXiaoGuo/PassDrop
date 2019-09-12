package com.linxiao.framework.dialog

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AppCompatDialogFragment
import android.util.Log


/**
 * Fragment 基类
 * Created by LinXiao on 2016-07-14.
 */
abstract class BaseDialogFragment : AppCompatDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = this.javaClass.simpleName
    }

    override fun onStart() {
        super.onStart()
        if(dialog != null){
            val win = dialog?.window ?: return
            val params = win.attributes
            win.setLayout(params.width, params.height)

            //点击外部区域消失
            dialog?.setCanceledOnTouchOutside(true)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (!this.isAdded) {
            super.show(manager, tag)
        } else {
            Log.d("BaseBottomSheetFragment", " has add to FragmentManager")
        }
    }

    companion object {
        lateinit var TAG: String
    }

}
