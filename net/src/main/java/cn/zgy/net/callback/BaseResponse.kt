package cn.zgy.net.callback

data class BaseResponse<T>(var code: Int = -1,var message: String? = null, var data: T? = null)