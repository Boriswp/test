package com.example.mytestapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.GridView
import androidx.activity.viewModels
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class GetContent:ViewModel() {
    val stringContent=MutableLiveData<JSONArray>()
    val ImageContent=MutableLiveData<Bitmap>()
    fun requestData(
        count: Int
    ): LiveData<JSONArray> { viewModelScope.launch(Dispatchers.IO) {
        val url = URL("http://pressa-api.imb2bs.com/api/v1/journal/categories/0/?offset=${count}&limit=10")
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true;
        connection.setRequestProperty("Project-Id", "2")
        connection.setRequestProperty("Language", "ru")
        val inputStream: InputStream = connection.inputStream
        val allText = inputStream.bufferedReader().use(BufferedReader::readText)
        val json = JSONArray(allText)
        stringContent.postValue(json)
        connection.disconnect()
    }
        return stringContent
    }

    fun DownloadAndCreateImage(url:Array<String>,bitmapArray:Array<Bitmap>,num:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            for (n in num until url.size) {
                val getBits = URL(url[n]).openStream()
                bitmapArray[n] = BitmapFactory.decodeStream(getBits)
                Log.d("image",url[n])
            }
        }
    }
}

