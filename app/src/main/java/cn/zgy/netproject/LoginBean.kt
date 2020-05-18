package cn.zgy.netproject

import java.io.Serializable

data class LoginBean(var user: LoginUser? = null) : Serializable

data class LoginUser(var userName: String? = null,
                     var trueName: String? = null,
                     var email: String? = null,
                     var tel: String? = null,
                     var actived: Boolean = false,
                     var mobile: String? = null,
                     var tenantId: Long = 0,
                     var roleId: Long = 0,
                     var platformId: Long = 0,
                     var userId: Long = 0,
                     var status: Long = 0,
                     var activeStatus: Long = 0,
                     var id: Long = 0,
                     var createdBy: String? = null,
                     var updatedBy: String? = null,
                     var isMcAdmin: Int = 0,
                     var productes: List<Product>? = null,
                     var organizations: List<Organization>? = null) : Serializable

data class Product(var id: Long = 0,
                   var siteName: String? = null): Serializable

data class Organization(var id: Long = 0,
                        var name: String? = null): Serializable