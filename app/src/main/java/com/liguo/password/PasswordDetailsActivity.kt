package com.liguo.password

import android.graphics.Color
import android.os.Bundle
import com.liguo.password.databinding.ActivityPasswordDetailsBinding
import com.linxiao.framework.activity.BaseActivity
import com.linxiao.framework.util.getDataBinding
import kotlinx.android.synthetic.main.activity_password_details.*

/**
 *
 * @author Extends
 * @date 2019/9/12/012
 */
class PasswordDetailsActivity:BaseActivity() {

    private val binding by lazy { getDataBinding<ActivityPasswordDetailsBinding>(R.layout.activity_password_details) }

    private val pd by lazy { intent.getSerializableExtra("data") as PasswordBean }

    override fun onCreateRootView() = binding.root

    override fun onInitView(savedInstanceState: Bundle?) {
        changeStatusBarColor(Color.TRANSPARENT,false)
        binding.data = pd
    }

    override fun initListener() {
        apd_title.setOnLeftClick { finish() }
    }

    override fun initData() {
    }
}