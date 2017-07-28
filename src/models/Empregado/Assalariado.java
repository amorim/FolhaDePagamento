/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models.Empregado;

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
}
