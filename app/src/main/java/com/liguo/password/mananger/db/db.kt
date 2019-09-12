package yz.yuzhua.yidian51.mananger.db

import android.content.Context
import com.liguo.password.mananger.db.PasswordDatabaseOpenHelper

/**
 *
 * @author Extends
 * @date 2019/8/13/013
 */

/**
 * 历史浏览数据库
 */
val Context.passwordDatabase: PasswordDatabaseOpenHelper
     get() = PasswordDatabaseOpenHelper.getInstance(applicationContext)