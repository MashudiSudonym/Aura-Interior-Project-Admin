package c.m.aurainteriorprojectadmin.ui.main

import android.util.Log
import c.m.aurainteriorprojectadmin.model.OrderResponse
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

    @Suppress("UNCHECKED_CAST")
    fun getOrder() {
        mainView?.showLoading()
        databaseReference.child("orders")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Err!!", "Load Error : $databaseError", databaseError.toException())

                    mainView?.showNoDataResult()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val orderData = dataSnapshot.children.flatMap {
                        mutableListOf(it.getValue(OrderResponse::class.java))
                    }

                    when (orderData.isEmpty()) {
                        true -> mainView?.showNoDataResult()
                        false -> {
                            mainView?.hideLoading()
                            mainView?.getOrders(orderData as List<OrderResponse>)
                        }
                    }
                }
            })
    }
}