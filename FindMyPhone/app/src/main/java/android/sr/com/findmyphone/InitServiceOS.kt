package android.sr.com.findmyphone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by SRaju on 8/6/17.
 */
class InitServiceOS: BroadcastReceiver() {
    override fun onReceive(cntxt: Context?, intent: Intent?) {
       if (intent!!.action.equals("android.intent.action.BOOT_COMPLETE") ) {
           val intent = Intent(cntxt,MapService::class.java)
           cntxt!!.startService(intent)
       }
    }

}