/*

package weather.sr.com.weather

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    protected fun GetWeather(view: View) {

        try {
            Log.i("GetWeather", "Entering GetWeather Method")
            var city = edCity.text.toString()
            //val url="https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+ city +"%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"
            val url ="http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22nome%2C%20ak%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"
            Log.i("GetWeather", "The URL is -> " + url)
            Log.i("GetWeather", "Before Calling Weather Async Task")
            WeatherAsyncTask().execute(url)
        } catch (ex: Exception)
        {
            Log.e("GetWeather", ex.message)
        }
    }

    inner class WeatherAsyncTask : AsyncTask<String, String, String>() {


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

                val oAstronomy = oChannel!!.getJSONObject("astronomy")
                val oItem =  oChannel!!.getJSONObject("item")
                val oImg = oChannel!!.getJSONObject("image")
                val oForecast = oItem!!.getJSONObject("forecast")
                //val oForecastItems = oForecast.getJSONArray("0")
                val wSunRise: String = oAstronomy!!.getString("sunrise")
                val wSunSet: String = oAstronomy!!.getString("sunset")
                // var wHigh: String? =null
                // var wLow: String? =null
                // var wText: String? =null


                //for ( i in 0..oForecastItems.length()) {

                //  when (i)  {
                //    3 -> wHigh = oForecastItems.getJSONObject(3).toString()
                //  4 -> wLow =oForecastItems.getJSONObject(4).toString()
                //5 -> wText = oForecastItems.getJSONObject(5).toString()
                //}
                //}
                var wImg:String =oImg!!.getString("url")

                tvSunRiseSet.text = "Sun Rise: " + wSunRise + " Sun Set: " + wSunSet

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
}
*/