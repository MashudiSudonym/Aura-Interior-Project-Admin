package c.m.aurainteriorprojectadmin.ui.cluster

import c.m.aurainteriorprojectadmin.model.CustomerResponse
import c.m.aurainteriorprojectadmin.util.base.BaseView

interface ClusterView : BaseView {
    fun getCustomer(customerData: List<CustomerResponse>)
}