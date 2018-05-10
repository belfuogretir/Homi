package com.example.belfu.homi.Model;

/**
 * Created by belfu on 29.03.2018.
 */

public class Expenses {
    private String gonderen, mesaj, zaman ;
    private Integer cost;
    public Expenses(){}


    public Integer getCost() {
        return cost;
    }


    public Expenses(String gonderen, String mesaj, String zaman, int cost) {
        this.gonderen = gonderen;
        this.mesaj = mesaj;
        this.zaman = zaman;
        this.cost = cost;

    }

    public String getGonderen() {
        return gonderen;
    }

    public void setGonderen(String gonderen) {
        this.gonderen = gonderen;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public String getZaman() {
        return zaman;
    }

    public void setZaman(String zaman) {
        this.zaman = zaman;
    }


}
