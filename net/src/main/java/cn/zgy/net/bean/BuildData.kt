package cn.zgy.net.bean

import cn.zgy.net.ui.LoadingDialog
import java.io.File

/**
 *  请求必初始化的data
 */
data class BuildData(
    var body: HashMap<String, String> = hashMapOf(),
    var params: HashMap<String, String> = hashMapOf(),
    var url: String = "",
    var json: String = "",
    var file: File = File(""),
    var filePath: String = "",
    var fileNameKey: String = "file",
    var flag: String = "",
    var dialog: LoadingDialog? = null,
    var needBaseResponse: Boolean? = null,
    var tag: Any? = null




)