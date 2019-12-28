package com.example.sathchaloodriver.Utilities


import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat.startActivity
import com.example.sathchaloodriver.R
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.AlertDialogBuilder
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

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

        fun getBiggerZoomValue(): Float {
            return 15f
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

        fun getDistance(pickupLatLng: LatLng, dropOffLatLng: LatLng) : Float{
            var location = Location("")
            location.latitude = pickupLatLng.latitude
            location.longitude = pickupLatLng.longitude

            var locationtwo = Location("")
            locationtwo.latitude = dropOffLatLng.latitude
            locationtwo.longitude = dropOffLatLng.longitude

            return location.distanceTo(locationtwo)
        }

        fun verifyAvailableNetwork(activity: AppCompatActivity):Boolean{
            val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo=connectivityManager.activeNetworkInfo
            return  networkInfo!=null && networkInfo.isConnected
        }

        fun isGPSEnable(locationManager: LocationManager): Boolean{
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        fun getAlertDialog(context: Context): AlertDialog.Builder{
            val alertDialog = AlertDialog.Builder(context, R.style.ThemeOverlay_MaterialComponents_Dialog)
            alertDialog.setTitle("Sath Chaloo")
            return alertDialog
        }

    }
}