package com.capstone.itshere.coupon;

import static com.capstone.itshere.StringAndFunction.timestampToString;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capstone.itshere.R;
import com.capstone.itshere.account.FirebaseID;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.common.value.qual.StringVal;

import java.util.Map;

public class CouponDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser User = mAuth.getCurrentUser();
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private final StorageReference pathref = reference.child("Image");
    private String document_email;
    private Uri imageUrl;

    private ImageButton back;
    private TextView title;
    private TextView cp_detail_contents, cp_detail_date,cp_detail_alarm;
    private ImageView imageView;
    private Button btn_delete;
    private String idNum;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_detail);

        //intent
        idNum = getIntent().getStringExtra("idNum");
        Log.e("in coupon detail", idNum);

        //툴바 설정
        back = (ImageButton) findViewById(R.id.tool_sub2_back);
        title = (TextView) findViewById(R.id.tool_sub2_title);
        title.setText("상세내용");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //init
        imageView = findViewById(R.id.cp_detail_imageview);
        cp_detail_contents = findViewById(R.id.cp_detail_contents);
        cp_detail_date = findViewById(R.id.cp_detail_date);
        cp_detail_alarm = findViewById(R.id.cp_detail_alarm);
        btn_delete = findViewById(R.id.cp_detail_btn_delete);

    }

    @Override
    protected void onStart() {
        super.onStart();
        document_email = User.getEmail();
        db.collection(FirebaseID.couponboard).document(document_email)
                .collection(FirebaseID.conn).document(idNum).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()){
                                Map<String, Object> shot = doc.getData();
                                String contents = (String) shot.get(FirebaseID.contents);
                                String date = timestampToString(String.valueOf(shot.get(FirebaseID.notedate)));
                                String alarm = (String) shot.get(FirebaseID.alarm);
                                String url = (String) shot.get(FirebaseID.imageurl);

                                cp_detail_contents.setText(contents);
                                cp_detail_date.setText(date);
                                cp_detail_alarm.setText(alarm + " 알림");
                                try{
                                    //content://media/external/images/media/1000001718

                                    StorageReference mimage = pathref.child("1671899339650.jpg");
                                    mimage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Glide.with(getApplicationContext()).load(uri).into(imageView);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            imageView.setImageResource(R.drawable.ic_image_not_supported);
                                        }
                                    });
                                }catch(Exception e){
                                    Toast.makeText(getApplicationContext(), "이미지지가 정상적으로 업로드 되지 않습니다.",Toast.LENGTH_SHORT).show();
                                    imageView.setImageResource(R.drawable.ic_image_not_supported);
                                }

                            }
                        }
                    }
                });
    }
}