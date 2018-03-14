package android.sr.com.findmyphone

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_phone_tracker.*
import kotlinx.android.synthetic.main.contact_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var adpUserContact: UserContactsMainAdapter? = null
    var oUserContact = ArrayList<UserContacts>()
    var mDBRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val usrInfo = UserInfo(this)
            usrInfo.getInitialPhoneNmbrFromSharedPref()
            //For Debug Onlu
            //loadStaticContacts()
            adpUserContact = UserContactsMainAdapter(this, oUserContact)
            lvUserContacts.adapter = adpUserContact
            lvUserContacts.onItemClickListener = AdapterView.OnItemClickListener {
                parent, view, position, id ->
                val usrInfoPos = oUserContact[position]
                val dtFormat = SimpleDateFormat("yyyy/MM/dd HH:MM:SS")
                val dt = Date()
                mDBRef!!.child("Users").child(usrInfoPos.contactPhNumber).child("Request").setValue(dtFormat.format(dt).toString())

                /** Capture Phone Number and call Maps Activity*/
                val intent = Intent(applicationContext,MapsActivity::class.java)
                intent.putExtra("contactPhNumber", usrInfoPos.contactPhNumber)
                Log.i("ListenerIntent", usrInfoPos.contactPhNumber)
                startActivity(intent)
            }
        } catch(ex:Exception) {Log.e("onCreate", ex.message.toString()) }

    }


    override fun onResume() {
        super.onResume()
        val usrInfo = UserInfo(this)
        if (usrInfo.getPhoneNmbrFromSharedPref() == "Empty") {
            return
        }
        refreshFindMeContacts()
        if (MapService.isServiceOn) return
        chkContactPermissions()
        chkLocPermission()
        trackUserLocation()
    }

    fun refreshFindMeContacts() {

        val usrInfo = UserInfo(this)
        mDBRef!!.child("Users").child(usrInfo.getPhoneNmbrFromSharedPref()).child("Finders").addValueEventListener(object: ValueEventListener{

            override fun onCancelled(p0: DatabaseError?) {
               // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {

                try {

                    Log.w("onDataChange", dataSnapshot!!.hasChildren().toString())

                    val tblData = dataSnapshot!!.value as HashMap<String, Any?>
                    oUserContact.clear()
                    if (tblData == null) {
                        Log.w("onDataChange", "DataSnapsshot is null")
                        oUserContact.add(UserContacts("None", "None"))
                        adpUserContact!!.notifyDataSetChanged()
                        return
                    }

                    for (key in tblData.keys) {
                        Log.i("Table Data", tblData.size.toString())
                        val name = hashMapOfContacts[key]
                        oUserContact.add(UserContacts(name.toString(), key))
                        Log.i("onDataChange Key Values", key + " -> " + name)
                        adpUserContact!!.notifyDataSetChanged()


                    }
                }catch (ex:Exception) {
                    Log.e("onDataChange", ex.message.toString())
                    oUserContact.clear()
                    oUserContact.add(UserContacts("None", "None"))
                    adpUserContact!!.notifyDataSetChanged()
                    return
                }
            }

        })
    }

    fun loadStaticContacts() {
        oUserContact.add(UserContacts("Anvit Raju", "9402317200"))
        oUserContact.add(UserContacts("Rajesh Chaganti", "2149983609"))
        oUserContact.add(UserContacts("Aishani Raju", "9405367879"))


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        try {
            when (item!!.itemId) {
                R.id.mAddTracker -> {
                    val intent = Intent(this, PhoneTracker::class.java)
                    startActivity(intent)
                }
                R.id.mHelp -> {
                   val intent = Intent(this,findme_help::class.java)
                    startActivity(intent)
                }
                else -> {
                    return super.onOptionsItemSelected(item)
                }
            }

        } catch (ex: Exception) {Log.e ("onOptionsItemSelected", ex.message.toString())}
        return true
    }

    inner class UserContactsMainAdapter: BaseAdapter {

        var oUserContactAdp = ArrayList<UserContacts>()
        var cntxt : Context? = null
        constructor(cntxt: Context, oUserContactAdp: ArrayList<UserContacts>) {
            this.cntxt = cntxt
            this.oUserContactAdp = oUserContactAdp
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val userContacts = oUserContactAdp[p0]
            if (userContacts.contactName.equals("None")) {
                //val inflator = cntxt!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val userContactNView = layoutInflater.inflate(R.layout.users_none,null)
                return userContactNView

            } else {
                Log.i ("getView - MainActivity", "Entering ")
                val userContactView = layoutInflater.inflate(R.layout.contact_view,null)
                userContactView.tvName.text = userContacts.contactName
                userContactView.tvPhoneNumber.text = userContacts.contactPhNumber
                return userContactView
            }
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            REQ_CODE_CONTACTS ->
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadContacts()
                } else {
                    Toast.makeText(this, "Permission Not Granted to access Contacts", Toast.LENGTH_LONG).show()
                }
            }
            LOC_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    trackUserLocation()
                } else {
                    Toast.makeText(this, "Permission Not Granted to access Contacts", Toast.LENGTH_LONG).show()
                }
            }
            else -> { super.onRequestPermissionsResult(requestCode, permissions, grantResults)}
        }
    }
    var hashMapOfContacts = HashMap<String, String>()

    fun loadContacts() {

        try {
            hashMapOfContacts.clear()
            val contactsCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
            contactsCursor.moveToFirst()

            do {
                val contactName = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val contactPhNumber = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                hashMapOfContacts.put(UserInfo.formatPhoneNumber(contactPhNumber), contactName)

            } while (contactsCursor.moveToNext())

        } catch(ex:Exception){Log.e("loadContacts", ex.message.toString())}

    }


    val REQ_CODE_CONTACTS = 1603
    fun chkContactPermissions() {

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) !=
                        PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), REQ_CODE_CONTACTS)
                    return
                }
            }
            loadContacts()
        } catch (ex:Exception) {Log.e("Contact Permission", ex.message.toString())}
    }

    val LOC_CODE = 1604

    fun chkLocPermission() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOC_CODE)
                    return
                }
            }

            trackUserLocation()
        } catch (ex:Exception){Log.e("Loc Permission", ex.message.toString())}
    }


    fun trackUserLocation() {

        if(!MapService.isServiceOn) {
            val intent = Intent(baseContext,MapService::class.java)
            startService(intent)
        }

    }


}
