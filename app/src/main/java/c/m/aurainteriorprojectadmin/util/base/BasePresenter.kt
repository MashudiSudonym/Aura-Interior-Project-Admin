package c.m.aurainteriorprojectadmin.util.base

interface BasePresenter<T : BaseView> {
    fun onAttach(view: T)

    fun onDetach()
}