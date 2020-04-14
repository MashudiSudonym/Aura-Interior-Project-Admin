package c.m.aurainteriorprojectadmin.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import c.m.aurainteriorprojectadmin.R
import c.m.aurainteriorprojectadmin.model.CustomerResponse
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_order.*

class MainAdapter(
    private val content: List<CustomerResponse>,
    private val onClickListener: (CustomerResponse) -> Unit
) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder =
        MainViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order, parent, false)
        )

    override fun getItemCount(): Int = content.size
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) =
        holder.bind(content[position], onClickListener)

    class MainViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(content: CustomerResponse, onClickListener: (CustomerResponse) -> Unit) {
            item_order_layout.setOnClickListener { onClickListener(content) }
            tv_name.text = content.name
            tv_phone.text = content.phone
        }
    }
}