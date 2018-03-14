package audioplayer.sr.com.audioplayer

/**
 * Created by SRaju on 7/4/17.
 */
class SongInfo {
    var Title: String? = null
    var Author:String? = null
    var songURL:String? = null

    constructor(Title: String, Author:String, songURL:String ){
        this.Title = Title
        this.Author = Author
        this.songURL = songURL
    }


}