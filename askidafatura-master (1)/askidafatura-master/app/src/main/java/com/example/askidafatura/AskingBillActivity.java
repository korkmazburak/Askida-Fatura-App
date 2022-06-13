package com.example.askidafatura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.askidafatura.adapters.BillsAdapter;
import com.example.askidafatura.models.Bills;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AskingBillActivity extends AppCompatActivity{
    RecyclerView rv_asking;
    ArrayList<Bills> billsArrayList;
    BillsAdapter billsAdapter;
    Bills user;
    Button btn_askIt;
    EditText et_tc;
    private FirebaseDatabase db;// Database e ulaşmamızı sağlayan sınıftan nesne oluşturduk
    private DatabaseReference dbRef; // database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asking_bill);

        db = FirebaseDatabase.getInstance();// veritabanına erişim sağladık
        et_tc=findViewById(R.id.et_tc);
        btn_askIt=findViewById(R.id.btn_askIt);
        rv_asking = findViewById(R.id.rv_asking);//recylerview tanımı
        billsArrayList = new ArrayList<>();//liste tanımı
        rv_asking.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//recylerview ı layout ile bağladık

        rv_asking.addOnItemTouchListener(new AdminActivity.RecyclerItemClickListener(getApplicationContext(), rv_asking, new AdminActivity.RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                user = billsAdapter.getItemName(position);//tıkladığımız liste elemanının tüm özelliklerini alıyoruz
                selectIslem();// işlem yap penceresini açıyoruz
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        btn_askIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_tc.getText().toString().trim().length()==11){
                    dbRef = db.getReference("Bills").child(et_tc.getText().toString().trim());
                    //Referancımızı oluşturduk firebase nodeumuz için
                    dbRef.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            billsArrayList.clear();
                                Bills task = dataSnapshot.getValue(Bills.class);
                                if(task.activeState==true){
                                    billsArrayList.add(task);
                                }


                            billsAdapter = new BillsAdapter(getApplicationContext(), billsArrayList,false);
                            billsAdapter.notifyDataSetChanged();
                            rv_asking.setAdapter(billsAdapter);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });


        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.ask);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.pay:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.create:
                        startActivity(new Intent(getApplicationContext(), HangingBillActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.ask:

                        return true;
                }
                return false;
            }
        });
    }
    private void selectIslem() {
        final CharSequence[] options = { "Tamamını Öde","Kısmen Öde"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AskingBillActivity.this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Tamamını Öde"))
                {
                    //İçinde veriler ile birlikte ödeme sayfasına gidiyoruz
                    Intent intent=new Intent(AskingBillActivity.this,PayTheBills.class);
                    intent.putExtra("tc",user.tc);
                    intent.putExtra("cost",user.cost);
                    startActivity(intent);
                    dialog.dismiss();
                }
                else if (options[item].equals("Kısmen Öde"))
                {
                    //İçinde veriler ile birlikte Kısmi ödeme sayfasına gidiyoruz
                    Intent intent=new Intent(AskingBillActivity.this,PayAsAskingBills.class);
                    intent.putExtra("tc",user.tc);
                    intent.putExtra("cost",user.cost);
                    startActivity(intent);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

}