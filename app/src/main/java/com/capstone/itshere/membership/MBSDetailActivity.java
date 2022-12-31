package com.capstone.itshere.membership;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.itshere.R;
import com.capstone.itshere.account.FirebaseID;
import com.capstone.itshere.coupon.CouponDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MBSDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser User = mAuth.getCurrentUser();
    private String email;

    private ImageButton back;
    private TextView title;
    private EditText ed_contents, ed_barcode;
    private Button btn_delete, btn_modi;
    private String id;
    private LinearLayout colorpicker;
    int defaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mbsdetail);

        //intent
        id = getIntent().getStringExtra("id");

        //툴바 설정
        back = (ImageButton) findViewById(R.id.tool_sub3_back);
        title = (TextView) findViewById(R.id.tool_sub3_title);
        title.setText("상세내용");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //init
        ed_barcode = findViewById(R.id.mbs_detail_barcode);
        ed_contents = findViewById(R.id.mbs_detail_contents);
        btn_delete = findViewById(R.id.mbs_detail_delete);
        btn_modi = findViewById(R.id.mbs_detail_modi);
        defaultColor = ContextCompat.getColor(MBSDetailActivity.this, R.color.myBlue);
        colorpicker = findViewById(R.id.mbs_detail_colorpicker);

        //colorpicker
        colorpicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog picker = new AmbilWarnaDialog(MBSDetailActivity.this, defaultColor,
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
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertOKdeleteMBS();
            }
        });

        btn_modi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertOKmodidata();
            }
        });
    }

    private void AlertOKmodidata() {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(MBSDetailActivity.this)
                .setTitle("수정하기").setMessage("내용을 수정하시겠습니까?")
                .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        modiMBS();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MBSDetailActivity.this, "수정 취소", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alertDialog = builder2.create();
        alertDialog.show();
    }

    private void modiMBS() {
        if(mAuth.getCurrentUser() != null){

            Map<String, Object> data = new HashMap<>();
            data.put(FirebaseID.barcode, ed_barcode.getText().toString());
            data.put(FirebaseID.contents, ed_contents.getText().toString());
            data.put(FirebaseID.color, defaultColor);

            db.collection(FirebaseID.MBSboard).document(email)
                    .collection(FirebaseID.conn).document(id)
                    .update(data);

            finish();
        }else{
            Toast.makeText(MBSDetailActivity.this, "실패! 잠시후 다시 시도해주세요.", Toast.LENGTH_LONG).show();
        }
    }

    private void AlertOKdeleteMBS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MBSDetailActivity.this)
        .setTitle("멤버십카드 삭제").setMessage("멤버십 카드를 목록에서 삭제하시겠습니까?")
        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteMBS();
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MBSDetailActivity.this, "삭제 취소", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteMBS() {
        db.collection(FirebaseID.MBSboard).document(email)
                .collection(FirebaseID.conn).document(id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("MBS_CARD_DELETE", "멤버십 카드 삭제 성공");
                        MBSDetailActivity.this.finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("in deleteMBS()", "Error deleting document", e);
                        Toast.makeText(MBSDetailActivity.this, "삭제실패! 잠시후 시도해주세요", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        email = User.getEmail();
        db.collection(FirebaseID.MBSboard).document(email)
                .collection(FirebaseID.conn).document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()){
                                Map<String, Object> shot = doc.getData();
                                String barcode = (String) shot.get(FirebaseID.barcode);
                                String contents = (String) shot.get(FirebaseID.contents);
                                String color = String.valueOf( shot.get(FirebaseID.color));

                                colorpicker.setBackgroundColor(Integer.parseInt(color));
                                ed_barcode.setText(barcode);
                                ed_contents.setText(contents);

                            }
                        }
                    }
                });
    }
}