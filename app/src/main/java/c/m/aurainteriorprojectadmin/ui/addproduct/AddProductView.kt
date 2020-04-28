package c.m.aurainteriorprojectadmin.ui.addproduct

import c.m.aurainteriorprojectadmin.util.base.BaseView

interface AddProductView : BaseView {
    fun progressDialogMessage(message: String)
    fun showProgressDialog()
    fun closeProgressDialog()
    fun backToMainActivity()
}