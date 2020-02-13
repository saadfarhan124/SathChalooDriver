package com.example.sathchaloodriver.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sathchaloodriver.R
import com.example.sathchaloodriver.dataModels.RoutesDataModel
import org.jetbrains.anko.onClick

class RouteSelectAdapter(private var context: Context,
                         private var listOfRoutes: MutableList<RoutesDataModel>
): RecyclerView.Adapter<RouteSelectAdapter.RouteSelectViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteSelectViewHolder {
        val LayoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = LayoutInflater.inflate(R.layout.activity_routeselect_row, parent, false)
        return RouteSelectViewHolder(cellForRow)
    }

    override fun getItemCount(): Int {
        return listOfRoutes.size
    }

    override fun onBindViewHolder(holder: RouteSelectViewHolder, position: Int) {
        holder.itemView.onClick {

        }
    }

    inner class RouteSelectViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var txt_pickupAddress: TextView = view.findViewById(R.id.txt_pickupAddress)
        internal var txt_dropoffAddress: TextView = view.findViewById(R.id.txt_dropoffAddress)
        internal var txt_startTime: TextView = view.findViewById(R.id.txt_startTime)
    }
}
