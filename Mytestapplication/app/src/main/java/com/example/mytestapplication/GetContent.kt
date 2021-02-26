package com.example.mytestapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class GetContent:ViewModel() {
    val stringContent=MutableLiveData<JSONArray>()
        suspend fun requestData(count: Int):JSONArray {
            var json=JSONArray()
          withContext(Dispatchers.IO) {
              val url = URL("http://pressa-api.imb2bs.com/api/v1/journal/categories/0/?offset=$count&limit=10")
              val connection = url.openConnection() as HttpURLConnection
              connection.doInput = true;
              connection.setRequestProperty("Project-Id", "2")
              connection.setRequestProperty("Language", "ru")
              if(connection.responseCode!=200) {
                  return@withContext
              }
              val inputStream: InputStream = connection.inputStream
              val allText = inputStream.bufferedReader().use(BufferedReader::readText)
              json = JSONArray(allText)
            }
            return json
        }


    suspend fun DownloadAndCreateImage(url:Array<String>, bitmapArray:Array<Bitmap>, num:Int) {
        return withContext(Dispatchers.IO) {
            for (n in num until url.size) {
                val getBits = URL(url[n]).openStream()
                bitmapArray[n] = BitmapFactory.decodeStream(getBits)
            }
        }
    }
}

