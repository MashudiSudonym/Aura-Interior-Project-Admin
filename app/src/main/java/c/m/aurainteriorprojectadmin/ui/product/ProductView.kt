package c.m.aurainteriorprojectadmin.ui.product

import c.m.aurainteriorprojectadmin.model.WallpaperResponse
import c.m.aurainteriorprojectadmin.util.base.BaseView

interface ProductView : BaseView {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getWallpaper(wallpaperData: List<WallpaperResponse>)
}