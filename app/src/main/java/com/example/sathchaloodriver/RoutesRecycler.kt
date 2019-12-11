package com.example.sathchaloodriver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sathchaloodriver.Utilities.Util
import com.google.android.gms.maps.model.LatLng

class RoutesRecycler : AppCompatActivity() {

    private lateinit var ListPickUpLatLng: MutableList<LatLng>
    private lateinit var ListDropOffLatLng: MutableList<LatLng>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routes_recycler)
        init()
        loadroutes()
    }

    private fun init(){
         ListPickUpLatLng = mutableListOf<LatLng>()
         ListDropOffLatLng = mutableListOf<LatLng>()
    }

    private fun loadroutes(){
        var db = Util.getFireStoreInstance()
        //Retrieve route ID
        db.collection("driver_routes")
            .whereEqualTo("driverId", Util.getDriverId())
            .get()
            .addOnCompleteListener{taskGetRouteId ->
                if(taskGetRouteId.isSuccessful){
                    //get bookings on that route for that day
                    db.collection("booking")
                        .whereEqualTo("routeId", taskGetRouteId.result!!.first()["routeId"])
                        .whereEqualTo("bookingDate", Util.getFormattedDate())
                        .get()
                        .addOnCompleteListener{taskGetBookingsAccordingToRouteIdAndCurrentDay ->
                            if(taskGetBookingsAccordingToRouteIdAndCurrentDay.isSuccessful){
                                for (doc in taskGetBookingsAccordingToRouteIdAndCurrentDay.result!!){
                                    var pickUpLatLng = LatLng(doc["pickupLat"].toString().toDouble(),
                                        doc["pickupLong"].toString().toDouble())
                                    var droppOffLatLng = LatLng(doc["dropOffLat"].toString().toDouble(),
                                        doc["dropOffLong"].toString().toDouble())

//                                    ListPickUpLatLng.add(pickUpLatLng)
//                                    ListDropOffLatLng.add(droppOffLatLng)
                                }

                            }
                        }
                }else{
                    Toast.makeText(applicationContext, "No routes found against driver", Toast.LENGTH_SHORT).show()
                }
            }

    }
}
