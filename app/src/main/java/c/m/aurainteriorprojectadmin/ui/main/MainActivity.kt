package c.m.aurainteriorprojectadmin.ui.main

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.aurainteriorprojectadmin.R
import c.m.aurainteriorprojectadmin.database.database
import c.m.aurainteriorprojectadmin.model.OrderResponse
import c.m.aurainteriorprojectadmin.model.OrderSqlite
import c.m.aurainteriorprojectadmin.ui.addproduct.AddProductActivity
import c.m.aurainteriorprojectadmin.ui.cluster.ClusterActivity
import c.m.aurainteriorprojectadmin.ui.detail.DetailActivity
import c.m.aurainteriorprojectadmin.util.Constants
import c.m.aurainteriorprojectadmin.util.gone
import c.m.aurainteriorprojectadmin.util.visible
import com.github.babedev.dexter.dsl.runtimePermission
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivity
import java.sql.SQLClientInfoException

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity(), MainView {

    private lateinit var presenter: MainPresenter
    private lateinit var mainAdapter: MainAdapter
    private val contentData: MutableList<OrderResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = MainPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.firebaseInit()

        supportActionBar?.apply { title = getString(R.string.app_name) }

        // get permission device
        permissionDevice()

        // get wallpaper data
        presenter.getOrder()

        // refresh data
        swipe_refresh_main.setOnRefreshListener {
            swipe_refresh_main.isRefreshing = false
            presenter.getOrder()
        }

        // setup recycler view
        setupRecyclerView()

        //setup local database
        query()
    }

    private fun query() {
        try {
            this.database.use {
                select(OrderSqlite.TABLE_ORDER).exec {
                    if (count != 0) {
                        delete(OrderSqlite.TABLE_ORDER, null, null)
                    }
                }
            }
        } catch (e: SQLClientInfoException) {
            Log.e("ERROR!!!", e.localizedMessage)
        }
    }

    private fun setupRecyclerView() {
        mainAdapter = MainAdapter(contentData) { response ->
            startActivity<DetailActivity>(
                Constants.UID to response.uid
            )
        }
        rv_order.setHasFixedSize(true)
        rv_order.adapter = mainAdapter
    }

    private fun permissionDevice() {
        runtimePermission {
            permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) {
                checked { }
            }
        }
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDetachView()
    }

    override fun showLoading() {
        shimmerStart()
        tv_no_data_main.gone()
        rv_order.gone()
    }

    override fun hideLoading() {
        shimmerStop()
        tv_no_data_main.gone()
        rv_order.visible()
    }

    override fun showNoDataResult() {
        shimmerStop()
        tv_no_data_main.visible()
        rv_order.gone()
    }

    override fun getOrders(customerData: List<OrderResponse>) {
        contentData.clear()
        contentData.addAll(customerData)
        mainAdapter.notifyDataSetChanged()

        // add data to local database
        customerData.forEach { data ->
            try {
                this.database.use {
                    with(OrderSqlite) {
                        insert(
                            TABLE_ORDER,
                            UID to data.uid,
                            NAME to data.name,
                            ADDRESS to data.address,
                            PHONE to data.phone,
                            LATITUDE to data.latitude,
                            LONGITUDE to data.longitude,
                            TYPE_WALLPAPER to data.typeWallpaperOrder,
                            PRICE_ESTIMATION to data.priceEstimation,
                            ROLL_ESTIMATION to data.rollEstimation,
                            CUSTOMER_UID to data.customerUID,
                            ORDER_STATUS to data.orderStatus,
                            ORDER_DATE to data.orderDate
                        )
                    }
                }
            } catch (e: SQLClientInfoException) {
                Log.e("ERROR!!!", e.localizedMessage)
            }
        }
    }

    // shimmer loading animation start
    private fun shimmerStart() {
        shimmer_frame_main.visible()
        shimmer_frame_main.startShimmer()
    }

    // shimmer loading animation stop
    private fun shimmerStop() {
        shimmer_frame_main.gone()
        shimmer_frame_main.stopShimmer()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                startActivity<AddProductActivity>()
                true
            }
            R.id.menu_list_wallpaper -> {
                true
            }
            R.id.menu_export_report -> {
                true
            }
            R.id.menu_maps -> {
                startActivity<ClusterActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
