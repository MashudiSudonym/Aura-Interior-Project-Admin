package c.m.aurainteriorprojectadmin.ui.add

import android.util.Log
import c.m.aurainteriorprojectadmin.model.OrderResponse
import c.m.aurainteriorprojectadmin.util.base.BasePresenter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddPresenter : BasePresenter<AddView> {
    private var addView: AddView? = null
    private lateinit var databaseReference: DatabaseReference

    override fun onAttach(view: AddView) {
        addView = view
    }

    override fun onDetach() {
        addView = null
    }

    fun firebaseInit() {
        databaseReference = FirebaseDatabase.getInstance().reference
    }

    fun sendData(
        name: String,
        address: String,
        phone: String,
        latitude: Double,
        longitude: Double,
        type: String
    ) {
        val uid = databaseReference.child("customers").push().key
        val customerData = OrderResponse(uid, name, address, phone, latitude, longitude, type)

        databaseReference.child("customers")
            .child(uid.toString())
            .setValue(customerData)
            .addOnSuccessListener {
                addView?.backToMainActivity()
            }
            .addOnFailureListener { e ->
                Log.e("ERROR!!", "$e")
            }
    }
}