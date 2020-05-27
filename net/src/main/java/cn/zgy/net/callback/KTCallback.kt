package cn.zgy.net.callback

import cn.zgy.net.manager.CallManager
import cn.zgy.net.utils.GenericUtils
import com.google.gson.reflect.TypeToken
import cn.zgy.net.rule.CallbackRule
import cn.zgy.net.utils.CallbackNeed
import cn.zgy.net.utils.GsonFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.ParameterizedType


/**
 *
 * @Description:    请求回调处理
 * @Author:         zhengy
 * @CreateDate:     2020/5/11 下午4:02
 * @Version:        1.0
 */

open class KTCallback<T>(private val callbackRule: CallbackRule<T>, private val need: CallbackNeed) :
    Callback {
    override fun onFailure(call: Call, e: IOException) {
        if(call.isCanceled()){
            CoroutineScope(Dispatchers.Main).launch {
                callbackRule.onCancel()
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            callbackRule.onFailed(need.err_msg)
        }
        need.dialog?.dismiss()
        call.cancel()
        CallManager.removeCall(need.tag, call)
    }

    override fun onResponse(call: Call, response: Response) {
        if (response.isSuccessful) {
            if (null == response.body) {
                CoroutineScope(Dispatchers.Main).launch {
                    callbackRule.onFailed(
                        need.err_msg
                    )
                }
                return
            } else {
                val body = response.body?.string() ?: ""
                try {

                    if(!need.needBase) {
                        val interfacesTypes = callbackRule.javaClass.genericInterfaces[0]
                        val resultType = (interfacesTypes as ParameterizedType).actualTypeArguments
                        val result = GsonFactory.format<T>(body, resultType[0])
                        CoroutineScope(Dispatchers.Main).launch {
                            callbackRule.onSuccess(
                                result,
                                need.flag
                            )
                        }
                    }else{
//                        val result = GsonFactory.format<BaseResponse<T>>(body,
//                            object : TypeToken<BaseResponse<T>>() {}.type
//                        )
                        /**
                         * 直接使用上面注释方法，获取BaseResponse<T>的type，无法获取到T的类型，解析后返回的是LinkedTreeMap，
                         * 通过TypeToken.getParameterized(BaseResponse<T>()::class.java, GenericUtils.getGenericType(callbackRule.javaClass)).type
                         * 来确定T的类型，然后解析，才能得到正确的数据结构
                         */
                        val result = GsonFactory.format<BaseResponse<T>>(body,
                            TypeToken.getParameterized(BaseResponse<T>()::class.java, GenericUtils.getGenericType(callbackRule.javaClass)).type
                        )
                        if(result.code == 0 && null != result.data){
                            CoroutineScope(Dispatchers.Main).launch {
                                callbackRule.onSuccess(
                                    result.data!!,
                                    need.flag
                                )
                            }
                        }else{
                            CoroutineScope(Dispatchers.Main).launch {
                                callbackRule.onFailed(
                                    result.msg ?: "数据服务异常，请联系管理员"
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    CoroutineScope(Dispatchers.Main).launch {
                        callbackRule.onFailed(
                            "数据服务异常，请联系管理员"
                        )
                        need.dialog?.dismiss()
                    }
                    return
                }

            }
        } else {
            CoroutineScope(Dispatchers.Main).launch{
                callbackRule.onFailed(
                    response.message
                )
            }
        }
        need.dialog?.dismiss()
        call.cancel()
        CallManager.removeCall(need.tag, call)
    }


}