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
public class Join {

    String fromTable;
    String toTable;
    String type;
    String priField;
    String secField;

    public Join() {

    }

    public Join(String type, String fromTable, String toTable, String pri, String sec) {
        this.fromTable = fromTable;
        this.toTable = toTable;
        this.type = type;
        this.priField = pri;
        this.secField = sec;
    }

    public String getPriField() {
        return priField;
    }

    public void setPriField(String priField) {
        this.priField = priField;
    }

    public String getSecField() {
        return secField;
    }

    public void setSecField(String secField) {
        this.secField = secField;
    }

    public String getFromTable() {
        return fromTable;
    }

    public void setFromTable(String fromTable) {
        this.fromTable = fromTable;
    }

    public String getToTable() {
        return toTable;
    }

    public void setToTable(String toTable) {
        this.toTable = toTable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
