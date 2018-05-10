package com.example.belfu.homi.Model;

/**
 * Created by belfu on 8.03.2018.
 */

public class Grup {
    private String name, members,expenses;
        public Grup(){}

        public Grup(String name, String members, String expenses){
            this.name = name;
            this.members = members;
            this.expenses = expenses;
        }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getName() {
            return name;
        }

    public void setName(String name) {
            this.name = name;
        }

    public String getExpenses() {
        return expenses;
    }

    public void setExpenses(String expenses) {
        this.expenses = expenses;
    }
}
