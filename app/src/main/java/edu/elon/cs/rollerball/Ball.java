package edu.elon.cs.rollerball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.WindowManager;

/**
 * The ball that rolls around for our game.
 * @author J. Hollingsworth
 * Modified by Michael Winkler
 *
 */
public class Ball {

    protected float x, y, accelX, accelY, width, height;
    private Bitmap bitmap;

    private int screenWidth, screenHeight;

    private final float SCALE = 0.1f;
   // private final float FILTER = 0.3f;

    public Ball (Context context){

        // get the image
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);

        // scale the size
        width = bitmap.getWidth() * SCALE;
        height = bitmap.getHeight() * SCALE;

        // figure out the screen width
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;
        screenWidth = size.x;
        // start in center
        y = screenHeight/2;
        x = screenWidth/2;
    }

    public void doDraw(Canvas canvas){
        // draw the ball
        canvas.drawBitmap(bitmap,
                null,
                new Rect((int) (x - width/2), (int) (y - height/2),
                        (int) (x + width/2), (int) (y + height/2)),
                null);
    }


    public void doUpdate(SensorEvent event) {
        //Use the accelerometer data passed from our GameLoopView to calculate
        //a new position
        if (event != null) {
            float newX = x + event.values[1];
            float newY = y + event.values[0];
            if (newX - (width / 2) >= 0 && (newX + width / 2) <= screenWidth) {
                x = newX;
            }

            if (newY - (height / 2) >= 0 && (newY + height / 2) <= screenHeight) {
                y = newY;
            }
        }

    }
}
