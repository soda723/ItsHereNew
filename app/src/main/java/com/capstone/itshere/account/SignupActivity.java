package com.capstone.itshere.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.capstone.itshere.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private EditText mEmail, mNickname, mPass, mPassConfirm;
    ImageButton back;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmail = findViewById(R.id.input_email_signup);
        mNickname = findViewById(R.id.input_nickname_signup);
        mPass = findViewById(R.id.input_pass_signup);
        mPassConfirm = findViewById(R.id.input_pass_confirm);

        //툴바설정
        title = (TextView) findViewById(R.id.tool_sub1_title);
        title.setText("회원가입");
        back = (ImageButton) findViewById(R.id.tool_sub1_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //회원가입
        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPass.getText().toString().equals(mPassConfirm.getText().toString())){
                    mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPass.getText().toString())
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // 회원가입 성공
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Map<String, Object> userMap = new HashMap<>();
                                        if (user != null) {
                                            userMap.put(FirebaseID.documentId, user.getUid());
                                            userMap.put(FirebaseID.email, mEmail.getText().toString());
                                            userMap.put(FirebaseID.nickname, mNickname.getText().toString());
                                            userMap.put(FirebaseID.password, mPass.getText().toString());
                                            mStore.collection(FirebaseID.user).document(user.getUid()).set(userMap, SetOptions.merge());
                                            finish();
                                        } else {
                                        }

                                    } else {
                                        // 회원가입 실패
                                        Toast.makeText(SignupActivity.this, "sign up error.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }else{
                    //비밀번호 확인이 일치하지 않음.
                    Toast.makeText(SignupActivity.this, "비밀번호(확인)를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }//onCreate
}