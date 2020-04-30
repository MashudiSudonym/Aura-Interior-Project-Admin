package c.m.aurainteriorprojectadmin.ui.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import c.m.aurainteriorprojectadmin.R
import c.m.aurainteriorprojectadmin.model.WallpaperResponse
import coil.api.load
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_wallpaper.*

class ProductAdapter(
    private val content: List<WallpaperResponse>,
    private val onClickListener: (WallpaperResponse) -> Unit
) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_wallpaper, parent, false)
        )

    override fun getItemCount(): Int = content.size
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bind(content[position], onClickListener)

    class ProductViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(content: WallpaperResponse, onClickListener: (WallpaperResponse) -> Unit) {
            item_wallpaper_layout.setOnClickListener { onClickListener(content) }
            tv_wallpaper_type.text = content.type
            img_wallpaper.load(content.imageWallpaper) {
                crossfade(true)
                placeholder(R.drawable.loading_animation)
                error(R.drawable.ic_broken_image)
            }
        }
    }
}