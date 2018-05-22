package com.slagkryssaren.funwithcubez;

/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Point;
import com.google.ar.core.Point.OrientationMode;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.slagkryssaren.common.helpers.CameraPermissionHelper;
import com.slagkryssaren.common.helpers.DisplayRotationHelper;
import com.slagkryssaren.common.helpers.FullScreenHelper;
import com.slagkryssaren.common.helpers.SnackbarHelper;
import com.slagkryssaren.common.helpers.TapHelper;
import com.slagkryssaren.common.rendering.BackgroundRenderer;
import com.slagkryssaren.common.rendering.ObjectRenderer;
import com.slagkryssaren.common.rendering.ObjectRenderer.BlendMode;
import com.slagkryssaren.common.rendering.PlaneRenderer;
import com.slagkryssaren.common.rendering.PointCloudRenderer;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.FloatBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * This is a simple example that shows how to create an augmented reality (AR) application using the
 * ARCore API. The application will display any detected planes and will allow the user to tap on a
 * plane to place a 3d model of the Android robot.
 */
public class ArActivity extends AppCompatActivity implements GLSurfaceView.Renderer {
    private static final String TAG = ArActivity.class.getSimpleName();

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView surfaceView;

    private boolean installRequested;

    private Session session;
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
    private DisplayRotationHelper displayRotationHelper;
    private TapHelper tapHelper;

    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private final ObjectRenderer virtualObject = new ObjectRenderer();
    private final ObjectRenderer virtualObjectShadow = new ObjectRenderer();
    private final PlaneRenderer planeRenderer = new PlaneRenderer();
    private final PointCloudRenderer pointCloudRenderer = new PointCloudRenderer();

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] anchorMatrix = new float[16];

    // Anchors created from taps used for object placing.
    private final ArrayList<Anchor> anchors = new ArrayList<>();

    private Button button1;


    private int counter = 0;
    private long startTime;
    private long elapsedTime = 0;

    BenchObjects benchObjects = new BenchObjects();
    BenchObjects benchPoints;
    BenchObjects benchPlanes;

    String sX = "0";
    String sZ = "0";
    int hashPlane = 0;
    ArrayList<PointObject> pointObjects = new ArrayList<>();
    ArrayList<PlaneObject> planeObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        surfaceView = findViewById(R.id.surfaceview);
        displayRotationHelper = new DisplayRotationHelper(/*context=*/ this);


        // Set up tap listener.
        tapHelper = new TapHelper(/*context=*/ this);
        surfaceView.setOnTouchListener(tapHelper);

        // Set up renderer.
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        surfaceView.setRenderer(this);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        installRequested = false;

        /*
            SECTION ADDED BY LOVE & ANNA
        */

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ArActivity.this, button1);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //session.pause();
                        onPause();
                        if(item.getTitle().equals("About")){
                            Intent i=new Intent(ArActivity.this,MainActivity.class);
                            startActivity(i);
                           // finish();
                        }
                        if(item.getTitle().equals("Settings")){
                            Intent i=new Intent(ArActivity.this,Settings.class);
                            startActivity(i);
                            //finish();
                        }
                        if(item.getTitle().equals("E-mail")){
                            File file = null;
                            try {
                                SharedPreferences settings = getSharedPreferences("prefs", 0);
                                int numberOfBenchmarks = settings.getInt("noOfBenchmarks", 1);
                                Log.d("KRASCH", Integer.toString(numberOfBenchmarks));
                                file = new File(benchObjects.createGson(pointObjects, planeObjects,numberOfBenchmarks).getAbsolutePath());
                                SharedPreferences.Editor editor = settings.edit();
                                numberOfBenchmarks++;
                                editor.putInt("noOfBenchmarks", numberOfBenchmarks);
                                editor.apply();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try{
                                sendEmail(file);
                            }catch (IOException e){
                                Log.e("ERR", "Could not send email");
                            }
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener

        /*
            END OF SECTION
        */

    }

    @Override
    protected void onResume() {
        super.onResume();
        session = null;
        if (session == null) {

            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this);
                    return;
                }

                isReadStoragePermissionGranted();
                isWriteStoragePermissionGranted();

                // Create the session.
                session = new Session(/* context= */ this);

            } catch (UnavailableArcoreNotInstalledException
                    | UnavailableUserDeclinedInstallationException e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update this app";
                exception = e;
            } catch (UnavailableDeviceNotCompatibleException e) {
                message = "This device does not support AR";
                exception = e;
            } catch (Exception e) {
                message = "Failed to create AR session";
                exception = e;
            }

            if (message != null) {
                messageSnackbarHelper.showError(this, message);
                Log.e(TAG, "Exception creating session", exception);
                return;
            }
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            session.resume();
        } catch (CameraNotAvailableException e) {
            // In some cases (such as another camera app launching) the camera may be given to
            // a different app instead. Handle this properly by showing a message and recreate the
            // session at the next iteration.
            messageSnackbarHelper.showError(this, "Camera not available. Please restart the app.");
            session = null;
            return;
        }

        startTime = System.currentTimeMillis();
        ArrayList<Long> pointTime = new ArrayList<>();
        ArrayList<Integer> points = new ArrayList<>();
        ArrayList<Long> planeTime = new ArrayList<>();
        ArrayList<String> planeX = new ArrayList<>();
        ArrayList<String> planeZ = new ArrayList<>();
        benchPoints = new BenchObjects(pointTime, points);
        benchPlanes = new BenchObjects(planeTime, planeX, planeZ);
        surfaceView.onResume();
        displayRotationHelper.onResume();

        messageSnackbarHelper.showMessage(this, "Searching for surfaces...");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            displayRotationHelper.onPause();
            surfaceView.onPause();
            session.pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                    .show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
        try {
            // Create the texture and pass it to ARCore session to be filled during update().
            backgroundRenderer.createOnGlThread(/*context=*/ this);
            planeRenderer.createOnGlThread(/*context=*/ this, "models/trigrid.png");
            pointCloudRenderer.createOnGlThread(/*context=*/ this);

              /*
                        Modified by Love & Anna at 25/4-18
                        replaced the Andy 3d model with a rubiks cube and solid blue color
                         */
            virtualObject.createOnGlThread(/*context=*/ this, "models/rubiks-cube.obj", "models/cube2.jpg");
            virtualObject.setMaterialProperties(0.0f, 2.0f, 0.5f, 6.0f);

            virtualObjectShadow.createOnGlThread(
                    /*context=*/ this, "models/andy_shadow.obj", "models/andy_shadow.png");
            virtualObjectShadow.setBlendMode(BlendMode.Shadow);
            virtualObjectShadow.setMaterialProperties(1.0f, 0.0f, 0.0f, 1.0f);

        } catch (IOException e) {
            Log.e(TAG, "Failed to read an asset file", e);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        displayRotationHelper.onSurfaceChanged(width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        SharedPreferences settings=getSharedPreferences("prefs",0);
        int maximumAnchors = settings.getInt("maxAnchors", 20);
      //  int benchTime = settings.getInt("timeToBench", 20)*1000+200;
      //  int rateOfCollect = settings.getInt("rateOfCollecting",1)*30;
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (session == null) {
            return;
        }
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(session);

        try {
            session.setCameraTextureName(backgroundRenderer.getTextureId());

            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            Frame frame = session.update();
            Camera camera = frame.getCamera();
                /*
                SECTION ADDED BY LOVE & ANNA
                */

            int benchTime = settings.getInt("timeToBench", 20);
            int rateOfCollect = settings.getInt("rateOfCollecting",2                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    );
            boolean benchMode = settings.getBoolean("benchMode", true);
            benchTime *= 1000;
            benchTime += 200;
            rateOfCollect *= 15;

            //If time has not passed the alotted benchtime and the application is set to benchmode
            //Collect data
            if(elapsedTime <= benchTime && benchMode) {
                //Collect data at the specific rate
                if(counter % rateOfCollect == 0) {
                    Log.d(TAG, "counter " + Integer.toString(counter));
                    //If no new plane is detected, add previous value
                    //also works if no plane has been detected at all
                    //Create new planeobject
                    if(frame.getUpdatedTrackables(Plane.class).isEmpty()){
                        long currentTimePlane = System.currentTimeMillis();
                        PlaneObject pl = new PlaneObject(hashPlane,sX, sZ, currentTimePlane);
                        planeObjects.add(pl);
                    }
                    //If a new plane has been detected create a new planeobject
                    //and add data related to it
                    for(Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                        long currentTimePlane = System.currentTimeMillis();
                        sX = Float.toString(plane.getExtentX());
                        sZ = Float.toString(plane.getExtentZ());
                        hashPlane = plane.hashCode();
                        PlaneObject pl = new PlaneObject(hashPlane, sX, sZ, currentTimePlane);
                        planeObjects.add(pl);

                    }
                    //Get current pointcloud and create a new pointobject
                    //add data to the pointobject (the pointcloud needs to be divided
                    // by four since there are four values per point).
                    PointCloud tempCloud = frame.acquirePointCloud();
                    FloatBuffer floatCloud = tempCloud.getPoints();
                    long currentTimePoint = System.currentTimeMillis();
                    PointObject po = new PointObject(floatCloud.remaining()/4, currentTimePoint);
                    pointObjects.add(po);
                    tempCloud.release();
                }
                counter++;
                elapsedTime = System.currentTimeMillis() - startTime;

            //If time has passed the alotted benchtime and the application is set to benchmode
            //Create gson file and send as email
            }else if(elapsedTime > benchTime && benchMode){
                File file = null;
                try {
                    SharedPreferences settings2 = getSharedPreferences("prefs", 0);
                    int numberOfBenchmarks = settings2.getInt("noOfBenchmarks", 1);
                    Log.d("KRASCH", Integer.toString(numberOfBenchmarks));
                    file = new File(benchObjects.createGson(pointObjects, planeObjects,numberOfBenchmarks).getAbsolutePath());
                    SharedPreferences.Editor editor = settings2.edit();
                    numberOfBenchmarks++;
                    editor.putInt("noOfBenchmarks", numberOfBenchmarks);
                    editor.apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try{
                    sendEmail(file);
                }catch (IOException e){
                    Log.e("ERR", "Could not send email");
                }
            }else if(!benchMode){
                //Don't collect data
            }

                /*
            END OF SECTION
                */

            // Handle taps. Handling only one tap per frame, as taps are usually low frequency
            // compared to frame rate.

            MotionEvent tap = tapHelper.poll();
            if (tap != null && camera.getTrackingState() == TrackingState.TRACKING) {
                for (HitResult hit : frame.hitTest(tap)) {
                    // Check if any plane was hit, and if it was hit inside the plane polygon
                    Trackable trackable = hit.getTrackable();
                    // Creates an anchor if a plane or an oriented point was hit.
                    if ((trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose()))
                            || (trackable instanceof Point
                            && ((Point) trackable).getOrientationMode()
                            == OrientationMode.ESTIMATED_SURFACE_NORMAL)) {
                        // Hits are sorted by depth. Consider only closest hit on a plane or oriented point.
                        // Cap the number of objects created. This avoids overloading both the
                        // rendering system and ARCore.

                        /*
                        Modified by Love & Anna at 25/4-18
                        replaced the number 20 with maximumAnchors to be able to change the number
                        maximum anchors
                         */
                        if (anchors.size() >= maximumAnchors) {
                            anchors.get(0).detach();
                            anchors.remove(0);
                        }
                        // Adding an Anchor tells ARCore that it should track this position in
                        // space. This anchor is created on the Plane to place the 3D model
                        // in the correct position relative both to the world and to the plane.
                        anchors.add(hit.createAnchor());
                        break;
                    }
                }
            }

            // Draw background.
            backgroundRenderer.draw(frame);

            // If not tracking, don't draw 3d objects.
            if (camera.getTrackingState() == TrackingState.PAUSED) {
                return;
            }

            // Get projection matrix.
            float[] projmtx = new float[16];
            camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

            // Get camera matrix and draw.
            float[] viewmtx = new float[16];
            camera.getViewMatrix(viewmtx, 0);

            // Compute lighting from average intensity of the image.
            // The first three components are color scaling factors.
            // The last one is the average pixel intensity in gamma space.
            final float[] colorCorrectionRgba = new float[4];
            frame.getLightEstimate().getColorCorrection(colorCorrectionRgba, 0);



            // Visualize tracked points.
            PointCloud pointCloud = frame.acquirePointCloud();
            pointCloudRenderer.update(pointCloud);
            pointCloudRenderer.draw(viewmtx, projmtx);


            // Application is responsible for releasing the point cloud resources after
            // using it.
           // messageSnackbarHelper.showMessage(this, Integer.toString(apa.remaining()));
            pointCloud.release();

            // Check if we detected at least one plane. If so, hide the loading message.
            if (messageSnackbarHelper.isShowing()) {
                for (Plane plane : session.getAllTrackables(Plane.class)) {
                    if (plane.getType() == com.google.ar.core.Plane.Type.HORIZONTAL_UPWARD_FACING
                            && plane.getTrackingState() == TrackingState.TRACKING) {
                       messageSnackbarHelper.hide(this);
                       break;
                    }
                }
            }

            // Visualize planes.
            planeRenderer.drawPlanes(
                    session.getAllTrackables(Plane.class), camera.getDisplayOrientedPose(), projmtx);

            // Visualize anchors created by touch.
            float scaleFactor = 1.0f;
            for (Anchor anchor : anchors) {
                if (anchor.getTrackingState() != TrackingState.TRACKING) {
                    continue;
                }
                // Get the current pose of an Anchor in world space. The Anchor pose is updated
                // during calls to session.update() as ARCore refines its estimate of the world.
                anchor.getPose().toMatrix(anchorMatrix, 0);

                // Update and draw the model and its shadow.
                virtualObject.updateModelMatrix(anchorMatrix, scaleFactor);
                virtualObjectShadow.updateModelMatrix(anchorMatrix, scaleFactor);
                virtualObject.draw(viewmtx, projmtx, colorCorrectionRgba);
                virtualObjectShadow.draw(viewmtx, projmtx, colorCorrectionRgba);
            }

        } catch (Throwable t) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(TAG, "Exception on the OpenGL thread", t);
        }
    }

    /*
    SECTION ADDED BY LOVE & ANNA
     */

    //Send a benchmark file in an email
    protected void sendEmail(File file) throws IOException {
        Log.i("Send email", "");

        String[] TO = {"SPECIFY EMAIL"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");

        Date currentTime = Calendar.getInstance().getTime();

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Benchmark result " + currentTime.toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, currentTime.toString());
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ArActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted1");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            return true;
        }
    }

    /*
    END OF SECTION
     */

}


