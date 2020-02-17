package com.example.sathchaloodriver.dataModels

import java.io.Serializable

class RoutesDataModel : Serializable {
    var startingAddress: String? = null
    var endingAddress: String? = null
    var startingTime: String? = null
    var routeID: String? = null

    constructor()

    constructor(startingAddress: String, endingAddress: String, startingTime: String){
        this.startingAddress = startingAddress
        this.endingAddress = endingAddress
        this.startingTime = startingTime
    }
}