package twitter.sr.com.twitterdemo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_tweet.view.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tweets_list.view.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var dbFirebase= FirebaseDatabase.getInstance()
    private var dbRef =dbFirebase.reference
    private var email: String? = null
    private var uUID:String? =null
    private var tweetArrLst = ArrayList<Tweet>()
    private var tweetsAdapter: TweetsLstAdapter?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var bundleFromLogin: Bundle = intent.extras
        email = bundleFromLogin.getString("email")
        uUID=bundleFromLogin.getString("uid")
        //Dummy Data
        tweetArrLst.add(Tweet("0","Android","URL","add"))

        tweetsAdapter =  TweetsLstAdapter(this,tweetArrLst)
        lvTweets.adapter = tweetsAdapter
        loadPostedImages()

    }

    inner class TweetsLstAdapter: BaseAdapter {

        var context: Context? = null
        var TweetsLstAdapter = ArrayList<Tweet>()

        constructor(context: Context, pTweetsLst: ArrayList<Tweet>) :  super() {
            this.TweetsLstAdapter = pTweetsLst
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            var tweetsLst = TweetsLstAdapter[position]
            if ((tweetsLst.tweetPersonUID).equals ("add")) {
                //Load add_tweet Layout
                var tweetsLstView = layoutInflater.inflate(R.layout.add_tweet, null)
                tweetsLstView.iv_attach.setOnClickListener(View.OnClickListener {

                    loadImage()
                })
                tweetsLstView.iv_post.setOnClickListener(View.OnClickListener {
                    try {
                        dbRef.child("Posted").push().setValue(
                                ImgPostInfo(uUID!!.toString(), tweetsLstView.etPost.text.toString(), downloadURL!!)
                        )
                    } catch(ex: Exception) {
                        Log.e("While Adding Msg", "the value of text " + tweetsLstView.etPost.text.toString())
                    }
                    tweetsLstView.etPost.setText("")
                })
                return tweetsLstView
            } else if (tweetsLst.tweetPersonUID.equals("loading")){
                var tweetsLstView=layoutInflater.inflate(R.layout.loading_image,null)
                return  tweetsLstView
            } else {
                var tweetsLstView=layoutInflater.inflate(R.layout.tweets_list,null)
                tweetsLstView.txt_tweet.setText(tweetsLst.tweetText)
                tweetsLstView.txtUserName.setText(tweetsLst.tweetPersonUID)
               // tweetsLstView.tweet_picture.setImageURI(tweetsLst.tweetImgURL)
                Picasso.with(context).load(tweetsLst.tweetImgURL).into(tweetsLstView.tweet_picture)

                dbRef.child("Users").child(tweetsLst.tweetPersonUID)
                        .addValueEventListener(object:ValueEventListener{

                            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                                try {
                                    var tblData =  dataSnapshot!!.value as HashMap<String,Any?>
                                   // Log.i(" ", "Entries in tblData -> "+ tblData!!.keys.toString())
                                    try {
                                        for (key in tblData.keys) {
                                            var userInfo = tblData[key] as String
                                            if (key.equals("ProfileImage")){
                                                Picasso.with(context).load(userInfo).into(tweetsLstView.picture_path)
                                                Log.i("Display ProfileImage ->", userInfo)
                                            } else {
                                                tweetsLstView.txtUserName.text = userInfo
                                                Log.i("Display UserName ->", userInfo)
                                            }


                                        }
                                    } catch (ex:Exception) { Log.i("Display UsrProfile ->", ex.message.toString())}


                                    //Log.i("UsrProfileFrUID", "The Tweet Count is " + tweetArrLst.size.toString())

                                }catch (ex: Exception) {Log.i("UsrProfileFrUID", ex.message.toString())}
                            }

                            override fun onCancelled(p0: DatabaseError?) {

                            }

                        })

                return tweetsLstView
            }

        }

        override fun getItem(position: Int): Any {
            return TweetsLstAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        /**
         * getView method will be triggered  n times based on the getCount Return
         */
        override fun getCount(): Int {
            return TweetsLstAdapter.size
        }

    }

    val IMAGEPICKCODE:Int= 333
    //Load Image
    fun loadImage() {
        Log.i("Load Image", "InProgress")
        val picPath = Environment.getExternalStorageDirectory().path
        Log.i("Getting Picture Path ", "The Path is " +picPath)

        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,IMAGEPICKCODE)
    }

    fun splitString(s: String): String {
        val sSplit = s.split("@")
        return sSplit[0]
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==IMAGEPICKCODE  && data!=null && resultCode == RESULT_OK) {
            try {


                val selImage = data.data
                val filePathColumns = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = contentResolver.query(selImage, filePathColumns, null, null, null)
                cursor.moveToFirst()
                val columnIndx = cursor.getColumnIndex(filePathColumns[0])
                val picPath = cursor.getString(columnIndx)
                cursor.close()
                uploadImg(BitmapFactory.decodeFile(picPath))
            } catch (ex:Exception) {Log.e("onActivityResult", ex.message.toString())}
        }
    }

    private var downloadURL:String? = null

    fun uploadImg(bitMap:Bitmap) {

        try {
            tweetArrLst.add(0,Tweet("0","Android","URL","loading"))
            tweetsAdapter!!.notifyDataSetChanged()

            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.getReferenceFromUrl("gs://twitterdemo-61ab3.appspot.com")
            val dtFormat = SimpleDateFormat("ddMMyyHHmmss")
            val dateObj = Date()
            val imgPath = splitString(email!!) + "." + dtFormat.format(dateObj) + ".jpg"
            val imgRef = storageRef.child("posted-images/" + imgPath)
            val byteArrOut = ByteArrayOutputStream()
            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrOut)
            val compData = byteArrOut.toByteArray()
            val uploadTask = imgRef.putBytes(compData)
            uploadTask.addOnFailureListener {
                Toast.makeText(applicationContext, "Failed To Upload", Toast.LENGTH_LONG).show()
            }.addOnSuccessListener { taskSnapshot ->
                downloadURL = taskSnapshot.downloadUrl!!.toString()
                tweetArrLst.removeAt(0)
                tweetsAdapter!!.notifyDataSetChanged()

            }
        } catch (ex: Exception) { Log.e("Upload Image", " The Exception is " +ex)}
    }

    private fun loadPostedImages() {
        Log.i("loadPostedImages", "Executing")
        dbRef.child("Posted")
                .addValueEventListener(object:ValueEventListener{

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        try {
                            tweetArrLst.clear()
                            tweetArrLst.add(Tweet("0","Kotlin","https://kotlinlang.org/","add"))
                            //tweetArrLst.add(Tweet("0","Kotlin","https://kotlinlang.org/","adS"))
                            var tblData =  dataSnapshot!!.value as HashMap<String,Any?>
                            Log.i("loadPostedImages", "Entries in tblData -> "+ tblData!!.keys.toString())
                           try {
                               for (key in tblData.keys) {
                                   var postedData = tblData[key] as HashMap<String, Any?>
                                   tweetArrLst.add(Tweet(
                                           key,
                                           postedData["message"] as String,
                                           postedData["postedImage"] as String,
                                           postedData["userUID"] as String
                                   ))
                                   Log.i("loadPostedImages", "Entries in postedData -> " + postedData["message"].toString())
                               }
                           } catch (ex:Exception) { Log.i("loadPostedImagesL ->", ex.message.toString())}


                            Log.i("loadPostedImages", "The Tweet Count is " + tweetArrLst.size.toString())
                            tweetsAdapter!!.notifyDataSetChanged()
                        }catch (ex: Exception) {Log.i("loadPostedImages", ex.message.toString())}
                    }

                    override fun onCancelled(p0: DatabaseError?) {

                    }

                })

    }
}
