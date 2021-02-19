package com.example.mytestapplication

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.AbsListView
import android.widget.GridView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import java.util.logging.Handler


class MainActivity : AppCompatActivity() {
    var hasCreate=false
    var end=false
    var updating=false
    lateinit var bitmap:Bitmap
    lateinit var adapter:MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var gvMain = findViewById<GridView>(R.id.gvMain)
        var numOfContent=10
        var count=0
        var Id=Array(numOfContent) {0}
         bitmap= createBitmap(1,1)
        var MainTextObj= Array(numOfContent) {""}
        var DiscriptiontextOb= Array(numOfContent) {""}
        var bitmapArray= Array(numOfContent) {bitmap}
        var url=Array(numOfContent) {""}
        createContent(gvMain,count,url,bitmapArray,MainTextObj,DiscriptiontextOb,Id)
        gvMain.setOnScrollListener(object : AbsListView.OnScrollListener {
                override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                    if(hasCreate) {
                        if (visibleItemCount + firstVisibleItem == totalItemCount && !end) {
                            url += Array(numOfContent) { "" }
                            Id += Array(numOfContent) { 0 }
                            MainTextObj += Array(numOfContent) { "" }
                            DiscriptiontextOb += Array(numOfContent) { "" }
                            bitmapArray += Array(numOfContent) { bitmap }
                            count += numOfContent
                            Log.d("count",count.toString())
                            createContent(gvMain, count, url, bitmapArray, MainTextObj, DiscriptiontextOb, Id)
                        }
                        if(end&&!updating) {
                            updating=true
                            val model: GetContent by viewModels()
                            model.DownloadAndCreateImage(url, bitmapArray,0)
                        }
                    }
                }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                when (scrollState) {
                    RecyclerView.SCROLL_STATE_IDLE ->{
                        if(hasCreate) {
                            adapter.UpdateImage(bitmapArray)
                            adapter.notifyDataSetChanged()
                        }
                        println("The RecyclerView is not scrolling")}
                    RecyclerView.SCROLL_STATE_DRAGGING ->{
                        if(hasCreate)
                          adapter.UpdateImage(bitmapArray)
                        println("Scrolling now")}
                    RecyclerView.SCROLL_STATE_SETTLING -> println("Scroll Settling")
                }
            }
        })
    }
    private fun createContent(gvMain:GridView, num:Int, url: Array<String>, bitmapArray: Array<Bitmap>, MainTextObj: Array<String>
                              , DiscriptiontextOb: Array<String>,Id:Array<Int>) {
        val model: GetContent by viewModels()
        model.requestData(num).observe(this, androidx.lifecycle.Observer {
            var ob = model.stringContent.value
            Log.d("Json",ob.toString());
            if (ob != null&&ob.toString()!="[]") {
                var textOb: String
                for (n in 0 until ob.length()) {
                    textOb = ob.getJSONObject(n)["name"].toString() + "\n"
                    MainTextObj[n+num] = textOb
                    var desObj = ob.getJSONObject(n)["description"]
                        if (desObj.toString().contentEquals("null") || desObj.toString().isEmpty()) {
                            desObj = " отсутствует"
                        }
                        DiscriptiontextOb[n+num] = "Описание:${desObj}\n"

                    var urlOb = ob.getJSONObject(n)["last_issue"] as JSONObject
                    url[n+num] = urlOb.getString("cover")
                }
                model.DownloadAndCreateImage(url, bitmapArray,num)
                if(!hasCreate) {
                    adapter=MyAdapter(this, DiscriptiontextOb, MainTextObj, bitmapArray, Id)
                    gvMain.adapter = adapter
                    hasCreate = true
                }
                else {
                    adapter.UpdateData(this, DiscriptiontextOb, MainTextObj,bitmapArray, Id)
                    adapter.notifyDataSetChanged()
                }
            }
            else
                end=true
        })
    }
}

