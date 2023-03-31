package com.example.nlcs_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Login extends AppCompatActivity {

    private DatabaseReference data;
    private FirebaseAuth mAuth;
    private Account user = new Account();
    private List<Account> users = new ArrayList<>();
    private int number = 0;
    private int IDLogin;
    private boolean flag = false;
    private boolean checkEmail = false;
    private boolean checkPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        data = FirebaseDatabase.getInstance().getReference();
        data.child("account").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if( flag){
                    user = new Account(snapshot.getValue(Account.class));
                    users.add(user);
                    number++;
                }
                else{
                    flag = true;
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button regristerButton = findViewById(R.id.regristerButtonId);
        regristerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText passwordEditText = findViewById(R.id.passwordId);
                String password = passwordEditText.getText().toString();

                Intent intent = new Intent( Activity_Login.this, Activity_Regrister.class);

                int REQUEST_CODE = 9;
                startActivity(intent);
            }
        });

        Button loginButton = findViewById(R.id.loginButtonId);
        EditText emailEditText = findViewById(R.id.emailNameId);
        EditText passwordEditText = findViewById(R.id.passwordId);

        loginButton.setOnClickListener(view -> {

            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if( existUser(email, password, users)){
                user = new Account(users.get(IDLogin));
                signin();
            }
            else{
                wrongEmail();
                wrongPassword();
            }
        });
    }

    public void signin(){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(Activity_Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser userLogin = mAuth.getCurrentUser(); //?
                            updateUserLogin();
                        } else {
                            Toast.makeText( Activity_Login.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateUserLogin(){

        Intent intent = new Intent( Activity_Login.this, Activity_Home.class);
        Bundle bundle = new Bundle();

        for ( int i = 0; i < users.size(); i++){
            bundle.putString("password"     + i, users.get(i).getPassword());
            bundle.putString("name"         + i, users.get(i).getName());
            bundle.putString("email"        + i, users.get(i).getEmail());
            bundle.putString("avata"        + i, users.get(i).getAvataUrl());
            bundle.putString("date"         + i, users.get(i).getDate().toString());
            bundle.putInt  ("id"           + i, users.get(i).getId());
            bundle.putInt  ("sizeContact"  + i, users.get(i).getListContact().size());
            for( int j = 0; j < users.get(i).getListContact().size(); j++) {
                bundle.putInt("position"             + i + " contact" + j, users.get(i).getListContact().get(j).getpositon());
                bundle.putString("name"                 + i + " contact" + j, users.get(i).getListContact().get(j).getName());
                bundle.putString("url"                  + i + " contact" + j, users.get(i).getListContact().get(j).getUrl());
                bundle.putStringArrayList("listMess"    + i + " contact" + j, (ArrayList<String>) users.get(i).getListContact().get(j).getListMess());
                bundle.putInt("ID user of contact"      + i + " contact" + j, users.get(i).getListContact().get(j).getIdUser());
                bundle.putInt("ID Contact"              + i + " contact" + j, users.get(i).getListContact().get(j).getIdContact());
            }
            bundle.putSerializable("listFriend"      + i, (Serializable) users.get(i).getListFriend());
        }
        bundle.putInt("LoginID", user.getId());
        bundle.putInt("number", number);

        int REQUEST_CODE = 9;
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void wrongEmail(){
        TextView errorEmail = findViewById(R.id.emailErrorTv);
        if( !checkEmail){
            errorEmail.setText("Người dùng không tồn tại!");
            errorEmail.setTextColor(Color.RED);
        }
        else{
            errorEmail.setText("");
        }
    }

    public void wrongPassword(){
        TextView errorPassword = findViewById(R.id.passwordErrorTv);
        if( checkEmail){
            if( !checkPassword){
                errorPassword.setText("Sai mật khẩu!");
                errorPassword.setTextColor(Color.RED);
            }
        }
        else{
            errorPassword.setText("");
        }
    }

    public boolean existUser( String email, String password, List<Account> users){

        int n = 0;
        while( n < number){
            if( email.trim().compareTo(users.get(n).getEmail()) == 0){
                checkEmail = true;
                if( password.compareTo(users.get(n).getPassword()) == 0){
                    TextView errorEmail = findViewById(R.id.emailErrorTv);
                    TextView errorPassword = findViewById(R.id.passwordErrorTv);
                    errorEmail.setText("");
                    errorPassword.setText("");
                    IDLogin = n;
                    checkPassword = true;
                    return true;
                }
                else{
                    checkPassword = false;
                    return false;   // correct email, wrong password
                }
            }
            else{
                n++;
                checkEmail = false;
            }
        }


        return false;
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 9) {
            if (data.hasExtra("logout"))
                Toast.makeText(this, data.getExtras().getString("logout"), Toast.LENGTH_SHORT).show();
        }
    }
}