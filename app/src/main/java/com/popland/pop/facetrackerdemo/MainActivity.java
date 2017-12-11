package com.popland.pop.facetrackerdemo;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
CameraSource cameraSource;
CameraSourcePreview csPreview;
GraphicOverlay gOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        csPreview = (CameraSourcePreview)findViewById(R.id.csPreview);
        gOverlay = (GraphicOverlay)findViewById(R.id.gOverlay);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            createCameraSource();
        else
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        csPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cameraSource!=null) {
            cameraSource.release();
            cameraSource = null;
        }
    }

    public void createCameraSource(){
        Log.i("AAA","createCameraSource");
        // .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)-> eyeOpen - smiling Probability
        FaceDetector faceDetector = new FaceDetector.Builder(this)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        //can use LargestFaceFocusingProcessor for ProminentFaceOnly
        //Processor use pipeline to handle frames
        faceDetector.setProcessor(new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory()).build());
        if(!faceDetector.isOperational()){
            Toast.makeText(MainActivity.this,"not operational",Toast.LENGTH_SHORT).show();
        }

        cameraSource = new CameraSource.Builder(this,faceDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1000,500)
                .setRequestedFps(30.0f)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(grantResults.length>0){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    createCameraSource();
                else
                    Toast.makeText(MainActivity.this,"permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startCameraSource(){
        Log.i("AAA","startCameraSource");
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(code != ConnectionResult.SUCCESS){
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,code,200);
            dlg.show();
        }

            try {
                if(cameraSource!=null)
                    csPreview.start(cameraSource, gOverlay);
            } catch (IOException e) {
                cameraSource.release();
                cameraSource = null;
            }
    }

    public class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face>{
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(gOverlay);
        }
    }

    public class GraphicFaceTracker extends Tracker<Face>{
         GraphicOverlay graphicOverlay;
         FaceGraphic faceGraphic;

        GraphicFaceTracker(GraphicOverlay go){
            graphicOverlay = go;
            faceGraphic = new FaceGraphic(go);
        }

        @Override
        public void onNewItem(int idFace, Face face) {//can not tell 2 faces alike
            super.onNewItem(idFace, face);
            faceGraphic.setId(idFace);//Tracker provide faceId
        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            super.onUpdate(detections, face);
            graphicOverlay.add(faceGraphic);
            faceGraphic.updateFace(face);
        }

        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);
            graphicOverlay.remove(faceGraphic);
        }

        @Override
        public void onDone() {
            super.onDone();
            graphicOverlay.remove(faceGraphic);
        }
    }
}

