package twitter.sr.com.twitterdemo

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.OnProgressListener
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.StorageReference
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*


class Login : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var dbFirebase= FirebaseDatabase.getInstance()
    private var dbRef =dbFirebase.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        ivPerson.setOnClickListener(View.OnClickListener {
            checkPermissions()
        })

        buLogin.setOnClickListener(View.OnClickListener {
            buLoginEvent()
        })
    }

    fun LoginToFirebase (email:String, password: String) {
        Log.i("LoginToFirebase", "In Progress")


        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Login Success", Toast.LENGTH_LONG).show()
                        //Save to Firebase Database
                        saveImageInFirebase()
                    } else {
                        Log.e("Error from Gmail ", task.exception.toString())
                        Toast.makeText(applicationContext, "Login Failed. Please check credentials", Toast.LENGTH_LONG).show()
                    }
                }
    }


    val READIMAGE: Int = 777
    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),READIMAGE)
                    return
            }
        }
        loadImage()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
       when(requestCode){
           READIMAGE -> {
               if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   loadImage()
               }else{
                   Toast.makeText(applicationContext,"Permission not given to access media", Toast.LENGTH_LONG).show()
               }
           } else ->
           super.onRequestPermissionsResult(requestCode, permissions, grantResults)

       }

    }


    fun buLoginEvent() {
        Log.i("buLoginEvent", "In Progress")

        LoginToFirebase(edEmail.text.toString(), edPassword.text.toString())
    }

    val IMAGEPICKCODE:Int= 333
    fun loadImage() {
      var intent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
      startActivityForResult(intent,IMAGEPICKCODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGEPICKCODE && resultCode == Activity.RESULT_OK && data != null){
            val selImage = data.data
            val filePathColumns = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selImage,filePathColumns,null,null,null)
            cursor.moveToFirst()
            val columnIndx = cursor.getColumnIndex(filePathColumns[0])
            val picPath = cursor.getString(columnIndx)
            cursor.close()
            ivPerson.setImageBitmap(BitmapFactory.decodeFile(picPath))

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun saveImageInFirebase() {

        var currentUser = mAuth!!.currentUser
        val email:String = currentUser!!.email.toString()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://twitterdemo-61ab3.appspot.com")
        val dtFormat=("ddMMyyHHmmss")
        val dateObj = Date()
        val imgPath =splitString(email)+"."+dtFormat.format(dateObj)+ ".jpg"
        val imgRef = storageRef.child("images/"+ imgPath)
        ivPerson.isDrawingCacheEnabled =true
        ivPerson.buildDrawingCache()
        val drawable=ivPerson.drawable as BitmapDrawable
        val bitmap=drawable.bitmap
        val byteArrOut =  ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrOut)
        val compData = byteArrOut.toByteArray()
        val uploadTask =imgRef.putBytes(compData)
        uploadTask.addOnFailureListener{
            Toast.makeText(applicationContext,"Failed To Upload", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener { taskSnapshot ->
            val downloadURL = taskSnapshot.downloadUrl!!.toString()
            dbRef.child("Users").child(currentUser.uid).child("Email").setValue(currentUser.email)
            dbRef.child("Users").child(currentUser.uid).child("ProfileImage").setValue(downloadURL)
            loadTweets()
        }
    }

    override fun onStart() {
        super.onStart()
        loadTweets()
    }
    fun loadTweets() {
        var currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)
            startActivity(intent)
        }
    }
    fun splitString(s: String): String {
        val sSplit = s.split("@")
        return sSplit[0]
    }
}
