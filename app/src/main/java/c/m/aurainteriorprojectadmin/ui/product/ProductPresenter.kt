package c.m.aurainteriorprojectadmin.ui.product

import android.util.Log
import c.m.aurainteriorprojectadmin.model.WallpaperResponse
import c.m.aurainteriorprojectadmin.util.base.BasePresenter
import com.google.firebase.database.*

@Suppress("UNCHECKED_CAST")
class ProductPresenter : BasePresenter<ProductView> {
    private var productView: ProductView? = null
    private lateinit var databaseReference: DatabaseReference

    override fun onAttach(view: ProductView) {
        productView = view
    }

    override fun onDetach() {
        productView = null
    }

    fun firebaseInit() {
        databaseReference = FirebaseDatabase.getInstance().reference
    }

    fun getWallpaper() {
        productView?.showLoading()
        databaseReference.child("wallpapers")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Err!!", "Load Error : $databaseError", databaseError.toException())

                    productView?.showNoDataResult()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val wallpaperData = dataSnapshot.children.flatMap {
                        mutableListOf(it.getValue(WallpaperResponse::class.java))
                    }

                    when (wallpaperData.isEmpty()) {
                        true -> productView?.showNoDataResult()
                        false -> {
                            productView?.hideLoading()
                            productView?.getWallpaper(wallpaperData as List<WallpaperResponse>)
                        }
                    }
                }
            })
    }
}