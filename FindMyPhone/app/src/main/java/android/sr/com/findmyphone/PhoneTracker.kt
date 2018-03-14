package android.sr.com.findmyphone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

import kotlinx.android.synthetic.main.activity_phone_tracker.*
import kotlinx.android.synthetic.main.contact_view.view.*

class PhoneTracker : AppCompatActivity() {

    var adpUserContact: UserContactsAdapter? = null
    var oUserContact = ArrayList<UserContacts>()
    var usrInfoContactData:UserInfo? = null
    val mDBRef =  FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_tracker)
        usrInfoContactData  =  UserInfo(applicationContext)

        // loadContacts()
        adpUserContact = UserContactsAdapter(this,oUserContact)
        lvUserContacts.adapter = adpUserContact
        lvUserContacts.onItemClickListener = AdapterView.OnItemClickListener {
            parent,view,position,id ->
            val usrInfoPos = oUserContact[position]
            UserInfo.phTracker.remove(usrInfoPos.contactPhNumber)
            usrInfoContactData!!.saveUserContactsInSharedPref()
            usrInfoContactData!!.loadUserContactFromSharedPref()

            //Clean up / remove from Firebase.
            val mDBRef = FirebaseDatabase.getInstance().reference

            val usrInfo =  UserInfo(applicationContext)
            mDBRef.child("Users").child(usrInfoPos.contactPhNumber).child("finders").child(usrInfo.getPhoneNmbrFromSharedPref()).removeValue()
            refreshTrackerData()

        }
        usrInfoContactData!!.loadUserContactFromSharedPref()
        refreshTrackerData()
    }

    fun loadContacts() {
        oUserContact.add(UserContacts("Anvit", "9723332107"))

    }

    val REQ_CODE_CONTACTS = 1603
    fun chkForPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS),REQ_CODE_CONTACTS)
                return
            }
        }
        retrieveContacts()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            REQ_CODE_CONTACTS ->
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    retrieveContacts()
                } else {
                    Toast.makeText(this, "Permission Not Granted to access Contacts", Toast.LENGTH_LONG).show()
                }
            }
            else -> { super.onRequestPermissionsResult(requestCode, permissions, grantResults)}
        }
    }
    val PICK_CONTACT_CODE = 1672
    fun retrieveContacts() {
        val intent = Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent,PICK_CONTACT_CODE)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        Log.i("onActivityResult", "In Progress")
        when (requestCode) {
            PICK_CONTACT_CODE -> if (resultCode == Activity.RESULT_OK) {
                val contactData = data.data
                Log.i("onActivityResult", "Result OK")
                val cur = contentResolver.query(contactData, null, null, null, null)
                if (cur!!.count > 0) {// thats mean some resutl has been found
                    Log.i("onActivityResult", "Contacts Found")
                    if (cur.moveToNext()) {
                        val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                        val name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        Log.i("Names", name)

                        if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                            val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)
                            while (phones!!.moveToNext()) {
                                var phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                Log.i("Number", phoneNumber)
                                phoneNumber = UserInfo.formatPhoneNumber(phoneNumber)
                                UserInfo.phTracker.put(phoneNumber, name)
                                usrInfoContactData!!.saveUserContactsInSharedPref()

                                //Save into Firebase
                                val usrInfo =  UserInfo(applicationContext)
                                mDBRef.child("Users").child(phoneNumber).child("Finders").child(usrInfo.getPhoneNmbrFromSharedPref()).setValue(true)

                                refreshTrackerData()

                            }
                            phones.close()
                        }
                    }
                }
                cur.close()
            } else {
                Log.i ("onActivityResult", "Result Not OK")
                super.onActivityResult(requestCode, resultCode, data)
            }

        }

    }
    fun refreshTrackerData() {
        oUserContact.clear()
        for ((key, value) in UserInfo.phTracker) {
            Log.i("refreshTrackerData", "Key is " + key + " Value is " + value )
            oUserContact.add(UserContacts(value, key))
            Log.i("refreshTrackerData", oUserContact.size.toString() )

        }
        adpUserContact!!.notifyDataSetChanged()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.phonetrackermenu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        try {
            when (item!!.itemId) {
                R.id.mFinishActivity -> {
                    finish()
                }
                R.id.mAddContact -> {
                    chkForPermissions()
                }
                else -> {
                    return super.onOptionsItemSelected(item)
                }
            }
        } catch (ex:Exception) {Log.e("onOptionsItemSel", ex.message.toString())}
        return true

    }

    inner class UserContactsAdapter: BaseAdapter {

        var oUserContactAdp = ArrayList<UserContacts>()
        var cntxt : Context? = null
        constructor(cntxt:Context, oUserContactAdp: ArrayList<UserContacts>) {
            this.cntxt = cntxt
            this.oUserContactAdp = oUserContactAdp
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val userContacts = oUserContactAdp[p0]
            val userContactView = layoutInflater.inflate(R.layout.contact_view,null)
            userContactView.tvName.text = userContacts.contactName
            userContactView.tvPhoneNumber.text = userContacts.contactPhNumber
            return userContactView
        }

        override fun getItem(p0: Int): Any {
            return oUserContactAdp[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return oUserContactAdp.size
        }

    }

}
