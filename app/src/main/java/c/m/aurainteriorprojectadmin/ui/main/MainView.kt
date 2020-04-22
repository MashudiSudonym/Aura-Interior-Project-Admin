package c.m.aurainteriorprojectadmin.ui.main

import c.m.aurainteriorprojectadmin.model.OrderResponse
import c.m.aurainteriorprojectadmin.util.base.BaseView

interface MainView : BaseView {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getOrders(customerData: List<OrderResponse>)
}