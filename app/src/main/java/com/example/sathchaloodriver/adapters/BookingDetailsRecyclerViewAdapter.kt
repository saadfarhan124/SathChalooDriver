package com.example.sathchaloodriver.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.prototype.dataModels.Booking
import com.example.sathchaloodriver.R
import org.jetbrains.anko.onClick
import kotlin.coroutines.coroutineContext

class BookingDetailsAdapter(private var context: Context,
                            private var listOfBoooking: MutableList<Booking>): RecyclerView.Adapter<BookingDetailsAdapter.BookingDetailsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingDetailsViewHolder {
        val LayoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = LayoutInflater.inflate(R.layout.activity_bookingdetails_row, parent, false)
        return BookingDetailsViewHolder(cellForRow)
    }



    override fun getItemCount(): Int {
        return listOfBoooking.size
    }

    override fun onBindViewHolder(holder: BookingDetailsViewHolder, position: Int) {

        val booking = listOfBoooking[position]
        holder.textViewBookingName.text = booking.username
        holder.textViewPickUpAddress.text = booking.pickUpAddress
        holder.textViewDropOffAddress.text = booking.dropOffAddress
        holder.textViewPickUpTime.text = booking.pickUpTime
        holder.textViewDropOffTime.text = booking.dropOffTime

        holder.itemView.onClick {
           // val intent = Intent(context,HelloActivity::class.java) startActivity(intent)
            Toast.makeText(context ,"1", Toast.LENGTH_LONG).show()
        }
    }

    inner class BookingDetailsViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v){
        internal var textViewBookingName: TextView = v.findViewById(R.id.textViewBookingName)
        internal var textViewPickUpAddress: TextView = v.findViewById(R.id.textViewPickUpAddress)
        internal var textViewDropOffAddress: TextView = v.findViewById(R.id.textViewDropOffAddress)
        internal var textViewPickUpTime: TextView = v.findViewById(R.id.textViewPickUpTime)
        internal var textViewDropOffTime: TextView = v.findViewById(R.id.textViewDropOffTime)

    }
}
