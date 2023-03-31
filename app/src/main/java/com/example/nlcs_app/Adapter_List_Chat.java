package com.example.nlcs_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nlcs_app.databinding.FragmentListChatBinding;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_List_Chat extends ClickableAdapter<Adapter_List_Chat.viewHodlder> implements Filterable  {

    private List<Account>           listAccount;
    private List<Contact>           listContact;
    private List<Contact>           listContactOld;
    private Account                 user;
    private FragmentListChatBinding binding;

    public Adapter_List_Chat(List<Account> listAccount, Account userLogin) {
        this.user           =   new Account(userLogin);
        this.listAccount    =   listAccount;
        this.listContact    =   this.user.getListContact();
        this.listContactOld =   this.listContact;
        Log.d("list contact", "Adapter_List_Chat: " + listContact);
    }

    @NonNull
    @Override
    public viewHodlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent,false);
        return new viewHodlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHodlder holder, int position) {
        super.onBindViewHolder(holder,position);
        if( listContact.get(position) == null || listContact.get(position).getIdUser() == -1){
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            return;
        }
        Log.d("listContact--", "Adapter_List_Chat: "+ listContact);

        if( listContact.get(position).getIdUser() != -1)
            setViewHolder( holder, position);
    }

    private void setViewHolder( viewHodlder holder, int position) {

        holder.name.setText(listContact.get(position).getName());
        Contact contact =  listContact.get(position);
        String message = "";
        if( contact.getListMess().size() != 0){
            message = contact.getListMess().get(contact.getListMess().size() -1);
            if ( message.charAt(0) != 'h') {
                if( message.charAt(0) == 'm')
                    holder.lastlistChat.setText( "Bạn" + ": "+ getMessage(message));
                else
                    holder.lastlistChat.setText( contact.getName() + ": "+ getMessage(message));
            }
        } else {
            holder.lastlistChat.setText("");
        }

        if( contact.getUrl().compareTo("") != 0){
            new fetchImage(contact.getUrl(), holder.circleImageView).start();
        } else {
            holder.circleImageView.setImageResource(R.drawable.ic_person);
        }


    }

    private String getMessage(String message) {

        char [] value = new char[message.length() - 3];
        message.getChars(3, message.length(), value, 0);
        return new String(value);
    }

    @Override
    public int getItemCount() {
        if ( listContact != null)
            return listContact.size()   ;
        return 0;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String search = (constraint.toString()).toLowerCase().trim();
                if( search.isEmpty()) {
                    listContact = listContactOld;
                } else {
                    listContact = findContact( search); 
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listContact;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listContact = (List<Contact>) results.values;
                notifyDataSetChanged();
            }
        };

    }

    private List<Contact> findContact(String search) {

        List<Contact> list = new ArrayList<>();
        List<Account> listTemp  = new ArrayList<>();
        List<Integer> listInt = new ArrayList<>();
        listTemp.addAll(listAccount);

        for( Contact contact: listContact) {
            if( contact.getName().toLowerCase().contains(search)){
                list.add(contact);
            }
            listInt.add(contact.getIdUser());
        }


        Contact contact;
        int size = - 1;
        List<String> listChatTemp = new ArrayList<>();
        for ( Account account: listTemp) {
            if( account.getName().toLowerCase().contains(search)) {
                if( account.getId() != user.getId()){
                    boolean check = true;
                        size    =   account.getListContact().size();
                    for( Integer i: listInt) {
                        if( account.getId() == i) {
                            check = false;
                            break;
                        }
                    }
                    if( check) {
                        contact = new Contact( size, account.getName(), listChatTemp, account.getAvataUrl(), account.getId(), user.getListContact().size());
                        list.add(contact);
                    }
                }
            }
        }
        return  list;
    }

    public class viewHodlder extends RecyclerView.ViewHolder{
        private CircleImageView circleImageView;
        private TextView name;
        private TextView lastlistChat;

        public viewHodlder(@NonNull View itemView) {
            super(itemView);
            this.circleImageView = itemView.findViewById(R.id.avatarUser);
            this.name = itemView.findViewById(R.id.nameUser);
            this.lastlistChat = itemView.findViewById(R.id.lastMessage);
        }
    }

    protected List<Contact>   getListContact(){
        return listContact;
    }

    static class fetchImage extends Thread {
        Handler handler = new Handler();
        String URL;
        Bitmap bitmap;
        CircleImageView imageView;

        public fetchImage(String url, @Nullable CircleImageView imageView) {
            this.URL = url;
            if( imageView != null) {
                this.imageView = imageView;
            }
        }

        @Override
        public void run() {

            InputStream inputStream = null;
            try {
                inputStream = new URL(URL).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if( imageView!= null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            imageView.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            Log.d("FAIL", "FAIL");
                        }
                    }
                });
            }
        }
    }
}
