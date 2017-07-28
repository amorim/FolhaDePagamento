package models.Empregado;

import models.Endereco;
import models.Usuario;


public class Empregado {

    private String name;
    private Endereco endereco;
    private Usuario access_user;
    
    

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
}
