package tictactoe.sr.com.tictactoeonline

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import android.content.Intent
import com.google.firebase.database.FirebaseDatabase
import android.util.Log

class Login : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    private var dbFirebase = FirebaseDatabase.getInstance()
    private var dbRef = dbFirebase.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
    }


    fun buLoginEvent (view:View) {

        Log.i("buLoginEvent", "In Progress")

        LoginToFirebase(edEmail.text.toString(), edPassword.text.toString())
    }

    fun LoginToFirebase (email:String, password: String) {
        Log.i("LoginToFirebase", "In Progress")

        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Login Success", Toast.LENGTH_LONG).show()

                        //Save to Firebase Database
                        var currentUser = mAuth!!.currentUser
                            if (currentUser != null ) {
                                dbRef.child("Users").child(splitString(currentUser.email.toString())).child("Request").setValue(currentUser.uid)
                            }
                            LoadMainActivity()
                    } else {
                        Toast.makeText(applicationContext, "Login Failed. Please check credentials", Toast.LENGTH_LONG).show()
                    }
                }
    }


    override fun onStart() {
        super.onStart()
        LoadMainActivity()
    }

    fun splitString(pSTR: String) : String {

        var name = pSTR.split("@")
        Log.i("SplitString ", "The Name is "+ name)
        return name[0]
    }

    fun LoadMainActivity () {
        var currentUser = mAuth!!.currentUser
        if (currentUser != null ) {
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)
            startActivity(intent)
        }
    }
}

