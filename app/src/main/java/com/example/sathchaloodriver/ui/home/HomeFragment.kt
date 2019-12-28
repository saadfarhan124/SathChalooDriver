package com.example.sathchaloodriver.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.prototype.dataModels.Booking
import com.example.sathchaloodriver.R
import com.example.sathchaloodriver.Utilities.Util
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.PolyUtil
import com.jakewharton.threetenabp.AndroidThreeTen
import org.jetbrains.anko.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class HomeFragment : Fragment(), OnMapReadyCallback, LocationListener {
    private var btn: Button? = null
    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager

    private lateinit var ListPickUpLatLng: MutableList<LatLng>
    private lateinit var ListDropOffLatLng: MutableList<LatLng>
    private lateinit var listBooking: MutableList<Booking>

    private lateinit var btnStartRide: Button
    private lateinit var btnEndRide: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var marker: Marker

    private lateinit var startingPointLatLng: LatLng
    private lateinit var endingPointLatLng: LatLng


    //Permission Vars
    private val FINE_LOCATION: String = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION: String = Manifest.permission.ACCESS_COARSE_LOCATION

    //Permission Flag
    private var permissionFlag = false

    private lateinit var root: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_home, container, false)

        if (Util.verifyAvailableNetwork(activity!! as AppCompatActivity)) {
            AndroidThreeTen.init(this.activity)
            getLocationPermission()
            init()
            Log.d("dragon", Util.getFormattedDate())
            loadroutes()

        } else {
            val confirmDialog =
                AlertDialog.Builder(root.context, R.style.ThemeOverlay_MaterialComponents_Dialog)
            confirmDialog.setTitle("Sath Chaloo")
            confirmDialog.setMessage("Please connect to internet")
            confirmDialog.setPositiveButton("Ok") { _, _ ->
                activity!!.finishAffinity();
                System.exit(0)
            }
            confirmDialog.show()
        }

        return root
    }


    private fun init() {

        ListPickUpLatLng = mutableListOf()
        ListDropOffLatLng = mutableListOf()
        listBooking = mutableListOf()
        progressBar = root.findViewById(R.id.progressBar)
        btnEndRide = root.findViewById(R.id.btnEndRide)
        btnStartRide = root.findViewById(R.id.btnStartRide)
        btnStartRide.setOnClickListener {
            moveCamera(
                LatLng(marker.position.latitude, marker.position.longitude),
                Util.getBiggerZoomValue()
            )
            btnStartRide.visibility = View.INVISIBLE
            btnEndRide.visibility = View.VISIBLE
        }

        btnEndRide.setOnClickListener {
            val confirmDialog =
                AlertDialog.Builder(root.context, R.style.ThemeOverlay_MaterialComponents_Dialog)
            confirmDialog.setTitle("Sath Chaloo")
            confirmDialog.setMessage("Finish ride")
            confirmDialog.setPositiveButton("Ok") { _, _ ->

            }
            confirmDialog.show()
        }
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = root.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (Util.isGPSEnable(locationManager)) {
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000,
                    10f,
                    this
                )
            } catch (e: SecurityException) {

            }
        } else {
            val alertDialog = Util.getAlertDialog(root.context)
            alertDialog.setMessage("Location need to be opened to use this application. Would you like to proceed?")
            alertDialog.setPositiveButton("Ok") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            alertDialog.setNegativeButton("No") { _, _ ->
                activity!!.finishAndRemoveTask()
                System.exit(0)
            }
            alertDialog.show()

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
                    var startingPoint =
                        taskGetRouteId.result!!.first()["startingGeoPoint"] as GeoPoint
                    var endingPoint = taskGetRouteId.result!!.first()["endingGeoPoint"] as GeoPoint
                    startingPointLatLng =
                        LatLng(startingPoint.latitude, startingPoint.longitude)
                    endingPointLatLng = LatLng(endingPoint.latitude, endingPoint.longitude)
                    addMarkerEnding(
                        endingPointLatLng
                        , "Ending"
                    )
                    addMarkerStarting(
                        startingPointLatLng
                        , "Starting"
                    )

                    //get bookings on that route for that day
                    db.collection("booking")
                        .whereEqualTo(
                            "routeId",
                            taskGetRouteId.result!!.first()["routeId"].toString()
                        )
                        .whereEqualTo("bookingDate", Util.getFormattedDate())
                        .get()
                        .addOnCompleteListener { taskGetBookingsAccordingToRouteIdAndCurrentDay ->
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
                                    val booking = doc.toObject(Booking::class.java)
                                    listBooking.add(booking)
                                    addMarkerPickUp(
                                        pickUpLatLng,
                                        "Pickup : ${doc["bookingMadeBy"]}"
                                    )
                                    addMarkerDropOff(
                                        droppOffLatLng,
                                        "Dropoff : ${doc["bookingMadeBy"]}"
                                    )
                                    if (ListDropOffLatLng.size > 0) {
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
                                                val routes: JSONArray =
                                                    response.getJSONArray("routes")
                                                val routesObject = routes.getJSONObject(0)
                                                val polylines =
                                                    routesObject.getJSONObject("overview_polyline")
                                                val encodedString = polylines.getString("points")
                                                val bounds = LatLngBounds.Builder().include(
                                                    LatLng(
                                                        startingPoint.latitude,
                                                        startingPoint.longitude
                                                    )
                                                ).include(
                                                    LatLng(
                                                        endingPoint.latitude,
                                                        endingPoint.longitude
                                                    )
                                                )
                                                    .include(pickUpLatLng)
                                                    .include(droppOffLatLng)
                                                mMap.animateCamera(
                                                    CameraUpdateFactory.newLatLngBounds(
                                                        bounds.build(),
                                                        180
                                                    )
                                                )
                                                mMap.addPolyline(
                                                    PolylineOptions().addAll(
                                                        PolyUtil.decode(
                                                            encodedString
                                                        )
                                                    ).color(
                                                        Color.BLUE
                                                    )
                                                )
                                                progressBar.visibility = View.INVISIBLE

                                            }
                                        }
                                    } else {
                                        Toast.makeText(
                                            root.context,
                                            "No bookings found",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }
                            }
                        }
                } else {
                    Toast.makeText(
                        root.context,
                        "No routes found against driver",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        MapsInitializer.initialize(root.context)
        mMap = googleMap
        if (permissionFlag) {
            try {
                mMap.isMyLocationEnabled = true
            } catch (e: SecurityException) {

            }

        }
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

    //Function to add markers of starting point
    private fun addMarkerStarting(latlng: LatLng, title: String?) {
        val markerOptions = MarkerOptions().position(latlng).title(title)
            .icon(
                BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_CYAN
                )
            )
        marker = mMap.addMarker(markerOptions)
    }

    //Function to add markers of ending point
    private fun addMarkerEnding(latlng: LatLng, title: String?) {
        val markerOptions = MarkerOptions().position(latlng).title(title)
        marker = mMap.addMarker(markerOptions)
    }

    //First
    //Function to get permission
    private fun getLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (ContextCompat.checkSelfPermission(
                root.context,
                FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                    root.context,
                    COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                permissionFlag = true
            } else {
                ActivityCompat.requestPermissions(
                    activity!!,
                    permissions,
                    Util.getLocationPermissionCode()
                )
            }
        } else {
            ActivityCompat.requestPermissions(
                activity!!,
                permissions,
                Util.getLocationPermissionCode()
            )
        }
    }

    //Permission result callback
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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

    override fun onLocationChanged(location: Location?) {

        moveCamera(LatLng(location!!.latitude, location!!.longitude), Util.getBiggerZoomValue())
        if (Util.getDistance(
                LatLng(location!!.latitude, location!!.longitude),
                endingPointLatLng
            ) < 1000
        ) {
            btnEndRide.enabled = true
        }
        for (booking in listBooking) {
            if (Util.getDistance(
                    LatLng(location!!.latitude, location!!.longitude),
                    LatLng(booking.pickupLat!!, booking.pickupLong!!)
                ) < 200 && booking.rideStatus == "booked"
            ) {

                val view = layoutInflater.inflate(R.layout.activity_pickup_bottomsheet, null)
                val dialog = BottomSheetDialog(root.context)
                view.findViewById<Button>(R.id.btnPickUp).onClick {
                    booking.rideStatus = "pickedUp"
                    dialog.dismiss()
                }
                view.findViewById<TextView>(R.id.bookingNameTextView).text = booking.bookingMadeBy
                view.findViewById<TextView>(R.id.noOfSeatsTextView).text = booking.numberOfSeats.toString()
                dialog.setContentView(view)
                dialog.show()

            } else if (Util.getDistance(
                    LatLng(location!!.latitude, location!!.longitude),
                    LatLng(booking.dropOffLat!!, booking.dropOffLong!!)
                ) < 200 && booking.rideStatus == "pickedUp"
            ) {

                val confirmDialog =
                    AlertDialog.Builder(
                        root.context,
                        R.style.ThemeOverlay_MaterialComponents_Dialog
                    )
                confirmDialog.setTitle("Sath Chaloo")
                confirmDialog.setMessage("Drop off  ${booking.bookingMadeBy}, Total expense : ${booking.totalFare}")
                confirmDialog.setPositiveButton("End ride") { _, _ ->
                    booking.rideStatus = "completed"
                    var db = Util.getFireStoreInstance()
                    db.collection("booking")
                        .document(booking.bookingId.toString())
                        .set(booking)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    root.context,
                                    "Ride ended successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
                confirmDialog.show()
            }
        }

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        val alertDialog = Util.getAlertDialog(root.context)
        alertDialog.setMessage("Location need to be opened to use this application. Would you like to proceed?")
        alertDialog.setPositiveButton("Ok") { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        alertDialog.setNegativeButton("No") { _, _ ->
            activity!!.finishAffinity();
            System.exit(0)
        }

    }
}