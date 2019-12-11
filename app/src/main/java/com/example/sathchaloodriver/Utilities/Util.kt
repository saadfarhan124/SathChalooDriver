package com.example.sathchaloodriver.Utilities

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class Util {
    companion object {
        fun getFireStoreInstance(): FirebaseFirestore {
            return FirebaseFirestore.getInstance()
        }

        fun getDriverId(): String {
            return "h2rVtOe6Lk4Qe6kVKoXR"
        }

        fun getFormattedDate(addDays: Long = 0): String {
            var date: String? = null
            if (addDays == 0.toLong()) {
                return LocalDateTime.now()
                    .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
            } else {
                return LocalDateTime.now().plusDays(addDays)
                    .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
            }
        }

        fun getZoomValue(): Float {
            return 10f
        }

        fun getURL(
            from: LatLng, to: LatLng, key: String, listPickUpLatLng: MutableList<LatLng>,
            listDropOffLatLng: MutableList<LatLng>
        ): String {
            val origin = "" + from.latitude + "," + from.longitude
            val dest = "" + to.latitude + "," + to.longitude
            var waypoint = ""
            for (pickUpLatLng in listPickUpLatLng){
                waypoint += "|${pickUpLatLng.latitude},${pickUpLatLng.longitude}"
            }
            for (dropOffLatLng in listDropOffLatLng){
                waypoint += "|${dropOffLatLng.latitude},${dropOffLatLng.longitude}"
            }
            return "https://maps.googleapis.com/maps/api/directions/json?\n" +
                    "origin=$origin&destination=$dest\n" +
                    "&waypoints=optimize:true|$waypoint\n" +
                    "&key=$key\n"
//            return "https://maps.googleapis.com/maps/api/directions/json?\n" +
//                    "origin=sydney,au&destination=perth,au\n" +
//                    "&waypoints=37.81223%2C144.96254%7C34.92788%2C138.60008\n" +
//                    "&key=$key"
            //            return "https://maps.googleapis.com/maps/api/directions/json?$params"
        }

        fun getLocationPermissionCode(): Int {
            return 1234
        }
    }
}