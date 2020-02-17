package com.example.sathchaloodriver.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sathchaloodriver.R
import com.example.sathchaloodriver.dataModels.DriverRideDataModel

class HistoryAdapter(private val context:Context,
                     private val listOfRides: MutableList<DriverRideDataModel>): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val LayoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = LayoutInflater.inflate(R.layout.activity_history_row, parent, false)
        return HistoryViewHolder(cellForRow)
    }

    override fun getItemCount(): Int {
       when{
           listOfRides.size == 0 -> return 1
           else -> return listOfRides.size
       }
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        if(listOfRides.size == 0){
            holder.textViewRideID.visibility = View.INVISIBLE
            holder.textViewDate.visibility = View.INVISIBLE
            holder.textViewPickUpAddress.visibility = View.INVISIBLE
            holder.textViewDropOffAddress.visibility = View.INVISIBLE
            holder.textViewStartingtime.visibility = View.INVISIBLE
            holder.textViewEndingTime.visibility = View.INVISIBLE
            holder.textViewErrorMessage.visibility = View.VISIBLE

        }else{
            val ride = listOfRides[position]
            holder.textViewRideID.text = (position+1).toString()
            holder.textViewDate.text = ride.date
            holder.textViewPickUpAddress.text = ride.startingAddress
            holder.textViewDropOffAddress.text = ride.endingAddress
            holder.textViewStartingtime.text = ride.startingTime
            holder.textViewEndingTime.text = ride.endingTime
        }
    }

    inner class HistoryViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view){
        internal var textViewRideID: TextView = view.findViewById(R.id.textViewRideID)
        internal var textViewDate: TextView = view.findViewById(R.id.textViewDate)
        internal var textViewPickUpAddress: TextView = view.findViewById(R.id.textViewPickUpAddress)
        internal var textViewDropOffAddress: TextView = view.findViewById(R.id.textViewDropOffAddress)
        internal var textViewStartingtime: TextView = view.findViewById(R.id.textViewStartingtime)
        internal var textViewEndingTime: TextView = view.findViewById(R.id.textViewEndingTime)
        internal var textViewErrorMessage: TextView = view.findViewById(R.id.textViewErrorMessage)
    }
}
