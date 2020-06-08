package cn.zgy.net.callback

import cn.zgy.net.manager.CallManager
import cn.zgy.net.rule.StringCallback
import cn.zgy.net.utils.CallbackNeed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
/**
  *
  * @Description:     回调处理，直接返回String格式的数据，不解析
  * @Author:         zhengy
  * @CreateDate:     2020/5/11 下午4:25
  * @Version:        1.0
 */

class KTStringCallback(private val callbackRule: StringCallback, private val need: CallbackNeed) : Callback {
    override fun onFailure(call: Call, e: IOException) {
        if(call.isCanceled()){
            CoroutineScope(Dispatchers.Main).launch {
                callbackRule.onCancel()
            }
        }
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
                CoroutineScope(Dispatchers.Main).launch {
                    callbackRule.onSuccess(
                        body,
                        need.flag
                    )

                }
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch(Dispatchers.Main) {
                callbackRule.onFailed(
                    response.message
                )
            }
        }
        call.cancel()
        CallManager.removeCall(need.tag, call)
    }
}