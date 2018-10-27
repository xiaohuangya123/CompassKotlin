package com.reload.xhy.compasskotlin.fragment

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
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.reload.xhy.compasskotlin.R
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PostcardFragment : Fragment(), ViewPager.OnPageChangeListener{

    val REQUEST_CODE = 119
    lateinit var imageUriCamera :Uri
    lateinit var imageUriPostcard :Uri
    lateinit var showImg :ImageView
    lateinit var imageFileCamera :File
    lateinit var viewpager :ViewPager
    lateinit var imageText :TextView
    lateinit var llPoint :LinearLayout
    lateinit var imageviews :ArrayList<ImageView>
    lateinit var texts : Array<String>
    lateinit var imageResIds :IntArray
    //记录轮播图片上第一个圆点的位置
    var lastPosition = 0
    var isRuning = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_postcard,container,false)
        showImg = view.findViewById(R.id.show_img)

        //初始化轮播图片相关view
        initPictureRollingViews(view)
        //初始化轮播图片，文字等数据
        initPictureRollingDate()
        //初始化适配器
        initPictureRollingAdapter()

        Thread{
            run {
                while (isRuning){
                    Thread.sleep(3000)
                    activity?.runOnUiThread{viewpager.currentItem =viewpager.currentItem + 1}
                }
            }
        }.start()

        val cameraImageView = view.findViewById<ImageView>(R.id.id_camera_iv)
        cameraImageView.setOnClickListener {
            //获取系统相机拍照
            getSysCameraToTakePhoto()
        }
        return view
    }

    //初始化适配器,或者说图片轮播逻辑
    private fun initPictureRollingAdapter(){
        llPoint.getChildAt(lastPosition).isEnabled = true
        imageText.text = texts[0]
        viewpager.adapter = MyPagerAdapter()
        viewpager.setOnPageChangeListener(this)
        viewpager.currentItem = 5000000
    }

    //初始化轮播图片相关view
    private fun initPictureRollingViews(view: View){
        viewpager = view.findViewById(R.id.id_postcard_viewpager)
        imageText = view.findViewById(R.id.id_postcard_text_tv)
        llPoint = view.findViewById(R.id.id_postcard_point_ll)
    }

    //初始化轮播图片，文字等数据
    private fun initPictureRollingDate(){
        //轮播图片资源
        imageResIds = intArrayOf(R.drawable.benz,R.drawable.ocean,R.drawable.qiaoba)
        //轮播图片文字描述信息
        texts = arrayOf<String>("奔驰","大海","乔巴")
        imageviews = ArrayList()
        var pointview :View
        for (id in imageResIds.indices){
            var imageView = ImageView(activity)
            imageView.setImageResource(imageResIds[id])
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageviews.add(imageView)

            //轮播图片上的小圆点
            pointview = View(activity)
            pointview.setBackgroundResource(R.drawable.selector_postcard_point)
            var params = LinearLayout.LayoutParams(20,20)
            if(id !=0){
                params.leftMargin = 20
            }
            pointview.isEnabled = false
            llPoint.addView(pointview, params)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        isRuning = false
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

    //ViewPager 的 adapter
    inner class MyPagerAdapter : PagerAdapter(){

        //指定复用的判断逻辑，固定写法：view == object
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            //当创建新的条目，又反回来，判断view是否可以被复用(即是否存在)
            return view === `object`
//            return false
        }

        //返回显示数据的总条数，为了实现无限循环，把返回的值设置为最大整数
        override fun getCount(): Int {
            return Int.MAX_VALUE
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var newPosition = position % imageviews.size
            var imageView = ImageView(activity)
            imageView.setBackgroundResource(imageResIds[newPosition])
//            var imageView = imageviews[newPosition]
            container.addView(imageView)
            return imageView
        }

       override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
           container.removeView(`object` as View)
       }
    }

    //ViewPager.OnPageChangeListener 需要实现的方法
    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        var newPositon = position % imageviews.size
        imageText.text = texts[newPositon]
        llPoint.getChildAt(lastPosition).isEnabled = false
        llPoint.getChildAt(newPositon).isEnabled = true
        lastPosition = newPositon
    }

}