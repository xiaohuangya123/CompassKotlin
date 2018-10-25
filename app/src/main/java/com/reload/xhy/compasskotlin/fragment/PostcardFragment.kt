package com.reload.xhy.compasskotlin.fragment

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.media.MediaScannerConnection
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
import com.bumptech.glide.Glide
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
    lateinit var imageFileCamera :File

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
                    //通知系统相册扫描新拍的照片，更新系统相册
                    MediaScannerConnection.scanFile(activity, arrayOf(imageFileCamera.absolutePath)
                            , arrayOf("image/jpeg")
                            , { path, uri ->  Log.d("TTT", path) })
                    //创建并保存带水印照片
                    saveWaterMarkPicture(bitmap)
                }
            }
        }
    }

    //获取系统相机拍照
    private fun getSysCameraToTakePhoto(){
        var filePath = Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DCIM+File.separator+"Camera"+File.separator
        imageFileCamera = File(filePath,SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".jpg")
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
        var fileName = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".jpg"
        var imageFilePostcard = File(filePath,fileName)
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

        var bitmaptemp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, Matrix(), true)
        //如果手机是三星，则进行相片旋转90°处理
        if("samsung" == getPhoneBrand()){
            var matrix = Matrix()
//            matrix.postRotate(readPictureDegree(imageFilePostcard.absolutePath))
            matrix.postRotate(90f)
            Log.d("TTTs", readPictureDegree(imageFilePostcard.absolutePath).toString())
            bitmaptemp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        var canvas = Canvas(bitmaptemp)
        var paint = Paint()
        paint.textSize = 70f
        paint.setColor(resources.getColor(R.color.colorWhite))

        var sp = activity?.getSharedPreferences("data", 0)
        var text = sp?.getString("city", "") +
                "\r\n" + "海拔：" + sp?.getString("altitude","") +  "m" +
                "\r\n" + "纬度：" + sp?.getString("latitude", "") +
                "\r\n" + "经度：" + sp?.getString("longitude", "")

        //获取经纬度，位置，海拔文字的高宽
        var bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        canvas.drawText(text, (bitmaptemp.width-bounds.width()).toFloat()-48
                , (bitmaptemp.height-bounds.height()).toFloat() ,paint)

        bitmaptemp.compress(Bitmap.CompressFormat.JPEG,100,bos)
        bos.flush()
        bos.close()
        //通知系统相册扫描新拍的照片，更新系统相册
        MediaScannerConnection.scanFile(activity, arrayOf(imageFilePostcard.absolutePath)
                , arrayOf("image/jpeg")
                , { path, uri ->  Log.d("TTT", path) })
        Glide.with(this).load(bitmaptemp).into(showImg)
//        showImg.setImageBitmap(bitmaptemp)
    }

    //获取照片exif信息中的旋转角度
    private fun readPictureDegree(path :String) :Float {
        var degree = 0
        var exifInterface = ExifInterface(path)
        var orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION ,ExifInterface.ORIENTATION_NORMAL)
        when(orientation){
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                degree = 90
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                degree = 180
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                degree = 270
            }
        }
        return degree.toFloat()
    }

    //获取手机品牌
    private fun getPhoneBrand() : String{
        return android.os.Build.BRAND
    }

}