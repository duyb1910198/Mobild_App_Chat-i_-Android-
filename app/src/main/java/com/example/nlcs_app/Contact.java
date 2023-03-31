package com.example.nlcs_app;

import java.util.ArrayList;
import java.util.List;

public class Contact {

    private String          name;
    private String          url;
    private int             idUser;
    private int             idContact;
    private int             positon;
    private List<String>    listMess;

    @Override
    public String toString() {
        return "Contact{" +
                "positon='" + positon + '\'' +
                ", name='" + name + '\'' +
                ", listMess=" + listMess +
                ", url='" + url + '\'' +
                ", idUser=" + idUser +
                ", idContact=" + idContact +
                '}';
    }

    public Contact() {
        this.positon    = -1;
        this.name       = "";
        this.url        = "";
        this.idUser     = -1;
        this.idContact  = -1;
        this.listMess   = new ArrayList<>();
    }

    public Contact(int positon, String name, List<String> listMess, String url, int idUser, int idContact) {
        this.positon = positon;
        this.name = name;
        this.listMess = listMess;
        this.url = url;
        this.idUser     = idUser;
        this.idContact  = idContact;
    }

    public Contact(Contact contact) {
        this.positon   = contact.getpositon();
        this.name       = contact.getName();
        this.listMess   = contact.getListMess();
        this.url        = contact.getUrl();
        this.idUser     = contact.getIdUser();
        this.idContact  = contact.getIdContact();
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdContact() {
        return idContact;
    }

    public void setIdContact(int idContact) {
        this.idContact = idContact;
    }

    public int getpositon() {
        return positon;
    }

    public void setpositon(int positon) {
        this.positon = positon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getListMess() {
        return listMess;
    }

    public void setListMess(List<String> listMess) {
        this.listMess = listMess;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
