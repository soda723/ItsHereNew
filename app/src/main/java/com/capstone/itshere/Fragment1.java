package com.capstone.itshere;

import static com.capstone.itshere.StringAndFunction.timestampToString;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.capstone.itshere.account.FirebaseID;
import com.capstone.itshere.accountBook.DailyNote;
import com.capstone.itshere.accountBook.DailyNoteAdapter;
import com.capstone.itshere.accountBook.ab_add_Activity;
import com.capstone.itshere.accountBook.statsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Fragment1 extends Fragment {

    public static Fragment CONTEXT;
    private static String TAG = "프레그먼트1";
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<DailyNote> arrayList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser User = mAuth.getCurrentUser();
    private static String document_email;
    private TextView tv_hint, tv_hint2;
    private LinearLayout ly_total;
    private Button btn_stats;
    private int income , outcome, total;
    private TextView tv_income, tv_outcome, tv_total;
    private Button left, right;
    private String MONTH;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        //Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_1, container, false);
        CONTEXT = this;

        //Initialize
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);//리사이클러뷰 기존성능강화
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // Dailynote 객체를 담을 어레이 리스트
        tv_hint = view.findViewById(R.id.tv_hint);
        tv_hint2 = view.findViewById(R.id.tv_hint2);
        ly_total = view.findViewById(R.id.ly_total);
        btn_stats = view.findViewById(R.id.btn_stats);
        tv_income = view.findViewById(R.id.tv_income);
        tv_outcome = view.findViewById(R.id.tv_outcome);
        tv_total = view.findViewById(R.id.tv_total);
        left = view.findViewById(R.id.btn_month_left);
        right = view.findViewById(R.id.btn_month_right);

        //등록버튼 설정
        fab = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ab_add_Activity.class);
                startActivity(intent);
            }
        });

        //통계창 버튼 클릭
        btn_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getContext(), statsActivity.class);
                startActivity(intent2);
            }
        });


        return view;
    }//OnCreateView

    @Override
    public void onStart(){
        super.onStart();
        loadData();

    }//onStart--*

    @Override
    public void onResume() {
        super.onResume();
        //
    }

    protected void loadData(){
        MONTH = "";
        MONTH = getYearMonth(); //오늘기준 년도-월값 지정 ('2022-05')
        try{
            document_email = User.getEmail(); //해당 사용자 email 값 불러오기
            //db에서 값 가져오기 > arraylist에 담기 > adpater에 저장 > 리사이클러 뷰에 뿌리기
            db.collection(FirebaseID.noteboard).document(document_email).collection(MONTH)
                    .orderBy(FirebaseID.notedate, Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null){
                                arrayList.clear();
                                income = 0; //총수입 초기화
                                outcome = 0; //총지출 초기화
                                total = 0; //총액 초기화
                                //firebase에서 불러온 데이터 항목들을 snap에 저장
                                for(DocumentSnapshot snap : value.getDocuments()){
                                    Map<String, Object> shot = snap.getData();
                                    //각 데이터 상세 항목값 불러오기
                                    String date = timestampToString(String.valueOf(shot.get(FirebaseID.notedate))); //날짜
                                    String category = String.valueOf(shot.get(FirebaseID.category)); //카테고리
                                    String note = String.valueOf(shot.get(FirebaseID.note));//내용
                                    int amount = Integer.parseInt(String.valueOf(shot.get(FirebaseID.amount)));//금액
                                    String bigcate = String.valueOf(shot.get(FirebaseID.bigcate));//수입/지출
                                    String docId = String.valueOf(shot.get(FirebaseID.documentId));//고유id
                                    if (bigcate.equals("수입")){
                                        income += amount; //수입 카테고리 데이터 금액을 총수입에 더함
                                    }else{
                                        outcome += amount; //지출 카테고리 데이터 금액을 총지출에 더함
                                    }
                                    DailyNote item = new DailyNote(bigcate, date, category, note, amount, docId, MONTH);
                                    arrayList.add(item);
                                }
                                if(arrayList.size() == 0){ //로그인후 데이터가 없을 경우
                                    tv_hint2.setVisibility(View.VISIBLE); //가계부를 작성하세요 메시지 레이어 보이기
                                }else{
                                    tv_hint2.setVisibility(View.GONE); // 데이터 있을때는 안보이게
                                }
                                adapter = new DailyNoteAdapter(arrayList, getContext());
                                recyclerView.setAdapter(adapter); //리사이클러뷰에 데이터 표시
                                total = income - outcome; //총액 계산
                                tv_income.setText(String.valueOf(income)); // 계산된 총수입/총지출/총액표시
                                tv_outcome.setText(String.valueOf(outcome));
                                tv_total.setText(String.valueOf(total));
                            }
                        }
                    });
            //--*db >...>뿌리기 끝
            tv_hint.setVisibility(View.GONE); // 로그인한 유저 없을 경우 메시지 레이아웃 안보이게
            ly_total.setVisibility(View.VISIBLE); //총 긍액 레이어 보이게
        }catch (NullPointerException e){
            //로그인한 유저가 없을때
            Log.e(TAG + " db error", String.valueOf(e));
            tv_hint.setVisibility(View.VISIBLE);
            ly_total.setVisibility(View.GONE);
            tv_hint2.setVisibility(View.GONE);// 가계부작성하세요 레이아웃 안보이게
        }
    }

    public static String getYearMonth(){
        return "2022-05";
    };
}