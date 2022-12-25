package com.capstone.itshere.membership;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.itshere.R;
import com.capstone.itshere.account.FirebaseID;
import com.capstone.itshere.accountBook.ab_add_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;


public class MembershipAddActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email;

    private ImageButton back;
    private TextView title;

    EditText mbs_add_barcode, mbs_add_contents;
    private Button btn_save;
    private LinearLayout colorpicker;
    int defaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_add);

        //툴바 설정
        back = (ImageButton) findViewById(R.id.tool_sub3_back);
        title = (TextView) findViewById(R.id.tool_sub3_title);
        title.setText("등록하기");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //init
        mbs_add_barcode = findViewById(R.id.mbs_add_barcode);
        mbs_add_contents = findViewById(R.id.mbs_add_contents);
        colorpicker = findViewById(R.id.mbs_colorpicker);
        defaultColor = ContextCompat.getColor(MembershipAddActivity.this, R.color.myBlue);

        //colorpicker
        colorpicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();
            }
        });

        //register
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerData();
            }
        });

    }//onCreate

    private void openColorPicker() {
        AmbilWarnaDialog picker = new AmbilWarnaDialog(this, defaultColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        //취소
                    }
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        defaultColor = color;
                        colorpicker.setBackgroundColor(defaultColor);
                    }
                });
        picker.show();
    }

    private void registerData() {
        if(mAuth.getCurrentUser() != null){
            //이메일 가져오기
            db.collection(FirebaseID.user).document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult() != null){
                                email = (String) task.getResult().getData()
                                        .get(FirebaseID.email);
                            }
                        }
                    });

            String mbsID = db.collection(FirebaseID.documentId).document().getId();
            Map<String, Object> data = new HashMap<>();
            data.put(FirebaseID.documentId, mbsID);
            data.put(FirebaseID.barcode, mbs_add_barcode.getText().toString());
            data.put(FirebaseID.contents, mbs_add_contents.getText().toString());
            data.put(FirebaseID.color, defaultColor);

            db.collection(FirebaseID.MBSboard).document(email)
                    .collection(FirebaseID.conn).document(mbsID)
                    .set(data, SetOptions.merge());
            finish();
        }else{
            Toast.makeText(MembershipAddActivity.this, "로그인해주세요.", Toast.LENGTH_LONG).show();
        }
    }
}