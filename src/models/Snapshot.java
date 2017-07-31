/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author lucas
 */
public class Snapshot {
    private int id;
    private String uuid;
    private String ocurrence;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOcurrence() {
        return ocurrence;
    }

    public void setOcurrence(String ocurrence) {
        this.ocurrence = ocurrence;
    }
    @Override
    public String toString() {
        return this.id + " - " + this.ocurrence;
    }
}
