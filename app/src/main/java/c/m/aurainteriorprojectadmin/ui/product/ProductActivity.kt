package c.m.aurainteriorprojectadmin.ui.product

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.aurainteriorprojectadmin.R
import c.m.aurainteriorprojectadmin.model.WallpaperResponse
import c.m.aurainteriorprojectadmin.ui.detailproduct.DetailProductActivity
import c.m.aurainteriorprojectadmin.util.Constants
import c.m.aurainteriorprojectadmin.util.gone
import c.m.aurainteriorprojectadmin.util.visible
import kotlinx.android.synthetic.main.activity_product.*
import org.jetbrains.anko.startActivity

class ProductActivity : AppCompatActivity(), ProductView {

    private lateinit var presenter: ProductPresenter
    private lateinit var productAdapter: ProductAdapter
    private val content: MutableList<WallpaperResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = ProductPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.firebaseInit()

        // get wallpaper data
        presenter.getWallpaper()

        supportActionBar?.apply {
            title = getString(R.string.wallpaper_list)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        // refresh data
        swipe_refresh_product.setOnRefreshListener {
            swipe_refresh_product.isRefreshing = false
            presenter.getWallpaper()
        }

        // setup recycler view
        setupRecyclerView()
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onDestroy() {
        onDetachView()
        super.onDestroy()
    }

    override fun showLoading() {
        shimmerStart()
        tv_no_data_product.gone()
        rv_wallpaper.gone()
    }

    override fun hideLoading() {
        shimmerStop()
        tv_no_data_product.gone()
        rv_wallpaper.visible()
    }

    override fun showNoDataResult() {
        shimmerStop()
        tv_no_data_product.visible()
        rv_wallpaper.gone()
    }

    override fun getWallpaper(wallpaperData: List<WallpaperResponse>) {
        content.clear()
        content.addAll(wallpaperData)
        productAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(content) { response ->
            startActivity<DetailProductActivity>(
                Constants.UID to response.uid,
                Constants.IMAGE to response.imageWallpaper,
                Constants.TYPE to response.type
            )
        }
        rv_wallpaper.setHasFixedSize(true)
        rv_wallpaper.adapter = productAdapter
    }

    // shimmer loading animation start
    private fun shimmerStart() {
        shimmer_frame_product.visible()
        shimmer_frame_product.startShimmer()
    }

    // shimmer loading animation stop
    private fun shimmerStop() {
        shimmer_frame_product.gone()
        shimmer_frame_product.stopShimmer()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
