package c.m.aurainteriorprojectadmin.model

data class OrderExport(
    var id: Long? = 0,
    var name: String? = "",
    var address: String? = "",
    var phone: String? = "",
    var typeWallpaperOrder: String? = "",
    var orderDate: String? = ""
)