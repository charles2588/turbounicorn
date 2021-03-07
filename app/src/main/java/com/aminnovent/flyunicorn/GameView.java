package com.aminnovent.flyunicorn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying;
    private int screenX,screenY,score=0;

    //
    private Flight flight;

    //Birds
    private Bird[] birds;
    private Random random;

    // To make it compatible with all device size.
    public static float screenRatioX=1, screenRationY=1;

    private Paint paint;

    private Background background1, background2;

    private List<Bullet> bullets;
    private boolean isGameOver = false;


    //For saving highscore
    private SharedPreferences prefers;

    GameActivity activity;


    //For Sound
    private SoundPool soundPool;
    private int sound;

    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;

        //for storing the score on phone
        prefers = activity.getSharedPreferences("game",Context.MODE_PRIVATE);

        //for sound
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        else{
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        }

        sound = soundPool.load(activity,R.raw.shootping,1);


        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRationY = 1080f / screenY;
        background1 = new Background(screenX,screenY, getResources());
        background2 = new Background(screenX,screenY,getResources());

        background2.x = screenX;

        this.bullets = new ArrayList<>();

        paint = new Paint();

        //For score
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        flight = new Flight(this,screenY,getResources());

        birds = new Bird[4];

        for (int i = 0; i < 4; i++){
            Bird bird = new Bird(getResources());
            birds[i] = bird;
        }



        random = new Random();
    }

    @Override
    public void run() {

        while(isPlaying){
            update();
            draw();
            sleep();
        }

    }

    private void draw() {

        if(getHolder().getSurface().isValid()){
            Canvas canvas = getHolder().lockCanvas();

            // Draw Background first
            canvas.drawBitmap(background1.background,background1.x,background1.y,paint);
            canvas.drawBitmap(background2.background,background2.x,background2.y,paint);

            //ReDraw the bird
            for(Bird bird: birds)
                canvas.drawBitmap(bird.getBird(),bird.x,bird.y,paint);

            //Display/Update score
            canvas.drawText(score+"",screenX/2f,164,paint);

            if(isGameOver){
                isPlaying = false;
                canvas.drawBitmap(flight.getdead(),flight.x,flight.y,paint);
                getHolder().unlockCanvasAndPost(canvas);

                saveIfHighScore();
                waitBeforeExiting();


                return;
            }

            //Draw the bird that has passed
            for(Bird bird: birds)
                canvas.drawBitmap(bird.getBird(),bird.x,bird.y,paint);

            // Draw the flying UniCorn
            canvas.drawBitmap(flight.getFlight(),flight.x,flight.y,paint);

            //Draw the bullets
            for(Bullet bullet:bullets){
                canvas.drawBitmap(bullet.bullet,bullet.x,bullet.y,paint);
            }

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    //Wait after the game is over
    private void waitBeforeExiting() {
        try {
            Thread.sleep(3000); // wait 3 seconds

            //Navigate to MainActivity
            activity.startActivity(new Intent(activity,MainActivity.class));
            activity.finish();//finish the gameactivity cleanup.

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Update HighScore
    private void saveIfHighScore() {

        if(prefers.getInt("highscore",0) < score){
            SharedPreferences.Editor editor = prefers.edit();
            editor.putInt("highscore",score);
            editor.apply();
        }

    }

    private void update() {

        background1.x -= 10*screenRatioX; // since we are moving x axis
        background2.x -= 10; // multipy to screenRatioY for y axis movement.

        if(background1.x + background1.background.getWidth() < 0){
            background1.x = screenX;
        }

        if(background2.x + background2.background.getWidth() < 0){
            background2.x = screenX;
        }

        // Update the unicorn movements on the screen
        if(flight.isGoingUp)
            flight.y -= 30 * screenRationY;
        else
            flight.y += 30 * screenRationY;

        //check to not overshoot on the y axis
        if(flight.y < 0)
            flight.y = 0;

        if(flight.y > screenY - flight.height)
            flight.y = screenY - flight.height;

        List<Bullet> trash = new ArrayList<>();

        for(Bullet bullet: bullets){

            if(bullet.x>screenX)
                trash.add(bullet);

            bullet.x += 50 * screenRatioX; // advance the position

            for (Bird bird: birds){

                //When bullet hits the bird
                if(Rect.intersects(bird.getCollisionShape(),bullet.getCollisionShape())){

                    score++; //increment score for hitting the bird
                    //move bird off the screen
                    bird.x = -500;
                    bullet.x = screenX + 500;

                    // when flight misses the bird or bird goes past flight to the left game should be over.
                    bird.wasShot = true;
                }

            }
        }

        for(Bullet bullet: trash){
            bullets.remove(bullet);
        }


        //Birds

        for(Bird bird:birds){

            bird.x -= bird.speed;

            if(bird.x + bird.width < 0){

                //if bird passes the flight/unicorn, game should get over
                if(!bird.wasShot){
                    isGameOver = true;
                    return;
                }

                int bound = (int) (30 * screenRatioX);
                bird.speed = random.nextInt(bound);

                if(bird.speed < 10 * screenRatioX) // minimum speed of our bird is 10 pixel
                    bird.speed = (int) (10 * screenRatioX);

                bird.x = screenX;
                bird.y = random.nextInt( screenY - bird.height);

                bird.wasShot = false;
            }

            //Collision -- create rectangles around bird and unicorn
            if(Rect.intersects(bird.getCollisionShape(),flight.getCollisionShape())){
                isGameOver = true;
                return;
            }
        }

    }

    public void sleep(){
        try {
            Thread.sleep(17); // 60 frames per second

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void resume(){

        isPlaying = true;
        thread = new Thread( this);
        thread.start();

    }

    public void pause(){

        try{
            isPlaying = false;
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // To move the unicorn up and down


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(event.getX() < screenX/2){ //User touches to left of the screen
                    flight.isGoingUp = true; // Move the unicorn down with some pixel as user touches the screen
                }
                break;
            case MotionEvent.ACTION_UP:
                flight.isGoingUp = false; // Stop the movement as soon as the user take the touch off the screen
                if(event.getX() > screenX/2){
                    flight.toShoot++;
                }
                break;
        }
        return true;
    }

    public void newbullet() {

        if(!prefers.getBoolean("isMute",false))//if game is not mute play sound
            soundPool.play(sound,1,1,0,0,1);

        Bullet bullet = new Bullet(getResources());
        bullet.x = flight.x + flight.width;
        bullet.y = flight.y + (flight.height/2);
        bullets.add(bullet);
    }
}
