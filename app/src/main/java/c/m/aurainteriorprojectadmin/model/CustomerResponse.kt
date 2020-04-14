package c.m.aurainteriorprojectadmin.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CustomerResponse(
    var uid: String? = "",
    var name: String? = "",
    var address: String? = "",
    var phone: String? = "0",
    var latitude: Double? = 0.0,
    var longitude: Double? = 0.0,
    var typeWallpaperOrder: String? = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "name" to name,
        "address" to address,
        "phone" to phone,
        "latitude" to latitude,
        "longitude" to longitude,
        "typeWallpaperOrder" to typeWallpaperOrder
    )
}