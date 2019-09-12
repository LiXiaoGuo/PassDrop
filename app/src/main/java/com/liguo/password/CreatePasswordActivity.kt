package com.liguo.password

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import com.blankj.utilcode.util.SpanUtils
import com.linxiao.framework.activity.BaseActivity
import com.linxiao.framework.util.onFastClick
import com.linxiao.framework.util.toast
import kotlinx.android.synthetic.main.activity_create_password.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import yz.yuzhua.yidian51.mananger.db.passwordDatabase

/**
 *
 * @author Extends
 * @date 2019/9/10/010
 */
class CreatePasswordActivity:BaseActivity() {

    private var number = true
    private var letter = true
    private var char = false
    private var length = 6
    private var auto = true

    private val pd by lazy { intent.getSerializableExtra("data") as PasswordBean? }

    override fun onCreateRootView() = R.layout.activity_create_password

    override fun onInitView(savedInstanceState: Bundle?) {
        changeStatusBarColor(Color.TRANSPARENT,false)

        acp_number.isChecked = number
        acp_letter.isChecked = letter
        acp_char.isChecked = char
        acp_length.progress = length

        acp_auto.isChecked = auto

        if(pd != null){
            acp_title.setTitleText("修改密码")
            acp_name.setText(pd?.name?:"")
            acp_account.setText(pd?.account?:"")
            acp_password1.setText(pd?.password?:"")
        }


        val a = arrayListOf<Int>()
        a.addAll(33 .. 47)
        a.addAll(58 .. 64)
        a.addAll(91 .. 96)
        a.addAll(123 .. 126)
        acp_char?.text = SpanUtils().append("使用特殊字符(").append(a.joinToString("") { it.toChar().toString() }).setForegroundColor(Color.RED).append(")").create()

    }

    override fun initListener() {
        acp_title.setOnLeftClick { finish() }
        acp_title.setOnRightClick { addData() }
        acp_number.setOnCheckedChangeListener { buttonView, isChecked ->
            if(number != isChecked){
                number = isChecked
                createPassword()
            }
        }
        acp_letter.setOnCheckedChangeListener { buttonView, isChecked ->
            if(letter != isChecked){
                letter = isChecked
                createPassword()
            }
        }
        acp_char.setOnCheckedChangeListener { buttonView, isChecked ->
            if(char != isChecked){
                char = isChecked
                createPassword()
            }
        }

        acp_auto.setOnCheckedChangeListener { buttonView, isChecked ->
            auto = isChecked
            if(isChecked){
                acp_password1_layout.visibility = View.GONE
                acp_password_layout.visibility = View.VISIBLE
            }else{
                acp_password1_layout.visibility = View.VISIBLE
                acp_password_layout.visibility = View.GONE
            }
        }

        acp_password_refresh.onFastClick { createPassword() }

        acp_length.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress != length){
                    length = progress
                    createPassword()
                    acp_length_text.text = "密码长度：${length}位"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun initData() {
        createPassword()
    }

    /**
     * 添加到数据库
     */
    private fun addData(){
        val name = acp_name?.editableText.toString()
        val account = acp_account?.editableText.toString()
        val password = if(auto) acp_password?.text.toString() else acp_password1?.editableText.toString()
        if(name.isBlank()){
            "${acp_name?.hint}不能为空".toast()
            return
        }
        if(password.isBlank()){
            "${acp_password1?.hint}不能为空".toast()
            return
        }
        if(pd == null){
            passwordDatabase.addData(PasswordBean(null,name,null,password,account,null))
        }else{
            passwordDatabase.updateData(PasswordBean(pd!!.id,name,pd!!.typeId,password,account,(if(pd!!.oldPassword.isNullOrEmpty()) "" else "${pd!!.oldPassword} ")+pd!!.password))
        }

        setResult(200)
        finish()
    }

    private fun createPassword(){
        val n = (48 .. 57)
        val lUpperCase = 65 .. 90
        val lLowerCase = 97 .. 122
        val c1 = 33 .. 47
        val c2 = 58 .. 64
        val c3 = 91 .. 96
        val c4 = 123 .. 126
        val array = arrayListOf<Int>()
        val arrayN = arrayListOf<Int>().apply {
            addAll(n)
        }
        val arrayL = arrayListOf<Int>().apply {
            addAll(lUpperCase)
            addAll(lLowerCase)
        }
        val arrayC = arrayListOf<Int>().apply {
            addAll(c1)
            addAll(c2)
            addAll(c3)
            addAll(c4)
        }

        if(number){
            array.addAll(n)
        }
        if(letter){
            array.addAll(lUpperCase)
            array.addAll(lLowerCase)
        }
        if(char){
            array.addAll(c1)
            array.addAll(c2)
            array.addAll(c3)
            array.addAll(c4)
        }
        array.shuffle()

        if(array.isEmpty()){
            acp_password.text = (0 until length).joinToString("") { "0" }
            return
        }

        val result = arrayListOf<Char>()

        if(number){
            result.add(arrayN[(Math.random()*arrayN.size).toInt()].toChar())
        }
        if(letter){
            result.add(arrayL[(Math.random()*arrayL.size).toInt()].toChar())
        }
        if(char){
            result.add(arrayC[(Math.random()*arrayC.size).toInt()].toChar())
        }
        val size = result.size
        (0 until (length - size)).forEach {
            result.add(array[(Math.random()*array.size).toInt()].toChar())
        }
        result.shuffle()
        acp_password.text = result.joinToString("") { it.toString() }
    }
}