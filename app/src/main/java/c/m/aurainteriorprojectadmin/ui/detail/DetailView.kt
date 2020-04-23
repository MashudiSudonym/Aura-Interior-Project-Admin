package c.m.aurainteriorprojectadmin.ui.detail

import c.m.aurainteriorprojectadmin.util.base.BaseView

interface DetailView : BaseView {
    fun getOrder(
        name: String,
        address: String,
        phone: String,
        latitude: Double,
        longitude: Double,
        typeWallpaper: String,
        priceEstimation: String,
        rollEstimation: String,
        customerUID: String,
        statusOrder: Int,
        dateOrder: String
    )
}