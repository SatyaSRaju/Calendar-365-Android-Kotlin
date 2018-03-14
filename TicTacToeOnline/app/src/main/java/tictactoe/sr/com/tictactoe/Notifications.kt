package tictactoe.sr.com.tictactoeonline

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat

/**
 * Created by SRaju on 6/24/17.
 */

class Notifications {
 val NOTIFYTAG = "New Request"
    fun Notify(context: Context, msg: String, nmbr: Int) {
        val intent = Intent(context,Login::class.java)
        val builder = NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle("New Request")
                .setContentText(msg)
                .setNumber(nmbr)
                .setSmallIcon(R.drawable.tictactoe)
                .setContentIntent( PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
        val notMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ECLAIR) {
            notMgr.notify(NOTIFYTAG,0,builder.build())
        }else{
            notMgr.notify(NOTIFYTAG.hashCode(),builder.build())
        }



    }

}