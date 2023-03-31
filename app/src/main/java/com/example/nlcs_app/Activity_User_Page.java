package com.example.nlcs_app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.nlcs_app.databinding.ActivityUserPageBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Activity_User_Page extends AppCompatActivity {

    private final FirebaseDatabase    database    = FirebaseDatabase.getInstance();
    private final DatabaseReference   databaseReference   =   database.getReference("account");


    private Account         friend          =   new Account();
    private Account         user            =   new Account();
    private Contact         contact         =   new Contact();

    private Button          addFriendButton;
    private Button          contactButton;
    private Button          deleteButton;
    private Button          backButton;
    private ActivityResultLauncher<String> takephoto;
    private ActivityUserPageBinding binding;

    private List<String> listMess;

    private String  recentUrl;

    public Activity_User_Page() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding         = ActivityUserPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mapping();


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if( bundle!=null) {

            user.setName(bundle.getString("name", ""));
            user.setAvataUrl(bundle.getString("url", ""));
            user.setId(bundle.getInt("ID", -1));
            listMess = bundle.getStringArrayList("listMess");

            if( listMess.size() == 0) {
                listMess.add("header");
            }

            contact = new Contact(  bundle.getInt("positonContact", -1),
                                    bundle.getString("nameContact", ""),
                                    listMess,
                                    bundle.getString("urlContact", ""),
                                    bundle.getInt("ID user of contact"),
                                    bundle.getInt("ID Contact")
            );

            friend.setId( contact.getIdUser());

            loadData();
        }
    }

    private void mapping() {

        addFriendButton = findViewById(R.id.addFriendUser);

        deleteButton    = findViewById(R.id.delete);

        backButton    = findViewById(R.id.back);

        contactButton = findViewById(R.id.contact);
    }

    private void loadData () {

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                try {
                        if ( Objects.requireNonNull(snapshot.getKey()).compareTo("user" + friend.getId()) == 0) {
                            friend = new Account(Objects.requireNonNull(snapshot.getValue(Account.class)));
                            friend.setName( snapshot.child("name").getValue(String.class));
                            friend.setEmail( snapshot.child("email").getValue(String.class));
                            friend.setDate( snapshot.child("date").getValue(Date.class));
                            recentUrl = friend.getAvataUrl();
                            friend.setAvataUrl( snapshot.child("avataUrl").getValue(String.class));
                            setInfo();
                            setListener();
                        }


                        if ( Objects.requireNonNull(snapshot.getKey()).compareTo("user" + user.getId()) == 0) {

                            user = new Account(snapshot.getValue(Account.class));

                            int i = 0;
                            boolean find = false;
                            for( DataSnapshot dataSnapshot: snapshot.child("listContact").getChildren()) {
                                if( dataSnapshot.getKey().compareTo(Integer.toString(contact.getIdContact())) == 0) {
                                    contact = new Contact(dataSnapshot.getValue(Contact.class));
                                    find = true;
                                    break;
                                }
                                i++;
                            }

                            if ( !find) {
                                contact.setIdContact(i);
                            }
                            setListener();
                        }
//                } catch ( Exception e) {
//                    Toast.makeText(Activity_User_Page.this, "Đã có lỗi xảy ra!!!", Toast.LENGTH_SHORT).show();
//                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                try {

                if ( Objects.requireNonNull(snapshot.getKey()).compareTo("user" + friend.getId()) == 0) {
                    friend.setAvataUrl( snapshot.child("avataUrl").getValue(String.class));

                    friend = new Account(snapshot.getValue(Account.class));

                    setInfo();
                    setListener();
                }

                if ( Objects.requireNonNull(snapshot.getKey()).compareTo("user" + user.getId()) == 0) {
                    user = new Account(snapshot.getValue(Account.class));

                    int i = 0;
                    boolean find = false;
                        for( DataSnapshot dataSnapshot: snapshot.child("listContact").getChildren()) {
                            if( dataSnapshot.getKey().compareTo(Integer.toString(contact.getIdContact())) == 0) {
                                contact = new Contact(dataSnapshot.getValue(Contact.class));
                                find = true;
                                break;
                            }
                            i++;
                        }

                    if ( !find) {
                        contact.setIdContact(i);
                    }
                    setListener();
                }


//                } catch ( Exception e) {
//                    Toast.makeText(Activity_User_Page.this, "Đã có lỗi xảy ra!!!", Toast.LENGTH_SHORT).show();
//                }
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

        databaseReference.child("user" + user.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {




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

    }

    protected void setListener() {
        Log.d( "list my friend", "setListener: " + user.getListFriend());
        setEnableInfo(false);
        setEnableActionDelete(false);
        setAddFriendButton();
        setContactButton();
    }

    private void setAddFriendButton() {

        if( user.getListFriend().get( Integer.toString(friend.getId())) != null){
            if( Boolean.TRUE.equals(user.getListFriend().get(Integer.toString(friend.getId())))) {
                addFriendButton.setText(R.string.delete);
                setEnableInfo( true);
                addFriendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setDeleteAction();
                    }
                });
            } else {
                addFriendButton.setText(R.string.answer);
                addFriendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setEnableActionDelete( true);
                        setAgreeAction();
                    }
                });

            }
        } else {
            if ( friend.getListFriend().get(Integer.toString(user.getId())) != null) {
                if( !Boolean.TRUE.equals(friend.getListFriend().get(Integer.toString(user.getId())))) {
                    addFriendButton.setText(R.string.cancel);
                    addFriendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addFriendButton.setText(R.string.add);
                            deleteFriend();
                            updateList(true, false);
                        }
                    });
                }

            } else {
                addFriendButton.setText(R.string.add);
                addFriendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setAddFriendAction();
                    }

                });

            }
        }
    }

    private void setAddFriendAction() {
        user.getListContact().add(contact);

        if( contact.getListMess().size() == 1) {
            databaseReference.child("user" + user.getId()).child("listContact")
                    .child(Integer.toString(contact.getIdContact())).setValue(contact);
            List<String> listMess = new ArrayList<>();

            for( String s: contact.getListMess()){
                if( s.charAt(0) != 'h') {
                    if( s.charAt(0) == 'm') {
                        char [] mess = new  char[ s.length() - 3];
                        String str;
                        s.getChars(3, s.length(), mess  , 0);
                        str = new String( mess);
                        listMess.add("fr:" + str);
                    } else {
                        char [] mess = new  char[ s.length() - 3];
                        String str;
                        s.getChars(3, s.length(), mess  , 0);
                        str = new String( mess);
                        listMess.add("my:" + str);
                    }
                } else {
                    listMess.add("header");
                }
            }

            Contact t = new Contact( contact.getIdContact(), user.getName(), listMess, user.getAvataUrl(), user.getId(), contact.getpositon());

            databaseReference.child("user" + friend.getId()).child("listContact").child(Integer.toString(contact.getpositon())).setValue(t);
        }
        friend.setListFriend(setAddList(friend.getListFriend(), false, user.getId()));
        updateList(true, false);
    }

    private void setContactButton() {
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_User_Page.this, Activity_Chat.class);
                setToChat( contact, intent);
            }
        });
    }

    protected void setToChat( Contact info, Intent intent) {
        Bundle bundle = new Bundle();

        // get postion of login user contact

        bundle.putInt("ID", user.getId());
        bundle.putString("name", user.getName());
        bundle.putString("url", user.getAvataUrl());

        bundle.putInt("positonContact", info.getpositon());
        bundle.putString("nameContact", info.getName());
        bundle.putString("urlContact", info.getUrl());

        bundle.putStringArrayList("listMess", (ArrayList<String>) info.getListMess());

        bundle.putInt("ID user of contact", info.getIdUser());
        bundle.putInt("ID Contact", info.getIdContact());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setInfo() {

        binding.nameUser.setText(friend.getName());
        binding.emailUser.setText(friend.getEmail());
        binding.dateUser.setText(friend.getDate().toString());
        if (friend.getAvataUrl().compareTo("") != 0) {
            new Adapter_List_Chat.fetchImage(friend.getAvataUrl(), binding.avatarUser).start();
        }
        if( recentUrl.compareTo(friend.getAvataUrl()) != 0) {
            if (friend.getAvataUrl().compareTo("") != 0) {
                recentUrl = friend.getAvataUrl();
                new Adapter_List_Chat.fetchImage(friend.getAvataUrl(), binding.avatarUser).start();
            }
        }


    }

    private void setEnableInfo(boolean b) {

        if ( b) {
            binding.emailUser.setVisibility(View.VISIBLE);
            binding.dateUser.setVisibility(View.VISIBLE);
        } else {
            binding.emailUser.setVisibility(View.INVISIBLE);
            binding.dateUser.setVisibility(View.INVISIBLE);
        }
    }

    private void setAgreeAction() {

        setDeleteText(false);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Boolean> h = new HashMap<>(friend.getListFriend());
                h.put(Integer.toString(user.getId()), true);
                friend.setListFriend(h);

                HashMap<String, Boolean> mh = new HashMap<>(user.getListFriend());
                mh.remove(Integer.toString(friend.getId()));
                mh.put(Integer.toString(friend.getId()), true);
                user.setListFriend(mh);

                setDeleteText( true);
                setEnableActionDelete(false);
                updateList(false, true);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, Boolean> h = new HashMap<>(user.getListFriend());
                h.remove(Integer.toString(friend.getId()));
                user.setListFriend(h);
                setDeleteText( true);
                setEnableActionDelete(false);
                updateList(true, true);
            }
        });



    }

    private void setDeleteText( boolean b) {
        if ( b) {
            deleteButton.setText(R.string.delete);
            backButton.setText(R.string.cancel);
        } else {
            deleteButton.setText(R.string.agree);
            backButton.setText(R.string.refuse);
        }
    }

    private void setDeleteAction() {

        setEnableActionDelete( true);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnableActionDelete(false);
                deleteFriend();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnableActionDelete(false);
            }
        });

    }

    private void deleteFriend() {
        user.setListFriend(setRemoveList(user.getListFriend(), friend.getId()));
        friend.setListFriend(setRemoveList(friend.getListFriend(), user.getId()));

        updateList(false, true);

    }

    private void setEnableActionDelete( boolean b) {
        if ( b) {
            addFriendButton.setVisibility(View.INVISIBLE);
            addFriendButton.setEnabled(false);
            backButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            backButton.setEnabled(true);
            deleteButton.setEnabled(true);
        } else {
            addFriendButton.setVisibility(View.VISIBLE);
            addFriendButton.setEnabled(true);
            backButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
            backButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    private Map<String, Boolean> setAddList( Map<String,Boolean> list, Boolean b, int id) {
        Map<String,Boolean> temp = new HashMap<>();
        temp.clear();
        temp.putAll(list);
        temp.put(Integer.toString(id), b);

        return temp;
    }

    private Map<String,Boolean> setRemoveList( Map<String,Boolean> list, int id) {
        HashMap<String, Boolean> temp = new HashMap<>();
        temp.clear();
        temp.putAll(list);
        temp.remove(Integer.toString(id));
        return temp;
    }

    private void updateList( @Nullable boolean b, boolean d) {
        if( !d) {
            if ( b) {
                databaseReference.child("user" + friend.getId()).child("listFriend")
                        .child(Integer.toString(user.getId())).setValue(friend.getListFriend().get(Integer.toString(user.getId())));
            } else {
                databaseReference.child("user" + user.getId()).child("listFriend")
                        .child(Integer.toString(friend.getId())).setValue(user.getListFriend().get(Integer.toString(friend.getId())));
            }
        } else {
            databaseReference.child("user" + user.getId()).child("listFriend")
                    .child(Integer.toString(friend.getId())).setValue(user.getListFriend().get(Integer.toString(friend.getId())));
            databaseReference.child("user" + friend.getId()).child("listFriend")
                    .child(Integer.toString(user.getId())).setValue(friend.getListFriend().get(Integer.toString(user.getId())));
        }
    }

}