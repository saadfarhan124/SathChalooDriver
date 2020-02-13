package com.example.sathchaloodriver.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sathchaloodriver.R
import org.jetbrains.anko.onClick
import kotlin.coroutines.coroutineContext

class BookingDetailsAdapter(private var context: Context): RecyclerView.Adapter<BookingDetailsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingDetailsViewHolder {
        val LayoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = LayoutInflater.inflate(R.layout.activity_bookingdetails_row, parent, false)
        return BookingDetailsViewHolder(cellForRow)
    }



    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: BookingDetailsViewHolder, position: Int) {

        holder.itemView.onClick {
           // val intent = Intent(context,HelloActivity::class.java) startActivity(intent)
            Toast.makeText(context ,"1", Toast.LENGTH_LONG).show()
        }


    }
}
class BookingDetailsViewHolder(v: View) : RecyclerView.ViewHolder(v) {

}