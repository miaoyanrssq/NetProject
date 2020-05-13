package cn.zgy.net.callback

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import cn.zgy.net.KTHttp
import com.stormkid.okhttpkt.rule.DownLoadRule


/**
  *
  * @Description:     下载回调
  * @Author:         zhengy
  * @CreateDate:     2020/5/12 上午10:38
  * @Version:        1.0
 */

class DownloadCallback(private val downLoadRule: DownLoadRule) : BroadcastReceiver() {

    companion object {
        val filter by lazy { IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE) }
    }

    private var ID = -1L

    fun initId(downId: Long) {
        this.ID = downId
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        ID = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0) ?: -1L
        if (ID != -1L) {
            val manager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uriForDownloadedFile = manager.getUriForDownloadedFile(ID)
            downLoadRule.onFinished(uriForDownloadedFile,this)
        } else {
            KTHttp.instance.checkId(ID, context!!)
        }
    }
}