package com.example.sathchaloodriver.dataModels

import com.example.prototype.dataModels.Booking
import java.io.Serializable

class DriverRideDataModel : Serializable{

    var driverID: String? = null
    var startingAddress: String? = null
    var endingAddress: String? = null
    var routeID: String? = null
    var listOfBookings: MutableList<Booking>? = null
    var date: String? = null

    constructor(driverID: String, startingAddress: String,
                endingAddress: String,
                routeID: String,
                listOfBookings: MutableList<Booking>, date: String){
        this.driverID = driverID
        this.startingAddress = startingAddress
        this.endingAddress = endingAddress
        this.routeID = routeID
        this.listOfBookings = listOfBookings
        this.date = date
    }

}