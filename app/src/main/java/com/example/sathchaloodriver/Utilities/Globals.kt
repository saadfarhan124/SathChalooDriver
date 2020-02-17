package com.example.sathchaloodriver.Utilities

import android.graphics.Bitmap
import android.net.Uri
import com.example.prototype.dataModels.Booking
import com.google.firebase.auth.FirebaseUser

class Globals{
    companion object Globals{
        var user: FirebaseUser? = null
        var imageUri: Uri? = null
        var userImage: Bitmap? = null
        var listOfCurrentBookings:MutableList<Booking> = mutableListOf()

    }
}