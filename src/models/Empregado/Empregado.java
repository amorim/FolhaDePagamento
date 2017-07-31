package models.Empregado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import models.Endereco;
import models.Usuario;


public abstract class Empregado {
    
    private int id;
    private String name;
    private Endereco endereco;
    private Usuario access_user;
    private int paymentType;
    private Integer tuid = null;
    private Double tufee = null;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTuid() {
        return tuid;
    }

    public void setTuid(Integer tuid) {
        this.tuid = tuid;
    }

    public Double getTufee() {
        return tufee;
    }

    public void setTufee(Double tufee) {
        this.tufee = tufee;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }
    

    public Usuario getAccess_user() {
        return access_user;
    }

    public void setAccess_user(Usuario access_user) {
        this.access_user = access_user;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the endereco
     */
    public Endereco getEndereco() {
        return endereco;
    }

    /**
     * @param endereco the endereco to set
     */
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
    
    abstract public Double getFee();
    abstract public Double getPayment();
    abstract public int getType();
}
