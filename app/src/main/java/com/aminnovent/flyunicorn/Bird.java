package com.aminnovent.flyunicorn;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.aminnovent.flyunicorn.GameView.screenRatioX;
import static com.aminnovent.flyunicorn.GameView.screenRationY;

public class Bird {

    public int speed = 20;
    public Boolean wasShot=true;
    int x,y, width, height;
    Bitmap bird1, bird2, bird3, bird4;
    private int birdCounter = 1;

    Bird(Resources res){

        bird1 = BitmapFactory.decodeResource(res,R.drawable.bird1);
        bird2 = BitmapFactory.decodeResource(res,R.drawable.bird2);
        bird3 = BitmapFactory.decodeResource(res,R.drawable.bird3);
        bird4 = BitmapFactory.decodeResource(res,R.drawable.bird4);

        width = bird1.getWidth();
        height = bird1.getHeight();

        width /= 6;
        height /= 6;

        // To make it compatible
        width *= (int) screenRatioX;
        height *= (int) screenRationY;

        bird1 = Bitmap.createScaledBitmap(bird1,width,height,false);
        bird2 = Bitmap.createScaledBitmap(bird2,width,height,false);
        bird3 = Bitmap.createScaledBitmap(bird3,width,height,false);
        bird4 = Bitmap.createScaledBitmap(bird4,width,height,false);

        y = -height;

    }

    Bitmap getBird(){
        // For bird animation
        if(birdCounter == 1){
            birdCounter++;
            return bird1;
        }

        if(birdCounter == 2){
            birdCounter++;
            return bird2;
        }

        if(birdCounter == 3){
            birdCounter++;
            return bird3;
        }

        birdCounter = 1;
        return bird4;
    }

    // for rectangle around bird
    Rect getCollisionShape(){
        return new Rect(x,y,x+width,y+height);
    }

}
