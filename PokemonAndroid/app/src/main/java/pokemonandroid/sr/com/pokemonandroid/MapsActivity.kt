package pokemonandroid.sr.com.pokemonandroid

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.File


class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    override fun onStop() {
        Log.i("onStop", "Delete Cache")
        deleteCache(this)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        /** Call CheckPermission method to get Access*/
        CheckPermission()
        Toast.makeText(this, "Loading Pokemons", Toast.LENGTH_LONG).show()
        loadPockemons()
    }

    var currentLoc:Location? = null


    inner class MyLocationListner:LocationListener {
        constructor() {
            currentLoc = Location("String")
            currentLoc!!.latitude = 0.0
            currentLoc!!.longitude = 0.0

        }
        override fun onLocationChanged(location: Location?) {
            currentLoc = location
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        //    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(provider: String?) {
          //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(provider: String?) {
          //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }


    /**
        Check Permisson. If the device is running Android 5.1 or lower, or your app's target SDK is 22 or lower:
        If you list a dangerous permission in your manifest, the user has to grant the permission when they install the app;
        if they do not grant the permission, the system does not install the app at all.
        If the device is running Android 6.0 or higher, and your app's target SDK is 23 or higher:
        The app has to list the permissions in the manifest, and it must request each dangerous permission it needs while the app is running.
        The user can grant or deny each permission, and the app can continue to run with limited capabilities even if the user denies a
        permission request.
     */

    val ACCESSLOCATION = 333

    fun CheckPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESSLOCATION)
                return
            }
        }
        GetUserLocation()
    }

    /**
     * When your app requests permissions, the system presents a dialog box to the user. When the user responds,
     * the system invokes your app's onRequestPermissionsResult() method, passing it the user response.
     * Your app has to override that method to find out whether the permission was granted.
     * The callback is passed the same request code you passed to requestPermissions().
     * For example, if an app requests ACCESS_FINE_LOCATION access it will have the following callback method:
     *
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode) {
            ACCESSLOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetUserLocation()
                } else {
                    Toast.makeText(this, "Cannot Access Location", Toast.LENGTH_LONG).show()
                }

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun deleteCache(context: Context) {
        try {
            val dir = context.cacheDir
            if (dir != null && dir.isDirectory) {
                Log.i ("deleteCache", "Calling Delete Dir")
                deleteDir(dir)
            }
        } catch (e: Exception) {
        }

    }

    fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir!!.isDirectory()) {
            val children = dir!!.list()
            for (i in children.indices) {
                Log.i ("deleteDir", "Calling Delete Dir " + i)
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir!!.delete()
    }


    fun GetUserLocation() {
        Toast.makeText(this, "User location access ON", Toast.LENGTH_LONG).show()
        var myLocation = MyLocationListner()
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocation)
        var mythread = MyThread()
        mythread.start()
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


    }

    var oldLocation: Location?=null

    inner class MyThread: Thread {
        constructor() : super() {
            oldLocation= Location("Start")
            oldLocation!!.latitude = 0.0
            oldLocation!!.longitude = 0.0
        }

        override fun run() {
            while (true) {
                try{

                    if (oldLocation!!.distanceTo(currentLoc) == 0f) {
                        continue
                    }

                    oldLocation = currentLoc

                    runOnUiThread() {

                        //Show Me
                        mMap!!.clear()
                        val sydney = LatLng(currentLoc!!.latitude, currentLoc!!.longitude)
                        mMap!!.addMarker(MarkerOptions()
                                .position(sydney)
                                .title("Marker in Sydney")
                                .snippet("here is my location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,14f))

                      //Show Pokeman
                        for (i in 0 .. (arrLstPockemons.size-1)) {
                            var newPockemon = arrLstPockemons[i]
                            if (newPockemon.isCaught == false) {
                                val pockemonLoc = LatLng(newPockemon.location!!.latitude, newPockemon.location!!.longitude)
                                mMap!!.addMarker(MarkerOptions()
                                        .position(pockemonLoc)
                                        .title(newPockemon.name!!)
                                        .snippet(newPockemon.desc!! + "power " + newPockemon.power)
                                        .icon(BitmapDescriptorFactory.fromResource(newPockemon.img!!)))

                                if (currentLoc!!.distanceTo(newPockemon.location) < 2f) {
                                    newPockemon.isCaught=true
                                    arrLstPockemons[i] = newPockemon
                                    playerPower += newPockemon.power!!
                                    Toast.makeText(applicationContext,"You caught Pokemon. Your power now is " + playerPower, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    Thread.sleep(1000)
                } catch(ex: Exception) {

                }
            }
        }
    }

    var playerPower = 0.0
    var arrLstPockemons = ArrayList<Pokemon>()

    fun loadPockemons() {


        arrLstPockemons.add(Pokemon("Charmender", "Charmander living in Flower Mound", R.drawable.charmander,33.067534,-97.05908569999997, 200.50))
        arrLstPockemons.add(Pokemon("Bulbasaur", "Bulbasaur living in Dallas", R.drawable.bulbasaur,39.0110587,-77.47114069999998, 50.00))
        arrLstPockemons.add(Pokemon("Squirtle", "Squirtle living in California", R.drawable.squirtle,37.7949568502667,-122.410494089127, 100.50))

    }

}
