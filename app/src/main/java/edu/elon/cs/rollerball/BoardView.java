package edu.elon.cs.rollerball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Contains both the surface to draw to and the game loop.
 *
 * @author J. Hollingsworth
 * Modified by Michael Winkler
 */

public class BoardView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {

    private BoardViewThread thread;
    private SurfaceHolder surfaceHolder;
    private Context context;
    private int screenWidth, screenHeight;
    private Bitmap background;
    private SensorManager sensorManager;
    private Sensor mAccel;
    private SensorEvent currentSensorEvent;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // remember the context for finding resources
        this.context = context;

        // want to know when the surface changes
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // game loop thread
        thread = new BoardViewThread();

        // figure out the screen width
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        //Set the background instance variable to reference the relevant background image
        background = BitmapFactory.decodeResource(context.getResources(),R.drawable.board);

        // Accelerometer
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
    }


    // SurfaceHolder.Callback methods:
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // thread exists, but is in terminated state
        if (thread.getState() == Thread.State.TERMINATED) {
            thread = new BoardViewThread();
        }

        // start the game loop
        thread.setIsRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        thread.setIsRunning(false);

        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }


    // touch events
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    // Game Loop Thread
    private class BoardViewThread extends Thread {

        private boolean isRunning = false;
        private long lastTime;

        // the ball and spot sprites
        private Ball ball;
        private Spot spot;

        // frames per second calculation
        private int frames;
        private long nextUpdate;

        public BoardViewThread() {
            ball = new Ball(context);
            spot = new Spot(context, ball);
        }

        public void setIsRunning(boolean isRunning) {
            this.isRunning = isRunning;
        }

        // the main loop
        @Override
        public void run() {

            lastTime = System.currentTimeMillis();

            while (isRunning) {

                // grab hold of the canvas
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas == null) {
                    // trouble -- exit nicely
                    isRunning = false;
                    continue;
                }

                synchronized (surfaceHolder) {

                    // compute how much time since last time around
                    long now = System.currentTimeMillis();
                    double elapsed = (now - lastTime) / 1000.0;
                    lastTime = now;

                    // update/draw
                    doUpdate(elapsed);
                    doDraw(canvas);

                    //updateFPS(now);
                }

                // release the canvas
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

        // an approximate frames per second calculation
        private void updateFPS(long now) {
            float fps = 0.0f;
            ++frames;
            float overtime = now - nextUpdate;
            if (overtime > 0) {
                fps = frames / (1 + overtime/1000.0f);
                frames = 0;
                nextUpdate = System.currentTimeMillis() + 1000;
                System.out.println("FPS: " + (int) fps);
            }
        }

        /* THE GAME */

        // move all objects in the game
        private void doUpdate(double elapsed) {

            ball.doUpdate(currentSensorEvent);
            spot.doUpdate(elapsed);
        }

        // draw all objects in the game
        private void doDraw(Canvas canvas) {

            // draw the background
            canvas.drawBitmap(background,
                    null,
                    new Rect(0, 0,
                            screenWidth, screenHeight),
                    null);

            spot.doDraw(canvas);
            ball.doDraw(canvas);
        }
    }


    public void onSensorChanged(SensorEvent event) {
        //When the user tilts the phone, asign the resulting Sensor event to currentSensorEvent.
        //It then gets passed to the ball object in ball.doUpdate so the ball knows where to roll.
           currentSensorEvent = event;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}