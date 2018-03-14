package android.sr.com.findmyphone

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import java.util.HashMap

/**
 * Created by SRaju on 7/19/17.
 */
class UserInfo {

    var context:Context? = null
    var sharedRef:SharedPreferences? = null
    constructor(context: Context) {
        this.context = context
        this.sharedRef = context.getSharedPreferences("FMPUserInfo", Context.MODE_PRIVATE)
    }

    fun saveInSharedPref(phNumber:String) {
        val edt = sharedRef!!.edit()
        edt.putString("PhoneNumber", phNumber)
        edt.apply()
    }

    fun getPhoneNmbrFromSharedPref():String {
        val phNumber = sharedRef!!.getString("PhoneNumber", "empty")
        return phNumber
    }

    fun getInitialPhoneNmbrFromSharedPref() {
        val phNumber = sharedRef!!.getString("PhoneNumber", "empty")
        if (phNumber.equals("empty")) {
            val intent = Intent(context,Login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(intent)
        }

    }


    fun saveUserContactsInSharedPref() {
        var mapOfPhoneTrackers = ""

        for ((key, value) in phTracker) {
            if (mapOfPhoneTrackers.length == 0) {
                mapOfPhoneTrackers = key + "%" + value
            } else {
                mapOfPhoneTrackers += "%"+ key + "%" + value
            }
        }

        if (mapOfPhoneTrackers.length == 0) {
            mapOfPhoneTrackers = "Empty"
        }
        val edt = sharedRef!!.edit()
        edt.putString("PhoneTracker", mapOfPhoneTrackers)
        edt.commit()
    }

    fun  loadUserContactFromSharedPref() {
        try {
            phTracker.clear()
            val phTrackerContacts = sharedRef!!.getString("PhoneTracker", "Empty")
            if (!phTrackerContacts.equals("Empty")) {
                val usrContactInfo = phTrackerContacts.split("%").toTypedArray()
                Log.i("Load From Share", phTrackerContacts.toString() + "->" + usrContactInfo.size)

                var i = 0
                while (i < usrContactInfo.size-1) {
                    phTracker.put(usrContactInfo[i], usrContactInfo[i + 1])
                    Log.i("Shared", "Processing While Loop")
                    //Log.i("Contacts from Shared", (usrContactInfo[i] + "->" + usrContactInfo[i + 1]))
                    i += 2
                }
            }
        }catch (ex: Exception) {Log.e("Contacts From Shared", ex.message.toString())}
    }

    companion object {
        var phTracker: MutableMap<String,String> = HashMap()
        fun formatPhoneNumber(phNumber: String):String {
            var onlyNumber= phNumber.replace("[^0-9]".toRegex(),"")
            if (phNumber[0]== '+') {
                onlyNumber ="+"+ phNumber
            }
            return  onlyNumber
        }

    }


}