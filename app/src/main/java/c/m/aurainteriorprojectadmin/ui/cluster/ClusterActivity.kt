package c.m.aurainteriorprojectadmin.ui.cluster

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import c.m.aurainteriorprojectadmin.R
import c.m.aurainteriorprojectadmin.model.CustomerResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_cluster.*

class ClusterActivity : AppCompatActivity(), ClusterView {

    private lateinit var presenter: ClusterPresenter
    private var content: MutableList<CustomerResponse> = mutableListOf()
    private var gpsLatitude: Double? = 0.0
    private var gpsLongitude: Double? = 0.0
    private lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cluster)

        initPresenter()
        onAttachView()

        map_cluster.onCreate(savedInstanceState)
    }

    private fun initPresenter() {
        presenter = ClusterPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.firebaseInit()
        presenter.getCustomer()

        supportActionBar?.apply {
            title = getString(R.string.cluster_title)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        getGPSCoordinate()

        map_cluster.getMapAsync { googleMap ->
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager
                    .PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
            } else {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, 101)
            }


            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isCompassEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true

            googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(LatLng(gpsLatitude as Double, gpsLongitude as Double))
                        .zoom(13f).build()
                )
            )

            content.forEach { response ->
                googleMap.run {
                    addMarker(
                        MarkerOptions()
                            .position(
                                LatLng(response.latitude as Double, response.longitude as Double)
                            )
                            .title(response.name)
                            .snippet(response.address)
                    ).tag = response.uid

                    setOnMarkerClickListener { marker ->
                        marker.showInfoWindow()
                        false
                    }

                    setOnInfoWindowClickListener { marker ->
                        val uid = marker.tag
                        val name = marker.title
                    }

                }
            }
        }
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onDestroy() {
        onDetachView()
        map_cluster.onDestroy()
        super.onDestroy()
    }

    override fun onResume() {
        map_cluster.onResume()
        super.onResume()
    }

    override fun onLowMemory() {
        map_cluster.onLowMemory()
        super.onLowMemory()
    }

    override fun getCustomer(customerData: List<CustomerResponse>) {
        content.clear()
        content.addAll(customerData)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun getGPSCoordinate() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (hasGps || hasNetwork) {
                if (hasGps) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0, 0f, object : LocationListener {
                            override fun onLocationChanged(location: Location?) {
                                if (location != null) {
                                    locationGps = location
                                    gpsLatitude = locationGps?.latitude
                                    gpsLongitude = locationGps?.longitude
                                }
                            }

                            override fun onStatusChanged(
                                provider: String?,
                                status: Int,
                                extras: Bundle?
                            ) {
                            }

                            override fun onProviderEnabled(provider: String?) {}

                            override fun onProviderDisabled(provider: String?) {}
                        })

                    val localGpsLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (localGpsLocation != null) locationGps = localGpsLocation
                } else {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                if (hasNetwork) {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 0, 0f, object : LocationListener {
                            override fun onLocationChanged(location: Location?) {
                                if (location != null) {
                                    locationNetwork = location
                                    gpsLatitude = locationNetwork?.latitude
                                    gpsLongitude = locationNetwork?.longitude
                                }
                            }

                            override fun onStatusChanged(
                                provider: String?,
                                status: Int,
                                extras: Bundle?
                            ) {
                            }

                            override fun onProviderEnabled(provider: String?) {}

                            override fun onProviderDisabled(provider: String?) {}
                        })

                    val localNetworkLocation =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (localNetworkLocation != null) locationNetwork = localNetworkLocation
                } else {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }

                if (locationGps != null && locationNetwork != null) {
                    if (locationGps?.accuracy as Float > locationNetwork?.accuracy as Float) {
                        gpsLatitude = locationNetwork?.latitude
                        gpsLongitude = locationNetwork?.longitude
                    } else {
                        gpsLatitude = locationNetwork?.latitude
                        gpsLongitude = locationNetwork?.longitude
                    }
                }
            } else {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, 101)
        }
    }

    private fun requestPermission(permissionType: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permissionType), requestCode)
    }
}
