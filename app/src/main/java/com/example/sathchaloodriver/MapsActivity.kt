package com.example.sathchaloodriver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sathchaloodriver.Utilities.Util

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var ListPickUpLatLng: MutableList<LatLng>
    private lateinit var ListDropOffLatLng: MutableList<LatLng>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
                    //get starting and ending point of route pick up and drop off
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
                                    addMarkerPickUp(pickUpLatLng, doc["bookingMadeBy"].toString())
                                    addMarkerDropOff(droppOffLatLng, doc["bookingMadeBy"].toString())
                                    moveCamera(pickUpLatLng, Util.getZoomValue())
                                }
//                                Log.d("SAAAAAD", ListPickUpLatLng[0].toString())
                            }
                        }
                }else{
                    Toast.makeText(applicationContext, "No routes found against driver", Toast.LENGTH_SHORT).show()
                }
            }

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    //Function to move Camera
    private fun moveCamera(latLng: LatLng, zoom:Float){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    //function to add pickup marker
    private fun addMarkerPickUp(latlng: LatLng, title:String?){
        val markerOptions = MarkerOptions().position(latlng).title(title)
        mMap.addMarker(markerOptions)
    }

    //function to add droppoff marker
    private fun addMarkerDropOff(latlng: LatLng, title:String?){
        val markerOptions = MarkerOptions().position(latlng).title(title)
            .icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_VIOLET
            ))
        mMap.addMarker(markerOptions)
    }
}
