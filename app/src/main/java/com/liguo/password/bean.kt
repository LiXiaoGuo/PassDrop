package com.liguo.password

import com.linxiao.framework.util.logi
import java.io.Serializable


/**
 *
 * @author Extends
 * @date 2019/9/11/011
 */

/**
 * 密码实体类
 */
data class PasswordBean(
    val id : Int? = null,
    var name : String? = null,
    var typeId : Int? = null,
    var password : String? = null,
    var account : String? = null,
    var oldPassword : String? = null,
    var time:Long? = 0
):Serializable{
    companion object{
        fun parsePassword(map:Map<String,Any?>):PasswordBean{
            val mId = (map["id"].toString().toIntOrNull()) ?: -1
            return PasswordBean(mId,map["name"]?.toString(),(map["typeId"].toString().toIntOrNull()) ?: -1,
                map["password"]?.toString(),map["account"]?.toString(),map["oldPassword"]?.toString(),(map["time"].toString().toLongOrNull()) ?: 0L)
        }
    }

    var visibility = false

    fun getSafePassword() = (0 until (password?.length?:1)).joinToString("") { "*" }

    fun getHistoryPassword() = oldPassword?.split(" ")?.joinToString("\n") { it }?:""

}

