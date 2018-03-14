package alarmanager.sr.com.alarmmnager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import java.util.*

/**
 * Created by SRaju on 7/18/17.
 */

class SaveAlarmSettings {

    var context:Context? = null
    var sharedRef:SharedPreferences? = null
    constructor(context: Context) {
        this.context = context
        this.sharedRef = context.getSharedPreferences("AlarmRef", Context.MODE_PRIVATE)
    }

    fun saveAlarmInSharedRef(hr:Int, mnt:Int) {
        var edt =sharedRef!!.edit()
        edt.putInt("Hour",hr)
        edt.putInt("Minutes", mnt)
        edt.commit()
    }

    fun readHourFromSharedRef(): Int {
        return sharedRef!!.getInt("Hour",0)
    }

    fun readMinutesFromSharedRef(): Int {
        return sharedRef!!.getInt("Minutes",0)
    }

    fun setAlarm() {
        val hour:Int = readHourFromSharedRef()
        val minute:Int = readMinutesFromSharedRef()
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE,minute)
        cal.set(Calendar.SECOND,0)


        val alarmMgr = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var intent = Intent(context,AlarmBroadcastReceiver::class.java)
        intent.putExtra("Message", "Alarm Time")
        intent.action="com.sr.alarmmanager"
        val pendingIntent =  PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,cal.timeInMillis,AlarmManager.INTERVAL_DAY,pendingIntent)
    }

}