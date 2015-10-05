package edu.elon.cs.rollerball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.WindowManager;

import java.util.Random;

/**
 * @author Michael Winkler
 */
public class Spot {
    private float x, y, width, height;
    private Bitmap bitmap;

    private int screenWidth, screenHeight;

    private final float SCALE = 0.2f;

    private double timeSinceBallEnteredSpot = 0.0;

    private Ball ball;

    public Spot (Context context, Ball ball) {

        // get the spot image
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.spot);

        //scale the size
        width = bitmap.getWidth() * SCALE;
        height = bitmap.getHeight() * SCALE;

        // figure out the screen width
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.y;
        screenHeight = size.x;

        this.ball = ball;
        x= 100;
        y=100;
        //generateNewLocation();
    }

    public void doDraw (Canvas canvas){
        canvas.drawBitmap(bitmap,
                null,
                new Rect((int) (x - width/2), (int) (y - height/2),
                        (int) (x + width/2), (int) (y + height/2)),
                null);
    }

    /*public void doUpdate(double elapsed) {
        if (timeSinceBallEnteredSpot >= 3000.0) {
            generateNewLocation();
            timeSinceBallEnteredSpot = 0;
        }

        else{
            if(((ball.x - ball.height/2) >= (x-height/2)) && ((ball.x + ball.height/2) <= (x+height/2))
                    && ((ball.y - ball.width/2) >= (y-width/2)) && ((ball.y + ball.width/2) <= (y+width/2))) { //ball in square
                timeSinceBallEnteredSpot += elapsed;
            }
            else {
                timeSinceBallEnteredSpot = 0;
            }
        }

    }*/


    public void doUpdate(double elapsed) {
        System.out.println("timeSince = "+ timeSinceBallEnteredSpot);
        if (timeSinceBallEnteredSpot >= 3.0) {
            generateNewLocation();
            timeSinceBallEnteredSpot = 0;
        }

        else{
            if(((ball.x - ball.height/2) >= (x-height/2)) && ((ball.x + ball.height/2) <= (x+height/2))
                    && ((ball.y - ball.width/2) >= (y-width/2)) && ((ball.y + ball.width/2) <= (y+width/2))) { //ball in square
                timeSinceBallEnteredSpot += elapsed;
            }
            else {
                timeSinceBallEnteredSpot = 0;
            }
        }

    }


    public void generateNewLocation(){
        float newX = (float) Math.random()*((screenWidth-(width/2)) - (width/2));
        newX += (width/2);
        x = newX;
        float newY = (float) Math.random()*((screenHeight-(height/2)) - (height/2));
        newY += (height/2);
        y = newY;
    }

}
