package com.example.sathchaloodriver.dataModels

import java.io.Serializable

class NotificationDataModel :Serializable{
    var heading: String? = null
    var subHeading:String? = null

    constructor( heading: String, subHeading:String){

        this.heading = heading
        this.subHeading = subHeading
    }
    constructor()
}