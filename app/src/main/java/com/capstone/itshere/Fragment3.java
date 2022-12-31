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
import com.capstone.itshere.coupon.CouponItem;
import com.capstone.itshere.coupon.CouponItemAdapter;
import com.capstone.itshere.membership.MBSitem;
import com.capstone.itshere.membership.MBSitemAdapter;
import com.capstone.itshere.membership.MembershipAddActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class Fragment3 extends Fragment {
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView fr3_tv_hint, fr3_tv_hint2;
    private ArrayList<MBSitem> arrayList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser User = mAuth.getCurrentUser();
    private static String document_email, TAG;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_3, container, false);

        //init
        TAG = "프래그먼트3";
        recyclerView = view.findViewById(R.id.fr3_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        fab = view.findViewById(R.id.fab3_add);
        fr3_tv_hint = view.findViewById(R.id.fr3_tv_hint);
        fr3_tv_hint2 = view.findViewById(R.id.fr3_tv_hint2);

        //등록버튼
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MembershipAddActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }//onCreate

    @Override
    public void onStart() {
        super.onStart();
        try{
            document_email = User.getEmail();
        }catch(Exception e){
            fr3_tv_hint.setVisibility(View.VISIBLE); //로그인하세요 보이게
            fr3_tv_hint2.setVisibility(View.GONE);// 가계부작성하세요 레이아웃 안보이게
        }
        loadMBSData();

    }

    private void loadMBSData() {
        try{
            db.collection(FirebaseID.MBSboard).document(document_email)
                    .collection(FirebaseID.conn)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null){
                                arrayList.clear();
                                for(DocumentSnapshot snap : value.getDocuments()){
                                    Map<String, Object> shot = snap.getData();
                                    String barcode = String.valueOf(shot.get(FirebaseID.barcode));
                                    String contents = String.valueOf(shot.get(FirebaseID.contents));
                                    Integer color = Integer.valueOf(String.valueOf(shot.get(FirebaseID.color)));
                                    String ID = String.valueOf(shot.get(FirebaseID.documentId));

                                    MBSitem item = new MBSitem(ID, contents, color, barcode);
                                    arrayList.add(item);
                                }
                                if(arrayList.size() == 0){
                                    fr3_tv_hint2.setVisibility(View.VISIBLE);
                                }else{
                                    fr3_tv_hint2.setVisibility(View.GONE);
                                }
                                adapter = new MBSitemAdapter(arrayList, getContext());
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    });
            //dbload
            fr3_tv_hint.setVisibility(View.GONE); // 로그하세요 안보이게

        }catch (Exception e){
            Log.e(TAG + " db error", String.valueOf(e));
            Toast.makeText(getContext(),"잠시후 다시 시도해주세요.", Toast.LENGTH_LONG).show();
        }

    }
}