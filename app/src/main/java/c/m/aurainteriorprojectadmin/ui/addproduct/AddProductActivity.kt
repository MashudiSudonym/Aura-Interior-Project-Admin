@file:Suppress("DEPRECATION")

package c.m.aurainteriorprojectadmin.ui.addproduct

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import c.m.aurainteriorprojectadmin.R
import c.m.aurainteriorprojectadmin.util.Constants
import coil.api.load
import com.github.babedev.dexter.dsl.runtimePermission
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class AddProductActivity : AppCompatActivity(), AddProductView {

    private lateinit var presenter: AddProductPresenter
    private lateinit var progressDialog: ProgressDialog
    private var filePath: Uri? = null
    private var imageFilePath: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = AddProductPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.firebaseInit()

        supportActionBar?.apply {
            title = getString(R.string.add_product)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        // request permission
        validatePermission()

        btn_select_image.setOnClickListener {
            alertSelectImage()
        }

        // get type text
        val typeWallpaper = edt_type_wallpaper.text

        btn_save.setOnClickListener {
            savingAlert(typeWallpaper)
        }
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onDestroy() {
        onDetachView()
        super.onDestroy()
    }

    override fun progressDialogMessage(message: String) {
        progressDialog.setMessage(message)
    }

    override fun showProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.apply {
            title = getString(R.string.upload_data_title)
            setCancelable(false)
            show()
        }
    }

    override fun closeProgressDialog() {
        progressDialog.dismiss()
    }

    override fun backToMainActivity() {
        onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun validatePermission() {
        runtimePermission {
            permissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) {
                checked { }
            }
        }
    }

    private fun showFileChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, Constants.PICK_PHOTO_CODE)
        }
    }

    private fun takePhoto() {
        try {
            // Temporary for preview image/bitmap not save to local storage (internal / external)
            val imageFile = createImageFile()
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (callCameraIntent.resolveActivity(packageManager) != null) {
                val authorities = "$packageName.fileprovider"

                filePath = FileProvider.getUriForFile(
                    this,
                    authorities, imageFile
                )
                callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath)
                startActivityForResult(callCameraIntent, Constants.CAMERA_REQUEST_CODE)
            }
        } catch (e: IOException) {
            toast("Could not create file")
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = "JPEG_" + timeStamp + "_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)

        if (storageDir?.exists() == false) storageDir.mkdirs()
        imageFilePath = imageFile.absolutePath

        return imageFile
    }

    private fun setScaledBitmap(): Bitmap {
        val imageViewWidth = img_photo_product.width
        val imageViewHeight = img_photo_product.height

        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFilePath, bmOptions)
        val bitmapWidth = bmOptions.outWidth
        val bitmapHeight = bmOptions.outHeight

        val scaleFactor = min(bitmapWidth.div(imageViewWidth), bitmapHeight.div(imageViewHeight))

        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        return BitmapFactory.decodeFile(imageFilePath, bmOptions)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    img_photo_product.load(setScaledBitmap()) {
                        crossfade(true)
                        placeholder(R.drawable.ic_photo_size_select_actual_black_24dp)
                        error(R.drawable.ic_broken_image)
                    }

                    // Upload to Storage
                    presenter.uploadUserPhoto(filePath)
                }
            }
            Constants.PICK_PHOTO_CODE -> {
                if (data != null) {
                    filePath = data.data

                    val selectedImage =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, filePath)

                    img_photo_product.load(selectedImage) {
                        crossfade(true)
                        placeholder(R.drawable.ic_photo_size_select_actual_black_24dp)
                        error(R.drawable.ic_broken_image)
                    }

                    // Upload to Storage
                    presenter.uploadUserPhoto(filePath)
                }
            }
            else -> toast("Unrecognized request code")
        }
    }

    private fun savingAlert(typeWallpaper: Editable) {
        if (filePath != null) {
            alert(getString(R.string.check_data_message), getString(R.string.warning)) {
                yesButton {
                    presenter.sendData(typeWallpaper.toString())
                }
                noButton { }
            }.apply {
                isCancelable = false
                show()
            }
        } else {
            alert(getString(R.string.select_wallpaper_image), getString(R.string.alert)) {
                yesButton {}
            }.apply {
                isCancelable = false
                show()
            }
        }
    }

    private fun alertSelectImage() {
        alert(getString(R.string.take_or_choose)) {
            positiveButton(getString(R.string.choose_from_gallery)) {
                // call File Manager or Gallery Internal / External Storage
                GlobalScope.launch(Dispatchers.IO) {
                    showFileChooser()
                }
            }
            negativeButton(getString(R.string.take_from_camera)) {
                // call camera intent
                GlobalScope.launch(Dispatchers.IO) {
                    takePhoto()
                }
            }
        }.show()
    }
}
