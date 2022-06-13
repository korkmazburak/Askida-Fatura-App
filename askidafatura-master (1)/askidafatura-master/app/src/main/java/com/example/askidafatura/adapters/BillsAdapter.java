package com.example.askidafatura.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askidafatura.R;
import com.example.askidafatura.models.Bills;

import java.util.ArrayList;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Bills> bagArrayList;
    private boolean isAdmin;

    public BillsAdapter(Context context, ArrayList<Bills> bagArrayList,boolean isAdmin) {
        this.context = context;
        this.bagArrayList = bagArrayList;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public BillsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Recylerviewin her bir rowu için hangi layoutu kullanacağımızı belirlediğimzi yer
        LayoutInflater inflater=LayoutInflater.from(context);
            View view =inflater.inflate(R.layout.card_info_bills,parent,false);
            return new BillsAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull BillsAdapter.ViewHolder holder, int position) {
        //layoutumzudaki her bir texte gönderdiğimiz dizilerin o rowunun pozisyonundaki değerleri atıyoruz yani Ürünlerim tablomuzu oluşturuyourz

        holder.tv_type.setText(String.valueOf(bagArrayList.get(position).type));
        holder.tv_cUid.setText(String.valueOf(bagArrayList.get(position).cUid));
        holder.tv_city.setText(String.valueOf(bagArrayList.get(position).city));
        holder.tv_cost.setText(String.valueOf(bagArrayList.get(position).cost)+"TL");
        if(bagArrayList.get(position).type.equals("Elektrik")){
            holder.payment_layout.setBackground(ContextCompat.getDrawable(context, R.drawable.main_header_selector));
        }else if(bagArrayList.get(position).type.equals("Su")){
            holder.payment_layout.setBackground(ContextCompat.getDrawable(context, R.drawable.main_header_selector3));

        }else{
            holder.payment_layout.setBackground(ContextCompat.getDrawable(context, R.drawable.main_header_selector2));
        }
        if(bagArrayList.get(position).activeState==true){
            holder.tv_state.setText("Hemen Öde");
        }else{
            holder.tv_state.setText("Etkinleştir");
        }
        if(isAdmin){
            holder.tv_tc.setText(String.valueOf(bagArrayList.get(position).tc));
            holder.tv_tc.setVisibility(View.VISIBLE);
            holder.tv_no.setText("Fatura No:"+bagArrayList.get(position).no);
            holder.tv_no.setVisibility(View.VISIBLE);
        }





    }

    @Override
    public int getItemCount() {
        return bagArrayList.size();
    }//loop değerleri kadar döndürür

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Define textviews
        TextView tv_type,tv_cost,tv_city,tv_cUid,tv_state,tv_tc,tv_no;
        LinearLayout payment_layout;


        ViewHolder(View itemView) {//DEFINE TEXTVİEWS AND BUTTON WITH XML FILES WITH ID
            super(itemView);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_tc = itemView.findViewById(R.id.tv_tc);
            tv_cost = itemView.findViewById(R.id.tv_cost);
            tv_city = itemView.findViewById(R.id.tv_city);
            tv_cUid = itemView.findViewById(R.id.tv_cUid);
            tv_state = itemView.findViewById(R.id.tv_state);
            tv_no = itemView.findViewById(R.id.tv_no);
            payment_layout = itemView.findViewById(R.id.payment_layout_old);


        }

    }

    public Bills getItemName(int position) {
        return bagArrayList.get(position);
    }

}

