package com.liguo.password

import android.app.Activity
import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.linxiao.framework.util.ClipboardUtils
import com.linxiao.framework.util.logi
import com.linxiao.framework.util.toast
import kotlinx.android.synthetic.main.diglog_bottom_password.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.share
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

/**
 *
 * @author Extends
 * @date 2019/9/11/011
 */
class PasswordBottomSheetDialog(context: Context) : BottomSheetDialog(context) {

    private var passwordBean:PasswordBean?=null

    /**
     * 删除回调
     */
    var deleteCall : ((pb:PasswordBean?) -> Unit)? = null

    init {
        setContentView(R.layout.diglog_bottom_password)

        dialog_bottom_password_copy_share.onClick {
            if(passwordBean != null){
                val shareStr = buildString {
                    if(!passwordBean!!.name.isNullOrEmpty()){
                        appendln(passwordBean!!.name)
                    }
                    if(!passwordBean!!.account.isNullOrEmpty()){
                        append("账号：").appendln(passwordBean!!.account)
                    }
                    if(!passwordBean!!.password.isNullOrEmpty()){
                        append("密码：").appendln(passwordBean!!.password)
                    }
                    append("by PassDrop share")
                }
                (context as Activity?)?.share(shareStr)
            }
            dismiss()
        }
        dialog_bottom_password_copy_accase.onClick {
            copy(passwordBean?.account)
        }
        dialog_bottom_password_copy_password.onClick {
            copy(passwordBean?.password)
        }
        dialog_bottom_password_delect.onClick {
            deleteCall?.invoke(passwordBean)
            dismiss()
        }
        dialog_bottom_password_update_password.onClick {
            if(passwordBean != null){
                (context as Activity?)?.startActivityForResult<CreatePasswordActivity>(200,"data" to passwordBean)
            }
            dismiss()
        }
    }

    /**
     * sdf
     */
    fun show(pb: PasswordBean) {
        passwordBean = pb
        super.show()
    }

    private fun copy(text:CharSequence?){
        ClipboardUtils.copyText(text)
        "已复制到剪贴板".toast()
        dismiss()
    }

}