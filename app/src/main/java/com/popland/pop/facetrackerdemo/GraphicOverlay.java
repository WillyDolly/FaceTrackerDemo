package com.popland.pop.facetrackerdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.android.gms.vision.CameraSource;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by hai on 08/12/2017.
 */

public class GraphicOverlay extends View {
Object mLock = new Object();
int mPreviewWidth;
static float widthScaleFactor = 1.0f;
int mPreviewHeight;
static float heightScaleFactor = 1.0f;
int mFacing =  CameraSource.CAMERA_FACING_BACK;
Set<Graphic> mGraphics = new HashSet<>();

    public static abstract class Graphic{
        GraphicOverlay gOverlay;

        Graphic(GraphicOverlay gOverlay){
            Log.i("AAA","Graphic Constructor");
            this.gOverlay = gOverlay;
        }

        public float scaleX(float horizontal){
            return horizontal * widthScaleFactor;
        }

        public float scaleY(float vertical){
            return vertical * heightScaleFactor;
        }

        public float translateX(float x){
            if(gOverlay.mFacing == CameraSource.CAMERA_FACING_FRONT)
                return gOverlay.getWidth() - scaleX(x);
            else
                return scaleX(x);
        }

        public float translateY(float y){
            return scaleY(y);
        }

        public abstract void draw(Canvas c);

        public void postInvalidate(){
            Log.i("AAA","Graphic postInvalidate");
            gOverlay.postInvalidate();
        }
    }

    public GraphicOverlay(Context context,AttributeSet attrs) {
        super(context,attrs);
        Log.i("AAA","GraphicOverlay Constructor");
    }

    public void clear(){
        //synchronized (mLock){
            mGraphics.clear();
        //}
        postInvalidate();
    }

    public void add(Graphic graphic){
        //synchronized(mLock){
            mGraphics.add(graphic);
        //}
        postInvalidate();
    }

    public void remove(Graphic graphic){
        //synchronized(mLock){
            mGraphics.remove(graphic);
        //}
        postInvalidate();
    }

    public void setCameraInfo(int width,int height,int camFacing){
        //synchronized(mLock) {
            mPreviewWidth = width;
            mPreviewHeight = height;
            mFacing = camFacing;
        //}
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //synchronized(mLock) {
            Log.i("AAA","GraphicOverlay onDraw");
            if (mPreviewWidth != 0 && mPreviewHeight != 0) {
                widthScaleFactor = (float) canvas.getWidth() / (float) mPreviewWidth;
                heightScaleFactor = (float) canvas.getHeight() / (float) mPreviewHeight;
                Log.i("AAA","canvas: "+canvas.getWidth()+"-"+canvas.getHeight());
                Log.i("AAA","scaleFactor: "+widthScaleFactor+"-"+heightScaleFactor);
            }

            for (Graphic graphic : mGraphics)
                graphic.draw(canvas);
       // }
    }
}
