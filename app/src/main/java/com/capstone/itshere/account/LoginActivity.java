package com.capstone.itshere.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.itshere.Fragment1;
import com.capstone.itshere.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private EditText mEmail, mPass;
    private TextView check_id;
    private Button goSignup, btnLogin, btnLogout;

    private LinearLayout ly_login, ly_logout;

    ImageButton back;
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ly_login = findViewById(R.id.ly_login);
        ly_logout = findViewById(R.id.ly_logout);

        mEmail = findViewById(R.id.input_email);
        mPass = findViewById(R.id.input_pass);
        check_id = findViewById(R.id.tv_currentUser);

        btnLogin = findViewById(R.id.btn_login);
        goSignup = findViewById(R.id.go_signup);
        btnLogout = findViewById(R.id.btn_logout);

        //툴바설정
        title = (TextView) findViewById(R.id.tool_sub1_title);
        title.setText("로그인");
        back = (ImageButton) findViewById(R.id.tool_sub1_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //회원가입으로이동
        goSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
        //로그인
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPass.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null){
                                        Toast.makeText(LoginActivity.this, "로그인 성공 : " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                        check_id.setText(user.getEmail() + "님 환영합니다."); //drawer header에 id와 인삿말표시
                                        ly_login.setVisibility(View.GONE); // 로그아웃시 메시지 레이아웃 가리기
                                        ly_logout.setVisibility(View.VISIBLE); // 로그인 메시지 레이아웃 보이기
                                    }

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "login error.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
        //로그아웃
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(LoginActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                ly_logout.setVisibility(View.GONE); // 로그인 메시지 레이아웃 보이기
                ly_login.setVisibility(View.VISIBLE); // 로그아웃시 메시지 레이아웃 보이기
            }
        });
    }//onCreate

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            ly_login.setVisibility(View.GONE);
            ly_logout.setVisibility(View.VISIBLE);
            Toast.makeText(this, "자동로그인" + user.getEmail(), Toast.LENGTH_SHORT).show();
            check_id.setText(user.getEmail() + "님 환영합니다.");

        }else{
            ly_logout.setVisibility(View.GONE);
            ly_login.setVisibility(View.VISIBLE);
        }
    }

}