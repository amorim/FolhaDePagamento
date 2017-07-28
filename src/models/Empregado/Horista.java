/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models.Empregado;

import java.util.ArrayList;
import models.CartaoDePonto;

/**
 *
 * @author lucas
 */
public class Horista extends Empregado {
    private double salarioPorHora;
    private ArrayList<CartaoDePonto> cartoes;
    
    public double getSalarioPorHora() {
        return salarioPorHora;
    }

    public void setSalarioPorHora(double salarioPorHora) {
        this.salarioPorHora = salarioPorHora;
    }
}
