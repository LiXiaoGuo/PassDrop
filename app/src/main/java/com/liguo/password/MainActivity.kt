package com.liguo.password

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.Gravity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.UriUtils
import com.google.gson.Gson
import com.liguo.password.databinding.ItemPasswordBinding
import com.liguo.password.utils.Ciphertext
import com.liguo.password.utils.DataPersistence
import com.linxiao.framework.activity.BaseActivity
import com.linxiao.framework.adapter.BaseAdapter
import com.linxiao.framework.util.fromJson
import com.linxiao.framework.util.onFastClick
import com.linxiao.framework.util.toJson
import com.linxiao.framework.util.toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig
import yz.yuzhua.yidian51.mananger.db.passwordDatabase
import java.io.File

/**
 * 主界面
 */
class MainActivity : BaseActivity() {

    private val bsd by lazy { PasswordBottomSheetDialog(this).apply {
        deleteCall = {
            if(it != null){
                passwordDatabase.delectDataFormId(it.id!!)
                getData()
            }
        }
    } }

    private val apt by lazy { BaseAdapter<PasswordBean,ItemPasswordBinding>(R.layout.item_password, arrayListOf()).apply {
        onBind { itemBingding, position, data ->
            itemBingding.itemPasswordVisibility.onFastClick {
                data.visibility = !data.visibility
                notifyItemChanged(position)
            }
            itemBingding.itemPasswordAction.onClick { bsd.show(data) }
            itemBingding.itemPasswordLayout.onClick { startActivity<PasswordDetailsActivity>("data" to data) }
        }
    } }

    override fun onCreateRootView() = R.layout.activity_main

    override fun onInitView(savedInstanceState: Bundle?) {
        if(intent.data != null){
            DataPersistence.externalFile = UriUtils.uri2File(intent.data!!)
            startActivity<LockActivity>()
            finish()
            return;
        }

        changeStatusBarColor(Color.TRANSPARENT,false)

        am_rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = apt
            (this.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }


    }

    private val qpb by lazy {
        QuickPopupBuilder.with(this)
            .contentView(R.layout.popup_action)
            .config(QuickPopupConfig().gravity(Gravity.RIGHT or Gravity.BOTTOM)
                .withClick(R.id.popup_action_export) { shareFile();closePopup() }
                .withClick(R.id.popup_action_import) { openFile();closePopup() }
            ).build()
    }
    private fun closePopup(){
        qpb.dismiss()
    }

    override fun initListener() {
        if(intent.data != null){
            return;
        }
        am_title.setOnRightClick {
            qpb.showPopupWindow(am_title)
        }
        am_add.onClick { startActivityForResult<CreatePasswordActivity>(200) }
    }

    override fun initData() {
        if(intent.data != null){ return; }
        getData()
        disposeLgFile()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 200 && resultCode == 200){
            getData()
        }else if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            DataPersistence.externalFile = UriUtils.uri2File(intent.data!!)
            disposeLgFile()
        }
    }

    /**
     * 处理LG文件
     */
    fun disposeLgFile(){
        if(DataPersistence.externalFile == null) return
        try {
            val f = DataPersistence.externalFile!!
            val json = Ciphertext.decode(f.readBytes())
            if(json == ""){
                "文件解码失败".toast()
                return
            }
            val list = Gson().fromJson<List<PasswordBean>>(json)
            var count = 0
            list?.forEach {
                if(passwordDatabase.importData(it)){
                    count ++
                }
            }
            "成功导入数量：$count 条".toast()
            if(count>0){
                getData()
            }
        }catch (e:Exception){
            "文件获取失败".toast()
            e.printStackTrace()
        }finally {
            DataPersistence.externalFile = null
        }
    }

    fun getData(){
        apt.clearAddAllData(passwordDatabase.getAllData())
    }

    /**
     * 分享密码库
     */
    fun shareFile(){
        try {
            val json = Gson().toJson<List<PasswordBean>>(apt.datas)
            //清空目录下的其他lg缓存文件
            File(cacheDir.absolutePath).listFiles().forEach {
                if(it.name.endsWith(".lg")){
                    it.delete()
                }
            }
            val path = cacheDir.absolutePath + "/data_${System.currentTimeMillis()}.lg"
            val f = File(path)
            f.writeBytes(Ciphertext.encryption(json))

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "*/*"
            //传输文件 采用流的方式
            val data: Uri?
            if (SDK_INT < VERSION_CODES.N) {
                data = Uri.fromFile(f)
            } else {
                val authority = "$packageName.utilcode.provider"
                data = FileProvider.getUriForFile(this, authority, f)
                shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            shareIntent.putExtra(Intent.EXTRA_STREAM, data)
            startActivity(shareIntent)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun openFile(){
        com.linxiao.framework.util.checkPermission(arrayOf(PermissionConstants.STORAGE),"存储",granted = {
            val intent = Intent(Intent.ACTION_GET_CONTENT);
            intent.type = "*/*";//设置类型，我这里是任意类型，任意后缀的可以这样写。
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent,1)
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent?.data != null){
            DataPersistence.externalFile = UriUtils.uri2File(intent.data!!)
        }
        disposeLgFile()
    }
}
