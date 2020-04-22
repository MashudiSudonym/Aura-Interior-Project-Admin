package c.m.aurainteriorprojectadmin.ui.main

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.aurainteriorprojectadmin.R
import c.m.aurainteriorprojectadmin.model.OrderResponse
import c.m.aurainteriorprojectadmin.ui.add.AddActivity
import c.m.aurainteriorprojectadmin.ui.cluster.ClusterActivity
import c.m.aurainteriorprojectadmin.ui.detail.DetailActivity
import c.m.aurainteriorprojectadmin.util.Constants
import c.m.aurainteriorprojectadmin.util.gone
import c.m.aurainteriorprojectadmin.util.visible
import com.github.babedev.dexter.dsl.runtimePermission
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

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
    }

    private fun setupRecyclerView() {
        mainAdapter = MainAdapter(contentData) { response ->
            startActivity<DetailActivity>(
                Constants.NAME to response.name,
                Constants.ADDRESS to response.address,
                Constants.PHONE to response.phone,
                Constants.LATITUDE to response.latitude,
                Constants.LONGITUDE to response.longitude,
                Constants.TYPE to response.typeWallpaperOrder
            )
        }
        rv_order.setHasFixedSize(true)
        rv_order.adapter = mainAdapter
    }

    private fun permissionDevice() {
        runtimePermission {
            permission(Manifest.permission.ACCESS_FINE_LOCATION) {}
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
                startActivity<AddActivity>()
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
