package com.example.belfu.homi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        btn = (Button) findViewById(R.id.grubaGitButton);
//        btn.setOnClickListener(this);

        Log.wtf("Main_Main:","Burda");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.grubaGitButton:
                Log.wtf("gruba git","tıklandı");
                startActivity(new Intent(MainActivity.this, InGrupActivity.class));
                finish();
                break;
        }
    }
}