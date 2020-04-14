package c.m.aurainteriorprojectadmin.ui.main

import c.m.aurainteriorprojectadmin.model.CustomerResponse
import c.m.aurainteriorprojectadmin.util.base.BaseView

interface MainView : BaseView {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getCustomer(customerData: List<CustomerResponse>)
}