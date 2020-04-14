package c.m.aurainteriorprojectadmin.ui.cluster

import android.util.Log
import c.m.aurainteriorprojectadmin.model.CustomerResponse
import c.m.aurainteriorprojectadmin.ui.main.MainView
import c.m.aurainteriorprojectadmin.util.base.BasePresenter
import com.google.firebase.database.*

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

    fun getCustomer() {
        databaseReference.child("customers")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Err!!", "Load Error : $databaseError", databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val customerData = dataSnapshot.children.flatMap {
                        mutableListOf(it.getValue(CustomerResponse::class.java))
                    }

                    clusterView?.getCustomer(customerData as List<CustomerResponse>)
                }
            })
    }
}