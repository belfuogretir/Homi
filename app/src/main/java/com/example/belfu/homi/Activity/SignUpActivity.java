package com.example.belfu.homi.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.belfu.homi.Model.User;
import com.example.belfu.homi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    EditText etMail, etPass;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.wtf("MainActivity:","Burda");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();

        Button yeniUyeButton = (Button) findViewById(R.id.yeniUyeButton);
        Button uyeyim = (Button) findViewById(R.id.uyeGirisButton);
        yeniUyeButton.setOnClickListener(this);
        uyeyim.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.yeniUyeButton:
                etMail = (EditText) findViewById(R.id.uyeEmail);
                etPass = (EditText) findViewById(R.id.uyeParola);
                String email = etMail.getText().toString();
                String pass = etPass.getText().toString();
                database = FirebaseDatabase.getInstance();

                 final String mail = etMail.getText().toString();

                final DatabaseReference dbRef = database.getReference("users");

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Lütfen emailinizi giriniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    Toast.makeText(getApplicationContext(),"Lütfen parolanızı giriniz",Toast.LENGTH_SHORT).show();
                }
                if(pass.length()<6){
                    Toast.makeText(getApplicationContext(),"Parola en az 6 haneli olmalıdır",Toast.LENGTH_SHORT).show();
                }
                auth.createUserWithEmailAndPassword(email,pass)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Yetkilendirme Hatası",
                                            Toast.LENGTH_SHORT).show();
                                }

                                //İşlem başarılı olduğu takdir de giriş yapılıp MainActivity e yönlendiriyoruz.
                                else {
                                    String id = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
                                    dbRef.getRef().push().setValue(new User(mail,id));
                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
                break;
            case R.id.uyeGirisButton:
                Log.wtf("zaten uyeyim","burda");
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }
}
