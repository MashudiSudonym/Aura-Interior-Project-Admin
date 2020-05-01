package c.m.aurainteriorprojectadmin.ui.cluster

import android.util.Log
import c.m.aurainteriorprojectadmin.model.OrderResponse
import c.m.aurainteriorprojectadmin.util.base.BasePresenter
import com.google.firebase.database.*

@Suppress("UNCHECKED_CAST")
class ClusterPresenter : BasePresenter<ClusterView> {
    private var clusterView: ClusterView? = null
    private lateinit var databaseReference: DatabaseReference

    override fun onAttach(view: ClusterView) {
        clusterView = view
    }

    override fun onDetach() {
        clusterView = null
    }

    fun firebaseInit() {
        databaseReference = FirebaseDatabase.getInstance().reference
    }

    fun getOrder() {
        databaseReference.child("orders")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Err!!", "Load Error : $databaseError", databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val orderData = dataSnapshot.children.flatMap {
                        mutableListOf(it.getValue(OrderResponse::class.java))
                    }

                    clusterView?.getCustomer(orderData as List<OrderResponse>)
                }
            })
    }
}