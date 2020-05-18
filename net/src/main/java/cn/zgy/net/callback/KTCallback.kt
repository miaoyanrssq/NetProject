package cn.zgy.net.callback

import com.google.gson.reflect.TypeToken
import com.stormkid.okhttpkt.rule.CallbackRule
import com.stormkid.okhttpkt.utils.CallbackNeed
import com.stormkid.okhttpkt.utils.GsonFactory
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
        CoroutineScope(Dispatchers.Main).launch { callbackRule.onFailed(need.err_msg) }
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
                        val result = GsonFactory.format<BaseResponse<T>>(body,
                            object : TypeToken<BaseResponse<T>>() {}.type
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
        call.cancel()
    }


}