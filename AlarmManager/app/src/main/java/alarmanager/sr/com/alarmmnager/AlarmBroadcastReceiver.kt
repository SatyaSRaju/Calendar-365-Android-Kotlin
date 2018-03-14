package alarmanager.sr.com.alarmmnager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Created by SRaju on 7/18/17.
 */

class AlarmBroadcastReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent!!.action.equals("com.sr.alarmmanager")) {
            var bundle = intent.extras
            Toast.makeText(context,bundle.getString("Message"),Toast.LENGTH_LONG).show()
        } else if(intent!!.action.equals("android.intent.action.BOOT_COMPLETED")) {

            val oAlarmSettings = SaveAlarmSettings(context!!)
            oAlarmSettings.setAlarm()

        }

    }
}