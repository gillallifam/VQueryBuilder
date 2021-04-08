/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Gill
 */
public class Relations {

    String priTable;
    String secTable;
    String prikey;
    String seckey;

    public Relations(String priTable, String secTable, String prikey, String seckey) {
        this.priTable = priTable;
        this.secTable = secTable;
        this.prikey = prikey;
        this.seckey = seckey;
    }

    public String getPriTable() {
        return priTable;
    }

    public void setPriTable(String priTable) {
        this.priTable = priTable;
    }

    public String getSecTable() {
        return secTable;
    }

    public void setSecTable(String secTable) {
        this.secTable = secTable;
    }

    public String getPrikey() {
        return prikey;
    }

    public void setPrikey(String prikey) {
        this.prikey = prikey;
    }

    public String getSeckey() {
        return seckey;
    }

    public void setSeckey(String seckey) {
        this.seckey = seckey;
    }

}
