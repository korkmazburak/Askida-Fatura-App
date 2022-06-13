package com.example.askidafatura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PayTheBills extends AppCompatActivity {
    private FirebaseDatabase db;// Database e ulaşmamızı sağlayan sınıftan nesne oluşturduk
    private DatabaseReference dbRef; // database
    TextView tv_borc;
    EditText et_creditCard,et_month,et_year,et_cvv;
    Button btn_pay;
    Bundle bundle;
    int cost;
    String tc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_the_bills);
        bundle=getIntent().getExtras();//bundle ve getIntent kullanarak diğer activityden gönderdiğimiz
        cost=bundle.getInt("cost");//Verilerin hepsini çekebiliyoruz integersa getInt Stringse getString
        tc=bundle.getString("tc");
        db = FirebaseDatabase.getInstance();// veritabanına erişim sağladık
        tv_borc=findViewById(R.id.tv_borc);
        et_creditCard=findViewById(R.id.et_creditCard);
        et_month=findViewById(R.id.et_month);
        et_year=findViewById(R.id.et_year);
        et_cvv=findViewById(R.id.et_cvv);
        btn_pay=findViewById(R.id.btn_pay);

        tv_borc.setText("Sözleşemeye Ait Toplam Borç: "+cost);

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_month.getText().toString().trim().length()>1 && et_creditCard.getText().toString().trim().length()>15 && et_year.getText().toString().trim().length()>3&&!et_cvv.getText().toString().isEmpty()){
                    dbRef = db.getReference("Bills").child(tc);
                    //Referancımızı oluşturduk firebase nodeumuz için
                    //ve verilerimizi hashmapleyerek firebase e atılmaya uygun hale getirdik
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("activeState", false);
                    hashMap.put("payState", 2);
                    //maplenmiş verilerimizi referansımıza value olarak verdik ve verimizi firebase e eklemiş olduk
                    dbRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Fatura Ödendi", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }else{
                    Toast.makeText(PayTheBills.this, "Her yeri doldurun!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}