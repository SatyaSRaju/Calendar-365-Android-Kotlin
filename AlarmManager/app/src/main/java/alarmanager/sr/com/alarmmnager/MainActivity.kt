package alarmanager.sr.com.alarmmnager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormatSymbols

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val oAlarmSettings = SaveAlarmSettings(applicationContext)
        tvTimer.text =oAlarmSettings.readHourFromSharedRef().toString() + ":" +oAlarmSettings.readMinutesFromSharedRef() .toString()

    }

    fun buSetTimer(view:View) {
        val popTimer = PopUpTimer()
        val fragMgr = fragmentManager
        popTimer.show(fragMgr,"Select Time")
    }

    fun setTimer(hrs:Int, mins:Int) {
        tvTimer.text = hrs.toString() + ":" + mins.toString()
        val oAlarmSettings = SaveAlarmSettings(applicationContext)
        oAlarmSettings.saveAlarmInSharedRef(hrs,mins)
        oAlarmSettings.setAlarm()

    }
}
