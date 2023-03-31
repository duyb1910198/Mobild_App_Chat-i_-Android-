package com.example.nlcs_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class Activity_Chat extends AppCompatActivity {

    private FirebaseDatabase database    = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference   =   database.getReference("account");

    private RecyclerView recyclerView;
    private Adapter_Chat  messageAdapter;
    private EditText editTextSend;
    private Button buttonSend;
    private Toolbar toolbar;
    private TextView nameView;
    private CircleImageView avatar;

    private int         id;
    private boolean get = true;
    private String      name;
    private String cKey = "";
    private String cMess = "";
    private String      url;
    private Contact contact;
    private List<Contact> listContact = new ArrayList<>();
    private HashMap<Integer, Map<String,String>> listMessage = new HashMap<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if( bundle!=null) {

            name    =  bundle.getString("name");
            url     =  bundle.getString("url");
            id      =  bundle.getInt("ID");
            contact = new Contact(bundle.getInt("positonContact", 0),
                    bundle.getString("nameContact", ""),
                    new ArrayList<String>(),
                    bundle.getString("urlContact", ""),
                    bundle.getInt("ID user of contact"),
                    bundle.getInt("ID Contact")
            );


            if ( contact.getListMess().size() != 0) {
                listMessage = setListMessage(contact.getListMess());
            }
            mapping();

            setMessListener();


        } else {
            Toast.makeText(this, "Lỗi vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }

        buttonSend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = editTextSend.getText().toString().trim();
                if(mess.compareTo("") != 0){
                    sendMessage(mess);
                }
            }
        });

    }

    public void mapping(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        buttonSend      = findViewById(R.id.buttonSend);
        editTextSend    = findViewById(R.id.textSend);
        toolbar         = findViewById(R.id.toolbarChat);
        nameView        = findViewById(R.id.nameUser);
        avatar          = findViewById(R.id.avatarUser);
        recyclerView    = findViewById(R.id.recyclerMessage);
        recyclerView.setLayoutManager(linearLayoutManager);
        setToobar();

        messageAdapter = new Adapter_Chat( listMessage);
        recyclerView.setAdapter(messageAdapter);
    }

    private void setMessListener() {
        databaseReference.child("user" + id).child("listContact").child(Integer.toString(contact.getIdContact()))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if( snapshot.getKey().compareTo("listMess") == 0) {
                            List<String> mess = new ArrayList<>();
                            for( DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                if( Integer.parseInt(dataSnapshot.getKey()) != 0) {
                                    mess.add(dataSnapshot.getValue(String.class));
                                }
                            }
                            listMessage.clear();
                            listMessage.putAll(setListMessage(mess));

                            contact.setListMess(mess);
                            messageAdapter.setChangeList( listMessage);
                            recyclerView.scrollToPosition(listMessage.size() -1);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if( snapshot.getKey().compareTo("listMess") == 0) {
                            List<String> mess = new ArrayList<>();
                            for( DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                if( Integer.parseInt(dataSnapshot.getKey()) != 0) {
                                    mess.add(dataSnapshot.getValue(String.class));
                                }
                            }
                            listMessage.clear();
                            listMessage.putAll(setListMessage(mess));
                            contact.setListMess(mess);
                            messageAdapter.setChangeList( listMessage);
                            recyclerView.scrollToPosition(listMessage.size() -1);
                        }
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
    }

    public void sendMessage(String mess) {

        if( TextUtils.isEmpty(mess)){
            return;
        }

        String size;


        contact.getListMess().add(0, "header");

        databaseReference.child("user" + id)
                .child("listContact")
                .child( Integer.toString(contact.getIdContact()))
                .setValue( setUpdateContact(contact, "my:" + mess));

        //set my contact
        List<String> list = new ArrayList<>();
        for( String s: contact.getListMess()) {
            if( s.charAt(0) != 'h') {
                setKey_Mess(s);
                if(cKey.compareTo("my") == 0) {
                    s = "fr:" + cMess;
                    list.add(s);
                } else {
                    s = "my:" + cMess;
                    list.add(s);
                }
            } else {
                list.add(s);
            }
        }


        Contact t = new Contact( contact.getIdContact(), name, list, url, id, contact.getpositon());
        databaseReference.child("user" + contact.getIdUser())
                .child("listContact")
                .child( Integer.toString(contact.getpositon()))
                .setValue( t);

        editTextSend.setText("");

    }

    private void setToobar() {

        new Adapter_List_Chat.fetchImage( contact.getUrl(), avatar).start();
        nameView.setText(contact.getName());

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( Activity_Chat.this, Activity_User_Page.class);
                setToUserPage( contact, intent);
            }
        });
    }

    protected void setToUserPage( Contact info, Intent intent) {
        Bundle bundle = new Bundle();

        bundle.putInt("ID", id);
        bundle.putString("name", name);
        bundle.putString("url", url);

        bundle.putInt("positonContact", info.getpositon());
        bundle.putString("nameContact", info.getName());
        bundle.putString("urlContact", info.getUrl());

        bundle.putStringArrayList("listMess", (ArrayList<String>) info.getListMess());

        bundle.putInt("ID user of contact", info.getIdUser());
        bundle.putInt("ID Contact", info.getIdContact());
        intent.putExtras(bundle);
        startActivity(intent);
    }


    public Contact setUpdateContact( Contact setContact, String message){

        List<String>    list = new ArrayList<>();
        Contact updateContact = new Contact();

        if( setContact.getListMess() != null) {
            list = setContact.getListMess();
        }
        list.add( message);
        updateContact = new Contact(    setContact.getpositon(), setContact.getName(),  list,
                                        setContact.getUrl(),     setContact.getIdUser(),setContact.getIdContact());
        return updateContact;
    }

    private void setKey_Mess(String s ) {
        int length = s.length();
        char []     key    = new char[2];
        char []     mess     = new char[length - 3];

        s.getChars(0, 2, key  , 0);
        s.getChars(3, length, mess  , 0);
        cKey = new String(key);
        cMess = new String(mess);
    }

    public HashMap<Integer, Map<String,String>> setListMessage( List<String> list) {
        HashMap<Integer, Map<String,String>> listHas = new HashMap<>();

        if( list != null){
                int i = 0;
                for( String s: list) {
                    if( s != null){
                        if(s.charAt(0) != 'h') {
                            HashMap<String,String> frame = new HashMap<String,String>();
                            // header
                            setKey_Mess(s);
                            frame.put(cKey,cMess);
                            listHas.put(i,frame);
                            i++;
                        } else {
                        }
                    }
                }
        }
        return listHas;
    }

}