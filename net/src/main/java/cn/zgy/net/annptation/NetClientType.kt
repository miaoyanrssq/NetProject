package cn.zgy.net.annptation

import androidx.annotation.StringDef
/**
  *
  * @Description:     请求类型
  * @Author:         zhengy
  * @CreateDate:     2020/5/12 上午9:28
  * @Version:        1.0
 */

@StringDef(NetClientType.HTTP_TYPE, NetClientType.HTTPS_TYPE,  NetClientType.COMMOM_TYPE)
@Retention(AnnotationRetention.SOURCE)
annotation class NetClientType {
    companion object{

        /**
         * 获取http请求的OkHttpclient对象
         */
        const val HTTP_TYPE = "HTTP"
        /**
         * 获取https请求的OkHttpclient对象
         */
        const val HTTPS_TYPE = "HTTPS"

        /**
         * 获取自定义OkHttpclient对象
         */
        const val COMMOM_TYPE = "COMMOM_TYPE"
    }
}