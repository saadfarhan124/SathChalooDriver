package com.example.sathchaloodriver.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sathchaloodriver.R

class BookingDetailsAdapter: RecyclerView.Adapter<BookingDetailsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingDetailsViewHolder {
        val LayoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = LayoutInflater.inflate(R.layout.activity_bookingdetails_row, parent, false)
        return BookingDetailsViewHolder(cellForRow)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: BookingDetailsViewHolder, position: Int) {

    }
}
class BookingDetailsViewHolder(v: View) : RecyclerView.ViewHolder(v) {

}