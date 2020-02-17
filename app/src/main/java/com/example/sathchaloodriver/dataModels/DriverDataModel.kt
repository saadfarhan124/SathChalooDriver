package com.example.sathchaloodriver.dataModels

import java.io.Serializable

class DriverDataModel:Serializable {
    var dateOfBirth: String? = null
    var gender: String? = null
    var email: String? = null
    var name: String? = null
    var number: String? = null
    var amount: Int? = 0

    constructor(dateOfBirth: String, gender: String, email: String, name: String, number: String, amount: Int) {
        this.dateOfBirth = dateOfBirth
        this.gender = gender
        this.email = email
        this.name = name
        this.number = number
        this.amount = amount

    }
}