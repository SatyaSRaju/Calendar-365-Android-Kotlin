package android.sr.com.findmyphone

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import java.text.SimpleDateFormat
import java.util.*


class Login : AppCompatActivity() {
    private var mAuth:FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        try {
            Log.i("onCreate", "InProgress")
            mAuth = FirebaseAuth.getInstance()
            signInAnonymously()
        } catch (ex: Exception) {Log.e("Login",ex.message.toString())}
    }

    fun signInAnonymously() {
        Log.i("signInAnonymously", "Started Execution")

        try {
            mAuth!!.signInAnonymously()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(applicationContext, "Authentication  Successful.",
                                    Toast.LENGTH_SHORT).show()
                            val user = mAuth!!.getCurrentUser()

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(applicationContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                            Log.e("signInAnonymously", task.exception.toString())

                        }
                    }
        } catch (ex: Exception) {
            Log.e("signInAnonymously", ex.message.toString())
        }
    }

    fun buRegisterEvent(view:View) {

        Log.i ("buRegisterEvent", "Started  Execution")

        try {
            val usrInfo = UserInfo(this)
            if (edPhoneNumber.text.trim().toString().equals(null)) {
                Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_LONG)
                return
            }
            usrInfo.saveInSharedPref(edPhoneNumber.text.toString())
            val mDBRef = FirebaseDatabase.getInstance().reference
            val dtFormat = SimpleDateFormat("yyyy/MM/dd HH:MM:SS")
            val dt = Date()
            mDBRef.child("Users").child(edPhoneNumber.text.toString()).child("Request").setValue(dtFormat.format(dt).toString())
            mDBRef.child("Users").child(edPhoneNumber.text.toString()).child("Finders").setValue(dtFormat.format(dt).toString())
            finish()
        } catch (ex: Exception) {Log.e("buRegisterEvent", ex.message.toString())}
    }
}
