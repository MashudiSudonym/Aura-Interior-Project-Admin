package c.m.aurainteriorprojectadmin.model

data class OrderSqlite(
    var id: Long? = 0,
    var uid: String? = "",
    var name: String? = "",
    var address: String? = "",
    var phone: String? = "",
    var latitude: String? = "",
    var longitude: String? = "",
    var typeWallpaperOrder: String? = "",
    var priceEstimation: String? = "",
    var rollEstimation: String? = "",
    var customerUID: String? = "",
    var orderStatus: String? = "",
    var orderDate: String? = ""
) {
    companion object {
        const val TABLE_ORDER: String = "TABLE_ORDER"
        const val ID: String = "_ID"
        const val UID: String = "UID"
        const val NAME: String = "NAME"
        const val ADDRESS: String = "ADDRESS"
        const val PHONE: String = "PHONE"
        const val LATITUDE: String = "LATITUDE"
        const val LONGITUDE: String = "LONGITUDE"
        const val TYPE_WALLPAPER: String = "TYPE_WALLPAPER"
        const val PRICE_ESTIMATION: String = "PRICE_ESTIMATION"
        const val ROLL_ESTIMATION: String = "ROLL_ESTIMATION"
        const val CUSTOMER_UID: String = "CUSTOMER_UID"
        const val ORDER_STATUS: String = "ORDER_STATUS"
        const val ORDER_DATE: String = "ORDER_DATE"
    }
}