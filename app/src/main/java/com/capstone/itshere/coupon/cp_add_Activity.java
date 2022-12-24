package com.capstone.itshere.coupon;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.itshere.R;
import com.capstone.itshere.StringAndFunction;
import com.capstone.itshere.account.FirebaseID;
import com.capstone.itshere.accountBook.ab_add_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class cp_add_Activity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private String email;
    private Uri imageUrl;
    private ImageModel imageModel;
    private String modelID;

    private ImageButton back;
    private TextView title;

    private ImageView addimage;
    EditText cp_add_contents, cp_add_date;
    Spinner alarm_spinner;
    private Button btn_save,btn_load_image;

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
        setContentView(R.layout.activity_cp_add);

        //email가져오기
        if(mAuth.getCurrentUser() != null) {
            db.collection(FirebaseID.user).document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult() != null) {
                                email = (String) task.getResult().getData()
                                        .get(FirebaseID.email);
                                Log.e("입력", email + " ");
                            }
                        }
                    });
        }

        //toolbar
        back = (ImageButton) findViewById(R.id.tool_sub2_back);
        title = (TextView) findViewById(R.id.tool_sub2_title);
        title.setText("등록하기");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //init
        btn_save = findViewById(R.id.cp_btn_save);
        addimage = findViewById(R.id.cpImagevView);
        cp_add_contents = findViewById(R.id.cp_add_contents);
        cp_add_date = findViewById(R.id.cp_add_date);
        alarm_spinner = findViewById(R.id.cp_add_alarm);

        //datepicker설정
        cp_add_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(cp_add_Activity.this,
                        myDatePicker,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        //alarm spinner
        String[] alarm_items = { StringAndFunction.cp_alarm1, StringAndFunction.cp_alarm2, StringAndFunction.cp_alarm3};
        ArrayAdapter<String> alarm_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, alarm_items);
        alarm_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alarm_spinner.setAdapter(alarm_adapter);



        //imageview
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult.launch(galleryIntent);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCouponData();
            }
        });
    }//onCreate

    //사진가져오기
    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if( result.getResultCode() == RESULT_OK && result.getData()  != null){
                        imageUrl = result.getData().getData();
                        addimage.setImageURI(imageUrl);
                }
            }
}
    );

    private void saveCouponData() {
        if(mAuth.getCurrentUser() != null){
            if(imageUrl != null){
                try{
                    //이미지업로드
                    uploadTofirebase(imageUrl);
                    //나머지 데이터입력
                    String cpNoteId = db.collection(FirebaseID.documentId).document().getId();
                    Map<String,Object> data = new HashMap<>();
                    data.put(FirebaseID.documentId, cpNoteId);
                    data.put(FirebaseID.contents, cp_add_contents.getText().toString());
                    data.put(FirebaseID.notedate, StringAndFunction.StringToTimeStamp(cp_add_date.getText().toString()));
                    data.put(FirebaseID.alarm, alarm_spinner.getSelectedItem().toString());
                    data.put(FirebaseID.imageurl, imageUrl.toString());
                    Log.e("입력8", imageUrl.toString());
                    db.collection(FirebaseID.couponboard).document(email)
                            .collection(FirebaseID.conn).document(cpNoteId)
                            .set(data, SetOptions.merge());
                    Log.e("입력9", "왜");
                } catch(Exception e){
                    Toast.makeText(cp_add_Activity.this,"잠시후 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                }
                finish();
            }else{
                //이미지 선택하지 않음
                Toast.makeText(cp_add_Activity.this, "이미지를 선택해주세요.", Toast.LENGTH_LONG).show();
            }
        }else{
            //로그인 되지 않은 상태라면 에러메시지를 보여주고 버튼이 작동하지 않음
            Toast.makeText(cp_add_Activity.this, "로그인해주세요.", Toast.LENGTH_LONG).show();
        }
    }

    //파이어베이스 이미지 업로드
    private void uploadTofirebase(Uri uri){
        try {
            StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtention(uri));
            fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //이미지 모델에 담기
                            imageModel = new ImageModel(uri.toString());

                            //키로 아이디 생성
                            modelID = root.push().getKey();

                            //데이터 넣기
                            root.child(modelID).setValue(imageModel);
                            Log.e("uploadTofirebase", modelID + " ");
                            addimage.setImageResource(R.drawable.ic_add_image);
                        }
                    });
                }
            });
        }catch(Exception e){
            Log.e("uploadtofirebase", e + " ");
        }
    }

    //파일 타입가져오기
    private String getFileExtention(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void updateLabel(){
        String myFormat = StringAndFunction.dateformat;
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        cp_add_date.setText(sdf.format(myCalendar.getTime()));

    }

}