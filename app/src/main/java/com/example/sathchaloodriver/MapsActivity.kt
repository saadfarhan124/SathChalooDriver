package com.example.sathchaloodriver

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.maps.android.PolyUtil
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sathchaloodriver.Utilities.Util
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.GeoPoint
import com.jakewharton.threetenabp.AndroidThreeTen
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    override fun onLocationChanged(location: Location?) {
        moveCamera(LatLng(location!!.latitude, location!!.longitude), Util.getBiggerZoomValue())
    }
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mMap: GoogleMap
    private lateinit var ListPickUpLatLng: MutableList<LatLng>
    private lateinit var ListDropOffLatLng: MutableList<LatLng>

    private lateinit var btnStartRide: Button
    private lateinit var marker: Marker


    //Permission Vars
    private val FINE_LOCATION: String = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION: String = Manifest.permission.ACCESS_COARSE_LOCATION

    //Permission Flag
    private var permissionFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        AndroidThreeTen.init(this);
        getLocationPermission()
        init()
        loadroutes()

    }


    private fun init() {

        ListPickUpLatLng = mutableListOf<LatLng>()
        ListDropOffLatLng = mutableListOf<LatLng>()
        btnStartRide = findViewById(R.id.btnStartRide)
        btnStartRide.setOnClickListener{
            moveCamera(LatLng(marker.position.latitude,marker.position.longitude), Util.getBiggerZoomValue())
        }
    }

    private fun loadroutes() {
        var db = Util.getFireStoreInstance()
        //Retrieve route ID
        db.collection("driver_routes")
            .whereEqualTo("driverId", Util.getDriverId())
            .get()
            .addOnCompleteListener { taskGetRouteId ->
                if (taskGetRouteId.isSuccessful) {
                    //get starting and ending point of route pick up and drop off
                    var startingPoint = taskGetRouteId.result!!.first()["startingGeoPoint"] as GeoPoint
                    var endingPoint = taskGetRouteId.result!!.first()["endingGeoPoint"] as GeoPoint
                    var startingPointLatLng = LatLng(startingPoint.latitude, startingPoint.longitude)
                    var endingPointLatLng = LatLng(endingPoint.latitude, endingPoint.longitude)
                    addMarker(
                        endingPointLatLng
                        , "Ending"
                    )
                    addMarker(
                        startingPointLatLng
                        , "Starting"
                    )

                    //get bookings on that route for that day
                    db.collection("booking")
                        .whereEqualTo("routeId", taskGetRouteId.result!!.first()["routeId"])
                        .whereEqualTo("bookingDate", Util.getFormattedDate())
                        .get()
                        .addOnCompleteListener { taskGetBookingsAccordingToRouteIdAndCurrentDay ->
                            Log.d("SAAAAAD", taskGetBookingsAccordingToRouteIdAndCurrentDay.result.toString())
                            if (taskGetBookingsAccordingToRouteIdAndCurrentDay.isSuccessful) {
                                for (doc in taskGetBookingsAccordingToRouteIdAndCurrentDay.result!!) {
                                    var pickUpLatLng = LatLng(
                                        doc["pickupLat"].toString().toDouble(),
                                        doc["pickupLong"].toString().toDouble()
                                    )
                                    var droppOffLatLng = LatLng(
                                        doc["dropOffLat"].toString().toDouble(),
                                        doc["dropOffLong"].toString().toDouble()
                                    )
                                    ListPickUpLatLng.add(pickUpLatLng)
                                    ListDropOffLatLng.add(droppOffLatLng)
                                    addMarkerPickUp(pickUpLatLng, "Pickup : ${doc["bookingMadeBy"]}")
                                    addMarkerDropOff(droppOffLatLng, "Dropoff : ${doc["bookingMadeBy"]}")
                                    if(ListDropOffLatLng.size > 0){
                                        val url = Util.getURL(
                                            startingPointLatLng,
                                            endingPointLatLng,
                                            getString(R.string.google_maps_key),
                                            ListPickUpLatLng,
                                            ListDropOffLatLng
                                        )
                                        async {
                                            val result = URL(url).readText()
                                            uiThread {
                                                val response = JSONObject(result)
                                                Log.d("Billie", response.toString())
                                                val routes: JSONArray = response.getJSONArray("routes")
                                                val routesObject = routes.getJSONObject(0)
                                                val polylines = routesObject.getJSONObject("overview_polyline")
                                                val encodedString = polylines.getString("points")
                                                val bounds = LatLngBounds.Builder().include(
                                                    LatLng(startingPoint.latitude, startingPoint.longitude)
                                                ).include(LatLng(endingPoint.latitude, endingPoint.longitude))
                                                    .include(pickUpLatLng)
                                                    .include(droppOffLatLng)
                                                mMap.animateCamera(
                                                    CameraUpdateFactory.newLatLngBounds(
                                                        bounds.build(),
                                                        180
                                                    )
                                                )
                                                mMap.addPolyline(
                                                    PolylineOptions().addAll(PolyUtil.decode(encodedString)).color(
                                                        Color.BLUE
                                                    )
                                                )

                                            }
                                        }
                                    }else{
                                        Toast.makeText(applicationContext, "No bookings found", Toast.LENGTH_SHORT).show()
                                    }

                                }
//
                            }
                        }
                } else {
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
        MapsInitializer.initialize(applicationContext)
        mMap = googleMap
        if(permissionFlag){
            try{
                mMap.isMyLocationEnabled = true
            }catch (e: SecurityException){

            }

        }

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }



    private fun initMap() {

    }

    //Function to move Camera
    private fun moveCamera(latLng: LatLng, zoom: Float) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    //function to add pickup marker
    private fun addMarkerPickUp(latlng: LatLng, title: String?) {
        val markerOptions = MarkerOptions().position(latlng).title(title)
            .icon(
                BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_BLUE
                )
            )
        mMap.addMarker(markerOptions)
    }

    //function to add droppoff marker
    private fun addMarkerDropOff(latlng: LatLng, title: String?) {
        val markerOptions = MarkerOptions().position(latlng).title(title)
            .icon(
                BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_VIOLET
                )
            )
        mMap.addMarker(markerOptions)
    }

    //Function to add markets
    private fun addMarker(latlng: LatLng, title: String?) {
        val markerOptions = MarkerOptions().position(latlng).title(title)
        marker = mMap.addMarker(markerOptions)
    }

    //First
    //Function to get permission
    private fun getLocationPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (ContextCompat.checkSelfPermission(applicationContext, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                permissionFlag = true
            } else {
                ActivityCompat.requestPermissions(this!!, permissions, Util.getLocationPermissionCode())
            }
        } else {
            ActivityCompat.requestPermissions(this!!, permissions, Util.getLocationPermissionCode())
        }
    }

    //Permission result callback
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            Util.getLocationPermissionCode() -> if (grantResults.isNotEmpty()) {
                for (i in 0 until grantResults.size - 1) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        permissionFlag = false
                        return
                    }
                }
                permissionFlag = true

            }
        }
    }
}
