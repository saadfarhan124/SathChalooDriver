package com.example.sathchaloodriver.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sathchaloodriver.R

class RouteSelectAdapter: RecyclerView.Adapter<RouteSelectViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteSelectViewHolder {
        val LayoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = LayoutInflater.inflate(R.layout.activity_routeselect_row, parent, false)
        return RouteSelectViewHolder(cellForRow)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: RouteSelectViewHolder, position: Int) {

    }
}
class RouteSelectViewHolder(v: View) : RecyclerView.ViewHolder(v) {

}