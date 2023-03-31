package com.example.nlcs_app;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.nlcs_app.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Activity_Home extends AppCompatActivity {

    public static final int REQUEST_CODE_NOTIFY = 592;
    public static final int REQUEST_CODE_STORAGE = 590;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = database.getReference("account");

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private ActivityHomeBinding binding;


    private long numberUser;
    private int IDLogin;
    private boolean check   =   false;
    private Account userLogin = new Account();
    private List<Account> listUser = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        getData(intent, bundle);
        checkNotificationsPermission();

    }


    public void checkNotificationsPermission() {
        if (checkVersion()) {
            if ( !check) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions( new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFY);
                }
                check = true;
            }
        }
    }

    public boolean  getImage(){
        if (checkVersion()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
                return false;
            }
        }
        return true;
    }
    public synchronized void getData (Intent intent, Bundle bundle) {
        if (bundle != null) {
            Date date = new Date();
            numberUser = bundle.getInt("number");
            IDLogin = bundle.getInt("LoginID");
            for (int i = 0; i < numberUser; i++) {
                Account account;
                List<Contact> listContact = new ArrayList<>();
                date.insertDate(bundle.getString("date" + i));
                int size = bundle.getInt("sizeContact" + i);

                for (int j = 0; j < size; j++)
                    if (bundle.getInt("ID user of contact" + i + " contact" + j) != -1) {
                        Contact contact = new Contact(
                                bundle.getInt("position" + i + " contact" + j),
                                bundle.getString("name" + i + " contact" + j),
                                bundle.getStringArrayList("listMess" + i + " contact" + j),
                                bundle.getString("url" + i + " contact" + j),
                                bundle.getInt("ID user of contact" + i + " contact" + j),
                                bundle.getInt("ID Contact" + i + " contact" + j)
                        );
                        listContact.add(contact);
                    }


                account = new Account(
                        bundle.getString("password" + i, ""),
                        bundle.getString("name" + i, ""),
                        bundle.getString("email" + i, ""),
                        bundle.getString("avata" + i, ""),
                        date,
                        bundle.getInt("id" + i, 0),
                        (Map<String, Boolean>) bundle.getSerializable("listFriend" + i),
                        listContact
                );


                listUser.add(account);
            }
            userLogin = new Account(listUser.get(IDLogin));
            initActivity();
        } else {
            getUserFirebase();
        }
    }

    private void initActivity() {
        Toast.makeText(this, "Xin chào " + userLogin.getName(), Toast.LENGTH_SHORT).show();
        mapping();
        setUpViewPager();
        setUpIcon();
    }

    private void getUserFirebase() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (snapshot.hasChildren()) {
                        Account account = new Account(Objects.requireNonNull(snapshot.getValue(Account.class)));
                        listUser.add(account);
                        if (account.getEmail().compareTo(email) == 0) {
                            IDLogin = account.getId();
                            userLogin = new Account(account);
                        }
                        if (account.getId() == numberUser - 1) {
                            initActivity();
                        }
                    } else {
                        numberUser = snapshot.getValue(Integer.class);
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

        }
    }

    public List<Account> getListUser() {
        return listUser;
    }

    @SuppressLint("NonConstantResourceId")
    public void setUpIcon() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.actionSms:
                    viewPager.setCurrentItem(0);
                    break;

                case R.id.actionListFriend:
                    viewPager.setCurrentItem(1);
                    break;

                case R.id.actionPrivate:
                    viewPager.setCurrentItem(2);
                    break;
            }
            return true;
        });
    }

    public void setUpViewPager() {

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.actionSms).setChecked(true);
                        setFragmentToolbar(" Tin nhắn");
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.actionListFriend).setChecked(true);
                        setFragmentToolbar(" Danh sách liên lạc");
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.actionPrivate).setChecked(true);
                        setFragmentToolbar(" Thông tin cá nhân");
                        break;
                }

            }
        });
    }

    public void setFragmentToolbar(String title) {
        Toolbar toolbar = binding.toolbar;
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
    }

    public void setListUser(List<Account> listUser) {
        this.listUser.addAll(listUser);
    }

    public Account getUserLogin() {
        return userLogin;
    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }

    public @NonNull ActivityHomeBinding getBinding() {
        return binding;
    }

    public void mapping() {
        viewPager = findViewById(R.id.viewPager);
        Adapter_Pager viewPagerAdapter = new Adapter_Pager(this);
        viewPager.setAdapter(viewPagerAdapter);
        bottomNavigationView = findViewById(R.id.bottomNav);
        viewPager.setOffscreenPageLimit(2);
    }

    private void setNewMessageListener() {
        databaseReference.child("user" + userLogin.getId()).child("listContact").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Contact contact = new Contact(Objects.requireNonNull(snapshot.getValue(Contact.class)));
                userLogin.getListContact().set(contact.getIdContact(), contact);
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

        databaseReference.child("user" + userLogin.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                userLogin = new Account(snapshot.getValue(Account.class));
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


    protected void getNotification(String mess, String name) {

        Uri notification_sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_sound);
        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setContentTitle(name)
                .setContentText(mess)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mess))
                .setSmallIcon(R.drawable.git)
                .setColor(getResources().getColor(R.color.teal_700))
                .setSound(notification_sound)
                .build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManagerCompat.notify(1, notification);// add alert to have agree of user
        }
    }

    public boolean checkVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_NOTIFY || requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Vui lòng cấp quyền để trãi nghiệm dịch vụ tốt nhất", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "full", Toast.LENGTH_SHORT).show();
            }
        }
    }





    public int getid() {
        return userLogin.getId();
    }

    @Override
    public void finish(){
        Intent data = new Intent( Activity_Home.this, Activity_Login.class);
        data.putExtra("logout", "Đã đăng xuất");
        setResult(RESULT_OK, data);
        startActivity(data);
        super.finish();
    }
}