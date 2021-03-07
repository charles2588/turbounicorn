package com.aminnovent.flyunicorn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //for volumecontrol
    private boolean isMute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //Play to start GameActivity
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,GameActivity.class));
            }
        });

        //For HighScoreText
        TextView highScoreText = findViewById(R.id.highScoreTxt);

        final SharedPreferences prefers = getSharedPreferences("game",MODE_PRIVATE);
        highScoreText.setText("HighScore : " + prefers.getInt("HighScore",0));

        //For VolumeControl
        isMute = prefers.getBoolean("isMute",false);

        final ImageView volumeCtrl = findViewById(R.id.volumeCtrl);

        if(isMute)
            volumeCtrl.setImageResource(R.drawable.ic_volume_down_black_24dp);
        else
            volumeCtrl.setImageResource(R.drawable.ic_volume_up_black_24dp);

        findViewById(R.id.volumeCtrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMute = !isMute;

                if(isMute)
                    volumeCtrl.setImageResource(R.drawable.ic_volume_down_black_24dp);
                else
                    volumeCtrl.setImageResource(R.drawable.ic_volume_up_black_24dp);

                SharedPreferences.Editor editor = prefers.edit();
                editor.putBoolean("isMute",isMute);
                editor.apply();
            }
        });

    }
}
