package pokemonandroid.sr.com.pokemonandroid

import android.location.Location

/**
 * Created by SRaju on 6/10/17.
 */
class Pokemon {

    var name: String? = null
    var desc: String? = null
    var img: Int? = null
    var power: Double? = null
    var isCaught: Boolean = false
    var location:Location? = null

    constructor(name: String, desc: String, img: Int, lat: Double, lon: Double,power: Double) {
    this.name = name
    this.desc = desc
    this.img = img
    this.location = Location(name)
    this.location!!.latitude = lat
    this.location!!.longitude = lon
    this.power = power
    this.isCaught = false
    }


}