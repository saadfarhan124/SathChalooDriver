package com.example.sathchaloodriver.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sathchaloodriver.R

class NotificationAdapter: RecyclerView.Adapter<NotificationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val LayoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = LayoutInflater.inflate(R.layout.activity_notification_row, parent, false)
        return NotificationViewHolder(cellForRow)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

    }
}
class NotificationViewHolder(v: View) : RecyclerView.ViewHolder(v) {

}