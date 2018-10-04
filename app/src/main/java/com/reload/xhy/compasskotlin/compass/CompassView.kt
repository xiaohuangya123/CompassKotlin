package com.reload.xhy.compasskotlin.compass

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.reload.xhy.compasskotlin.R

class CompassView : View {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    //传感器转动的角度
    var degreeVal = 0f
    lateinit var mCanvas : Canvas
    //自定义正方形compassView的宽度，
    var compassViewWidth :Float = 0F
    //圆心坐标
    var circleX = 0f
    var circleY = 0f
    //外接圆半径
    var outsideRadius = 0f
    //内圆半径
    var insideRadius = 0f
    //小三角形边长
    var triangleSideLength = 40f
    //小三角形高
    var triangleHeight = (triangleSideLength * 0.87).toFloat()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //确定View为一个屏幕宽度的正方形
        compassViewWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()

        //初始化主要参数
        outsideRadius = compassViewWidth* 2/5
        insideRadius = compassViewWidth*13/40
        circleX = compassViewWidth/2
        circleY = outsideRadius + triangleHeight

        setMeasuredDimension(compassViewWidth.toInt(), (triangleHeight + outsideRadius*2).toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //初始化画板
        mCanvas = canvas

        //画指南针外接圆和小三角
        drawCompassOutsideCircleAndTriangle()
        //画指南针内圆,小红三角，刻度，刻度上NESW,30,60等度数
        drawCompassInsideCircleTriangleDegree()
        //画内部渐变圆
        drawInnerCircle()
        //画指南针圆心度数
        drawDegree()
    }

    //画指南针圆心度数
    private fun drawDegree(){
        val degree = degreeVal.toInt().toString() + "°"
        val paint = Paint()
        paint.textSize = 70f
        paint.color = resources.getColor(R.color.colorLightYellow)
        val nRect = Rect()
        paint.getTextBounds(degree,0,degree.length, nRect)
        val width = nRect.width()
        val height = nRect.height()
        mCanvas.drawText(degree,circleX-width/2,circleY+height/2,paint)

    }
    //画内部渐变圆
    private fun drawInnerCircle(){
        val shader = RadialGradient(circleX,circleY,insideRadius*3/5
                , intArrayOf(Color.parseColor("#2B2B2B"),Color.parseColor("#1F1F1F"))
                ,null, Shader.TileMode.CLAMP)
        val paint = Paint()
        paint.setAntiAlias(true)
        paint.setShader(shader)
        mCanvas.drawCircle(circleX,circleY,insideRadius*2/3,paint)
    }

    //画指南针内圆,小红三角，刻度，刻度上NESW,30,60等度数
    private fun drawCompassInsideCircleTriangleDegree(){
//        mCanvas.save()
        mCanvas.rotate(degreeVal, circleX, circleY)

        //用于画内圆，小红三角，刻度
        val circlePaint = Paint()
        circlePaint.setStyle(Paint.Style.STROKE)
        circlePaint.strokeWidth = 5f
        circlePaint.setAntiAlias(true)

        val rectf : RectF = RectF(compassViewWidth/2-insideRadius,triangleHeight + compassViewWidth*3/40,
                compassViewWidth/2+insideRadius,insideRadius*2+triangleHeight + compassViewWidth*3/40)
        circlePaint.setColor(resources.getColor(R.color.colorLightGray))
        mCanvas.drawArc(rectf,-85f,350f,false,circlePaint)

        //画出小红三角形
        circlePaint.setStyle(Paint.Style.FILL)
        circlePaint.setColor(resources.getColor(R.color.colorRed))
        val mOutsideTrianglePath: Path = Path()
        mOutsideTrianglePath.moveTo(compassViewWidth/2, compassViewWidth*3/40)
        mOutsideTrianglePath.lineTo(compassViewWidth/2 - triangleSideLength/2, triangleSideLength + compassViewWidth*3/40)
        mOutsideTrianglePath.lineTo(compassViewWidth/2 + triangleSideLength/2, triangleSideLength + compassViewWidth*3/40)
        mOutsideTrianglePath.close()
        mCanvas.drawPath(mOutsideTrianglePath, circlePaint)

        //用于画深灰色刻度
        circlePaint.style = Paint.Style.STROKE
        circlePaint.strokeWidth = 3f
        circlePaint.setColor(resources.getColor(R.color.colorDarkGray))

        //用于画浅灰色刻度
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        paint.color = resources.getColor(R.color.colorLightGray)

        //用于画指南针内部刻度上NESW
        val textPaint = Paint()
        textPaint.textSize = 45f
        textPaint.style = Paint.Style.FILL
        textPaint.setTypeface(Typeface.DEFAULT_BOLD)

        //用于画指南针内部刻度上30,60等度数
        val degreePaint = Paint()
        degreePaint.textSize = 35f
        degreePaint.style = Paint.Style.FILL
        degreePaint.setTypeface(Typeface.DEFAULT_BOLD)
        degreePaint.color = resources.getColor(R.color.colorDarkGray)

        //获取N,S,W,E字符宽度
        val nTextWidth = getCharWidth("N", textPaint)
        val sTextWidth = getCharWidth("S", textPaint)
        val wTextWidth = getCharWidth("W", textPaint)
        val eTextWidth = getCharWidth("E", textPaint)

        for (i in 0..240){
            //画刻度线
            if(i==240 || i==60 || i==120 || i==180){
                mCanvas.drawLine(compassViewWidth/2, triangleHeight + compassViewWidth*3/40 + 10
                        , compassViewWidth/2, triangleHeight + compassViewWidth*3/40 + 10 +20,paint)
            }else{
                mCanvas.drawLine(compassViewWidth/2, triangleHeight + compassViewWidth*3/40 + 10
                        , compassViewWidth/2, triangleHeight + compassViewWidth*3/40 + 10 +20,circlePaint)
            }

            when(i){
                240 -> {
                    textPaint.color = resources.getColor(R.color.colorRed)
                    mCanvas.drawText("N", compassViewWidth/2 - nTextWidth/2, triangleHeight + compassViewWidth*3/40 + 10 + 20 + 50,textPaint)
                    }
                60 -> {
                    textPaint.color = resources.getColor(R.color.colorWhite)
                    mCanvas.drawText("E", compassViewWidth/2 - eTextWidth/2, triangleHeight + compassViewWidth*3/40 + 10 + 20 + 50,textPaint)
                }
                120 -> {
                    textPaint.color = resources.getColor(R.color.colorLightBlue)
                    mCanvas?.drawText("S", compassViewWidth/2 - sTextWidth/2, triangleHeight + compassViewWidth*3/40 + 10 + 20 + 50,textPaint)

                }
                180 -> {
                    textPaint.color = resources.getColor(R.color.colorWhite)
                    mCanvas.drawText("W", compassViewWidth/2 - wTextWidth/2, triangleHeight + compassViewWidth*3/40 + 10 + 20 + 50,textPaint)
                }
                20 -> mCanvas.drawText("30", compassViewWidth/2, triangleHeight + compassViewWidth*3/40 + 10 + 20 + 50,degreePaint)
                40 -> mCanvas.drawText("60", compassViewWidth/2, triangleHeight + compassViewWidth*3/40 + 10 + 20 + 50,degreePaint)
                80 -> mCanvas.drawText("120", compassViewWidth/2, triangleHeight + compassViewWidth*3/40 + 10 + 20 + 50,degreePaint)
                100 -> mCanvas.drawText("150", compassViewWidth/2, triangleHeight + compassViewWidth*3/40 + 10 + 20 + 50,degreePaint)
                140 -> mCanvas.drawText("210", compassViewWidth/2, triangleHeight + compassViewWidth*3/40 + 10 + 20 + 50,degreePaint)
                160 -> mCanvas.drawText("240", compassViewWidth/2, triangleHeight + compassViewWidth*3/40 + 10 + 20 + 50,degreePaint)
                200 -> mCanvas.drawText("300", compassViewWidth/2, triangleHeight + compassViewWidth*3/40 + 10 + 20 + 50,degreePaint)
                220 -> mCanvas.drawText("330", compassViewWidth/2, triangleHeight + compassViewWidth*3/40 + 10 + 20 + 50,degreePaint)

            }
            mCanvas.rotate(1.5f,compassViewWidth/2,triangleHeight + compassViewWidth*2/5)
        }
//        mCanvas.restore()
    }

    //画指南针外接圆和小三角
    private fun drawCompassOutsideCircleAndTriangle(){

        val outCirclePaint = Paint()
        outCirclePaint.setStyle(Paint.Style.STROKE)
        outCirclePaint.strokeWidth = 5f
        outCirclePaint.setAntiAlias(true)

        //画最外层圆弧
        val rectf : RectF = RectF(circleX-outsideRadius,triangleHeight,
                circleX+outsideRadius,outsideRadius*2+triangleHeight)
        outCirclePaint.setColor(resources.getColor(R.color.colorDarkGray))
        mCanvas.drawArc(rectf,-80f,120f,false,outCirclePaint)
        outCirclePaint.setColor(resources.getColor(R.color.colorLightGray))
        mCanvas.drawArc(rectf,40f,20f,false,outCirclePaint)
        outCirclePaint.setColor(resources.getColor(R.color.colorDarkRed))
        mCanvas.drawArc(rectf,120f,110f,false,outCirclePaint)
        outCirclePaint.setColor(resources.getColor(R.color.colorDarkGray))
        mCanvas.drawArc(rectf,-130f,30f,false,outCirclePaint)

        //画出小三角形
        outCirclePaint.setStyle(Paint.Style.FILL)
        var mOutsideTrianglePath: Path = Path()
        mOutsideTrianglePath.moveTo(circleX, 0f)
        mOutsideTrianglePath.lineTo(circleX - triangleSideLength/2, triangleHeight)
        mOutsideTrianglePath.lineTo(circleX + triangleSideLength/2, triangleHeight)
        mOutsideTrianglePath.close()
        mCanvas?.drawPath(mOutsideTrianglePath, outCirclePaint)
    }

    //获取文字宽度，如获取字符“N”的宽度
    fun getCharWidth(text :String, paint :Paint) : Int{
        val nRect = Rect()
        paint.getTextBounds("N",0,1, nRect)
        val nTextWidth = nRect.width()
        return nTextWidth
    }

    fun setDegreeValue(degree:Float) {
        degreeVal = degree
        invalidate()
    }
}