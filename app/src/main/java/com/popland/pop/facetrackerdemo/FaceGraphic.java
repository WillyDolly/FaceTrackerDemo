package com.popland.pop.facetrackerdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;

/**
 * Created by hai on 08/12/2017.
 */

public class FaceGraphic extends GraphicOverlay.Graphic {
float FACE_POSITION_RADIUS = 10.0f;
float ID_TEXT_SIZE = 40.0f;
float ID_OFFSET_X = -50.0f;
float ID_OFFSET_Y = 50.0f;
float BOX_STROKE_WIDTH = 5.0f;

Paint facePositionPaint, textPaint , boxPaint, eyePaint,cheekPaint, nosePaint, mouthPaint;
int colors[] = {Color.BLUE,Color.CYAN,Color.RED,Color.GREEN,Color.YELLOW,Color.MAGENTA,Color.WHITE};
int colorCurrentIndex = 0;
int faceId;
Face mFace;

    FaceGraphic(GraphicOverlay gOverlay){
        super(gOverlay);
        Log.i("AAA","faceGraphic constructor");
        colorCurrentIndex = (colorCurrentIndex + 1) % colors.length;
        int selectedColor = colors[colorCurrentIndex];

        facePositionPaint = new Paint();
        facePositionPaint.setColor(selectedColor);

        textPaint = new Paint();
        textPaint.setTextSize(ID_TEXT_SIZE);
        textPaint.setColor(selectedColor);

        boxPaint = new Paint();
        boxPaint.setColor(selectedColor);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

        eyePaint = new Paint();
        eyePaint.setColor(Color.WHITE);
        cheekPaint = new Paint();
        cheekPaint.setColor(Color.GREEN);
        nosePaint = new Paint();
        nosePaint.setColor(Color.YELLOW);
        mouthPaint = new Paint();
        mouthPaint.setColor(Color.RED);
    }

    public void setId(int id){
        Log.i("AAA","setId");
        faceId = id;
    }

    public void updateFace(Face face){
        Log.i("AAA","updateFace");
        mFace = face;
        postInvalidate();//->Graphic.postInvalidate{ gOverlay.postInvalidate} -> gOverlay.onDraw{ Graphic.draw} -> FaceGraphic.draw
    }

    @Override
    public void draw(Canvas c) {
        Log.i("AAA","draw");
        Face face = mFace;
        if(face ==  null)
            return;
        //face's center point
        float x = translateX(face.getPosition().x + face.getWidth()/2);//preview'ss coordinates -> screen's coordinates
        float y = translateY(face.getPosition().y + face.getHeight()/2);
        //face's left-top position
        float faceLocX = translateX(face.getPosition().x);
        float faceLocY = translateY(face.getPosition().y);
        float EulerZ = face.getEulerZ();
        Log.i("EEE",""+EulerZ);
        c.drawText(""+EulerZ,faceLocX,faceLocY,textPaint);
//        c.drawCircle(faceLocX,faceLocY,FACE_POSITION_RADIUS,facePositionPaint);
        c.drawText(""+faceId, x, y,textPaint);
//        c.drawText("happiness: "+String.format("%.2f",face.getIsSmilingProbability()), x-ID_OFFSET_X, y-ID_OFFSET_Y,textPaint);
//        c.drawText("right eye: "+String.format("%.2f",face.getIsRightEyeOpenProbability()), x+ID_OFFSET_X*2, y+ID_OFFSET_Y*2,textPaint);
//        c.drawText("left eye: "+String.format("%.2f",face.getIsLeftEyeOpenProbability()),  x-ID_OFFSET_X*2, y-ID_OFFSET_Y*2,textPaint);

        //draw on landmarks
        List<Landmark> landmarks = face.getLandmarks();
        for(int i=0;i<landmarks.size();++i){// size = 8, features: eye, cheek, nose, lower lip, mouth's corner
            float X = translateX(landmarks.get(i).getPosition().x);
            float Y = translateY(landmarks.get(i).getPosition().y);
            switch (landmarks.get(i).getType()){
                case Landmark.LEFT_EYE:
                case Landmark.RIGHT_EYE:
                    c.drawCircle(X, Y, FACE_POSITION_RADIUS, eyePaint);
                    break;
                case Landmark.LEFT_CHEEK:
                case Landmark.RIGHT_CHEEK:
                    c.drawCircle(X, Y, FACE_POSITION_RADIUS, cheekPaint);
                    break;
                case Landmark.NOSE_BASE:
                    c.drawCircle(X, Y, FACE_POSITION_RADIUS, nosePaint);
                    break;
                case Landmark.LEFT_MOUTH:
                case Landmark.RIGHT_MOUTH:
                case Landmark.BOTTOM_MOUTH:
                    c.drawCircle(X, Y, FACE_POSITION_RADIUS, mouthPaint);
                    break;
            }
        }



        float xOffset = scaleX(face.getWidth()/2);
        float yOffset = scaleY(face.getHeight()/2);
        //from width's center point -> left , right
        float left = x - xOffset;
        float right = x + xOffset;
        //from height's center point -> top , bottom
        float top =  y - yOffset;
        float bottom = y + yOffset;
        c.drawRect(left,top,right,bottom,boxPaint);
    }
}
