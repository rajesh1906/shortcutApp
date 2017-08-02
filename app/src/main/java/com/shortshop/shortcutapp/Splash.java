package com.shortshop.shortcutapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends Activity {
    private Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.slpash);
        t = new Thread(new Runnable() {
            @SuppressWarnings("static-access")
            @Override
            public void run() {
                try {
                    t.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent i = new Intent(Splash.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
        t.start();
    }
}
