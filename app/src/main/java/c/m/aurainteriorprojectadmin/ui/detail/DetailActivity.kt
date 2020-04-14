package c.m.aurainteriorprojectadmin.ui.detail

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import c.m.aurainteriorprojectadmin.R
import c.m.aurainteriorprojectadmin.util.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private var name: String? = ""
    private var address: String? = ""
    private var phone: String? = ""
    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0
    private var type: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        map_location.onCreate(savedInstanceState)

        supportActionBar?.apply {
            title = getString(R.string.detail_title)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val intent = intent
        name = intent.getStringExtra(Constants.NAME)
        address = intent.getStringExtra(Constants.ADDRESS)
        phone = intent.getStringExtra(Constants.PHONE)
        latitude = intent.getDoubleExtra(Constants.LATITUDE, 0.0)
        longitude = intent.getDoubleExtra(Constants.LONGITUDE, 0.0)
        type = intent.getStringExtra(Constants.TYPE)

        tv_name.text = name
        tv_address.text = address
        tv_phone.text = phone
        tv_type.text = type

        // show map
        map_location.getMapAsync { googleMap ->
            // setup maps type
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            // Control settings
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isCompassEnabled = true

            googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder().target(
                        LatLng(latitude as Double, longitude as Double)
                    ).zoom(16f).build()
                )
            )

            googleMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        latitude as Double,
                        longitude as Double
                    )
                ).draggable(true)
            )
        }
    }

    override fun onResume() {
        map_location.onResume()
        super.onResume()
    }

    override fun onLowMemory() {
        map_location.onLowMemory()
        super.onLowMemory()
    }

    override fun onDestroy() {
        map_location.onDestroy()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
