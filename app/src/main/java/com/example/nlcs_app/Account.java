package com.example.nlcs_app;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Account {

    private final String  password;
    private String  name;
    private String  email;
    private String  avataUrl;
    private int    id;
    private Map< String, Boolean> listFriend;
    private List<Contact>   listContact;
    private Date    date;


    public Account() {
        password        = "";
        name            = "";
        email           = "";
        date            = new Date();
        avataUrl        = "";
        id              = 0;
        listFriend      = new HashMap<>();
        listContact     = new ArrayList<>();
    }
    public Account( String password, String name, String email, String avataUrl,
                    Date date, int id, Map<String, Boolean> listFriend, List<Contact> listContact) {
        this.password       = password;
        this.name           = name;
        this.email          = email;
        this.avataUrl       = avataUrl;
        this.date           = new Date(date);
        this.id             = id;
        this.listFriend     = listFriend;
        this.listContact    = listContact;
    }

    public Account(Account account) {
        password        = account.password;
        name            = account.name;
        email           = account.email;
        avataUrl        = account.avataUrl;
        date            = new Date(account.date);
        id              = account.id;
        listFriend      = account.listFriend;
        listContact     = account.listContact;
    }

    public void setListFriend( Map<String,Boolean> listFriend) {
        this.listFriend = listFriend;
    }

    public void setListContact(List<Contact> listContact) {
        this.listContact.clear();
        this.listContact.addAll(listContact);
    }

    @NonNull
    @Override
    public String toString() {
        return "Account{" +
                "password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", avataUrl='" + avataUrl + '\'' +
                ", id=" + id +
                ", listFriend=" + listFriend +
                ", listContact=" + listContact +
                ", date=" + date +
                '}';
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAvataUrl(){
        return avataUrl;
    }

    public Date   getDate() {
        return date;
    }

    public int   getId(){
        return id;
    }

    public Map<String, Boolean>   getListFriend(){
        return listFriend;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvataUrl(String avataUrl) {
        this.avataUrl = avataUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(com.example.nlcs_app.Date date) {
        this.date = date;
    }

    public List<Contact> getListContact() {
        return listContact;
    }

}