package notes.sr.com.notesapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast
import kotlin.reflect.KTypeProjection

/**
 * Created by SRaju on 6/17/17.
 */

class DBManager {
    val dbName = "NotesDB"
    val dbTable = "Notes"
    val colID = "ID"
    val colTitle = "Title"
    val colContent = "Content"
    val dbVer:Int = 1
    var sqlDb: SQLiteDatabase? = null
    var sqlCreateTbl = "CREATE TABLE IF NOT EXISTS " +  dbTable + "(" + colID + " INTEGER PRIMARY KEY, "+ colTitle  +" TEXT, " + colContent + " TEXT );"

    constructor(context: Context) {
        val db = DBHelperNotes(context)
        sqlDb = db.writableDatabase

    }
    //Create Database
    inner  class DBHelperNotes: SQLiteOpenHelper {

        var context: Context? = null

        constructor(context: Context) : super(context,dbTable,null,dbVer) {
            this.context=context
        }

        override fun onCreate(db: SQLiteDatabase?) {
          try {

              db!!.execSQL(sqlCreateTbl)
              Toast.makeText(this.context,"Notes Database Created",Toast.LENGTH_LONG).show()
          } catch (ex: Exception) {
              Toast.makeText(this.context,"Cannot create Notes Database",Toast.LENGTH_LONG).show()

          }

        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
           db!!.execSQL(" DROP TABLE IF EXISTS" + dbTable)
        }

    }

    fun createNotes(values: ContentValues): Long {
        val ID = sqlDb!!.insert(dbTable,null,values)
        return ID
    }

    fun queryNotes(projection: Array<String>, selection: String, selectionArgs: Array<String>, sortOrder: String): Cursor {

        val qryBuilder = SQLiteQueryBuilder()
        qryBuilder.tables=dbTable

        val dbCursor = qryBuilder.query(sqlDb, projection,selection,selectionArgs,null,null,sortOrder)
        return dbCursor
    }

    fun deleteNotes(selection: String, selectionArgs: Array<String>) : Int {

        val cnt = sqlDb!!.delete(dbTable,selection,selectionArgs)
        return cnt
    }

    fun updateNotes(values: ContentValues, selection:String, selectionArgs: Array<String>) : Int {

        val cnt = sqlDb!!.update(dbTable, values, selection,selectionArgs)
        return cnt
    }
}