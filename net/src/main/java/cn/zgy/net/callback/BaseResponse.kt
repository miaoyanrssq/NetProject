package cn.zgy.net.callback

data class BaseResponse<T>(var code: Int = -1,var msg: String? = null, var data: T? = null)