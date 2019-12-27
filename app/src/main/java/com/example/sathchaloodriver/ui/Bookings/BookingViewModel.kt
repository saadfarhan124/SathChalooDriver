package com.example.sathchaloodriver.ui.Bookings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookingViewModel: ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Booking Fragment"
    }
    val text: LiveData<String> = _text
}