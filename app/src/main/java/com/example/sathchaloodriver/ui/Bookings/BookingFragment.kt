package com.example.sathchaloodriver.ui.Bookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sathchaloodriver.R
import com.example.sathchaloodriver.Utilities.Util
import com.example.sathchaloodriver.adapters.BookingDetailsAdapter

class BookingFragment: Fragment() {
//    private lateinit var bookingViewModel: BookingViewModel
private lateinit var mRecyclerView: RecyclerView
    private lateinit var root: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_booking_details, container, false)
        if(Util.getGlobals().listOfCurrentBookings.size > 0){
            mRecyclerView = root.findViewById(R.id.bookingRecyclerView)
            mRecyclerView.layoutManager = LinearLayoutManager(root.context)
            mRecyclerView.adapter = BookingDetailsAdapter(root.context, Util.getGlobals().listOfCurrentBookings)
        }
        return root
    }
}