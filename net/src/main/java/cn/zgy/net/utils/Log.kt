package cn.zgy.net.utils

import android.util.Log
import cn.zgy.net.BuildConfig

/**
自定义logger
@author ke_li
@date 2019/6/14
 */
object Log {
    private const val APP_LOGGER = "APP_LOGGER"
    // 默认log 显示跟随系统 如果是debug 环境，那么就显示log ，如果不是debug 环境，那么就隐藏log
    private var isEnable = BuildConfig.LOG_SHOW

    fun setEnable(isEnable: Boolean){
        cn.zgy.net.utils.Log.isEnable = isEnable
    }

    fun init(isEnable: Boolean){
        cn.zgy.net.utils.Log.isEnable = isEnable
    }

    fun w(msg: Any) {
        if (isEnable)
            Log.w(APP_LOGGER, msg.toString())
    }

    fun d(msg: Any) {
        if (isEnable)
            Log.d(APP_LOGGER, msg.toString())
    }

    fun e(msg: Any) {
        if (isEnable)
            Log.e(APP_LOGGER, msg.toString())
    }

    fun i(msg: Any) {
        if (isEnable)
            Log.i(APP_LOGGER, msg.toString())
    }

    fun i(key:String ,msg: Any) {
        if (isEnable)
            Log.i(key, msg.toString())
    }

    fun e(key:String, msg: Any) {
        if (isEnable)
            Log.e(key, msg.toString())
    }

    fun d(key:String, msg: Any) {
        if (isEnable)
            Log.d(key, msg.toString())
    }

    fun w(key:String, msg: Any) {
        if (isEnable)
            Log.w(key, msg.toString())
    }
}