package com.example.askidafatura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminLogin extends AppCompatActivity {
    private EditText editTextUserName;
    private EditText editTextUserPassword;
    private Button buttonLogin;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String userName;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        editTextUserName = (EditText)findViewById(R.id.editTextUserName);
        editTextUserPassword = (EditText)findViewById(R.id.editTextUserPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser(); // authenticate olan kullaniciyi aliyoruz eger var ise

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName = editTextUserName.getText().toString();//yazdığımız login emailini çekiyoruz gettext ile
                userPassword = editTextUserPassword.getText().toString();// yazdığımız passwordu çekiyoruz gettext ile
                if(userName.isEmpty() || userPassword.isEmpty()){// is empty komutuyla verilerin boş olup olmadığını kontrol ediyoruz

                    Toast.makeText(getApplicationContext(),"Lütfen gerekli alanları doldurunuz!",Toast.LENGTH_SHORT).show();

                }else{
                    login();

                }
            }
        });

    }
    private void login() {
        // mAuth nesnemiz signinwithemailpaswword ile böyle bir kullanıcı var mı yok mu kontrol ediyor hazır bir koddur
        mAuth.signInWithEmailAndPassword(userName,userPassword).addOnCompleteListener(AdminLogin.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){// dönen sonuç basarılı ise
                                Intent i = new Intent(AdminLogin.this, AdminActivity.class);
                                startActivity(i);
                                finish();

                            //giriş başarılı olması halinde bir sonraki sayfaya yönlendiren kod bloğu


                        }
                        else{
                            // hata mesajını ekrana basan kod
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }
}