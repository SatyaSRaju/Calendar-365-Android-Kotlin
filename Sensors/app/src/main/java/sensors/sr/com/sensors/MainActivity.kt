package sensors.sr.com.sensors

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout
import android.widget.Toast

class MainActivity : AppCompatActivity(), SensorEventListener {
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    var sensor:Sensor? = null
    var sensorMgr:SensorManager? = null


    var isRunning:Boolean = false

    override fun onSensorChanged(sEvent: SensorEvent?) {
       if (sEvent!!.values[0]> 40 && isRunning == false) {
           isRunning = true
           try{
               var mPlayer:MediaPlayer? = null
               var url:String? = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
               mPlayer!!.setDataSource(url)
               mPlayer!!.prepare()
               mPlayer!!.start()

           }catch (ex:Exception){}

       }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorMgr = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorMgr!!.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    override fun onResume() {
        super.onResume()
        sensorMgr!!.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onStop() {
        super.onStop()
        sensorMgr!!.unregisterListener(this)
    }
}
