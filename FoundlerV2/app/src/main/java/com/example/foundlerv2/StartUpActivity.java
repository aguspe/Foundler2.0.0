package com.example.foundlerv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartUpActivity extends AppCompatActivity {

    private Button mLogin, mSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        mLogin = (Button) findViewById(R.id.login_button);
        mSignUp = (Button) findViewById(R.id.signup_button);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartUpActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}
