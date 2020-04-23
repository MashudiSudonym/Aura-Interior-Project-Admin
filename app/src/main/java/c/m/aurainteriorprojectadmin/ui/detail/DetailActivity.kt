package c.m.aurainteriorprojectadmin.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import c.m.aurainteriorprojectadmin.R
import c.m.aurainteriorprojectadmin.util.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity(), DetailView {

    private var uid: String? = ""
    private lateinit var presenter: DetailPresenter
    private lateinit var alertDialog: AlertDialog
    private lateinit var statusItem: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        map_location.onCreate(savedInstanceState)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = DetailPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.firebaseInit()

        supportActionBar?.apply {
            title = getString(R.string.detail_title)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val intent = intent
        uid = intent.getStringExtra(Constants.UID)

        presenter.getOrder(uid.toString())
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    @SuppressLint("SetTextI18n")
    override fun getOrder(
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
        // show map
        map_location.getMapAsync { googleMap ->
            // setup maps type
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            // Control settings
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isCompassEnabled = true

            googleMap.run {
                animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder().target(
                            LatLng(latitude, longitude)
                        ).zoom(16f).build()
                    )
                )

                addMarker(
                    MarkerOptions().position(
                        LatLng(
                            latitude,
                            longitude
                        )
                    ).draggable(true)
                )
            }
        }

        // initial text
        tv_name.text = name
        tv_address.text = address
        tv_phone.text = phone
        tv_type_wallpaper.text = typeWallpaper
        tv_price_estimation.text = priceEstimation
        tv_roll_estimation.text = "$rollEstimation roll wallpaper"
        tv_order_date.text = dateOrder

        // initial order status
        when (statusOrder) {
            0 -> tv_order_status.text = getString(R.string.waiting_status)
            1 -> tv_order_status.text = getString(R.string.order_accept_status)
            2 -> tv_order_status.text = getString(R.string.order_cancel_status)
        }

        // dialog change status order
        statusItem = arrayOf(
            getString(R.string.waiting_status),
            getString(R.string.order_accept_status),
            getString(R.string.order_cancel_status)
        )

        tv_order_status.setOnClickListener {
            alertDialog = AlertDialog.Builder(this).apply {
                setSingleChoiceItems(
                    statusItem,
                    statusOrder
                ) { _, i ->
                    when (i) {
                        0 -> {
                            presenter.updateOrderStatus(
                                uid.toString(),
                                name,
                                address,
                                phone,
                                latitude,
                                longitude,
                                typeWallpaper,
                                priceEstimation,
                                rollEstimation,
                                customerUID,
                                0,
                                dateOrder
                            )
                            presenter.sendNotification(
                                customerUID,
                                getString(R.string.your_order_status),
                                "Pesanan anda berstatus MENUNGGU."
                            )
                        }
                        1 -> {
                            presenter.updateOrderStatus(
                                uid.toString(),
                                name,
                                address,
                                phone,
                                latitude,
                                longitude,
                                typeWallpaper,
                                priceEstimation,
                                rollEstimation,
                                customerUID,
                                1,
                                dateOrder
                            )
                            presenter.sendNotification(
                                customerUID,
                                getString(R.string.your_order_status),
                                "Pesanan anda diterima oleh admin. Anda akan segera dihubungi melalui whatsapp atau telepon oleh admin."
                            )
                        }
                        2 -> {
                            presenter.updateOrderStatus(
                                uid.toString(),
                                name,
                                address,
                                phone,
                                latitude,
                                longitude,
                                typeWallpaper,
                                priceEstimation,
                                rollEstimation,
                                customerUID,
                                2,
                                dateOrder
                            )
                            presenter.sendNotification(
                                customerUID,
                                getString(R.string.your_order_status),
                                "Pesanan anda dibatalkan oleh admin. Anda akan segera dihubungi untuk informasi lebih lanjut tentang pembatalan pesanan.."
                            )
                        }
                    }
                }
                setPositiveButton(getString(R.string.ok)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                create()
                setCancelable(false)
            }.show()
        }
    }

    override fun onResume() {
        map_location.onResume()
        super.onResume()
    }

    override fun onLowMemory() {
        map_location.onLowMemory()
        onDetachView()
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
