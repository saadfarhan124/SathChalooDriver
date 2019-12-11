package com.example.sathchaloodriver.Utilities

import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class Util {
    companion object{
        fun getFireStoreInstance():FirebaseFirestore{
            return FirebaseFirestore.getInstance()
        }

        fun getDriverId():String{
            return "h2rVtOe6Lk4Qe6kVKoXR"
        }

        fun getFormattedDate(addDays:Long = 0):String{
            var date:String? = null
            if(addDays == 0.toLong()){
                return LocalDateTime.now()
                    .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
            }else{
                return LocalDateTime.now().plusDays(addDays)
                    .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
            }
        }

        fun getZoomValue():Float{
            return 10f
        }
    }
}