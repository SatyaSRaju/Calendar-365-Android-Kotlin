package android.sr.com.findmyphone

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var mDBRef : DatabaseReference ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        try {
            val bundle: Bundle = intent.extras
            val contactPhNumber =  bundle.getString("contactPhNumber")

            Log.i("Maps:Contact Ph Number",contactPhNumber)
            mDBRef = FirebaseDatabase.getInstance().reference
            mDBRef!!.child("Users").child(contactPhNumber).child("Location").addValueEventListener(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {
                    try{
                        val tblData = p0!!.value as HashMap<String,Any>
                        val lat = tblData["Lat"].toString()
                        val lon = tblData["Lon"].toString()
                        val lastSeen = tblData["LastSeen"].toString()
                        MapsActivity.mGeoLoc = LatLng(lat.toDouble(), lon.toDouble())
                        MapsActivity.mLastSeen = lastSeen
                        loadMap()
                    }catch (ex:Exception){Log.e("Maps onDataChange",ex.message.toString())}
                }


            })
        } catch (ex:Exception){Log.e("onCreate", ex.message.toString())}

    }

    fun loadMap() {


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

    companion object {
        var mGeoLoc = LatLng(-34.0, 151.0)
        var mLastSeen = "Not Defined"
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

        mMap.addMarker(MarkerOptions().position(mGeoLoc).title(mLastSeen))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mGeoLoc,10f))
    }
}
