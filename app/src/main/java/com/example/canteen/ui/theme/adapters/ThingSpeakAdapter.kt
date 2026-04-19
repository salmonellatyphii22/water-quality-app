package com.example.canteen.ui.theme.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.canteen.databinding.ItemSensorBinding
import com.example.canteen.ui.theme.model.Feed

class ThingSpeakAdapter(private val list: List<Feed>) :
    RecyclerView.Adapter<ThingSpeakAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSensorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSensorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.tvPh.text = "pH: ${item.field1 ?: "--"}"
        holder.binding.tvTds.text = "TDS: ${item.field2 ?: "--"}"
        holder.binding.tvTurbidity.text = "Turbidity: ${item.field4 ?: "--"}"
        holder.binding.tvTemp.text = "Temp: ${item.field3 ?: "--"}"
        holder.binding.tvTime.text = item.created_at
    }

    override fun getItemCount() = list.size
}