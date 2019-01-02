package com.example.q.cs496w1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;



/**
 * 이미지를 보여주면서 이벤트를 처리하기 위해 정의한 뷰 클래스
 *
 * @author Mike
 *
 */
public class ImageDisplayView extends View implements OnTouchListener {

    public static final String TAG = "ImageDisplayView";

    Context mContext;

    Canvas mCanvas;

    Bitmap mBitmap;

    Paint mPaint;

    int lastX;
    int lastY;

    public float SourceRatio;

    public float totalTranslatedX;
    public float totalTranslatedY;

    Bitmap sourceBitmap;

    Matrix mMatrix;

    float sourceWidth = 0.0F;
    float sourceHeight = 0.0F;

    float bitmapCenterX;
    float bitmapCenterY;

    boolean actionDone;

    float scaleRatio;
    float totalScaleRatio;

    public float displayWidth = 0.0F;
    public float displayHeight = 0.0F;

    int displayCenterX = 0;
    int displayCenterY = 0;

    float displayedImageWidth;
    float displayedImageHeight;

    public float startX;
    public float startY;
    public float endX;
    public float endY;

    public float pivotX;
    public float pivotY;

    public static float MAX_SCALE_RATIO = 5.0F;
    public static float MIN_SCALE_RATIO = 0.1F;

    float oldDistance = 0.0F;

    int oldPointerCount = 0;
    boolean isScrolling = false;
    float distanceThreshold = 3.0F;

    /**
     * 생성자
     *
     * @param context
     */
    public ImageDisplayView(Context context) {
        super(context);

        mContext = context;

        init();
    }

    /**
     * 생성자
     *
     * @param context
     * @param attrs
     */
    public ImageDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        init();
    }

    /**
     * 초기화
     */
    private void init() {
        mPaint = new Paint();
        mMatrix = new Matrix();

        actionDone = true;
        lastX = -1;
        lastY = -1;
        totalTranslatedX = 0f;
        totalTranslatedY = 0f;

        setOnTouchListener(this);
    }

    /**
     * 화면에 보여지기 전에 호출되는 메소드
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w > 0 && h > 0) {
            Log.d(TAG,"OnsizeChanged 실행");
            newImage(w, h);

            redraw();
        }
    }

    /**
     * 새로운 비트맵 이미지를 메모리에 생성
     *
     * @param widthi
     * @param heighti
     */
    public void newImage(int widthi, int heighti) {
        Bitmap img = Bitmap.createBitmap(widthi, heighti, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);

        mBitmap = img;
        mCanvas = canvas;

        displayWidth = (float)widthi;
        displayHeight = (float)heighti;


        displayCenterX = widthi/2;
        displayCenterY = heighti/2;

        // 고화질 이미지를 화면 크기에 맞게 조정
        float widthRatio, heightRatio, ratio;
        widthRatio = (float) sourceWidth / displayWidth;
        heightRatio = (float) sourceHeight / displayHeight;
        if (widthRatio > heightRatio) {
            // landscape
            ratio = widthRatio;

        } else {
            // portrait
            ratio = heightRatio;
        }
        if(ratio > 1){
            SourceRatio = ratio;
            mMatrix.postScale(1/ratio,1/ratio,bitmapCenterX,bitmapCenterY);
            displayedImageWidth = sourceWidth / ratio;
            displayedImageHeight = sourceHeight / ratio;
        }else{
            displayedImageWidth = sourceWidth;
            displayedImageHeight = sourceHeight;
            SourceRatio = 1;
        }
        Log.d("소스 라시오", SourceRatio + "");
        totalScaleRatio = 1;

    }

    public void drawBackground(Canvas canvas) {
        if (canvas != null) {
            canvas.drawColor(Color.BLACK);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    public void setImageData(Bitmap image) {
        recycle();

        sourceBitmap = image;

        sourceWidth = sourceBitmap.getWidth();
        sourceHeight = sourceBitmap.getHeight();

        bitmapCenterX = sourceBitmap.getWidth()/2;
        bitmapCenterY = sourceBitmap.getHeight()/2;

        scaleRatio = 1.0F;
        totalScaleRatio = 1.0F;
    }

    public void recycle() {
        if (sourceBitmap != null) {
            sourceBitmap.recycle();
        }
    }

    public void redraw() {
        if (sourceBitmap == null) {
            Log.d(TAG, "sourceBitmap is null in redraw().");
            return;
        }

        drawBackground(mCanvas);

        float originX = (displayWidth - (float)sourceBitmap.getWidth()) / 2.0F;
        float originY = (displayHeight - (float)sourceBitmap.getHeight()) / 2.0F;

        mCanvas.translate(originX, originY);
        mCanvas.drawBitmap(sourceBitmap, mMatrix, mPaint);
        mCanvas.translate(-originX, -originY);

        invalidate();
    }


    /**
     * 터치 이벤트 처리
     */
    public boolean onTouch(View v, MotionEvent ev) {
        final int action = ev.getAction();

        int pointerCount = ev.getPointerCount();
        Log.d(TAG, "Pointer Count : " + pointerCount);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d("태스", "ACTION DOWN실행 " + pointerCount);
                if (pointerCount == 1) {
                    float curX = ev.getX();
                    float curY = ev.getY();

                    startX = curX;
                    startY = curY;

                } else if (pointerCount == 2) {
                    endX = ev.getX(1);
                    endY = ev.getY(1);


                    isScrolling = true;
                }
                oldDistance = 0.0F;
                redraw();

                return true;
            case MotionEvent.ACTION_MOVE:

                if (pointerCount == 1) {

                    if (isScrolling) {	// just double tap scrolling -> ignore it
                        return true;
                    }

                    float curX = ev.getX();
                    float curY = ev.getY();

                    if (startX == 0.0F) {
                        startX = curX;
                        startY = curY;

                        return true;
                    }

                    float offsetX = startX - curX;
                    float offsetY = startY - curY;

                    if (oldPointerCount == 2) {
                        startX = endX;
                        startY = endY;
                    } else {
                        Log.d(TAG, "ACTION_MOVE : " + offsetX + ", " + offsetY);

                        if (totalScaleRatio > 1.0F) {
                            if(actionDone==true){
                                moveImage(-offsetX, -offsetY);
                                updateTranslate();
                            }else{
                                actionDone=true;
                            }
                        }

                        startX = curX;
                        startY = curY;
                    }


                } else if (pointerCount == 2) {

                    float x1 = ev.getX(0);
                    float y1 = ev.getY(0);
                    float x2 = ev.getX(1);
                    float y2 = ev.getY(1);


                    float dx = x1 - x2;
                    float dy = y1 - y2;
                    float distance = new Double(Math.sqrt(new Float(dx * dx + dy * dy).doubleValue())).floatValue();
                    pivotX = (x1+x2)/2;
                    pivotY = (y1+y2)/2;
                    float outScaleRatio = 0.0F;
                    if (oldDistance == 0.0F) {
                        oldDistance = distance;

                        break;
                    }

                    if (distance > oldDistance) {
                        if ((distance-oldDistance) < distanceThreshold) {
                            return true;
                        }

                        outScaleRatio = scaleRatio + (oldDistance / distance * 0.05F);
                    } else if (distance < oldDistance) {
                        if ((oldDistance-distance) < distanceThreshold) {
                            return true;
                        }

                        outScaleRatio = scaleRatio - (distance / oldDistance * 0.05F);
                    }

                    if (outScaleRatio < MIN_SCALE_RATIO || outScaleRatio > MAX_SCALE_RATIO) {
                        Log.d(TAG, "Invalid scaleRatio : " + outScaleRatio);
                    } else {
                        Log.d(TAG, "Distance : " + distance + ", ScaleRatio : " + outScaleRatio);
                        scaleImage(distance/oldDistance,pivotX,pivotY);
                        updateTranslate();

                    }


                    oldDistance = distance;
                }

                oldPointerCount = pointerCount;
                break;

            case MotionEvent.ACTION_UP:

                if (pointerCount == 1) {
                    Log.d(TAG, "MOTION UP이 실행되었습니다. 그리고 POINTER COUNTER는 1입니다.");
                    float curX = ev.getX();
                    float curY = ev.getY();

                    float offsetX = startX - curX;
                    float offsetY = startY - curY;

                    if (oldPointerCount == 2) {
                        startX = endX;
                        startY = endY;
                    } else {
//                        moveImage(-offsetX, -offsetY);
                    }

                    // 올바른 이미지로 업데이트
                    updateScale();
                    updateTranslate();
                    oldDistance = 0.0f;

                } else {
                    Log.d(TAG, "MOTION UP이 실행되었습니다. 그리고 POINTER COUNTER는 2입니다.");
                    isScrolling = false;
                }

                return true;
            case MotionEvent.ACTION_POINTER_2_UP:{
                Log.d(TAG, "MOTION PINTER UP이 실행되었습니다. 그리고 POINTER COUNTER는 2입니다.");
                oldDistance=0;
                return true;
            }
//            case MotionEvent.ACTION_:{
//                Log.d(TAG, "MOTION PINTER DOWN이 실행되었습니다. 그리고 POINTER COUNTER는 2입니다.");
//                oldDistance=0;
//            }
        }
        Log.d("이건 꼭 봐야해", action + "");

        return true;
    }

    /**
     * 축소 액션을 취했는데 기본으로 보여준 image 보다 작아지면 기본 이미지 크기로 업데이트함.
     *
     */
    private void updateScale(){
        if(totalScaleRatio<1){
            Log.d(TAG, "UPDATE SCALE 함수가실행되었습니다.");
            mMatrix.postScale(1/totalScaleRatio,1/totalScaleRatio,bitmapCenterX, bitmapCenterY);
            mMatrix.postTranslate(-totalTranslatedX,-totalTranslatedY);
            displayedImageWidth /= totalScaleRatio;
            displayedImageHeight /= totalScaleRatio;
            totalScaleRatio = 1;
            totalTranslatedX = 0;
            totalTranslatedY = 0;

        }
        Log.d("태그", displayWidth + " * " + displayHeight + ", source : " + sourceWidth + " * " + sourceHeight);
    }


    /**
     * 이미지 이동시킬때 적당히 제한을 걸어도록 함.
     */
    private void updateTranslate(){
        float curImageTranslatedY = totalTranslatedY*totalScaleRatio;
        float curImageTranslatedX = totalTranslatedX*totalScaleRatio;
        float imagemoveY=0f, imagemoveX=0f;

        if(displayedImageHeight > displayHeight){
            if(curImageTranslatedY > (displayedImageHeight-displayHeight)/2){
                imagemoveY = -(curImageTranslatedY-(displayedImageHeight-displayHeight)/2);
                moveImage(0,imagemoveY);
            }else if(curImageTranslatedY < (-displayedImageHeight+displayHeight)/2){
                imagemoveY = -(curImageTranslatedY+(displayedImageHeight-displayHeight)/2);
                moveImage(0,imagemoveY);
            }
        }
        if(curImageTranslatedX > (displayedImageWidth-displayWidth)/2){
            imagemoveX = -(curImageTranslatedX-(displayedImageWidth-displayWidth)/2);
            moveImage(imagemoveX,0);
        }else if(curImageTranslatedX < -(displayedImageWidth-displayWidth)/2){
            imagemoveX = -(curImageTranslatedX+(displayedImageWidth-displayWidth)/2);
            moveImage(imagemoveX,0);
        }
        redraw();
        Log.d("업데이트 트랜스래이트",imagemoveX +" , " + imagemoveY);
    }


    /**
     * 확대/축소
     *
     * @param inScaleRatio
     */
    private void scaleImage(float inScaleRatio) {

        mMatrix.postScale(inScaleRatio, inScaleRatio, bitmapCenterX, bitmapCenterY);
//        mMatrix.postRotate(0);


        totalScaleRatio = totalScaleRatio * inScaleRatio;


        displayedImageHeight *= inScaleRatio;
        displayedImageWidth *= inScaleRatio;
        Log.d(TAG, "scaleImage() called : " + displayedImageWidth + " , " + displayedImageHeight);
    }

    private void scaleImage(float inScaleRatio, float pX, float pY){
        float disanceCenterpXbefoeScale = displayCenterX-pX;
        float disanceCenterpYbefoeScale = displayCenterY-pY;
        float distanceCenterXafterScale = disanceCenterpXbefoeScale*inScaleRatio;
        float distanceCenterYafterScale = disanceCenterpYbefoeScale*inScaleRatio;
        Log.d("피봇", pX + " * " + pY);
        mMatrix.postScale(inScaleRatio, inScaleRatio, bitmapCenterX, bitmapCenterY);
        mMatrix.postRotate(0);

        totalScaleRatio = totalScaleRatio * inScaleRatio;
        displayedImageHeight *= inScaleRatio;
        displayedImageWidth *= inScaleRatio;

        moveImage(distanceCenterXafterScale-disanceCenterpXbefoeScale,distanceCenterYafterScale-disanceCenterpYbefoeScale);
    }

    /**
     * 이동
     *
     * @param offsetX
     * @param offsetY
     */
    private void moveImage(float offsetX, float offsetY) {
        Log.d(TAG, "moveImage() called : " + offsetX + ", " + offsetY);
        if(displayedImageHeight > displayHeight){
            mMatrix.postTranslate(0,offsetY);
            totalTranslatedY += offsetY/totalScaleRatio;
        }
        if(displayedImageWidth > displayWidth ){
            mMatrix.postTranslate(offsetX, 0);
            totalTranslatedX += offsetX/totalScaleRatio;
        }

    }


}