package com.bougueddach.pokemongo.model

import android.location.Location

/**
 * Created by macbook on 11/8/17.
 */
class Pockemon {

    var name: String? = null
    var description: String? = null
    var image: Int? = null
    var power: Double? = null
    var location: Location? = null
    var isCatched: Boolean? = false

    constructor(name: String, description: String, image: Int, power: Double, latitude: Double, longitude: Double) {
        this.name = name
        this.description = description
        this.image = image
        this.power = power
        this.location=Location("start")
        this.location!!.latitude = latitude
        this.location!!.longitude = longitude
    }
}