package com.example.mytestapplication

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.AbsListView
import android.widget.GridView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.logging.Handler


class MainActivity : AppCompatActivity() {
    var end=false
    var updating=false
    var needUpdate=false
    lateinit var bitmap:Bitmap
    lateinit var adapter:MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val model: GetContent by viewModels()
        var gvMain = findViewById<GridView>(R.id.gvMain)
        var numOfContent=10
        var count=0
        var Id=Array(numOfContent) {0}
         bitmap= createBitmap(1,1)
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        var MainTextObj= Array(numOfContent) {""}
        var DiscriptiontextOb= Array(numOfContent) {""}
        var bitmapArray= Array(numOfContent) {bitmap}
        var url=Array(numOfContent) {""}
        adapter=MyAdapter(this, DiscriptiontextOb, MainTextObj, bitmapArray, Id)
        gvMain.adapter = adapter
        scope.launch { createContent(gvMain,model,count,url,bitmapArray,MainTextObj,DiscriptiontextOb,Id)}
        gvMain.setOnScrollListener(object : AbsListView.OnScrollListener {
                override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                    if(needUpdate)
                        if (!end) {
                            url += Array(numOfContent) { "" }
                            Id += Array(numOfContent) { 0 }
                            MainTextObj += Array(numOfContent) { "" }
                            DiscriptiontextOb += Array(numOfContent) { "" }
                            bitmapArray += Array(numOfContent) { bitmap }
                            count += numOfContent
                            Log.d("count",count.toString())
                            needUpdate=false;
                            scope.launch{createContent(gvMain,model, count, url, bitmapArray, MainTextObj, DiscriptiontextOb, Id)}
                    }
                }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                when (scrollState) {
                    RecyclerView.SCROLL_STATE_IDLE ->{
                        println("The RecyclerView is not scrolling")}
                    RecyclerView.SCROLL_STATE_DRAGGING ->{
                        println("Scrolling now")}
                    RecyclerView.SCROLL_STATE_SETTLING -> println("Scroll Settling")
                }
            }
        })
    }

    private suspend fun createContent(gvMain:GridView, model: GetContent, num:Int, url: Array<String>, bitmapArray: Array<Bitmap>, MainTextObj: Array<String>
                                      , DiscriptiontextOb: Array<String>, Id:Array<Int>) {
        val job: Job =GlobalScope.launch {
            var ob=model.requestData(num)
            Log.d("Json",ob.toString())
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
                Log.d("num",num.toString())
                model.DownloadAndCreateImage(url, bitmapArray,num)
                needUpdate=true
            }
            else {
                end = true
                needUpdate=false
            }
        }
        job.join()
        if(!end) {
            adapter.UpdateData(this, DiscriptiontextOb, MainTextObj, bitmapArray, Id)
            adapter.notifyDataSetChanged()
        }
        }
    }