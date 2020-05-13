package cn.zgy.net.annptation

import androidx.annotation.StringDef
/**
  *
  * @Description:     对象模式
  * @Author:         zhengy
  * @CreateDate:     2020/5/12 上午9:21
  * @Version:        1.0
 */

@StringDef(Client.FACTORY_CLIENT, Client.SINGLE_CLIENT)
@Retention(AnnotationRetention.SOURCE)
annotation class Client {

    companion object{
        /**
         * 获取单例对象
         */
        const val SINGLE_CLIENT = "SINGLE_CLIENT"
        /**
         * 获取工厂对象
         */
        const val FACTORY_CLIENT = "FACTORY_CLIENT"
    }
}