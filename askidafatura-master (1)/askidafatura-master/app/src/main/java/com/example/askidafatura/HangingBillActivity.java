package com.example.askidafatura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

//BU CLASSA ADAPTERVİEW CLİCK LİSTENERI İMPLEMENT ETTİK DROPDOWN YANİ SPİNNERLARDAN GELEN VERİLERİ DİNLEYECEK METHODLARA ULAŞMAK İÇİN
public class HangingBillActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner city,type;
    String[] cities,types;
    String choosenCity="",choosenType;
    EditText et_tc,et_cost,et_no;
    Button btn_hangIt;
    TextView tv_cUid;
    String plaka;
    private FirebaseDatabase db;// Database e ulaşmamızı sağlayan sınıftan nesne oluşturduk
    private DatabaseReference dbRef; // database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hanging_bill);
        db = FirebaseDatabase.getInstance();// veritabanına erişim sağladık
        //Spinner tanımı
        city=findViewById(R.id.city);
        type=findViewById(R.id.type);
        et_tc=findViewById(R.id.et_tc);
        et_cost=findViewById(R.id.et_cost);
        et_no=findViewById(R.id.et_no);
        btn_hangIt=findViewById(R.id.btn_hangIt);
        tv_cUid=findViewById(R.id.tv_cUid);
        // Spinner click fonksiyonuna bağlanması
        city.setOnItemSelectedListener(this);
        type.setOnItemSelectedListener(this);

        cities=getResources().getStringArray(R.array.my_array);
        types=getResources().getStringArray(R.array.my_types);

        loadSpinnerData();

        btn_hangIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city.getSelectedItemPosition()<9){
                    plaka="0"+(city.getSelectedItemPosition()+1);
                }else{
                    plaka=String.valueOf((city.getSelectedItemPosition()+1));
                }


                //Girdiğimiz verilerin boş olmadığına dikkat ettik
                if(et_tc.getText().toString().trim().length()==11&&et_cost.getText().toString().trim().length()>2&&!et_no.getText().toString().isEmpty()){
                    dbRef = db.getReference("Bills").child(et_tc.getText().toString().trim());
                    //Referancımızı oluşturduk firebase nodeumuz için
                    //ve verilerimizi hashmapleyerek firebase e atılmaya uygun hale getirdik
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("activeState", false);
                    hashMap.put("cUid", plaka);
                    hashMap.put("city", city.getSelectedItem().toString());
                    hashMap.put("cost", Integer.valueOf(et_cost.getText().toString()));
                    hashMap.put("askingPay", 0);
                    hashMap.put("payState", 0);
                    hashMap.put("no", et_no.getText().toString().trim());
                    hashMap.put("tc", et_tc.getText().toString().trim());
                    hashMap.put("type", type.getSelectedItem().toString());
                    //maplenmiş verilerimizi referansımıza value olarak verdik ve verimizi firebase e eklemiş olduk
                    dbRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Fatura Eklendi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(HangingBillActivity.this, "Lütfen bilgileri eksiksiz giriniz", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.create);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.pay:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.create:
                        return true;
                    case R.id.ask:
                        startActivity(new Intent(getApplicationContext(), AskingBillActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
    private void loadSpinnerData() {
        //This is how to load spinners with data  SPINNERS=DROPDOWNS

        // Creating adapter for spinner and take all databese datas
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, cities);
        //Fill the database with cities
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner for load data to spinner
        city.setAdapter(dataAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, types);
        //Fill the database with cities
        // Drop down layout style - list view with radio button
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner for load data to spinner
        type.setAdapter(typeAdapter);




    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        choosenCity=city.getSelectedItem().toString();
        choosenType=type.getSelectedItem().toString();
        if(city.getSelectedItemPosition()<9){
            tv_cUid.setText("Plaka: 0"+(city.getSelectedItemPosition()+1));
        }else{
            tv_cUid.setText("Plaka: "+(city.getSelectedItemPosition()+1));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}