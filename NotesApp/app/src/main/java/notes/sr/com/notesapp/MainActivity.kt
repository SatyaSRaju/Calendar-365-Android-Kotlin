package notes.sr.com.notesapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.notes_list.view.*
import android.support.v4.view.MenuItemCompat
import android.view.*


class MainActivity : AppCompatActivity() {

    var notesArrLst = ArrayList<Notes>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Dummy Data -- To be moved to DB
        // notesArrLst.add(Notes(1, "Learn Android Kotlin ", "Kotlin is expressive, concise, extensible, powerful, and a joy to read and write. It has wonderful safety features in terms of nullability and immutability, which aligns with our investments to make Android apps healthy and performant by default. Best of all, it's interoperable with our existing Android languages and runtime"))
        // notesArrLst.add(Notes(2, "Good about Go Lang", "Network applications live and die by concurrency, and Go's native concurrency features -- goroutines and channels, mainly -- are well suited for such work. Consequently, many Go projects are for networking, distributed functions, or services: APIs, Web servers, minimal frameworks for Web applications, and the rest."))
        // notesArrLst.add(Notes(3, "Why Scala", "Scala is a type-safe JVM language that incorporates both object oriented and functional programming into an extremely concise, logical, and extraordinarily powerful language."))


        //var notesLstAdapter = NotesLstAdapter(notesArrLst)
        //lvNotes.adapter= notesLstAdapter

        //Data From DB

        DisplayNotes("%")

    }

    fun DisplayNotes(Title: String) {
        var dbMgr = DBManager(this)
        val projections = arrayOf("ID", "Title", "Content")
        val selectionArgs = arrayOf(Title)
        val dbCursor = dbMgr.queryNotes(projections, "Title like ?", selectionArgs, "Title")
        notesArrLst.clear()

        if (dbCursor.moveToFirst()) {
            do {

                val ID = dbCursor.getInt(dbCursor.getColumnIndex("ID"))
                val Title = dbCursor.getString(dbCursor.getColumnIndex("Title"))
                val Content = dbCursor.getString(dbCursor.getColumnIndex("Content"))
                notesArrLst.add(Notes(ID, Title, Content))
            } while (dbCursor.moveToNext())
        }
        var notesLstAdapter = NotesLstAdapter(this, notesArrLst)
        lvNotes.adapter = notesLstAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)
        //Functionality for Search

        val menuItem = menu!!.findItem(R.id.app_bar_search)
        val srchView = MenuItemCompat.getActionView(menuItem) as SearchView
        // val srchView = menu!!.findItem(R.id.app_bar_search) as SearchView -- Does not work
        val srchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        srchView.setSearchableInfo(srchManager.getSearchableInfo(componentName))
        srchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //TODO Search on a Database
                Toast.makeText(applicationContext, query, Toast.LENGTH_LONG).show()
                DisplayNotes("%" + query + "%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                DisplayNotes("%" + newText + "%")
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        DisplayNotes("%")
    }
    override fun onStart() {
        super.onStart()
        Toast.makeText(this, "onStart", Toast.LENGTH_LONG).show()
    }
    override fun onPause() {
        super.onPause()

        Toast.makeText(this, "onPause", Toast.LENGTH_LONG).show()
    }
    override fun onStop() {
        super.onStop()

        Toast.makeText(this, "onStop", Toast.LENGTH_LONG).show()
    }
    override fun onDestroy() {
        super.onDestroy()

        Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show()
    }
    override fun onRestart() {
        super.onRestart()
        Toast.makeText(this, "onRestart", Toast.LENGTH_LONG).show()
    }
    /**
     * When the user executes a search from the search dialog or a search widget,
     * the system creates an Intent and stores the user query in it.
     * The system then starts the activity that you've declared to handle searches (the "searchable activity")
     * and delivers it the intent.
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.addNote -> {

                    var intent = Intent(this, AddNotes::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    inner class NotesLstAdapter : BaseAdapter {

        var context: Context? = null
        var notesLstAdapter = ArrayList<Notes>()

        constructor(context: Context, pNotesLst: ArrayList<Notes>) : super() {
            this.notesLstAdapter = pNotesLst
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            var notesLstView = layoutInflater.inflate(R.layout.notes_list, null)
            var notesLst = notesLstAdapter[position]
            notesLstView.tvTitle.text = notesLst.noteTitle
            notesLstView.tvContent.text = notesLst.noteDesc

            notesLstView.ivDelete.setOnClickListener(View.OnClickListener {

                var dbMgr = DBManager(this.context!!)
                val selectionArgs = arrayOf(notesLst.noteID!!.toString())
                dbMgr.deleteNotes("ID = ?", selectionArgs)
                DisplayNotes("%")
            })

            notesLstView.ivEdit.setOnClickListener(View.OnClickListener {

                updateNotes(notesLst)
            })

            return notesLstView

        }

        override fun getItem(position: Int): Any {
            return notesLstAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        /**
         * getView method will be triggered  n times based on the getCount Return
         */
        override fun getCount(): Int {
            return notesLstAdapter.size
        }

    }
    fun updateNotes(oNote: Notes) {
        var intent = Intent(this, AddNotes::class.java)
        intent.putExtra("ID", oNote.noteID)
        intent.putExtra("Title", oNote.noteTitle)
        intent.putExtra("Content", oNote.noteDesc)
        startActivity(intent)
    }
}
