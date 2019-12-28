package com.example.prototype.dataModels

import java.io.Serializable

class Booking : Serializable {

    var routeId: String? = null
    var customerID: String? = null
    var startingTime: String? = null
    var startingPoint: String? = null
    var endingTime: String? = null
    var endingPoint: String? = null
    var bookingId: Long = 0
    var numberOfSeats: Long = 0
    var bookingDate: String? = null
    var bookingMadeBy: String? = null
    var totalFare: Long = 0
    var id: String = ""
    var rideStatus: String = ""

    //Stop Details
    var pickupLat: Double? = null
    var pickupLong: Double? = null
    var dropOffLat: Double? = null
    var dropOffLong: Double? = null
    var pickUpSpotName: String? = null
    var dropOffSpotName: String? = null


    constructor() {}
    constructor(
        routeId: String?,
        customerID: String,
        startingTime: String?,
        startingPoint: String?,
        endingTime: String?,
        endingPoint: String?,
        bookingId: Long,
        numberOfSeats: Long,
        bookingDate: String?,
        bookingMadeBy: String?,
        totalFare: Long,
        pickupLat: Double,
        pickupLong: Double,
        dropOffLat: Double,
        dropOffLong: Double,
        pickUpSpotName: String,
        dropOffSpotName: String,
        rideStatus: String
    ) {
        this.routeId = routeId
        this.customerID = customerID
        this.startingTime = startingTime
        this.startingPoint = startingPoint
        this.endingTime = endingTime
        this.endingPoint = endingPoint
        this.bookingId = bookingId
        this.numberOfSeats = numberOfSeats
        this.bookingDate = bookingDate
        this.bookingMadeBy = bookingMadeBy
        this.totalFare = totalFare
        this.pickupLat = pickupLat
        this.pickupLong = pickupLong
        this.dropOffLat = dropOffLat
        this.dropOffLong = dropOffLong
        this.pickUpSpotName = pickUpSpotName
        this.dropOffSpotName = dropOffSpotName
        this.rideStatus = rideStatus
    }


}