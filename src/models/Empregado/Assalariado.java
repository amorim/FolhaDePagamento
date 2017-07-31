/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models.Empregado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import models.Endereco;
import models.Usuario;

/**
 *
 * @author lucas
 */
public class Assalariado extends Empregado {
    private double salarioMensal;
    
    
    public double getSalarioMensal() {
        return salarioMensal;
    }

    public void setSalarioMensal(double salarioMensal) {
        this.salarioMensal = salarioMensal;
    }

    @Override
    public Double getFee() {
        return null;
    }

    @Override
    public Double getPayment() {
        return salarioMensal;
    }

    @Override
    public int getType() {
        return 1;
    }

}
