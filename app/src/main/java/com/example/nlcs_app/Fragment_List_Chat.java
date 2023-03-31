package com.example.nlcs_app;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;


import com.example.nlcs_app.databinding.FragmentListChatBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class Fragment_List_Chat extends Fragment {//implements MenuProvider


    private FirebaseDatabase        database    = FirebaseDatabase.getInstance();
    private FirebaseStorage         storage = FirebaseStorage.getInstance();
    private DatabaseReference       databaseReference   =   database.getReference("account");

    private FragmentListChatBinding binding;
    private Activity_Home           mainScreen;

    private ViewPager2              viewPager2;
    private RecyclerView            recyclerView;
    private Adapter_List_Chat       userAdapter;
    private Toolbar                 toolbar;
    private SearchView              searchView;
    private MenuItem                menuItem;

    private boolean                 onset = false;
    private Account                 userLogin = new Account();
    private List<Account>           listUser;

    public Fragment_List_Chat() {
        // Required empty public constructor
    }

    public static Fragment_List_Chat newInstance() {
        Fragment_List_Chat fragment = new Fragment_List_Chat();
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
        userLogin       = new Account(((Activity_Home) getActivity()).getUserLogin());
        binding         = FragmentListChatBinding.inflate(inflater, container, false);

        setToolbarMenu();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(position == 0 && !onset) { // 0 is number of message page
                    try {
                        recyclerView    =  viewPager2.findViewById(R.id.recyclerViewUser);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(linearLayoutManager);

                        setFragmentToolbar();
                        setListUser();
                        setView();
                        setReLoadDataLitener();

                        RecyclerView.ItemDecoration itemDecoration  =   new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
                        recyclerView.addItemDecoration(itemDecoration);
                        onset = true;
                    } catch (Exception e) {
                        onset = false;
                    }

                }

            }
        });

        return binding.getRoot();
    }

    private void setToolbarMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {

                menuInflater.inflate(R.menu.main_menu, menu);
                SearchManager searchManager   = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
                menuItem    = menu.findItem(R.id.actionLogout);
                menuItem.setVisible(false);
                menuItem    = menu.findItem(R.id.actionSearch);
                searchView  = (SearchView) menuItem.getActionView(); //
                searchView.setIconified(true);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
                searchView.setMaxWidth(Integer.MAX_VALUE);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        userAdapter.getFilter().filter(query);
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        userAdapter.getFilter().filter(newText);
                        return true;
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if( menuItem.getItemId() == R.id.actionSearch){
                    return  true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    public void setFragmentToolbar() {
        toolbar         = mainScreen.getBinding().toolbar;
        toolbar.setTitle("Tin nhắn");
        ((Activity_Home) requireActivity()).setSupportActionBar(toolbar);
    }

    private void setView() {
        userAdapter    = new Adapter_List_Chat(getLisUser(), userLogin);
        recyclerView.setAdapter(userAdapter);
        userAdapter.setClickItemListener(new ClickableAdapter.ClickItemListener() {
            @Override
            public void onClickItem(View v, int position) {

                Contact info  = new Contact(userAdapter.getListContact().get(position));

                    Intent intent = new Intent(getContext(),Activity_Chat.class);
                    setToChat(info, intent);
            }
        });

    }

    protected void setToChat( Contact info, Intent intent) {
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

    private void setReLoadDataLitener() {

        databaseReference.child("user" + userLogin.getId()).child("listContact").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Contact contact = new Contact(snapshot.getValue(Contact.class));
                Log.d("userLogin", "contact11: " + contact);

                // condition 1: null error
                // condition {2,3}: don't add {old,header} contact
                if( contact.getIdContact()  >= userLogin.getListContact().size()){
                    if (contact != null && contact.getIdUser() != -1) {
                        List<Contact> list = new ArrayList<>();
                        list.addAll(userLogin.getListContact());
                        list.add(contact);
                        userLogin.setListContact(list);
                        listUser.set(userLogin.getId(), userLogin);
                        userAdapter.notifyDataSetChanged();
                        mainScreen.setListUser(listUser);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Contact contact = new Contact(snapshot.getValue(Contact.class));
                if (contact != null) {
                    Log.d("userLogin", "onChildChanged: " + userLogin);
                    List<Contact> list = new ArrayList<>();
                    list.addAll(userLogin.getListContact());
                    list.set(contact.getIdContact(), contact);
                    userLogin.setListContact(list);
                    listUser.set(userLogin.getId(), userLogin);
                    Log.d("userLogin", "onChildChanged: " + userLogin);
                    userAdapter.notifyDataSetChanged();
                    mainScreen.setListUser(listUser);
                    String message = contact.getListMess().get( contact.getListMess().size() - 1);

                    if ( message.charAt(0) == 'f') {

                        char [] cMess = new char[message.length() - 3];
                        message.getChars(3, message.length(), cMess, 0);
                        message = new String(cMess);
                        mainScreen.getNotification( message, contact.getName());
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Contact contact = new Contact(snapshot.getValue(Contact.class));
                if( contact != null) {
                    List<Contact> list = new ArrayList<>();
                    list.addAll(userLogin.getListContact());
                    list.remove(contact.getIdContact() - 1);
                    userLogin.setListContact(list);
                    listUser.set(userLogin.getId(),userLogin);
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setListUser() {
        listUser = mainScreen.getListUser();

    }

    public List<Account> getLisUser(){
        return listUser;
    }

    class fetchImage extends Thread{
        Handler handler = new Handler();
        String URL;
        Bitmap bitmap;
        CircleImageView imageView;

        public fetchImage(String url, @Nullable CircleImageView imageView){
            this.URL = url;
            this.imageView = imageView;
        }

        @Override
        public  void run(){

            InputStream inputStream = null;
            try {
                inputStream =  new URL(URL).openStream();
                bitmap  = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView = binding.getRoot().findViewById(R.id.avatarUser);
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    } catch ( Exception e){
                        Log.d("FAIL", "FAIL");
                    }
                }
            });

        }
    }

}