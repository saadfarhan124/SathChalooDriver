package com.example.sathchaloodriver.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sathchaloodriver.R
import com.example.sathchaloodriver.dataModels.NotificationDataModel

class NotificationAdapter(private val notificationList: MutableList<NotificationDataModel>)
    : RecyclerView.Adapter<NotificationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val LayoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = LayoutInflater.inflate(R.layout.activity_notification_row, parent, false)
        return NotificationViewHolder(cellForRow)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.textViewHeading.text = notification.heading
        holder.textViewSubHeading.text = notification.subHeading
    }
}

class NotificationViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    internal var textViewHeading: TextView = v.findViewById(R.id.textViewNotifHeading)
    internal var textViewSubHeading: TextView = v.findViewById(R.id.textViewNotifSubHeading)
}