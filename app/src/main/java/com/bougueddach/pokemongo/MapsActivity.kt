package com.bougueddach.pokemongo

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.bougueddach.pokemongo.model.Pockemon

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        checkPermission()
        loadPockemons()
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    var ACCESSLOCATION = 123

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), ACCESSLOCATION)
                return
            }
        }
        getUserLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            ACCESSLOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "We need permission to access to your location", Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        // Toast.makeText(this,"get location called", Toast.LENGTH_LONG).show()
        var myLocationListner = MyLocationListner()
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3, 3f, myLocationListner)
        var myThread = MyThread()
        myThread.start()
    }

    var myPower= 0.0
    var myLocation: Location? = null

    inner class MyLocationListner : LocationListener {
        constructor() {
            myLocation = Location("start")
            myLocation!!.latitude = 0.0
            myLocation!!.longitude = 0.0
        }

        override fun onLocationChanged(location: Location?) {
            myLocation = location
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(provider: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(provider: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    var oldLocation: Location? = null

    inner class MyThread : Thread {
        constructor() : super() {
            oldLocation = Location("start")
            oldLocation!!.latitude = 0.0
            oldLocation!!.longitude = 0.0
        }

        override fun run() {
            while (true) {
                if (oldLocation!!.distanceTo(myLocation) == 0f) {
                    continue
                }
                oldLocation = myLocation
                try {
                    runOnUiThread {
                        mMap!!.clear()
                        //show me
                        val location = LatLng(myLocation!!.latitude, myLocation!!.longitude)
                        mMap.addMarker(MarkerOptions()
                                .position(location)
                                .title("Me")
                                .snippet("Here is my location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
                        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 11f))

                        // show pockemons
                        for (i in 0..pockemonList.size - 1) {
                            var newPockemon = pockemonList[i]
                            if (newPockemon.isCatched == false) {
                                val pockemonLocation = LatLng(newPockemon.location!!.latitude!!, newPockemon.location!!.longitude!!)
                                var newmarker=mMap.addMarker(MarkerOptions()
                                        .position(pockemonLocation)
                                        .title(newPockemon.name)
                                        .snippet(newPockemon.description + ". power: " + newPockemon.power)
                                        .icon(BitmapDescriptorFactory.fromResource(newPockemon.image!!)))
                                if (newPockemon.location!!.distanceTo(myLocation) < 2) {
                                    newPockemon.isCatched = true
                                    pockemonList[i] = newPockemon
                                    myPower += newPockemon.power!!
                                    newmarker.remove()
                                    Toast.makeText(applicationContext," Pockemon ${newPockemon.name} is catched",Toast.LENGTH_LONG).show()
                                    Toast.makeText(applicationContext, " Your new power is $myPower", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    Thread.sleep(1000)
                } catch (e: Exception) {

                }
            }
        }
    }

    var pockemonList = ArrayList<Pockemon>()
    fun loadPockemons() {
        pockemonList.add(Pockemon("charmander", "desc", R.drawable.charmander, 55.0, 35.7344687, -5.892942))
        pockemonList.add(Pockemon("charmander", "desc", R.drawable.charmander, 55.0, 33.5771835, -7.6260191))
        pockemonList.add(Pockemon("squirtle", "desc", R.drawable.squirtle, 90.0, 33.5743409, -7.6835096))
        pockemonList.add(Pockemon("bulbasaur", "desc", R.drawable.bulbasaur, 20.0, 33.5269069, -7.6409483))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}
