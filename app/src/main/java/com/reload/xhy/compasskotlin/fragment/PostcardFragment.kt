package com.reload.xhy.compasskotlin.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.reload.xhy.compasskotlin.R
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PostcardFragment : Fragment(){

    val REQUEST_CODE = 119
    lateinit var imageUriCamera :Uri
    lateinit var imageUriPostcard :Uri
    lateinit var showImg :ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_postcard,container,false)
        showImg = view.findViewById(R.id.show_img)
        val cameraImageView = view.findViewById<ImageView>(R.id.id_camera_iv)
        cameraImageView.setOnClickListener {
            //获取系统相机拍照
            getSysCameraToTakePhoto()
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_CODE ->{
                //RESULT_OK = -1
                if(resultCode == -1){
                    //保存原始照片
                    var bitmap = BitmapFactory.decodeStream(activity?.contentResolver?.openInputStream(imageUriCamera))
                    activity?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUriCamera))
                    //创建并保存带水印照片
                    saveWaterMarkPicture(bitmap)
                }
            }
        }
    }

    //获取系统相机拍照
    private fun getSysCameraToTakePhoto(){
        var filePath = Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DCIM+File.separator+"Camera"+File.separator
        var imageFileCamera = File(filePath,SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".jpg")
        //判断目录是否存在 没有则创建目录
        if(!File(filePath).exists()){
            File(filePath).mkdir()
        }
        if(imageFileCamera.exists()){
            imageFileCamera.delete()
        }
        imageFileCamera.createNewFile()

        if(Build.VERSION.SDK_INT >=24){
            imageUriCamera = FileProvider.getUriForFile(requireContext(),"com.xhy.fileprovider",imageFileCamera)
        }else{
            imageUriCamera = Uri.fromFile(imageFileCamera)
        }

        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriCamera)
        startActivityForResult(intent, REQUEST_CODE)
    }

    //创建并保存带水印照片
    private fun saveWaterMarkPicture( bitmap : Bitmap){
        var filePath = Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DCIM + File.separator+"postcard"+File.separator
        Log.d("TTT", filePath)
        var imageFilePostcard = File(filePath,SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".jpg")
        //判断目录是否存在 没有则创建目录
        if(!File(filePath).exists()){
            File(filePath).mkdir()
        }
        if(imageFilePostcard.exists()){
            imageFilePostcard.delete()
        }
        imageFilePostcard.createNewFile()

        if(Build.VERSION.SDK_INT >=24){
            imageUriPostcard = FileProvider.getUriForFile(requireContext(),"com.xhy.fileprovider",imageFilePostcard)
        }else{
            imageUriPostcard = Uri.fromFile(imageFilePostcard)
        }

        var bos = BufferedOutputStream(FileOutputStream(imageFilePostcard))

        var bitmapConfig : Bitmap.Config = bitmap.config
        if(bitmapConfig == null){
            bitmapConfig = Bitmap.Config.ARGB_8888
        }
        var bitmaptemp = bitmap.copy(bitmapConfig,true)
        var canvas = Canvas(bitmaptemp)
        var paint = Paint()
        paint.textSize = 200f
        paint.setColor(resources.getColor(R.color.colorAccent))

        var sp = activity?.getSharedPreferences("data", 0)
        var text = sp?.getString("city", "") + "\r\n" + sp?.getString("latitude", "") + "\r\n" + sp?.getString("longitude", "")
        canvas.drawText(text, 200f, 200f ,paint)

        bitmaptemp.compress(Bitmap.CompressFormat.JPEG,100,bos)
        bos.flush()
        bos.close()
        activity?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUriPostcard))
        showImg.setImageBitmap(bitmaptemp)
    }
}