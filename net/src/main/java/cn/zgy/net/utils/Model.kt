package com.stormkid.okhttpkt.utils

import android.content.Context
import cn.zgy.net.ui.LoadingDialog

/**
请求传参
@author ke_li
@date 2018/5/25
 */
data class CallbackNeed(val flag: String, val err_msg: String, val needBase: Boolean, val dialog: LoadingDialog?)

data class FileCallbackNeed(val selfPath: String, val context: Context, var initTotal: Long)
