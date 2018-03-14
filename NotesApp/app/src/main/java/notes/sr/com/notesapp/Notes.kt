package notes.sr.com.notesapp

/**
 * Created by SRaju on 6/14/17.
 */


class Notes {
    var noteID:Int? = null
    var noteTitle:String? = null
    var noteDesc:String ? = null

    constructor(noteID:Int, noteTitle:String, noteDesc:String) {

        this.noteID = noteID
        this.noteTitle = noteTitle
        this.noteDesc = noteDesc
    }

}