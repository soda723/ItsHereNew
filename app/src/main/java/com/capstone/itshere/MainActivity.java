package com.capstone.itshere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.capstone.itshere.account.FirebaseID;
import com.capstone.itshere.account.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView mBottomNV;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private ImageButton menubutton;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menubutton = (ImageButton) findViewById(R.id.tool_menu);

        //하단 네비게이션 바 설정
        mBottomNV = findViewById(R.id.bottomNavi);
//      mBottomNV.setItemIconTintList(null);

        //drawer 네비게이션 바 설정
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.drawer);

        //툴바 메뉴 버튼&뒤로가기 설정
        menubutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        //처음화면 설정
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new Fragment1()).commit(); //FrameLayout에 fragment1.xml 띄우기

        // 바텀 네비게이션뷰 안의 아이템 설정
        mBottomNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //item을 클릭시 id값을 가져와 FrameLayout에 fragment.xml띄우기
                    case R.id.fragment_1: getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment1()).commit();
                        break;
                    case R.id.fragment_2: getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment2()).commit();
                        break;
                    case R.id.fragment_3: getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment3()).commit();
                        break;
                }
                return true;
            }
        });

        //drawer menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.drawer_login:
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        break;
                    case R.id.drawer_info:
                        break;
                    case R.id.drawer_setting:
                        break;
                }
                drawerLayout.closeDrawer(navigationView);
                return false;
            }
        });

    }
    @Override
    public void onBackPressed(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        View header = navigationView.getHeaderView(0); // xml 파일에서 넣어놨던 header 선언

        // header에 있는 리소스 가져오기
        TextView dh_name = header.findViewById(R.id.dh_name);
        LinearLayout ly_yes_user = header.findViewById(R.id.ly_dh_yes_user);
        LinearLayout ly_no_user = header.findViewById(R.id.ly_dh_no_user);
        if(mAuth.getCurrentUser() != null){
            db.collection(FirebaseID.user).document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult() != null){
                                email = (String) task.getResult().getData()
                                        .get(FirebaseID.email);
                                dh_name.setText(email + " 님,");
                                ly_yes_user.setVisibility(View.VISIBLE);
                                ly_no_user.setVisibility(View.GONE);
                            }
                        }
                    });

        }else{
            email = null;
            ly_no_user.setVisibility(View.VISIBLE);
            ly_yes_user.setVisibility(View.GONE);
        }

    }

}

