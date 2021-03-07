package com.aminnovent.flyunicorn;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.aminnovent.flyunicorn.GameView.screenRatioX;
import static com.aminnovent.flyunicorn.GameView.screenRationY;

public class Bullet {
    int x,y, width, height;
    Bitmap bullet;

    public Bullet(Resources res) {

        bullet = BitmapFactory.decodeResource(res,R.drawable.unicornhorn);

        width = bullet.getWidth();
        height = bullet.getHeight();

        width /= 4;
        height /= 4;

        width *= (int) screenRatioX;
        height *= (int) screenRationY;

        bullet = Bitmap.createScaledBitmap(bullet,width,height,false);
    }

    // for rectangle around bullet for collision
    Rect getCollisionShape(){
        return new Rect(x,y,x+width,y+height);
    }
}
