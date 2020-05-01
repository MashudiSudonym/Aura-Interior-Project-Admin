package c.m.aurainteriorprojectadmin.ui.detailproduct

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.aurainteriorprojectadmin.R
import c.m.aurainteriorprojectadmin.util.Constants
import coil.api.load
import com.ceylonlabs.imageviewpopup.ImagePopup
import kotlinx.android.synthetic.main.activity_detail_product.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class DetailProductActivity : AppCompatActivity() {

    private var uid: String? = ""
    private var image: String? = ""
    private var typeWallpaper: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)

        supportActionBar?.apply {
            title = getString(R.string.detail_product)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val intent = intent
        uid = intent.getStringExtra(Constants.UID)
        image = intent.getStringExtra(Constants.IMAGE)
        typeWallpaper = intent.getStringExtra(Constants.TYPE)

        // show image wallpaper
        img_photo_product.load(image) {
            crossfade(true)
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }

        // full screen view for image wallpaper
        ImagePopup(this).apply {
            isImageOnClickClose = true
            isHideCloseIcon = true
            isFullScreen = true
            initiatePopupWithGlide(image)
            img_photo_product.onClick { viewPopup() }
        }

        tv_type_wallpaper.text = typeWallpaper
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
