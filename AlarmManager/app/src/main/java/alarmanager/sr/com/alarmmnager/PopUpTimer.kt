package alarmanager.sr.com.alarmmnager

import android.app.DialogFragment
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
/**
 * Created by SRaju on 7/10/17.
 */

class PopUpTimer: DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var popUpView = inflater!!.inflate(R.layout.popup_timer,container,false)

        var butDon: Button = popUpView.findViewById(R.id.buDone) 
        var timePicker:TimePicker = popUpView.findViewById(R.id.tpicker)

        butDon.setOnClickListener({
            val mActivity = activity as MainActivity
            if (Build.VERSION.SDK_INT >= 23) {
                mActivity.setTimer(timePicker.hour,timePicker.minute)
            } else {
                mActivity.setTimer(timePicker.currentHour,timePicker.currentMinute)
            }
            this.dismiss()
        })
        return popUpView
    }

}