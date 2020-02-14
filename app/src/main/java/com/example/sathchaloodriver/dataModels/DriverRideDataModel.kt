package com.example.sathchaloodriver.dataModels

import com.example.prototype.dataModels.Booking
import java.io.Serializable

class DriverRideDataModel : Serializable{

    var driverID: String? = null
    var listOfBookings: MutableList<Booking>? = null
    var date: String? = null

    constructor(driverID: String, listOfBookings: MutableList<Booking>, date: String){
        this.driverID = driverID
        this.listOfBookings = listOfBookings
        this.date = date
    }

}