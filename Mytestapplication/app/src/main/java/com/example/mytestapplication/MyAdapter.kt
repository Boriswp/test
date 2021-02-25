package com.example.mytestapplication
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels


class MyAdapter : BaseAdapter
{
    private lateinit var description:Array<String>
    private lateinit var titles: Array<String>
    var images:Array<Bitmap>
    private var mContext: Context? = null
    private var Id:Array<Int>
    constructor(context: Context,desc:Array<String>,title:Array<String>,image:Array<Bitmap>,ids:Array<Int>){
        description=desc
        titles=title
        images=image
        Id=ids
        mContext=context
    }
    fun UpdateData(context:Context,desc:Array<String>,title:Array<String>,image:Array<Bitmap>,ids:Array<Int>){
        description=desc
        titles=title
        images=image
        Id=ids
        mContext=context
    }
    fun UpdateImage(image:Array<Bitmap>){
        images=image
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var grid: View
        val inflater = mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            grid = inflater.inflate(R.layout.content, null)
            val imageView: ImageView = grid.findViewById<View>(R.id.pictures) as ImageView
            val textView = grid.findViewById<View>(R.id.MainText) as TextView
            val textView2 = grid.findViewById<View>(R.id.DiscriptionText) as TextView
            textView.text = titles[position]
            textView2.text=description[position]
            imageView.setImageBitmap(images[position])
        return grid
    }

    override fun getItem(position: Int): Any {
       return position
    }

    override fun getItemId(position: Int): Long {
        return Id[position].toLong()
    }

    override fun getCount(): Int {
        return Id.size-1
    }

}