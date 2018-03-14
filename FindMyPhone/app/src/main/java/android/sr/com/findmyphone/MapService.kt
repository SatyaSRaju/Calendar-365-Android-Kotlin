package android.sr.com.findmyphone

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by SRaju on 8/6/17.
 */
class MapService: Service() {

    private var mDBRef:DatabaseReference? = null

    override fun onBind(p0: Intent?): IBinder {
        return null!!
    }

    override  fun onCreate() {
        super.onCreate()
        mDBRef = FirebaseDatabase.getInstance().reference
        isServiceOn = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        try {
            var usrInfo = UserInfo(this)
            val mPhoneNumber = usrInfo.getPhoneNmbrFromSharedPref()
            var myLoc = findMeLocListener()
            var mLocMgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3f, myLoc)
            /** Listen to request*/
            mDBRef!!.child("Users").child(mPhoneNumber).child("Request").addValueEventListener(
                    object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(p0: DataSnapshot?) {

                            try {
                                if (MapService.myLoc == null) return

                                val dtFormat = SimpleDateFormat("yyyy/MM/dd HH:MM:SS")
                                val dt = Date()

                                mDBRef!!.child("Users").child(mPhoneNumber)
                                        .child("Location").child("Lat").setValue(MapService.myLoc!!.latitude)

                                mDBRef!!.child("Users").child(mPhoneNumber)
                                        .child("Location").child("Lon").setValue(MapService.myLoc!!.longitude)

                                mDBRef!!.child("Users").child(mPhoneNumber)
                                        .child("Location").child("LastSeen").setValue(dtFormat.format(dt).toString())
                                Log.i("trackUserLoc ->", "Lat ->" + MapService.myLoc!!.latitude + "Lon ->" + MapService.myLoc!!.longitude)

                            } catch (ex: Exception) {
                                Log.e("trackUserLoc", ex.message.toString())
                            }
                        }

                    }

            )
        }catch (ex:Exception) {
            Log.e("trackUserLoc", ex.message.toString() )
        }

        return Service.START_NOT_STICKY
    }


    companion object {
        var myLoc: Location? = null
        var isServiceOn = false
    }

    inner class findMeLocListener: LocationListener {

        constructor(): super() {
            myLoc = Location("Me")
            myLoc!!.latitude = 0.0
            myLoc!!.longitude = 0.0
        }
        override fun onLocationChanged(p0: Location?) {
            myLoc = p0
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }

    }

}