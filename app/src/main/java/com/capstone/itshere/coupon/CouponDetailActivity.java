package com.capstone.itshere.coupon;

import static com.capstone.itshere.StringAndFunction.timestampToString;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.text.AllCapsTransformationMethod;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capstone.itshere.R;
import com.capstone.itshere.StringAndFunction;
import com.capstone.itshere.account.FirebaseID;
import com.capstone.itshere.accountBook.DailyDetailActivity;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CouponDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    final FirebaseUser User = mAuth.getCurrentUser();
    private String document_email, imageurl;

    private ImageButton back;
    private TextView title;
    private EditText cp_detail_contents, cp_detail_date;
    private Spinner spinner;
    private ImageView imageView;
    private Button btn_delete, btn_modify;
    private String idNum;

    private Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_detail);

        //intent
        idNum = getIntent().getStringExtra("idNum");

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
        //spinner = findViewById(R.id.cp_detail_spinner);
        btn_delete = findViewById(R.id.cp_detail_btn_delete);
        btn_modify = findViewById(R.id.cp_detail_btn_modify);

        cp_detail_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CouponDetailActivity.this,
                        myDatePicker,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        //alarm spinner
        /*String[] alarm_items = { StringAndFunction.cp_alarm1, StringAndFunction.cp_alarm2, StringAndFunction.cp_alarm3};
        ArrayAdapter<String> alarm_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, alarm_items);
        alarm_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(alarm_adapter);*/

        //삭제버튼
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAndDelete();
            }
        });

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAndModify();
            }
        });

    }

    private void showDialogAndModify() {
        AlertDialog.Builder msgBuilder2 = new AlertDialog.Builder(CouponDetailActivity.this)
                .setTitle("쿠폰 수정")
                .setMessage("쿠폰 정보를 수정하시겠습니다?")
                .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Modifycoupon();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(CouponDetailActivity.this, "수정 취소", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog msDig = msgBuilder2.create();
        msDig.show();
    }

    private void Modifycoupon() {
        if(mAuth.getCurrentUser() != null){
            Map<String, Object > data = new HashMap<>();

            data.put(FirebaseID.notedate, StringAndFunction.StringToTimeStamp(cp_detail_date.getText().toString()));
            data.put(FirebaseID.contents, cp_detail_contents.getText().toString());
            //data.put(FirebaseID.alarm, spinner.getSelectedItem().toString());

            db.collection(FirebaseID.couponboard).document(document_email)
                    .collection(FirebaseID.conn).document(idNum)
                    .update(data);
            finish();
        }else{
            Toast.makeText(CouponDetailActivity.this, "잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }

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
                                //String alarm = (String) shot.get(FirebaseID.alarm);
                                String url = (String) shot.get(FirebaseID.imageurl);

                                cp_detail_contents.setText(contents);
                                cp_detail_date.setText(date);
                                //spinner.setSelection(findAlarmIndex(alarm));
                                try{
                                    imageurl = "coupon/"+document_email + idNum+".jpg";
                                    storageRef.child(imageurl).getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    //성공
                                                    Glide.with(CouponDetailActivity.this).load(uri).into(imageView);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //실패
                                            imageView.setImageResource(R.drawable.ic_image_not_supported);
                                        }
                                    });
                                }catch(Exception e){
                                    Toast.makeText(CouponDetailActivity.this, "이미지 불러오기를 실패했습니다..",Toast.LENGTH_SHORT).show();
                                    imageView.setImageResource(R.drawable.ic_image_not_supported);
                                }

                            }
                        }
                    }
                });
    }//onStrart

    private int findAlarmIndex(String alarm) {
        if (StringAndFunction.cp_alarm1.equals(alarm)) {
            return 0;
        } else if (StringAndFunction.cp_alarm2.equals(alarm)) {
            return 1;
        } else if (StringAndFunction.cp_alarm3.equals(alarm)) {
            return 2;
        }
        return 0;
    }

    private void showDialogAndDelete(){
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(CouponDetailActivity.this)
                .setTitle("쿠폰 삭제")
                .setMessage("쿠폰을 목록에서 삭제하시겠습니다?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Deletecoupon();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(CouponDetailActivity.this, "삭제 취소", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog msDig = msgBuilder.create();
        msDig.show();
    }

    private void Deletecoupon(){
        //쿠폰(이미지)삭제
        StorageReference desertRef = storageRef.child(imageurl);
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //삭제 성공 > 데이터 삭제
                DeleteCpData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CouponDetailActivity.this, "삭제실패! 잠시후 시도해주세요", Toast.LENGTH_SHORT).show();
                Log.w("in Deletecoupon() : ", "Error deleting document");
            }
        });
    }

    private void DeleteCpData(){
        db.collection(FirebaseID.couponboard).document(document_email)
                .collection(FirebaseID.conn).document(idNum).delete() //고유id값을 가진 데이터를 찾아서 삭제
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("CP Note delete", "DocumentSnapshot successfully deleted!");
                        //진짜 다 삭제 > 액티비티 종료
                        CouponDetailActivity.this.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("in DeleteCpData()", "Error deleting document", e);
                        Toast.makeText(CouponDetailActivity.this, "삭제실패! 잠시후 시도해주세요", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateLabel(){
        String myFormat = StringAndFunction.dateformat;
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        cp_detail_date.setText(sdf.format(myCalendar.getTime()));

    }
}