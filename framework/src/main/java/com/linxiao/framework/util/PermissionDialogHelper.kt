package com.linxiao.framework.util

import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.PermissionUtils
import com.linxiao.framework.R


/**
 * 对话框工具类
 * @author Extends
 * @date 2018/7/23/023
 */
object PermissionDialogHelper {
    /**
     * 申请权限时调用
     */
    fun showRationaleDialog(shouldRequest: PermissionUtils.OnRationaleListener.ShouldRequest,permissionName:String=""){
        val topActivity = ActivityUtils.getTopActivity() ?: return
        AlertDialog.Builder(topActivity, R.style.BDAlertDialog)
                .setTitle("权限申请")
                .setMessage("您拒绝我们申请${permissionName}的授权，请同意授权，否则功能不能正常使用！")
                .setPositiveButton("同意") { _, _ -> shouldRequest.again(true) }
                .setNegativeButton("取消") { _, _ -> shouldRequest.again(false) }
                .setCancelable(false)
                .create()
                .show()
    }

    /**
     * 用户拒绝权限时调用
     */
    fun showOpenAppSettingDialog(permissionName:String="",cancel:()->Unit={}){
        val topActivity = ActivityUtils.getTopActivity() ?: return
        AlertDialog.Builder(topActivity,R.style.BDAlertDialog)
                .setTitle("权限申请")
                .setMessage("我们需要您所拒绝的${permissionName}或系统未能应用成功，请手动设置到页面授权，否则该功能不能正常使用！")
                .setPositiveButton(android.R.string.ok) { _, _ -> PermissionUtils.launchAppDetailsSettings() }
                .setNegativeButton(android.R.string.cancel) { _, _ -> cancel()}
                .setCancelable(false)
                .create()
                .show()
    }
}