package com.example.sathchaloodriver.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sathchaloodriver.MainActivity
import com.example.sathchaloodriver.R
import com.example.sathchaloodriver.dataModels.RoutesDataModel
import com.example.sathchaloodriver.ui.home.HomeFragment
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
        val route = listOfRoutes[position]

        holder.txt_pickupAddress.text = route.startingAddress
        holder.txt_dropoffAddress.text = route.endingAddress
        holder.txt_startTime.text = route.startingTime

        holder.itemView.onClick {
            var intent = Intent(context, MainActivity::class.java)
            intent.putExtra("selectedRouteID", route.routeID)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    inner class RouteSelectViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var txt_pickupAddress: TextView = view.findViewById(R.id.txt_pickupAddress)
        internal var txt_dropoffAddress: TextView = view.findViewById(R.id.txt_dropoffAddress)
        internal var txt_startTime: TextView = view.findViewById(R.id.txt_startTime)
    }
}
