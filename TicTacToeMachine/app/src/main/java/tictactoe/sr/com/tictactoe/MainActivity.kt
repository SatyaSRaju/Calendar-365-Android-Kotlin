package tictactoe.sr.com.tictactoe

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    protected fun buClick(view: View) {

        val buSelected = view as Button
        var cellID=0

        when (buSelected.id) {
            R.id.bu1 -> cellID =1
            R.id.bu2 -> cellID =2
            R.id.bu3-> cellID =3
            R.id.bu4 -> cellID =4
            R.id.bu5 -> cellID =5
            R.id.bu6 -> cellID =6
            R.id.bu7 -> cellID =7
            R.id.bu8 -> cellID =8
            R.id.bu9 -> cellID =9
        }

        //Toast.makeText(this,"ID :" + cellID, Toast.LENGTH_SHORT).show()
        PlayGame(cellID,buSelected)

    }

    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()
    var activePlayer= 1


    protected fun PlayGame(cellID:Int, buSelected:Button) {

        if (activePlayer==1) {
            buSelected.text= "X"
            buSelected.setBackgroundResource(R.color.blue)
            player1.add(cellID)
            activePlayer=2
            AutoPlay()
        } else {
            buSelected.text= "O"
            buSelected.setBackgroundResource(R.color.darkgreen)
            player2.add(cellID)
            activePlayer=1

        }
        buSelected.isEnabled=false
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
                winner=1
            }
            if (player2.contains(1) && player2.contains(2) && player2.contains(3)) {
                winner=2
            }

        //Row2
            if (player1.contains(4) && player1.contains(5) && player1.contains(6)) {
                winner=1
            }
            if (player2.contains(4) && player2.contains(5) && player2.contains(6)) {
                winner=2
            }
        //Row3
            if (player1.contains(6) && player1.contains(7) && player1.contains(8)) {
                winner=1
            }
            if (player2.contains(6) && player2.contains(7) && player2.contains(8)) {
                winner = 2
            }
        //Col1
            if (player1.contains(1) && player1.contains(4) && player1.contains(7)) {
                winner=1
            }
            if (player2.contains(1) && player2.contains(4) && player2.contains(7)) {
                winner=2
            }

            //Col2
            if (player1.contains(2) && player1.contains(5) && player1.contains(8)) {
                winner=1
            }
            if (player2.contains(2) && player2.contains(5) && player2.contains(8)) {
                winner=2
            }
            //Col3
            if (player1.contains(3) && player1.contains(6) && player1.contains(9)) {
                winner=1
            }
            if (player2.contains(3) && player2.contains(6) && player2.contains(9)) {
                winner = 2
            }

            //Hor Col1
            if (player1.contains(1) && player1.contains(5) && player1.contains(9)) {
                winner=1
            }
            if (player2.contains(1) && player2.contains(5) && player2.contains(9)) {
                winner = 2
            }

            //Hor Col2
            if (player1.contains(3) && player1.contains(5) && player1.contains(7)) {
                winner=1
            }
            if (player2.contains(3) && player2.contains(5) && player2.contains(7)) {
                winner = 2
            }

        //if (winner != 1) {

                if (winner == 1) {
                    Toast.makeText(this, "The Winner Is Player1", Toast.LENGTH_SHORT).show()
                }

                if (winner == 2) {
                    Toast.makeText(this,"The Winner Is Player2", Toast.LENGTH_SHORT).show()

                }
        //}

        }

    protected fun AutoPlay() {
        var emptyCells = ArrayList<Int>()
        var buSelected: Button?

        /* Find the list of Cells that were not selected yet*/
        for (cell in 1..9) {
            if ( !(player1.contains(cell) || player2.contains(cell)))
                emptyCells.add(cell)
        }

        /* Generate Random Index from the Empty/Unselected Cells*/
        Collections.shuffle(emptyCells)
        val autoSelCell = emptyCells.get(0)

        when (autoSelCell) {
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
                buSelected=bu1
            }

        }
        PlayGame(autoSelCell,buSelected)

    }
}
