package com.capstone.itshere.accountBook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.itshere.Fragment1;
import com.capstone.itshere.R;
import com.capstone.itshere.StringAndFunction;
import com.capstone.itshere.account.FirebaseID;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ab_add_Activity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email;

    private ImageButton back;
    private TextView title;

    private RadioGroup radio;
    private RadioButton radioValue;
    EditText ab_add_date, ab_add_amount, ab_add_note, ab_add_memo;
    Spinner spinner_account, spinner_category;
    private Button btn_save;
    private String MONTH;

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
        setContentView(R.layout.activity_ab_add);
        /*ps 패키지 추가후 R에서 오류가 나면 R을 import 해야한다
        * import com.example.blahblah.R; */

        //툴바 설정
        back = (ImageButton) findViewById(R.id.tool_sub1_back);
        title = (TextView) findViewById(R.id.tool_sub1_title);
        title.setText("등록하기");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //객체 초기화
        radio = findViewById(R.id.ab_add_radio);
        ab_add_date = findViewById(R.id.ab_add_date);
        spinner_account = findViewById(R.id.ab_add_account);
        spinner_category = findViewById(R.id.ab_add_category);
        ab_add_amount = findViewById(R.id.ab_add_amount);
        ab_add_note = findViewById(R.id.ab_add_note);
        ab_add_memo = findViewById(R.id.ab_add_memo);
        btn_save = findViewById(R.id.ab_add_save);

        //라디오 설정
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.ab_add_radio_income){
                    Toast.makeText(getApplicationContext(), "수입", Toast.LENGTH_SHORT).show();
                }else if(checkedId == R.id.ab_add_radio_expense){
                    Toast.makeText(getApplicationContext(), "지출", Toast.LENGTH_SHORT).show();
                }
                radioValue = findViewById(radio.getCheckedRadioButtonId());
            }
        });
        if (radioValue == null){
            radio.check(R.id.ab_add_radio_expense);
        }




        //datepicker설정
        ab_add_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ab_add_Activity.this,
                        myDatePicker,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        //자산 spinner
        String[] acc_items = {"현금", "카드", "은행"};
        ArrayAdapter<String> acc_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, acc_items);
        acc_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_account.setAdapter(acc_adapter);

        //분류 spinner
        String[] cate_items = {"식비", "차량/교통", "문화생활", "패션/미용", "생활용품", "경조사/회비", "기타"};
        ArrayAdapter<String> cate_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cate_items);
        cate_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_category.setAdapter(cate_adapter);

        //사용자ID 가져오기
        if(mAuth.getCurrentUser() != null){
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
        }

        //add버튼 클릭
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser() != null){
                    String noteId = db.collection(FirebaseID.note).document().getId(); //고유 id값 생성
                    Map<String,Object> data = new HashMap<>(); //HashMap을 통해 데이터 (이름)-(값)지정
                    MONTH = getYearMonth(ab_add_date.getText().toString()); // 지정한 날짜에서 년도-월 값 지정
                    data.put(FirebaseID.documentId, noteId);
                    try{
                        data.put(FirebaseID.bigcate, radioValue.getText().toString());
                    }catch(NullPointerException e){
                        data.put(FirebaseID.bigcate, "지출"); // 수입/지출항목을 지정하지 않으면 기본값 지출로 설정
                    }
                    data.put(FirebaseID.notedate, StringAndFunction.StringToTimeStamp(ab_add_date.getText().toString()));
                    data.put(FirebaseID.account, spinner_account.getSelectedItem().toString());
                    data.put(FirebaseID.category, spinner_category.getSelectedItem().toString());
                    data.put(FirebaseID.amount, Integer.parseInt(String.valueOf(ab_add_amount.getText())));
                    data.put(FirebaseID.note, ab_add_note.getText().toString());
                    data.put(FirebaseID.memo, ab_add_memo.getText().toString());
                    //hashMap데이터를 firestore에 저장
                    db.collection(FirebaseID.noteboard).document(email)
                            .collection(MONTH).document(noteId)
                            .set(data, SetOptions.merge());
                    finish(); //완료되면 액티비티 종료
                }else{
                    //로그인 되지 않은 상태라면 에러메시지를 보여주고 버튼이 작동하지 않음
                    Toast.makeText(ab_add_Activity.this, "로그인해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }//onCreate

    private String getYearMonth(String stringdate) {
        String[] temp = stringdate.split("-");
        return temp[0] + "-" + temp[1];
    }

    private void updateLabel(){
        String myFormat = StringAndFunction.dateformat;
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        ab_add_date.setText(sdf.format(myCalendar.getTime()));
    }
    

}