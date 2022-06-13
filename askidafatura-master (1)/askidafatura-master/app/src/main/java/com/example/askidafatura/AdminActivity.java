package com.example.askidafatura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.askidafatura.adapters.BillsAdapter;
import com.example.askidafatura.models.Bills;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Bills> billsArrayList;
    Bills bills;
    BillsAdapter billsAdapter;
    private FirebaseDatabase db;// Database e ulaşmamızı sağlayan sınıftan nesne oluşturduk
    private DatabaseReference dbRef; // databasede hangi path e ulaşacağımızı belirlediğimiz sınıftan nesne oluşturduk
    private FirebaseUser fUser; // firebasein kullanıcı sınıfından bir nesne oluşturduk current user değerlerini alabilmek için
    String selectedIndex;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();// veritabanına erişim sağladık
        fUser = FirebaseAuth.getInstance().getCurrentUser(); // şuanki kullanıcının verilerini firebaseuser classının nesnesinin içine attık
        recyclerView = findViewById(R.id.rv_adminBills);
        billsArrayList = new ArrayList<>();
        StoreDataList(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        //listenin elemanlarının click fonksiyonları

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
               bills = billsAdapter.getItemName(position);
                dbRef = db.getReference("Bills").child(bills.tc);
                //Referancımızı oluşturduk firebase nodeumuz için
                //ve verilerimizi hashmapleyerek firebase e atılmaya uygun hale getirdik
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("activeState", !bills.activeState);
                //maplenmiş verilerimizi referansımıza value olarak verdik ve verimizi firebase e eklemiş olduk
                dbRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Fatura Aktif/Pasif hale getirildi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onLongItemClick(View view, int position) {
                selectedIndex = String.valueOf(position);

            }
        }));
    }

    private void StoreDataList(boolean state) {
        dbRef = db.getReference("Bills");
        dbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               billsArrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                   Bills task = ds.getValue(Bills.class);
                   if(task.activeState==state&&task.payState==0){
                       billsArrayList.add(task);
                    }else{

                    }

                }

                billsAdapter = new BillsAdapter(getApplicationContext(), billsArrayList,true);
                billsAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(billsAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.active_deactive_menu, menu);
        return true;
    }
    //Admin logine gitmemizi sağlayan menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.active:
                StoreDataList(true);
                break;
            case R.id.deactive:
                StoreDataList(false);
                break;
            case R.id.logout:
                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                startActivity(intent);
                mAuth.signOut();
                finish();
                break;
        }
        return true;
    }

public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;
    //click için bu kısmı yukarıda recylerview ile kullanıyoruz
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLongItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
}
}