package weather.sr.com.weather

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso
import java.net.URI


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }


    protected fun GetWeather(view: View) {

        try {
            Log.i("GetWeather", "Entering GetWeather Method")
            var city = edCity.text.toString()
            val url="https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+ city +"%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"
            //val url ="http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22nome%2C%20ak%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"
            Log.i("GetWeather", "The URL is -> " + url)
            Log.i("GetWeather", "Before Calling Weather Async Task")
            WeatherAsyncTask().execute(url)
        } catch (ex: Exception)
        {
            Log.e("GetWeather", ex.message)
        }
    }

    inner class WeatherAsyncTask : AsyncTask<String, String, String>() {

        var context: Context? = null

        /*
        constructor(): super() {
            this.context = context
        }
        */

        fun ConvertStreamToString(inStream: InputStream): String {

                Log.i("ConvertStreamToString: Input to the function", inStream.toString())

                val bufferReader=BufferedReader(InputStreamReader(inStream))
                var line:String
                var completeString:String = ""

                try{
                    do {
                        line = bufferReader.readLine()
                        //Log.i("ConvertStreamToString: Reading Buffer DO-WHILE Loop", "Read Line -> " + line)
                        if (line != null) {
                            completeString += line
                            Log.i("ConvertStreamToString: Reading Buffer DO-WHILE Loop", "Read Line -> " + completeString)
                        }
                    } while ( line != null)
                    inStream.close()
                } catch (ex: Exception) {
                    Log.e("ConvertStreamToString", ex.message)
                }
                return completeString
            }
            /**
              * Make HTTP Call to Weather EndPoint and Convert the return stream into a String.
              * Call publishProgress Method
             */
        override fun doInBackground(vararg params: String?): String {

                Log.i("doInBackground", "Entering into the process" )
                try {
                    val url = URL(params[0])
                    Log.i("doInBackground", "The URL is " + url)
                    val urlConnect =url.openConnection() as HttpURLConnection
                    urlConnect.connectTimeout=7000
                    urlConnect.requestMethod
                    var responseCode = urlConnect.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        var inString = ConvertStreamToString(urlConnect.inputStream)
                        Log.i("doInBackground", "The output after calling ConvertStreamToString -> " + inString)
                        publishProgress(inString)
                        }
                        else {Log.e("Connecting to Weather Service Failed with HTTP Status " , responseCode.toString())}


                }catch (ex: Exception) {
                    Log.e("doInBackground", ex.message)
                }
                return " "
            }

        /**
         * Parse the JSON Object and Retrieve the
         */
        override fun onProgressUpdate(vararg values: String?) {

            try {

                var oWeather = JSONObject(values[0])
                val oQuery = oWeather!!.getJSONObject("query")
                val oResults = oQuery!!.getJSONObject("results")
                val oChannel = oResults!!.getJSONObject("channel")
                val oItems = oChannel!!.getJSONObject("item")
                val oAstronomy = oChannel!!.getJSONObject("astronomy")
                val oDesc = oItems!!.getString("description")
                val wSunRise: String = oAstronomy!!.getString("sunrise")
                val wSunSet: String = oAstronomy!!.getString("sunset")

               Log.i("The Desc Value is ", oDesc.toString())


                tvSunRiseSet.text = "Sun Rise: " + wSunRise + " Sun Set: " + wSunSet
               // Picasso.with(context).load(imgURL).into(ivWeather)

            } catch (ex: Exception) {
                Log.e("OnProgressUpdate", ex.message)
            }
        }


        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

    }

     fun deleteCache(context: Context) {
        try {
            val dir = context.cacheDir
            if (dir != null && dir.isDirectory) {
                Log.i ("deleteCache", "Calling Delete Dir")
                deleteDir(dir)
            }
        } catch (e: Exception) {
        }

    }

    fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir!!.isDirectory()) {
            val children = dir!!.list()
            for (i in children.indices) {
                Log.i ("deleteDir", "Calling Delete Dir " + i)
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir!!.delete()
    }

    override fun onStop() {
        deleteCache(this)
        super.onStop()
    }

}

