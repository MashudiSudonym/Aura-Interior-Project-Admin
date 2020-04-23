package c.m.aurainteriorprojectadmin.ui.detail

import android.util.Log
import c.m.aurainteriorprojectadmin.model.MessageNotification
import c.m.aurainteriorprojectadmin.model.Notification
import c.m.aurainteriorprojectadmin.model.OrderResponse
import c.m.aurainteriorprojectadmin.util.base.BasePresenter
import c.m.aurainteriorprojectadmin.util.webservice.ApiInterface
import c.m.aurainteriorprojectadmin.util.webservice.RetrofitService
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailPresenter : BasePresenter<DetailView> {
    private var detailView: DetailView? = null
    private lateinit var databaseReference: DatabaseReference
    private val apiService =
        RetrofitService.getInstance("https://fcm.googleapis.com/").create(ApiInterface::class.java)

    override fun onAttach(view: DetailView) {
        detailView = view
    }

    override fun onDetach() {
        detailView = null
    }

    fun firebaseInit() {
        databaseReference = FirebaseDatabase.getInstance().reference
    }

    fun getOrder(uid: String) {
        databaseReference.child("orders")
            .orderByChild("uid")
            .equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Err!!", "Load Error : $databaseError", databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val orderData = dataSnapshot.children.flatMap {
                        mutableListOf(it.getValue(OrderResponse::class.java))
                    }

                    orderData.forEach { result ->
                        detailView?.getOrder(
                            result?.name.toString(),
                            result?.address.toString(),
                            result?.phone.toString(),
                            result?.latitude as Double,
                            result.longitude as Double,
                            result.typeWallpaperOrder.toString(),
                            result.priceEstimation.toString(),
                            result.rollEstimation.toString(),
                            result.customerUID.toString(),
                            result.orderStatus as Int,
                            result.orderDate.toString()
                        )
                    }
                }
            })
    }

    fun updateOrderStatus(
        uid: String,
        name: String,
        address: String,
        phone: String,
        latitude: Double,
        longitude: Double,
        typeWallpaper: String,
        priceEstimation: String,
        rollEstimation: String,
        customerUID: String,
        statusOrder: Int,
        dateOrder: String
    ) {
        val orderData = OrderResponse(
            uid,
            name,
            address,
            phone,
            latitude,
            longitude,
            typeWallpaper,
            priceEstimation,
            rollEstimation,
            customerUID,
            statusOrder,
            dateOrder
        ).toMap()

        databaseReference.child("orders/$uid").updateChildren(orderData)
    }

    fun sendNotification(
        customerUID: String,
        orderTitleMessage: String,
        orderBodyMessage: String
    ) {
        val messageNotification = MessageNotification(
            "/topics/$customerUID",
            Notification(orderBodyMessage, orderTitleMessage)
        )

        CoroutineScope(Dispatchers.IO).launch {
            apiService.postMessage(messageNotification)
        }
    }
}