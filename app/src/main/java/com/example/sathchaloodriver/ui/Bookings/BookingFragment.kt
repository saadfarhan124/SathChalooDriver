package com.example.sathchaloodriver.ui.Bookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sathchaloodriver.R

class BookingFragment: Fragment() {
//    private lateinit var bookingViewModel: BookingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        bookingViewModel =
//            ViewModelProviders.of(this).get(BookingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_booking_details, container, false)
//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        bookingViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
        return root
    }
}