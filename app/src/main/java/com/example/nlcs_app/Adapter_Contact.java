package com.example.nlcs_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Contact extends ClickableAdapter<Adapter_Contact.viewholder>{

    private List<Account>           listUser;
    private Map< String, Boolean>   listFriend          =   new HashMap<>();
    private List<Integer>           listID              =   new ArrayList<>();
    private FirebaseDatabase        database            =   FirebaseDatabase.getInstance();
    private DatabaseReference       databaseReference   =   database.getReference("account");
    private int IDLogin;

    public Adapter_Contact() {
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        super.onBindViewHolder(holder, position);
        if( listID.get(position) == -1) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            return;
        } else {
            Account account = listUser.get(listID.get(position));
            holder.name.setText(account.getName());

            if( account.getAvataUrl().compareTo("") != 0){
                new Adapter_List_Chat.fetchImage(account.getAvataUrl(), holder.avatar).start();
            } else {
                holder.avatar.setImageResource(R.drawable.ic_person);
            }

            if( listFriend.get(Integer.toString(listID.get(position))) != null){

                if (Boolean.TRUE.equals(listFriend.get(Integer.toString(listID.get(position))))) {
                    setEnableAgreeAction( false, holder);
                    setAgreeListener( holder, position);
                } else {
                    setEnableAgreeAction( true, holder);
                    setAgreeListener( holder, position);
                }
            }
        }

    }

    public List<Integer> getListID() {
        return listID;
    }

    protected void setAgreeListener(viewholder holder, int position) {
        holder.agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Boolean> list = new HashMap<>(listFriend);
                String id = new String(Integer.toString(listID.get(position)));
                list.remove( id);
                list.put(id, true);
                setlistFriend(list);
                setEnableAgreeAction(false, holder);
                databaseReference.child("user" + IDLogin).child("listFriend").setValue(listFriend);
                Log.d( "listid", "onClick: " + listUser.get(listID.get(position)));
                Log.d( "listid", "onClick: " + listID.get(position));
                databaseReference.child("user" + listUser.get(listID.get(position)).getId()).child("listFriend")
                        .child( Integer.toString(IDLogin)).setValue(true);
            }
        });

        holder.refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Boolean> list = new HashMap<>(listFriend);
                String id = new String(Integer.toString(listID.get(position)));
                list.remove( id);
                setlistFriend(list);
                setEnableAgreeAction(false, holder);
                notifyDataSetChanged();
                databaseReference.child("user" + IDLogin).child("listFriend").setValue(listFriend);
            }
        });
    }

    private void setlistFriend(HashMap<String, Boolean> list) {
        this.listFriend = list;
    }

    @Override
    public int getItemCount() {
        if( listFriend != null)
            return listFriend.size();
        return 0;
    }

    private void setEnableAgreeAction(boolean b, viewholder holder) {
        if( b) {
            holder.refuse.setVisibility(View.VISIBLE);
            holder.agree.setVisibility(View.VISIBLE);
            holder.refuse.setEnabled(true);
            holder.agree.setEnabled(true);
            Log.d("true", "setEnableAgreeAction: true");
        } else {
            holder.refuse.setVisibility(View.INVISIBLE);
            holder.agree.setVisibility(View.INVISIBLE);
            holder.refuse.setEnabled(false);
            holder.agree.setEnabled(false);
            Log.d("false", "setEnableAgreeAction: false");

        }
    }


    public void setList( int IDLogin, Map<String,Boolean> listFriend, List<Account> listUser) {
        this.listFriend = listFriend;
        this.listUser   = listUser;
        this.IDLogin    = IDLogin;
        setkey();
        notifyDataSetChanged();
    }

    private void setkey() {
        for( String key: listFriend.keySet()){
            listID.add(Integer.parseInt(key));
        }
    }


    public class viewholder extends RecyclerView.ViewHolder {

        private CircleImageView avatar;
        private TextView        name;
        private TextView        info;
        private Button          agree;
        private Button          refuse;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            avatar      =   itemView.findViewById(R.id.avatarUser);
            name        =   itemView.findViewById(R.id.nameContact);
            info        =   itemView.findViewById(R.id.infoContact);
            agree       =   itemView.findViewById(R.id.agree);
            refuse      =   itemView.findViewById(R.id.refuse);
        }
    }


}
