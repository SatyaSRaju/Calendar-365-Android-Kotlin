package audioplayer.sr.com.audioplayer

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.BaseAdapter
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.audio_list.*
import kotlinx.android.synthetic.main.audio_list.view.*

class MainActivity : AppCompatActivity() {

    var lstSongs  = ArrayList<SongInfo>()
    var adpLstSongs:SongsAdapter?= null
    var mPlayer:MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CheckPermission()
        //loadOnlineURLS()

        //Enable the following lines for Online URL Play
        //adpLstSongs = SongsAdapter(lstSongs)
        //lvSongs.adapter = adpLstSongs
        var songProgress = TrackSongProgress()
        songProgress.start()


    }

    fun loadOnlineURLS() {
        lstSongs.add(SongInfo("DJ", "Satya", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"))
        lstSongs.add(SongInfo("DJ", "Raju", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3"))
    }

    inner class SongsAdapter:BaseAdapter {

        var myLstSongs: ArrayList<SongInfo>? = null
        constructor(myLstSongs:ArrayList<SongInfo>):super(){
            this.myLstSongs = myLstSongs

        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val adpView = layoutInflater.inflate(R.layout.audio_list,null)
            val adpSongInfo = this.myLstSongs!![position]
            adpView.tvTitle.text = adpSongInfo.Title
            adpView.tvAuthor.text = adpSongInfo.Author
            adpView.butPlay.setOnClickListener(View.OnClickListener {
                if ( adpView.butPlay.text.equals("Stop") ) {
                    mPlayer!!.stop()
                    adpView.butPlay.text = "Start"
                } else {
                    mPlayer = MediaPlayer()
                    try {
                        mPlayer!!.setDataSource(adpSongInfo.songURL)
                        mPlayer!!.prepare()
                        mPlayer!!.start()
                        adpView.butPlay.text = "Stop"
                        sbProgress.max = mPlayer!!.duration
                    } catch (ex: Exception) { Log.e ("WhilePlayingMedia", ex.message.toString())}
                }
            })


                return adpView
        }


        override fun getItem(position: Int): Any {
            return this.myLstSongs!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return this.myLstSongs!!.size

        }


    }

    inner class TrackSongProgress: Thread {
        constructor():super(){}

        override fun run() {
            while (true) {
                try {
                    Thread.sleep(1000)
                } catch (ex: Exception) { }
                runOnUiThread {
                    if (mPlayer != null) {
                        sbProgress.progress = mPlayer!!.currentPosition
                        if (sbProgress.progress == mPlayer!!.duration) {
                            sbProgress.progress = 0
                            butPlay.text="Play"
                        }

                    }
                }
            }
        }
    }



/**
Check Permisson. If the device is running Android 5.1 or lower, or your app's target SDK is 22 or lower:
If you list a dangerous permission in your manifest, the user has to grant the permission when they install the app;
if they do not grant the permission, the system does not install the app at all.
If the device is running Android 6.0 or higher, and your app's target SDK is 23 or higher:
The app has to list the permissions in the manifest, and it must request each dangerous permission it needs while the app is running.
The user can grant or deny each permission, and the app can continue to run with limited capabilities even if the user denies a
permission request.
 */

    fun CheckPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),REQUEST_CODE_ASK_PERMISSIONS)
                return
            }
        }
        loadSong()
    }

    /**
     * When your app requests permissions, the system presents a dialog box to the user. When the user responds,
     * the system invokes your app's onRequestPermissionsResult() method, passing it the user response.
     * Your app has to override that method to find out whether the permission was granted.
     * The callback is passed the same request code you passed to requestPermissions().
     * For example, if an app requests ACCESS_FINE_LOCATION access it will have the following callback method:
     *
     */

    private val REQUEST_CODE_ASK_PERMISSIONS = 777
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS  -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadSong()
                } else {
                    Toast.makeText(this, "Cannot Access Location. Permission denied", Toast.LENGTH_LONG).show()
                }

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    fun loadSong() {
        val songsURL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        Log.i("loadSong",songsURL.toString())
        val select = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val cursor=contentResolver.query(songsURL, null,select,null,null)
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                do {
                    val songURL = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val songAuthor = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    lstSongs.add(SongInfo(songName,songAuthor,songURL))
                }while (cursor!!.moveToNext())
            }
        }
        cursor.close()
        adpLstSongs = SongsAdapter(lstSongs)
        lvSongs.adapter = adpLstSongs

    }

}

