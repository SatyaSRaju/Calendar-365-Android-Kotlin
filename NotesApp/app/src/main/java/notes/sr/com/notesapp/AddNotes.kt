package notes.sr.com.notesapp

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Display
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_notes.*

class AddNotes : AppCompatActivity() {

    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)


        try {
            var bundle: Bundle = intent.extras
            id = bundle.getInt("ID", 0)
            if (id != 0) {
                edTitle.setText(bundle.getString("Title").toString())
                edContent.setText(bundle.getString("Content").toString())
            }
        } catch (ex: Exception){}
    }

    fun iv_AddNotes(view: View) {

        var values = ContentValues()
        var dbMgr = DBManager(this)

        values.put("Title", edTitle.text.toString())
        values.put("Content", edContent.text.toString())

        if (id ==  0) {
            val ID = dbMgr.createNotes(values)

                if (ID > 0) {
                    Toast.makeText(this, "Notes Added Successfully", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Unable to add Notes", Toast.LENGTH_LONG).show()
                }

        } else {

            val selectionArgs = arrayOf(id.toString())
            val ID = dbMgr.updateNotes(values, "ID = ?", selectionArgs )
            if (ID > 0) {
                Toast.makeText(this, "Notes Added Successfully", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Unable to add Notes", Toast.LENGTH_LONG).show()
            }

        }
    }


}
