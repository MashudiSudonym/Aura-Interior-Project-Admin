package c.m.aurainteriorprojectadmin.ui.main

import android.util.Log
import c.m.aurainteriorprojectadmin.model.CustomerResponse
import c.m.aurainteriorprojectadmin.util.base.BasePresenter
import com.google.firebase.database.*

class MainPresenter : BasePresenter<MainView> {
    private var mainView: MainView? = null
    private lateinit var databaseReference: DatabaseReference

    override fun onAttach(view: MainView) {
        mainView = view
    }

    override fun onDetach() {
        mainView = null
    }

    fun firebaseInit() {
        databaseReference = FirebaseDatabase.getInstance().reference
    }

    fun getCustomer() {
        mainView?.showLoading()
        databaseReference.child("customers")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Err!!", "Load Error : $databaseError", databaseError.toException())

                    mainView?.showNoDataResult()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val customerData = dataSnapshot.children.flatMap {
                        mutableListOf(it.getValue(CustomerResponse::class.java))
                    }

                    when (customerData.isEmpty()) {
                        true -> mainView?.showNoDataResult()
                        false -> {
                            mainView?.hideLoading()
                            mainView?.getCustomer(customerData as List<CustomerResponse>)
                        }
                    }
                }
            })
    }
}