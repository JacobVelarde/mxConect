package org.mxitz.mxconectado;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;


public class SplashActivty extends ActionBarActivity {
    //Establece la duracion  del Splash Screen
    private  static final long SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //estable la orientacion del dispositivo en vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Elima la barra de arriva
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //Empieza la siguiente actividad
                Intent principal = new Intent().setClass(SplashActivty.this,Opciones.class);
                startActivity(principal);

                //Cierre la actividad por lo que el usuario no será capaz de volver este
                finish();
            }
        };
        Timer tiempo = new Timer();
        tiempo.schedule(task,SPLASH_SCREEN_DELAY);

    }
}
