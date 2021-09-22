package com.alumnus.zebra.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alumnus.zebra.R
import com.alumnus.zebra.pojo.AccelerationStringData
import java.util.*

/**
 * RecyclerView Adapter class for CSV file viewer
 * Used in CsvExplorerActivity class
 *
 * @author Arnab Kundu
 */
class AccelerationDataAdapter(private val accelerations: ArrayList<AccelerationStringData>) : RecyclerView.Adapter<AccelerationDataAdapter.ViewHolder>() {
    var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_acceleration_data, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTs.text = accelerations[position].ts
        holder.tvX.text = accelerations[position].x
        holder.tvY.text = accelerations[position].y
        holder.tvZ.text = accelerations[position].z
        if (position == 0) {
            holder.tvTs.background = context!!.getDrawable(R.drawable.bg_black_with_border)
            holder.tvX.background = context!!.getDrawable(R.drawable.bg_black_with_border)
            holder.tvY.background = context!!.getDrawable(R.drawable.bg_black_with_border)
            holder.tvZ.background = context!!.getDrawable(R.drawable.bg_black_with_border)
        } else if (position % 2 == 0) {
            holder.tvTs.background = context!!.getDrawable(R.drawable.bg_gray_with_border)
            holder.tvX.background = context!!.getDrawable(R.drawable.bg_gray_with_border)
            holder.tvY.background = context!!.getDrawable(R.drawable.bg_gray_with_border)
            holder.tvZ.background = context!!.getDrawable(R.drawable.bg_gray_with_border)
        } else {
            holder.tvTs.background = context!!.getDrawable(R.drawable.bg_white_with_border)
            holder.tvX.background = context!!.getDrawable(R.drawable.bg_white_with_border)
            holder.tvY.background = context!!.getDrawable(R.drawable.bg_white_with_border)
            holder.tvZ.background = context!!.getDrawable(R.drawable.bg_white_with_border)
        }
        if (position == 0) {
            holder.tvTs.setTextColor(context!!.getColor(R.color.white))
            holder.tvX.setTextColor(context!!.getColor(R.color.white))
            holder.tvY.setTextColor(context!!.getColor(R.color.white))
            holder.tvZ.setTextColor(context!!.getColor(R.color.white))
        } else {
            holder.tvTs.setTextColor(context!!.getColor(R.color.black))
            holder.tvX.setTextColor(context!!.getColor(R.color.black))
            holder.tvY.setTextColor(context!!.getColor(R.color.black))
            holder.tvZ.setTextColor(context!!.getColor(R.color.black))
        }
    }

    override fun getItemCount(): Int {
        return accelerations.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTs: TextView
        var tvX: TextView
        var tvY: TextView
        var tvZ: TextView

        init {
            tvTs = itemView.findViewById(R.id.tvTs)
            tvX = itemView.findViewById(R.id.tvX)
            tvY = itemView.findViewById(R.id.tvY)
            tvZ = itemView.findViewById(R.id.tvZ)
        }
    }
}