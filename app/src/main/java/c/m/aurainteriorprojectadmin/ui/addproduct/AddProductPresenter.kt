package c.m.aurainteriorprojectadmin.ui.addproduct

import android.net.Uri
import android.util.Log
import c.m.aurainteriorprojectadmin.model.WallpaperResponse
import c.m.aurainteriorprojectadmin.util.base.BasePresenter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.math.ceil

class AddProductPresenter : BasePresenter<AddProductView> {
    private var addProductView: AddProductView? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var uid: String = ""

    override fun onAttach(view: AddProductView) {
        addProductView = view
    }

    override fun onDetach() {
        addProductView = null
    }

    fun firebaseInit() {
        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference
        uid = databaseReference.child("customers").push().key.toString()
    }

    fun uploadUserPhoto(filePath: Uri?) {
        val imageReference = storageReference.child("image-wallpaper/$uid")

        // Photo Profile Upload
        if (filePath != null) {
            addProductView?.showProgressDialog()

            imageReference.putFile(filePath)
                .addOnSuccessListener {
                    addProductView?.closeProgressDialog()
                }
                .addOnFailureListener {
                    addProductView?.closeProgressDialog()
                }
                .addOnProgressListener {
                    val progress = 100.0 * it.bytesTransferred / it.totalByteCount

                    addProductView?.progressDialogMessage("Uploaded ${ceil(progress)} %...")
                }
        }
    }

    fun sendData(typeWallpaper: String) {
        val imageReference = storageReference.child("image-wallpaper/$uid")

        imageReference.downloadUrl.addOnSuccessListener {
            addProductView?.showProgressDialog()

            val wallpaperData = WallpaperResponse(
                uid,
                typeWallpaper,
                (it?.toString() ?: "-")
            )

            databaseReference.child("wallpapers")
                .child(uid)
                .setValue(wallpaperData)
                .addOnSuccessListener {
                    addProductView?.progressDialogMessage("Proses upload...")
                    addProductView?.closeProgressDialog()
                    addProductView?.backToMainActivity()
                }
                .addOnFailureListener { e ->
                    addProductView?.progressDialogMessage(e.toString())
                    addProductView?.closeProgressDialog()
                    Log.e("ERROR!!", "$e")
                }
        }
    }
}