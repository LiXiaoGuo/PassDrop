package com.liguo.password.mananger.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.liguo.password.PasswordBean
import com.linxiao.framework.util.loge
import com.linxiao.framework.util.logi
import org.jetbrains.anko.db.*
import java.lang.Exception

/**
 * 密码数据库Helper
 * @author Extends
 * @date 2019/8/12/012
 */
class PasswordDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "Password", null, 1) {
    //上下文，数据库名，数据库工厂，版本号
    companion object{
        const val TABLE_NAME = "Password"
        private var instance: PasswordDatabaseOpenHelper? = null
        @Synchronized
        fun getInstance(ctx: Context): PasswordDatabaseOpenHelper {
            if (instance == null) {
                instance = PasswordDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {        // Here you create tables
        db?.createTable(TABLE_NAME, false,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT, //id
                "name" to TEXT,   //备注，是哪里的密码
                "typeId" to INTEGER,     //预留参数，类型，如：游戏，娱乐，社交等等
                "password" to TEXT,   //加密后的密码
                "account" to TEXT,     //账号，允许不输入
                "oldPassword" to TEXT,  //历史密码，以空格分割
                "time" to INTEGER       //密码更新的时间
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //数据库升级示例代码
//        if (newVersion == 2) {
//            if (oldVersion < newVersion) {
//                ai("newVersion--$newVersion")
//                db.execSQL("  ALTER TABLE Person RENAME TO temp_person")
//                db?.createTable("Person", true, "id" to INTEGER + PRIMARY_KEY + UNIQUE, "name" to TEXT, "age" to INTEGER, "address" to TEXT, "sex" to INTEGER)
//                db.execSQL("INSERT INTO Person SELECT id, name, age,address,'' FROM temp_person")
//                db.dropTable("temp_person")
//            }
//        }
    }

    /**
     * 添加历史记录
     */
    fun addData(pb : PasswordBean){
        //保存到历史记录
        use{
            //插入数据
            insert(TABLE_NAME,
                "name" to (pb.name?:""),
                "typeId" to (pb.typeId?:0),
                "password" to (pb.password?:""),
                "account" to (pb.account?:""),
                "oldPassword" to (pb.oldPassword?:""),
                "time" to System.currentTimeMillis()
            )
        }
    }

    /**
     * 导入数据
     */
    fun importData(pb : PasswordBean):Boolean{
        var result = false
        use {
            val r = select(TABLE_NAME).whereSimple("(name = ?) and (account = ?) and (password = ?)", pb.name?:"",pb.account?:"",pb.password?:"").parseOpt(object : MapRowParser<Map<String,Any?>>{
                override fun parseRow(columns: Map<String, Any?>): Map<String, Any?> {
                    return columns
                }
            })
            if(r == null){
                insert(TABLE_NAME,
                    "name" to (pb.name?:""),
                    "typeId" to (pb.typeId?:0),
                    "password" to (pb.password?:""),
                    "account" to (pb.account?:""),
                    "oldPassword" to (pb.oldPassword?:""),
                    "time" to System.currentTimeMillis()
                )
                result = true
            }
        }
        return result
    }

    /**
     * 修改密码实体类
     */
    fun updateData(pb : PasswordBean){
        try {
            if(pb.id == null) throw Exception("id is not null")
            val sql = buildString {
                if(pb.typeId != null){
                    append(" , typeId = ${pb.typeId} ")
                }
                if(pb.name != null){
                    append(" , name = '${pb.name}' ")
                }
                if(pb.password != null){
                    append(" , password = '${pb.password}' ")
                }
                if(pb.account != null){
                    append(" , account = '${pb.account}' ")
                }
                if(pb.oldPassword != null){
                    append(" , oldPassword = '${pb.oldPassword}' ")
                }
            }
            use {
                //修改数据
                execSQL("update $TABLE_NAME set time = ${System.currentTimeMillis()} $sql where id = ${pb.id}")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    /**
     * 获取全部数据
     */
    fun getAllData() = use {
        select(TABLE_NAME).parseList(object : MapRowParser<PasswordBean>{
            override fun parseRow(columns: Map<String, Any?>): PasswordBean {
                columns.loge()
                return PasswordBean.parsePassword(columns)
            }
        })
    }

    fun delectDataFormId(id:Int){
        use {
            val sql = "delete from $TABLE_NAME where id = $id"
            sql.logi()
            execSQL(sql)
        }
    }

    /**
     * 清空数据库
     */
    fun delectTable(){
        use {
            delete(TABLE_NAME)
        }
    }
}