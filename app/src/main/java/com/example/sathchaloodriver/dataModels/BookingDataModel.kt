package com.example.prototype.dataModels

import java.io.Serializable

class Booking : Serializable {

    var routeID: String? = null
    var userID: String? = null
    var pickUpTime: String? = null
    var pickUpAddress: String? = null
    var dropOffTime: String? = null
    var dropOffAddress: String? = null
    var bookingId: String? = null
    var username: String? = null
    var id: String = ""
    var rideStatus: String = ""

    //Stop Details
    var pickUpLat: Double? = null
    var pickUpLong: Double? = null
    var dropOffLat: Double? = null
    var dropOffLong: Double? = null



    constructor() {}



}