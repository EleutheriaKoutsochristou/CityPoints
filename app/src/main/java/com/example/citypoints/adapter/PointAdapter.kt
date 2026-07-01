package com.example.citypoints.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.citypoints.database.SustainablePoint
import com.example.citypoints.databinding.ItemPointBinding

class PointAdapter(
    private var points: List<SustainablePoint>
) : RecyclerView.Adapter<PointAdapter.PointViewHolder>() {

    inner class PointViewHolder(val binding: ItemPointBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {

        val binding = ItemPointBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PointViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {

        val point = points[position]

        holder.binding.tvTitle.text = point.title
        holder.binding.tvCategory.text = point.category
        holder.binding.tvRating.text = point.rating.toString()

    }

    override fun getItemCount(): Int = points.size

    fun updateList(newList: List<SustainablePoint>) {
        points = newList
        notifyDataSetChanged()
    }
}