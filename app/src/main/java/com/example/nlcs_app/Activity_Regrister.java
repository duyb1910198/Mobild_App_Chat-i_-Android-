package com.example.nlcs_app;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.Objects;


public class Activity_Regrister extends AppCompatActivity {

    FirebaseAuth mAuth;

    private EditText passwordE;
    private EditText repeatPasswordE;
    private EditText nameE;
    private EditText emailE;
    private EditText dateE;
    private DatabaseReference data;

    private TextView    nameErrorTv;
    private TextView    dateErrorTv;
    private TextView    passwordErrorTv;
    private TextView    repeatePasswordErrorTv;
    private TextView    emailErrorTv;

    final public String   nameError         = "Tên ít nhất 6 ký tự";
    final public String   dateError         = "Ngày sinh không đúng\n( ví dụ 02/01/1999)";
    final public String   passwordError     = "Mật khẩu ít nhất 8 ký tự";
    final public String   emailError        = "Email không hợp lệ";
    final public String   repeatPasswordError     = "Mật khẩu không khớp";

    private String  nameInfo;
    private String  passwordInfo;
    private String  emailInfo;
    private String  repeatPasswordInfo;
    private Account         user        = new Account();
    private List<Account>   users       = new ArrayList<>();
    private Date            dateInfo    = new Date();

    private int     numberUser  = 0;
    private int     number      = 0;
    private boolean deny        = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regrister);



        data = FirebaseDatabase.getInstance().getReference();

        data.child("account").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //get number account on database
                if( !snapshot.hasChildren()){
                    numberUser = snapshot.getValue(int.class);
                }
                else {
                    Account user = new Account(Objects.requireNonNull(snapshot.getValue(Account.class)));
                    users.add(user);
                    number++;
                }

                if ( users != null){
                    if( users.size() == numberUser + 1){
                        Intent intent = new Intent( Activity_Regrister.this, Activity_Home.class);
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
                                bundle.putInt("positon"                 + i + " contact" + j, users.get(i).getListContact().get(j).getpositon());
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

        mAuth = FirebaseAuth.getInstance();
        Button regristerbutton     =   findViewById(R.id.regristerButtonId);
        regristerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapping();
                regrister();
            }
        });

    }

    private void regrister(){
        if( !deny){
            mAuth.createUserWithEmailAndPassword(emailInfo, passwordInfo)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // regrister success
                                Toast.makeText(Activity_Regrister.this, "Tài khoảng đã được đăng ký.",  // notification
                                        Toast.LENGTH_SHORT).show();

                                // update account data
                                List<String>    list = new ArrayList<String>();     list.add("header");
                                Map<String,Boolean> listf = new HashMap<>();    listf.put("-1", false);
                                List<Contact>   listContact = new ArrayList<>();    listContact.add(new Contact(0,"header",list, "", -1,-1));
                                user = new Account( passwordInfo, nameInfo, emailInfo, "",
                                        dateInfo,numberUser,listf,listContact);
                                signin(numberUser);
                            } else {    // failed
                                if(passwordInfo.length() >= 8)
                                    emailErrorTv.setText(emailError);
                                Toast.makeText(Activity_Regrister.this, " Đăng ký không thành công!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }



    // signin
    public void signin( int id){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(Activity_Regrister.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser userLogin = mAuth.getCurrentUser();
                            updateUser(id);
                        } else {
                            Toast.makeText( Activity_Regrister.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //update data
    public void updateUser( int id) {

        // update number account
        List<String> list = new ArrayList<>();
        data.child("account").child("user" + numberUser).setValue(user);
        data.child("account").child("numberUser").setValue(numberUser + 1);
        //update info user to MainScreen;

    }

    //check info user
    private void checkinfo( String nameInfo, String passwordInfo, String repeatPassworInfo){

        boolean wronglength         = false;
        boolean wasExist            = false;
        boolean wrongFormatDate     = false;
        if( nameInfo.length() < 6 || passwordInfo.length() < 8
                || repeatPassworInfo.compareTo(passwordInfo) != 0){

            if( nameInfo.length() < 6){
                nameErrorTv.setText(nameError);
            }

            if( passwordInfo.length() < 8){
                passwordErrorTv.setText(passwordError);
            }

            if( repeatPassworInfo.compareTo(passwordInfo) != 0){
                repeatePasswordErrorTv.setText(repeatPasswordError);
            }

            wronglength  = true;
        }
        else {
            wronglength = false;
        }

        String  datestr = dateE.getText().toString();
        //  convert to date
        try {
            dateInfo.insertDate(datestr);
            wrongFormatDate = false;
        } catch (Exception e) {
            dateErrorTv.setText(dateError);
            wrongFormatDate = true;
        }

        if( wronglength || wrongFormatDate || wasExist){
            deny = true;
        }
        else {
            deny = false;
        }
    }

    //get data from EditText
    private void mapping(){
        // get EditText
        emailE              =   findViewById(R.id.emailId);
        passwordE           =   findViewById(R.id.passwordId);

        nameE               =   findViewById(R.id.nameId);
        dateE               =   findViewById(R.id.dateId);
        repeatPasswordE     =   findViewById(R.id.repeatPasswordId);

        // Get text info
        nameInfo            =   nameE.getText().toString();
        passwordInfo        =   passwordE.getText().toString();
        emailInfo           =   emailE.getText().toString();
        repeatPasswordInfo  =   repeatPasswordE.getText().toString();
//        test.add("1");//test
//        test.add("2");
//        test.add("3");
//        test.add("4");
//        test.add("5");


        //  Get error TextView
        nameErrorTv             =   findViewById(R.id.nameErrorTv);
        dateErrorTv             =   findViewById(R.id.dateErrorTv);
        passwordErrorTv         =   findViewById(R.id.passwordErrorTv);
        emailErrorTv            =   findViewById(R.id.emailErrorTv);
        repeatePasswordErrorTv  = findViewById(R.id.repeatPasswordIdErrorTv);

        //  Constructor  error TextView
        nameErrorTv.setText("");
        dateErrorTv.setText("");
        passwordErrorTv.setText("");
        emailErrorTv.setText("");
        repeatePasswordErrorTv.setText("");

        // check error enter
        checkinfo( nameInfo, passwordInfo, repeatPasswordInfo);
    }
}