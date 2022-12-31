package com.capstone.itshere.accountBook;

import static com.capstone.itshere.StringAndFunction.timestampToString;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.capstone.itshere.R;
import com.capstone.itshere.StringAndFunction;
import com.capstone.itshere.account.FirebaseID;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DailyDetailActivity extends AppCompatActivity {

    private ImageButton back;
    private TextView title;
    private EditText dd_inex,dd_date,dd_amount,dd_note,dd_memo;
    private Button btn_modify,btn_delete;
    private String idNum;
    private String MONTH;
    private Spinner spinner_account, spinner_category;
    private RadioGroup radio;
    private RadioButton radioValue;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser User = mAuth.getCurrentUser();
    private String document_email;

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
        setContentView(R.layout.activity_daily_detail);

        //intent //액티비티 시작시 상세내용의 고유 id와 년도-월값을 받는다.
        idNum = getIntent().getStringExtra("idNum");
        MONTH = getIntent().getStringExtra("MONTH");
        Log.e("in ddactivity", idNum);

        //툴바 설정
        back = (ImageButton) findViewById(R.id.tool_sub1_back);
        title = (TextView) findViewById(R.id.tool_sub1_title);
        title.setText("상세내용");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //객체 초기화
        radio = findViewById(R.id.dd_radio);
        dd_date = findViewById(R.id.dd_date);
        spinner_account = findViewById(R.id.dd_account);
        spinner_category = findViewById(R.id.dd_category);
        dd_amount = findViewById(R.id.dd_amount);
        dd_note = findViewById(R.id.dd_note);
        dd_memo = findViewById(R.id.dd_memo);
        btn_modify = findViewById(R.id.btn_modify);
        btn_delete = findViewById(R.id.btn_delete);

        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.dd_radio_income){
                    Toast.makeText(getApplicationContext(), "수입", Toast.LENGTH_SHORT).show();
                }else if(checkedId == R.id.dd_radio_expense){
                    Toast.makeText(getApplicationContext(), "지출", Toast.LENGTH_SHORT).show();
                }
                radioValue = findViewById(radio.getCheckedRadioButtonId());
            }
        });
        if (radioValue == null){
            radio.check(R.id.dd_radio_expense);
        }

        dd_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(DailyDetailActivity.this,
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

        //수정버튼
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertOkModify();
            }
        });

        //삭제버튼
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //팝업창 설정
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DailyDetailActivity.this);
                alertDialogBuilder.setTitle("가계부 내역 삭제"); //팝업창 제목
                alertDialogBuilder
                        .setMessage("가계부 내역을 삭제하시겠습니까?")//팝업창 메시지
                        .setCancelable(false) //취소버튼 - 취소
                        .setPositiveButton("삭제", //긍정버튼 이름은 삭제, 기능은 아래와 같이 실행
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //삭제 기능 실행
                                        db.collection(FirebaseID.noteboard).document(document_email).collection(MONTH)
                                                .document(idNum).delete() //고유id값을 가진 데이터를 찾아서 삭제
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("Daily Note delete", "DocumentSnapshot successfully deleted!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("Daily Note delete", "Error deleting document", e);
                                                    }
                                                });
                                        //삭제하면 해당 데이터가 존재하지 않으므로 액티비티 종료
                                        DailyDetailActivity.this.finish();
                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();
                // 다이얼로그 보여주기
                alertDialog.show();
            }
        });
    }//--onCreate--*

    private void AlertOkModify() {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(DailyDetailActivity.this);
        builder2.setTitle("가계부 내역 수정") //팝업창 제목
                .setMessage("가계부 내역을 수정하시겠습니까?")//팝업창 메시지
                .setCancelable(false) //취소버튼 - 취소
                .setPositiveButton("수정", //긍정버튼 이름은 삭제, 기능은 아래와 같이 실행
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                modifyNote();
                            }
                        });

        AlertDialog alertDialog2 = builder2.create();
        alertDialog2.show();
    }

    private void modifyNote() {
        if(mAuth.getCurrentUser() != null){
            Map<String, Object > data = new HashMap<>();

            try{
                data.put(FirebaseID.bigcate, radioValue.getText().toString());
            }catch(NullPointerException e){
                data.put(FirebaseID.bigcate, "지출"); // 수입/지출항목을 지정하지 않으면 기본값 지출로 설정
            }
            data.put(FirebaseID.notedate, StringAndFunction.StringToTimeStamp(dd_date.getText().toString()));
            data.put(FirebaseID.account, spinner_account.getSelectedItem().toString());
            data.put(FirebaseID.category, spinner_category.getSelectedItem().toString());
            data.put(FirebaseID.amount, Integer.parseInt(String.valueOf(dd_amount.getText())));
            data.put(FirebaseID.note, dd_note.getText().toString());
            data.put(FirebaseID.memo, dd_memo.getText().toString());

            db.collection(FirebaseID.noteboard).document(document_email)
                    .collection(MONTH).document(idNum)
                    .update(data);
            finish();
        }else{
            Toast.makeText(DailyDetailActivity.this, "잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        document_email = User.getEmail();
        //db에서 값 가져오기 > arraylist에 담기 > adpater에 저장 > 리사이클러 뷰에 뿌리기
        db.collection(FirebaseID.noteboard).document(document_email).collection(MONTH)
                .document(idNum).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()){
                                Map<String, Object> shot = doc.getData();
                                String date = timestampToString(String.valueOf(shot.get(FirebaseID.notedate)));
                                String category = String.valueOf(shot.get(FirebaseID.category));
                                String note = String.valueOf(shot.get(FirebaseID.note));
                                String amount = String.valueOf(shot.get(FirebaseID.amount));
                                String account = String.valueOf(shot.get(FirebaseID.account));
                                String bigcate = String.valueOf(shot.get(FirebaseID.bigcate));
                                String memo = String.valueOf(shot.get(FirebaseID.memo));
                                if(bigcate.equals("수입")) radio.check(R.id.dd_radio_income);
                                else radio.check(R.id.dd_radio_expense);
                                dd_date.setText(date);
                                spinner_account.setSelection(findAccountSpinnerIndex(account));
                                spinner_category.setSelection(findCategorySpinnerIndex(category));
                                dd_amount.setText(amount);
                                dd_note.setText(note);
                                dd_memo.setText(memo);
                            }//--if doc exits-*
                        }//--if task-*
                    }
                });


    }//--OnStart--**

    private void updateLabel(){
        String myFormat = StringAndFunction.dateformat;
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        dd_date.setText(sdf.format(myCalendar.getTime()));
    }

    private Integer findAccountSpinnerIndex(String text){
        //{"현금", "카드", "은행"};
        switch (text){
            case "현금" : return 0;
            case "카드" : return 1;
            case "은행" : return 2;
            default:return 0;
        }
    }

    private Integer findCategorySpinnerIndex(String text){
        //{"식비", "차량/교통", "문화생활", "패션/미용", "생활용품", "경조사/회비", "기타"};
        switch (text){
            case "식비" : return 0;
            case "차량/교통" : return 1;
            case "문화생활" : return 2;
            case "패션/미용" : return 3;
            case "생활용품" : return 4;
            case "경조사/회비" : return 5;
            case "기타" : return 6;
            default:return 0;
        }
    }

}