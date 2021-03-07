package com.aminnovent.flyunicorn;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.aminnovent.flyunicorn.GameView.screenRatioX;
import static com.aminnovent.flyunicorn.GameView.screenRationY;

public class Flight {

    public boolean isGoingUp;
    public int toShoot=0;
    int x,y, width, height;

    Bitmap flight1, flight2, shoot1, shoot2, shoot3, shoot4, shoot5, dead;

    int toggleCounter = 0, shootCounter = 1;

    private GameView gameView;

    Flight(GameView gameView,int screenY, Resources res){

        this.gameView = gameView;

        flight1 = BitmapFactory.decodeResource(res,R.drawable.unicorn1);
        flight2 = BitmapFactory.decodeResource(res,R.drawable.unicorn2);

        width = flight1.getWidth();
        height = flight1.getHeight();

        // Because the image is very big
        width /= 8;
        height /= 8;

        width *= (int) screenRatioX;
        height *= (int) screenRationY;


        flight1 = Bitmap.createScaledBitmap(flight1,width,height,false);
        flight2 = Bitmap.createScaledBitmap(flight2,width,height,false);


        shoot1 = BitmapFactory.decodeResource(res,R.drawable.unicornhorn1);
        shoot2 = BitmapFactory.decodeResource(res,R.drawable.unicornhorn2);
        shoot3 = BitmapFactory.decodeResource(res,R.drawable.unicornhorn3);
        shoot4 = BitmapFactory.decodeResource(res,R.drawable.unicornhorn4);
        shoot5 = BitmapFactory.decodeResource(res,R.drawable.unicornhorn5);


        shoot1 = Bitmap.createScaledBitmap(shoot1,width,height,false);
        shoot2 = Bitmap.createScaledBitmap(shoot2,width,height,false);
        shoot3 = Bitmap.createScaledBitmap(shoot3,width,height,false);
        shoot4 = Bitmap.createScaledBitmap(shoot4,width,height,false);
        shoot5 = Bitmap.createScaledBitmap(shoot5,width,height,false);

        dead = BitmapFactory.decodeResource(res,R.drawable.deadunicorn);
        dead = Bitmap.createScaledBitmap(dead,width,height,false);


        y = screenY / 2;
        x = (int) (64 * screenRatioX);

    }


    Bitmap getFlight(){

        // To toggle between shooting and not shooting
        if(toShoot != 0){

            if(shootCounter == 1){
                shootCounter++;
                return shoot1;
            }

            if(shootCounter == 2){
                shootCounter++;
                return shoot2;
            }

            if(shootCounter == 3){
                shootCounter++;
                return shoot3;
            }

            if(shootCounter == 4){
                shootCounter++;
                return shoot4;
            }

            shootCounter = 1;
            toShoot--;
            gameView.newbullet();
            return shoot5;
        }

        // To toggle between two bitmap images to show the flying effect.
        if (toggleCounter == 0){
            toggleCounter++;
            return flight1;
        }

        toggleCounter--;

        return flight2;
    }

    // for rectangle around Flight(Unicorn)
    Rect getCollisionShape(){
        return new Rect(x,y,x+width,y+height);
    }

    Bitmap getdead(){
        return dead;
    }

}
