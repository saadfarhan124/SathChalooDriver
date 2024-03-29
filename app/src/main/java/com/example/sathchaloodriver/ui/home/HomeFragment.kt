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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prototype.dataModels.Booking
import com.example.sathchaloodriver.R
import com.example.sathchaloodriver.Utilities.Util
import com.example.sathchaloodriver.adapters.RouteSelectAdapter
import com.example.sathchaloodriver.dataModels.DriverRideDataModel
import com.example.sathchaloodriver.dataModels.RoutesDataModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.PolyUtil
import com.jakewharton.threetenabp.AndroidThreeTen
import org.jetbrains.anko.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


class HomeFragment : Fragment(), OnMapReadyCallback, LocationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager

    private lateinit var ListPickUpLatLng: MutableList<LatLng>
    private lateinit var ListDropOffLatLng: MutableList<LatLng>
    private lateinit var listBooking: MutableList<Booking>

    private lateinit var btnStartRide: Button
    private lateinit var btnEndRide: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var marker: Marker

    //floating button open bottomsheet of route select
    private lateinit var btn_bottomsheet: FloatingActionButton
    private lateinit var mRecyclerView: RecyclerView

    private lateinit var startingPointLatLng: LatLng
    private lateinit var endingPointLatLng: LatLng

    //Flag to check if the properties been initilized
    private var initializeFlag: Boolean = false

    //Variable to save document ID
    private lateinit var driverDocumentRef: DocumentReference

    //Selected Route
    private lateinit var selectedRoute: DocumentSnapshot


    //Permission Vars
    private val FINE_LOCATION: String = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION: String = Manifest.permission.ACCESS_COARSE_LOCATION

    //Permission Flag
    private var permissionFlag = false

    //Selected Route ID
    private lateinit var routeID: String

    //List of routes assigned to a driver
    private lateinit var listOfRouteIds: MutableList<String>


    private lateinit var root: View

    //Starting Time and Ending Time vars
    private var startingTime: String? = null
    private var endingTime: String? = null

    //Start and ending buttons flag
    private var startingFlag: Boolean = false
    private var endingFlag: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        retainInstance = true

        if (Util.verifyAvailableNetwork(activity!! as AppCompatActivity)) {
            Util.getFireStoreInstance().collection("DriverRoute")
                .whereEqualTo("driverId", Util.getGlobals().user!!.uid)
                .get()
                .addOnSuccessListener {
                    listOfRouteIds = mutableListOf()
                    for (doc in it.documents) {
                        listOfRouteIds.add(doc["routeId"].toString())
                    }

                    AndroidThreeTen.init(this.activity)
                    getLocationPermission()
                    init()
                    if (activity!!.intent.extras != null) {
                        routeID = activity!!.intent.extras!!["selectedRouteID"].toString()
                        Util.getFireStoreInstance().collection("DriverRoute")
                            .whereEqualTo("routeID", routeID)
                            .get()
                            .addOnSuccessListener {
                                getDocumentId()
                                loadroutes()

                            }
                    }

                }
        } else {
            val confirmDialog =
                AlertDialog.Builder(root.context, R.style.ThemeOverlay_MaterialComponents_Dialog)
            confirmDialog.setTitle("Sath Chaloo")
            confirmDialog.setMessage("Please connect to internet")
            confirmDialog.setPositiveButton("Ok") { _, _ ->
                activity!!.finishAffinity()
                exitProcess(0)
            }
            confirmDialog.show()
        }


        return root
    }

    private fun updateLocationInDb(location: Location?) {
        if (location != null) {
            driverDocumentRef
                .update("driver_location", GeoPoint(location.latitude, location.longitude))
        }
    }

    private fun getDocumentId() {
        Util.getFireStoreInstance().collection("DriverRoute")
            .whereEqualTo("routeId", routeID)
            .get()
            .addOnSuccessListener {
                driverDocumentRef = Util.getFireStoreInstance().collection("DriverRoute")
                    .document(it.first().id)
            }
    }


    private fun init() {
        progressBar = root.findViewById(R.id.progressBar)


        val listOfRoutes = mutableListOf<RoutesDataModel>()
        for (routeID in listOfRouteIds) {
            Util.getFireStoreInstance().collection("Routes")
                .document(routeID)
                .get()
                .addOnSuccessListener {
                    val route = it.toObject(RoutesDataModel::class.java)
                    route!!.routeID = it.id
                    listOfRoutes.add(route)
                    progressBar.visibility = View.INVISIBLE
                }
        }
        //routes select bottom sheet
        btn_bottomsheet = root.findViewById(R.id.floatingActionButtonRouteSelect)
        btn_bottomsheet.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.activity_routeselect_bottomsheet, null)
            mRecyclerView = view.findViewById(R.id.routeselectRecyclerView)
            mRecyclerView.layoutManager = LinearLayoutManager(root.context)
            mRecyclerView.adapter = RouteSelectAdapter(root.context, listOfRoutes)
            val dialog = BottomSheetDialog(root.context)
            dialog.setContentView(view)
            dialog.show()
        }


        ListPickUpLatLng = mutableListOf()
        ListDropOffLatLng = mutableListOf()
        listBooking = mutableListOf()
        btnEndRide = root.findViewById(R.id.btnEndRide)
        btnEndRide.onClick {
            if(endingFlag){
                endingTime = SimpleDateFormat("HH:mm:ss").format(Date())
                var driverRide = DriverRideDataModel(
                    Util.getGlobals().user!!.uid,
                    selectedRoute["startingAddress"].toString(),
                    selectedRoute["endingAddress"].toString(),
                    startingTime!!,
                    endingTime!!,
                    selectedRoute.id,
                    listBooking,
                    Util.getFormattedDate()
                )
                val alert = Util.getAlertDialog(root.context)
                alert.setMessage("Do you really want to complete this ride ?")
                alert.setPositiveButton("Yes") { _, _ ->
                    Util.getFireStoreInstance().collection("DriverRides")
                        .add(driverRide)
                        .addOnSuccessListener {
                            mMap.clear()
                            moveCamera(LatLng(25.1921465, 66.5949924), 6f)
                        }
                }
                alert.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                alert.show()
            }else{
                Toast.makeText(root.context, "You are away from your ending point", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        btnStartRide = root.findViewById(R.id.btnStartRide)
        btnStartRide.setOnClickListener {
            if(startingFlag){
                progressBar.visibility = View.VISIBLE
                startingTime = SimpleDateFormat("HH:mm:ss").format(Date())
                moveCamera(
                    LatLng(marker.position.latitude, marker.position.longitude),
                    Util.getBiggerZoomValue()
                )
                //Updating booking status
                driverDocumentRef.update("rideStatus", "started")
                    .addOnSuccessListener {
                        btnStartRide.visibility = View.INVISIBLE
                        btnEndRide.visibility = View.VISIBLE
                        progressBar.visibility = View.INVISIBLE
                    }

            }else{
                Toast.makeText(root.context, "You are away from your starting point", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = root.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (Util.isGPSEnable(locationManager)) {
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    1f,
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
        val db = Util.getFireStoreInstance()
        //retrieve route
        db.collection("Routes").document(routeID)
            .get()
            .addOnSuccessListener { routeDetails ->
                val startingPoint = routeDetails["startingPoint"] as GeoPoint

                val endingPoint = routeDetails["endingPoint"] as GeoPoint

                selectedRoute = routeDetails

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
                db.collection("Booking")
                    .whereEqualTo(
                        "routeID",
                        routeDetails.id
                    )
                    .whereEqualTo("selectedDay", "Wednesday")
                    .whereEqualTo("rideStatus", "Booked")
                    .get()
                    .addOnFailureListener {
                        Toast.makeText(root.context, it.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnSuccessListener { taskGetBookingsAccordingToRouteIdAndCurrentDay ->
                        if (taskGetBookingsAccordingToRouteIdAndCurrentDay.isEmpty) {
                            Toast.makeText(root.context, "No Bookings Found", Toast.LENGTH_SHORT)
                                .show()
                            progressBar.visibility = View.INVISIBLE
                        } else {
                            for (doc in taskGetBookingsAccordingToRouteIdAndCurrentDay.documents) {
                                Log.d("SAAD", doc.toString())
                                val pickUpLatLng = LatLng(
                                    doc["pickUpLat"].toString().toDouble(),
                                    doc["pickUpLong"].toString().toDouble()
                                )
                                val droppOffLatLng = LatLng(
                                    doc["dropOffLat"].toString().toDouble(),
                                    doc["dropOffLong"].toString().toDouble()
                                )
                                ListPickUpLatLng.add(pickUpLatLng)
                                ListDropOffLatLng.add(droppOffLatLng)
                                val booking = doc.toObject(Booking::class.java)
                                booking!!.bookingId = doc.id
                                listBooking.add(booking)
                                addMarkerPickUp(
                                    pickUpLatLng,
                                    "Pickup : ${booking.username}"
                                )
                                addMarkerDropOff(
                                    droppOffLatLng,
                                    "Dropoff : ${booking.username}"
                                )
                            }
                            if (ListDropOffLatLng.size > 0) {
                                Util.getGlobals().listOfCurrentBookings = listBooking
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
                                        initializeFlag = true
                                    }
                                }
                            }
                        }
                    }
            }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        MapsInitializer.initialize(root.context)
        mMap = googleMap
        if (permissionFlag) {
            try {
                val karachi = LatLng(25.1921465, 66.5949924)
                moveCamera(karachi, 6f)
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
        Toast.makeText(root.context, location.toString(), Toast.LENGTH_SHORT).show()
        if (initializeFlag) {
            moveCamera(LatLng(location!!.latitude, location.longitude), Util.getBiggerZoomValue())
            updateLocationInDb(location)
            if (Util.getDistance(
                    LatLng(location.latitude, location.longitude),
                    endingPointLatLng
                ) < 10000
            ) {
                endingFlag = true
            }
            if(Util.getDistance(LatLng(location.latitude, location.longitude),
                    startingPointLatLng) < 10000){
                startingFlag = true
            }
            for (booking in listBooking) {
                if (Util.getDistance(
                        LatLng(location.latitude, location.longitude),
                        LatLng(booking.pickUpLat!!, booking.pickUpLong!!)
                    ) < 1000 && booking.rideStatus == "Booked"
                ) {
                    //Bottom sheet
                    val view = layoutInflater.inflate(R.layout.activity_pickup_bottomsheet, null)
                    val dialog = BottomSheetDialog(root.context)
                    view.findViewById<Button>(R.id.btnPickUp).onClick {
                        //Update booking status
                        booking.rideStatus = "pickedUp"

                        //Updating ride status in db
                        Util.getFireStoreInstance().collection("Booking")
                            .document(booking.bookingId.toString())
                            .set(booking)
                            .addOnSuccessListener {
                                Util.getGlobals().listOfCurrentBookings = listBooking
                                dialog.dismiss()
                            }
                    }
                    view.findViewById<TextView>(R.id.bookingNameTextView).text =
                        booking.username

                    dialog.setContentView(view)
                    dialog.show()

                } else if (Util.getDistance(
                        LatLng(location.latitude, location.longitude),
                        LatLng(booking.dropOffLat!!, booking.dropOffLong!!)
                    ) < 1000 && booking.rideStatus == "pickedUp"
                ) {
                    val view = layoutInflater.inflate(R.layout.activity_dropoff_bottomsheet, null)
                    val dialog = BottomSheetDialog(root.context)
                    view.findViewById<Button>(R.id.btnDropOff).onClick {
                        booking.rideStatus = "completed"

                        Util.getFireStoreInstance().collection("Booking")
                            .document(booking.bookingId.toString())
                            .set(booking)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    root.context,
                                    "Ride ended successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                Util.getGlobals().listOfCurrentBookings = listBooking
                                dialog.dismiss()
                            }

                    }
                    view.findViewById<TextView>(R.id.bookingNameTextView).text =
                        booking.username

                    dialog.setContentView(view)
                    dialog.show()
                }
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {
        val alertDialog = Util.getAlertDialog(root.context)
        alertDialog.setMessage("Location need to be opened to use this application. Would you like to proceed?")
        alertDialog.setPositiveButton("Ok") { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        alertDialog.setNegativeButton("No") { _, _ ->
            activity!!.finishAffinity()
            exitProcess(0)
        }

    }


}