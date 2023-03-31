package com.example.nlcs_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nlcs_app.databinding.FragmentContactBinding;
import com.example.nlcs_app.databinding.FragmentListChatBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Fragment_Contact extends Fragment {
    private FirebaseDatabase database    = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference   =   database.getReference("account");
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private Activity_Home mainScreen;
    private FragmentContactBinding binding;
    private Toolbar toolbar;

    private ViewPager2 viewPager2;
    private RecyclerView recyclerView;
    private Adapter_Contact contactAdapter;

    private int id;
    private boolean onset = true;
    private Account userLogin = new Account();

    public Fragment_Contact() {
        // Required empty public constructor
    }

    public static Fragment_Contact newInstance(String param1, String param2) {
        Fragment_Contact fragment = new Fragment_Contact();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainScreen      = (Activity_Home) getActivity();
        viewPager2      = mainScreen.getViewPager();
        id              = ((Activity_Home) getActivity()).getid();
        binding         = FragmentContactBinding.inflate(inflater, container, false);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(position == 1 && onset) { // 0 is number of message page
                        recyclerView    =  viewPager2.findViewById(R.id.recyclerViewContact);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(linearLayoutManager);

                        RecyclerView.ItemDecoration itemDecoration  =   new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
                        recyclerView.addItemDecoration(itemDecoration);
                        setReLoadDataLitener();

                    onset = false;
                }

            }
        });

        return binding.getRoot();
    }

    private void setView() {

        contactAdapter    = new Adapter_Contact();
        contactAdapter.setList( userLogin.getId(), userLogin.getListFriend(), mainScreen.getListUser());
        recyclerView.setAdapter(contactAdapter);
        Log.d( " úe", "setView: " + userLogin);
        setListener();
    }

    private void setListener() {
        contactAdapter.setClickItemListener(new ClickableAdapter.ClickItemListener() {
            @Override
            public void onClickItem(View v, int position) {
                Contact info = new Contact();
                boolean find = false;
                for( Contact contact: mainScreen.getUserLogin().getListContact()){
                    if( contactAdapter.getListID().get(position) == contact.getIdUser()){
                        info  = new Contact(contact);
                        find  = true;
                        break;
                    }
                }
                if( !find){
                    Account account = new Account(mainScreen.getListUser().get(contactAdapter.getListID().get(position)));
                    info = new Contact( userLogin.getListContact().size(),
                                        account.getName(),
                                        (List<String>) new ArrayList<String>(),
                                        account.getAvataUrl(),
                                        account.getId(),
                                account.getListContact().size());
                }
                Intent intent = new Intent(getContext(),Activity_User_Page.class);
                setToUserPage(info, intent);
            }
        });
    }

    private void setToUserPage(Contact info, Intent intent) {
        Bundle bundle = new Bundle();

        bundle.putInt("ID", userLogin.getId());
        bundle.putString("name", userLogin.getName());
        bundle.putString("url", userLogin.getAvataUrl());

        bundle.putInt("positonContact", info.getpositon());
        bundle.putString("nameContact", info.getName());
        bundle.putString("urlContact", info.getUrl());

        bundle.putStringArrayList("listMess", (ArrayList<String>) info.getListMess());

        bundle.putInt("ID user of contact", info.getIdUser());
        bundle.putInt("ID Contact", info.getIdContact());
        intent.putExtras(bundle);
        startActivity(intent);
    }


    public void setFragmentToolbar() {
        toolbar = mainScreen.getBinding().toolbar;
    }

    private void setReLoadDataLitener() {

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if ( Objects.requireNonNull(snapshot.getKey()).compareTo("user" + id) == 0) {
                    userLogin = new Account(snapshot.getValue(Account.class));
                    setView();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if ( Objects.requireNonNull(snapshot.getKey()).compareTo("user" + id) == 0) {
                    userLogin = new Account(snapshot.getValue(Account.class));
                    Log.d( "lifriend", "onChildChanged: " + userLogin.getListFriend());
                    contactAdapter.setList( userLogin.getId(), userLogin.getListFriend(), mainScreen.getListUser());
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

}