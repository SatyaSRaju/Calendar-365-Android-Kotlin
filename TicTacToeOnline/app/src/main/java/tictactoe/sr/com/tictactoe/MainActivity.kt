package tictactoe.sr.com.tictactoeonline

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var dbFirebase = FirebaseDatabase.getInstance()
    private var dbRef = dbFirebase.reference
    private var myEmail: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        var bundleFromLogin: Bundle = intent.extras
        myEmail = bundleFromLogin.getString("email")
        IncomingRequests()
    }



        
        fun buClick(view: View) {

            val buSelected = view as Button
            var cellID = 0
            Log.i("buClick", "InProgress")
            when (buSelected.id) {

                tictactoe.sr.com.tictactoeonline.R.id.bu1 -> cellID = 1
                tictactoe.sr.com.tictactoeonline.R.id.bu2 -> cellID = 2
                tictactoe.sr.com.tictactoeonline.R.id.bu3 -> cellID = 3
                tictactoe.sr.com.tictactoeonline.R.id.bu4 -> cellID = 4
                tictactoe.sr.com.tictactoeonline.R.id.bu5 -> cellID = 5
                tictactoe.sr.com.tictactoeonline.R.id.bu6 -> cellID = 6
                tictactoe.sr.com.tictactoeonline.R.id.bu7 -> cellID = 7
                tictactoe.sr.com.tictactoeonline.R.id.bu8 -> cellID = 8
                tictactoe.sr.com.tictactoeonline.R.id.bu9 -> cellID = 9
            }

            //Toast.makeText(this,"ID :" + cellID, Toast.LENGTH_SHORT).show()
            dbRef.child("OnlinePlayers").child(sessionID).child(cellID.toString()).setValue(myEmail)
        }


    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()
    var activePlayer = 1


    protected fun PlayGame(cellID: Int, buSelected: Button) {

        if (activePlayer == 1) {
            buSelected.text = "X"
            buSelected.setBackgroundResource(R.color.blue)
            player1.add(cellID)
            activePlayer = 2

        } else {
            buSelected.text = "O"
            buSelected.setBackgroundResource(R.color.darkgreen)
            player2.add(cellID)
            activePlayer = 1

        }
        buSelected.isEnabled = false
        CheckWinner()
    }

    /* Check to see if there is a Winner. Scan thro all rows , straight and horizontal columns
    *
    *
    * */


    protected fun CheckWinner() {

        var winner = -1

        //Row1
        if (player1.contains(1) && player1.contains(2) && player1.contains(3)) {
            winner = 1
        }
        if (player2.contains(1) && player2.contains(2) && player2.contains(3)) {
            winner = 2
        }

        //Row2
        if (player1.contains(4) && player1.contains(5) && player1.contains(6)) {
            winner = 1
        }
        if (player2.contains(4) && player2.contains(5) && player2.contains(6)) {
            winner = 2
        }
        //Row3
        if (player1.contains(6) && player1.contains(7) && player1.contains(8)) {
            winner = 1
        }
        if (player2.contains(6) && player2.contains(7) && player2.contains(8)) {
            winner = 2
        }
        //Col1
        if (player1.contains(1) && player1.contains(4) && player1.contains(7)) {
            winner = 1
        }
        if (player2.contains(1) && player2.contains(4) && player2.contains(7)) {
            winner = 2
        }

        //Col2
        if (player1.contains(2) && player1.contains(5) && player1.contains(8)) {
            winner = 1
        }
        if (player2.contains(2) && player2.contains(5) && player2.contains(8)) {
            winner = 2
        }
        //Col3
        if (player1.contains(3) && player1.contains(6) && player1.contains(9)) {
            winner = 1
        }
        if (player2.contains(3) && player2.contains(6) && player2.contains(9)) {
            winner = 2
        }

        //Hor Col1
        if (player1.contains(1) && player1.contains(5) && player1.contains(9)) {
            winner = 1
        }
        if (player2.contains(1) && player2.contains(5) && player2.contains(9)) {
            winner = 2
        }

        //Hor Col2
        if (player1.contains(3) && player1.contains(5) && player1.contains(7)) {
            winner = 1
        }
        if (player2.contains(3) && player2.contains(5) && player2.contains(7)) {
            winner = 2
        }

        //if (winner != 1) {

        if (winner == 1) {
            Toast.makeText(this, "The Winner Is Player1", Toast.LENGTH_SHORT).show()
        }

        if (winner == 2) {
            Toast.makeText(this, "The Winner Is Player2", Toast.LENGTH_SHORT).show()

        }
        //}

    }

    protected fun AutoPlay(cellID: Int) {

        var buSelected: Button?


        when (cellID) {
            1 -> buSelected = bu1
            2 -> buSelected = bu2
            3 -> buSelected = bu3
            4 -> buSelected = bu4
            5 -> buSelected = bu5
            6 -> buSelected = bu6
            7 -> buSelected = bu7
            8 -> buSelected = bu8
            9 -> buSelected = bu9
            else -> {
                buSelected = bu1
            }

        }
        PlayGame(cellID, buSelected)

    }

    fun ivRequestEvent(view: View) {
        Log.i("ivRequestEvent", "In Progress")
        var reqEmail = etEmail.text.toString()
        Log.i("ivRequestEvent", "The requested email is " + reqEmail)
        dbRef.child("Users").child(splitString(reqEmail)).child("Request").push().setValue(myEmail)
        PlayOnline(splitString(myEmail!!)+splitString(reqEmail!!))
        playerSymbol = "X"
    }

    fun ivAcceptEvent(view: View) {
        var reqEmail = etEmail.text.toString()
        dbRef.child("Users").child(splitString(reqEmail)).child("Request").push().setValue(myEmail)
        PlayOnline(splitString(reqEmail!!)+splitString(myEmail!!))
        playerSymbol = "O"
    }

    fun splitString(pSTR: String): String {

        var name = pSTR.split("@")
        Log.i("SplitString ", "The Name is " + name)
        return name[0]
    }

    var nmbr = 0
    fun IncomingRequests() {
        dbRef.child("Users").child(splitString(myEmail.toString())).child("Request")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {

                        try {
                            val tblData = dataSnapshot!!.value as HashMap<String, Any>
                            if (tblData != null) {
                                var value: String
                                for (key in tblData.keys) {
                                    value = tblData[key] as String
                                    etEmail.setText(value)
                                    /** Send Notification upon Incoming  */
                                    val notifyMe = Notifications()
                                    notifyMe.Notify(applicationContext,value + " wants to play Tic Tac Toe with you", nmbr)
                                    nmbr++
                                    dbRef.child("Users").child(splitString(myEmail!!)).child("Request").setValue(value)
                                    break
                                }

                            }

                        } catch (ex: Exception) {
                        }

                    }

                    override fun onCancelled(p0: DatabaseError?) {

                    }
                })

    }


    var sessionID: String? = null
    var playerSymbol: String? = null

    fun PlayOnline(sessionID: String) {

        this.sessionID = sessionID
        dbRef.child("OnlinePlayers").removeValue()
        dbRef.child("OnlinePlayers").child(sessionID)
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        try {
                            player1.clear()
                            player2.clear()
                            val tblData = dataSnapshot!!.value as HashMap<String, Any>
                            if (tblData != null) {
                                var value: String
                                for (key in tblData.keys) {
                                    value = tblData[key] as String
                                    if (value != myEmail) {
                                        activePlayer = if (playerSymbol === "X") 1 else 2
                                    } else {
                                        activePlayer = if (playerSymbol === "X") 2 else 1
                                    }
                                    AutoPlay(key.toInt())
                                }
                            }
                        } catch (ex: Exception) {
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {

                    }
                })
    }

}