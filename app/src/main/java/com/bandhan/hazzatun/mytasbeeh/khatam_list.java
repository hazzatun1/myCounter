package com.bandhan.hazzatun.mytasbeeh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class khatam_list extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    khatam_adapter kAdapter;
    ArrayList<KhatamUser> list_khatam;
    String email1 ="", providerId="", cname="";
    LinearLayout layout;
    String kName="", tgt="", k_count="";
    TextView k_name, k_target, k_member, k_total, k_tdate, myCount, header;
    String tday="", total_k="", email="", t_count="", my_count="";
    FirebaseUser user;
    int ttl=0;
    //User us;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khatam_list);
        layout=findViewById(R.id.kl_back);
        list_khatam = new ArrayList<KhatamUser>();
        // us=new User();
        if (getIntent().hasExtra("name") || getIntent().hasExtra("tgt")){
            kName = getIntent().getStringExtra("name");
            tgt=getIntent().getStringExtra("tgt");
        }

        if (getIntent().hasExtra("k_count")
                &&getIntent().hasExtra("tgt")
                &&getIntent().hasExtra("cname")
                ){
//&&getIntent().hasExtra("t_count")
            kName = getIntent().getStringExtra("cname");
            k_count=getIntent().getStringExtra("k_count");
            tgt=getIntent().getStringExtra("tgt");
           // total_k=getIntent().getStringExtra("t_count");

        }



        header =findViewById(R.id.head_info);
        k_name=findViewById(R.id.k_name);
        k_target=findViewById(R.id.k_target);
        k_member=findViewById(R.id.k_member);
        k_total=findViewById(R.id.k_total);
        k_tdate=findViewById(R.id.d_tday);
        myCount=findViewById(R.id.myCount);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        tday = df.format(c);
        k_tdate.setText(getString(R.string.date_today)+tday);
        k_name.setText(getString(R.string.groupname) +kName);
        k_target.setText(getString(R.string.target_string) +tgt);
        myCount.setText(getString(R.string.my_count)+k_count);
        k_total.setText(getString(R.string.totalcount) +total_k);
        database = FirebaseDatabase.getInstance().getReference("MyDigitalCounter");
         user = FirebaseAuth.getInstance().getCurrentUser();
        // header.setText(getString(R.string.hello) + user.getDisplayName());


        database.child("Group").child(kName).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //list_khatam.clear();
                for (DataSnapshot booksSnapshot : snapshot.getChildren()) {
                    try {
                        KhatamUser users = booksSnapshot.getValue(KhatamUser.class);
                        assert users != null;
                        String mail=users.getK_acc_email();
                        if(mail.matches(user.getEmail())) {
                            my_count = users.getMyCount();
                            t_count = users.gettCount();
                            myCount.setText(getString(R.string.my_count) + my_count);
                            k_total.setText(getString(R.string.totalcount) + t_count);
                        }

                    } catch (DatabaseException e) {
                        //Log the exception and the key
                        booksSnapshot.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

            SharedPreferences set_back = getSharedPreferences("set_back", Context.MODE_PRIVATE);
            String back = set_back.getString("pic_name", "");
            if (back != null && !back.equals("")) {

                switch (back) {
                    case "bk":
                        layout.setBackgroundResource(R.drawable.bk);
                        break;
                    case "pic_1":
                        layout.setBackgroundResource(R.drawable.pic_1);
                        break;
                    case "pic_2":
                        layout.setBackgroundResource(R.drawable.pic_2);
                        break;
                    default:
                        break;
                }
            }

        recyclerView = findViewById(R.id.khatamListList);
        recyclerView.setLayoutManager(new LinearLayoutManager(khatam_list.this));
        Query numberQuery = database.child("Group").orderByKey().equalTo(kName);
        numberQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //list_khatam.clear();
                for (DataSnapshot booksSnapshot : snapshot.getChildren()) {
                    k_member.setText(getString(R.string.groupmember) +String.valueOf(booksSnapshot.getChildrenCount()));
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    public void saveToCount(View view) {
        Intent i = new Intent(this, KhatamCount.class);
        i.putExtra("k_name", kName);
        i.putExtra("tgt", tgt);
        i.putExtra("myCount", my_count);
        startActivity(i);
        this.finish();
    }


    public void tCount(View view) {
        addgroupCounts();

    }

    public void addgroupCounts() {
        DatabaseReference updateData = FirebaseDatabase.getInstance()
                .getReference("MyDigitalCounter")
                .child("Group").child(kName);
        // Query query = updateData.orderByKey().limitToLast(1);

        updateData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int total=0;
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        KhatamUser usa=snapshot.getValue(KhatamUser.class);
                        int tcou=Integer.parseInt(usa.getMyCount());
                        total += tcou;
                        ttl=total;
                        // snapshot.getRef().child("tCount").setValue(String.valueOf(total));
                    }
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                         snapshot.getRef().child("tCount").setValue(String.valueOf(total));
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void go_main(View view) {
        Intent i = new Intent(this, KhotomAccount.class);
        startActivity(i);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), KhotomAccount.class);
        // i.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        this.finish();
    }



}