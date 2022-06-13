package com.example.askidafatura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.askidafatura.adapters.BillsAdapter;
import com.example.askidafatura.models.Bills;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Integer> totalPaidPrice;//toplam ödenen miktarı tuttuğumuz liste
    ImageButton ibtn_info;//info kısmını açan button
    RangeSlider slider;//fiyat aralığını seçtiğimiz slider
    List<Float> sliderValues;//sldierdan gelen verileri tutacağımız liste
    TextView tv_sliderStart,tv_sliderEnd,tv_paidBills,tv_waitedBills,tv_paidCost;
    RecyclerView rv_billsInfo;
    ArrayList<Bills> billsArrayList;//içinde faturaları tutan liste bunu doldurup ekrana basıyoruz
    Bills bills;//specifik olarak fatura çektiğimiz nesne recylerview tıklandığında çekiyoruz
    Button btn_filter;
    EditText et_search;
    int selectedIndex=0;
    BillsAdapter billsAdapter;
    private FirebaseDatabase db;// Database e ulaşmamızı sağlayan sınıftan nesne oluşturduk
    private DatabaseReference dbRef; // databasede hangi path e ulaşacağımızı belirlediğimiz sınıftan nesne oluşturduk
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        slider=findViewById(R.id.slider);
        ibtn_info=findViewById(R.id.ibtn_info);
        et_search=findViewById(R.id.et_search);
        db = FirebaseDatabase.getInstance();// veritabanına erişim sağladık
        // tüm widget tanımlamaları burada textview edittext button her şeyi tanımlıyoruz her sayfada yapıyoruz bunu
        tv_sliderStart=findViewById(R.id.tv_sliderStart);
        tv_sliderEnd=findViewById(R.id.tv_sliderEnd);
        tv_paidBills=findViewById(R.id.tv_paidBills);
        tv_waitedBills=findViewById(R.id.tv_waitedBills);
        tv_paidCost=findViewById(R.id.tv_paidCost);
        btn_filter=findViewById(R.id.btn_filter);
        sliderValues = new ArrayList<>();
        slider.setValues(50f,350f);//range sliderın default değerlerini verdik
        tv_sliderStart.setText("50TL");//range in ilk verdiğimiz default değerlerini textelre yazdık
        tv_sliderEnd.setText("350TL");
        rv_billsInfo = findViewById(R.id.rv_billsInfo);
        billsArrayList = new ArrayList<>();
        totalPaidPrice = new ArrayList<>();
        getPaidInfoBills();
        StoreDataList();
        rv_billsInfo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        sliderValues=slider.getValues();
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //filtreleye tıklayınca yazılan search textini alıp başlangıç ve bitiş miktarı ile birlikte
                //fonksiyona gönderip filtreleme yapıyoıruz
                String city=et_search.getText().toString().toLowerCase();
                StoreDataList(sliderValues.get(1).intValue(),sliderValues.get(0).intValue(),city);
            }
        });

        slider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                //Sliderın onchange fonksiyonu aralığı değiştridiğimizde yeni aralıkları alıyoruz ve textlere veriyoruz
                sliderValues=slider.getValues();
                tv_sliderStart.setText(sliderValues.get(0)+"TL");
                tv_sliderEnd.setText(sliderValues.get(1)+"TL");
            }
        });

        rv_billsInfo.addOnItemTouchListener(new AdminActivity.RecyclerItemClickListener(getApplicationContext(), rv_billsInfo, new AdminActivity.RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Bu fonksiyon recyclerviewın onclick fonksiyonu
                 bills = billsAdapter.getItemName(position);//tıkladığımız liste elemanının tüm özelliklerini alıyoruz
                selectIslem();// işlem yap penceresini açıyoruz
            }

            @Override
            public void onLongItemClick(View view, int position) {
                //kullanmıyoruz
                selectedIndex = position;

            }
        }));

        ibtn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //info buttonu pop ı açıyor
                PopUpClass popUpClass = new PopUpClass();
                popUpClass.showPopupWindow(view);
            }
        });

        //Alt havigation bölümünün kodları hangisine tıklandığında geçiş yapılacağını ve bu sayfa açıldığında hangisinin selected olduğunu yazdığımız yer
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.pay);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.pay:
                        return true;
                    case R.id.create:
                        startActivity(new Intent(getApplicationContext(), HangingBillActivity.class));
                        overridePendingTransition(0,0);
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
    private void StoreDataList() {
        //referance a göre tüm faturaları çektiğimiz yer
        dbRef = db.getReference("Bills");
        dbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                billsArrayList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                       Bills task = ds.getValue(Bills.class);
                  if(task.activeState==true){
                      billsArrayList.add(task);
                    }
                }
                billsAdapter = new BillsAdapter(getApplicationContext(), billsArrayList,false);
                billsAdapter.notifyDataSetChanged();
                rv_billsInfo.setAdapter(billsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void StoreDataList(int max, int min,String city) {
        //referance a göre Filtreli faturaları çektiğimiz yer
        dbRef = db.getReference("Bills");
        dbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                billsArrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Bills task = ds.getValue(Bills.class);
                    if(task.activeState==true&&task.cost>min&&task.cost<max&&task.city.toLowerCase().contains(city)){
                        billsArrayList.add(task);
                    }
                }

                billsAdapter = new BillsAdapter(getApplicationContext(), billsArrayList,false);
                billsAdapter.notifyDataSetChanged();
                rv_billsInfo.setAdapter(billsAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void getPaidInfoBills() {
        //Ödene faturan bekleyen fatura gibi bilgileri doldurduğumuz yer
        dbRef = db.getReference("Bills");
        dbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalPaidPrice.clear();
                int paidBills=0;
                int waitedBills=0;
                int totalPrice=0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Bills task = ds.getValue(Bills.class);
                    if(task.payState!=0){
                        if(task.payState==1){
                            waitedBills ++;
                            totalPaidPrice.add(task.askingPay);
                        }else if(task.payState==2){
                            totalPaidPrice.add(task.cost+task.askingPay);
                            paidBills++;
                        }

                    }else{
                        waitedBills++;
                    }
                }
                for(int i=0; i<totalPaidPrice.size(); i++){
                    totalPrice=totalPaidPrice.get(i)+totalPrice;
                }
                tv_paidCost.setText(String.valueOf(totalPrice));
                tv_paidBills.setText(String.valueOf(paidBills));
                tv_waitedBills.setText(String.valueOf(waitedBills));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void selectIslem() {
        //işlem fonkisyonu alertdialog olarak açılır seçileni yapar
        final CharSequence[] options = { "Tamamını Öde","Kısmen Öde"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Tamamını Öde"))
                {
                    //İçinde veriler ile birlikte ödeme sayfasına gidiyoruz
                    Intent intent=new Intent(MainActivity.this,PayTheBills.class);
                    intent.putExtra("tc", bills.tc);
                    intent.putExtra("cost", bills.cost);
                    startActivity(intent);
                    dialog.dismiss();
                }
                else if (options[item].equals("Kısmen Öde"))
                {
                    //İçinde veriler ile birlikte Kısmi ödeme sayfasına gidiyoruz
                    Intent intent=new Intent(MainActivity.this,PayAsAskingBills.class);
                    intent.putExtra("tc", bills.tc);
                    intent.putExtra("cost", bills.cost);
                    startActivity(intent);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }
    //Admin logine gitmemizi sağlayan menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.admin:
                Intent intent = new Intent(MainActivity.this, AdminLogin.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}