package com.example.askidafatura;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class PopUpClass {

    //PopupWindow display method

    public void showPopupWindow(final View view) {

        //Hazır Class ingilizce olarak yazılmış açıklama eklemiyorum oyüzden
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.alert_dialog, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler

       /* TextView title = popupView.findViewById(R.id.tv_include);
        title.setText("Askıda Fatura");
        TextView include = popupView.findViewById(R.id.tv_title);
        include.setText("Değerli hayırseverler,\\n\\nBuraya askıda fatura bırakan ihtiyaç sahipleri, 2022 asgari ücretteki artışa paralel olarak hane içi kişi başı geliri maksimum 1417 TL ve altı olduğunu başvuru esnasında beyan eden vatandaşlarımız olup, bu rakamın üzerinde geliri olanların askıya fatura bırakmaları mümkün değildir. Yeni ihtiyaç sahiplerinin listeye girmesi nedeniyle, aşamalı olarak yeni faturalar da sisteme eklenmektedir.\\n\\nAyrıca İstanbul Büyükşehir Belediyesi bu yardımlara yalnızca aracılık yapmakta olup, siz yardımsever dostlar doğrudan yardıma muhtaç kişinin faturasını ilgili kurumuna ödemektesiniz. Ödemeleri kabul eden kurumun veya ödemeye aracılık eden kurumun işlemlerinde zaman zaman aksamalar olabileceğinden, yarım kalan ödeme işlemlerinizi tekrarlamanızı öneriyoruz.\\n\\nTüm zorlukları yenmeyi Birlikte Başaracağız!");
*/
        Button buttonEdit = popupView.findViewById(R.id.btn_okay);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.dismiss();
            }
        });



        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }

}
