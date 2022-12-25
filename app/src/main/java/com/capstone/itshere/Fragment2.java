package com.capstone.itshere;

import static com.capstone.itshere.StringAndFunction.DdayCounter;
import static com.capstone.itshere.StringAndFunction.timestampToString;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.itshere.account.FirebaseID;
import com.capstone.itshere.accountBook.DailyNote;
import com.capstone.itshere.accountBook.DailyNoteAdapter;
import com.capstone.itshere.accountBook.ab_add_Activity;
import com.capstone.itshere.coupon.CouponDetailActivity;
import com.capstone.itshere.coupon.CouponItem;
import com.capstone.itshere.coupon.CouponItemAdapter;
import com.capstone.itshere.coupon.cp_add_Activity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class Fragment2 extends Fragment {
    private static String TAG = "프래그먼트2";
    private FloatingActionButton fab2;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CouponItem> arrayList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser User = mAuth.getCurrentUser();
    private static String document_email;

    private TextView fr2_tv_hint, fr2_tv_hint2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        //Initialize
        recyclerView = (RecyclerView) view.findViewById(R.id.fr2_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        arrayList = new ArrayList<>();
        fr2_tv_hint = view.findViewById(R.id.fr2_tv_hint);
        fr2_tv_hint2 = view.findViewById(R.id.fr2_tv_hint2);

        //등록버튼 설정
        fab2 = (FloatingActionButton) view.findViewById(R.id.fr2_fab_add);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), cp_add_Activity.class);
                startActivity(intent);
            }
        });


        return view;
    }//OnCreateView

    @Override
    public void onStart(){
        super.onStart();
        try{
            document_email = User.getEmail();
        }catch(Exception e){
            fr2_tv_hint.setVisibility(View.VISIBLE); //로그인하세요 보이게
            fr2_tv_hint2.setVisibility(View.GONE);// 가계부작성하세요 레이아웃 안보이게
        }
        loadCouponData();

    }//onStart--*

    private void loadCouponData() {
        try{
            //
            db.collection(FirebaseID.couponboard).document(document_email)
                    .collection(FirebaseID.conn)
                    .orderBy(FirebaseID.notedate, Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null){
                                arrayList.clear();
                                for(DocumentSnapshot snap : value.getDocuments()){
                                    Map<String, Object> shot = snap.getData();
                                    String contents = String.valueOf(shot.get(FirebaseID.contents));
                                    String date = timestampToString(String.valueOf(shot.get(FirebaseID.notedate)));
                                    String idnum = String.valueOf(shot.get(FirebaseID.documentId));

                                    String dday = DdayCounter(date);
                                    CouponItem item = new CouponItem(idnum, contents, date, dday);
                                    arrayList.add(item);
                                }
                                if(arrayList.size() == 0){
                                    fr2_tv_hint2.setVisibility(View.VISIBLE);
                                }else{
                                    fr2_tv_hint2.setVisibility(View.GONE);
                                }
                                adapter = new CouponItemAdapter(arrayList, getContext());
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    });
            //dbload
            fr2_tv_hint.setVisibility(View.GONE); // 로그하세요 안보이게
        }catch(NullPointerException e){
            Log.e(TAG + " db error", String.valueOf(e));
            Toast.makeText(getContext(),"잠시후 다시 시도해주세요.", Toast.LENGTH_LONG).show();
        }
    }
}