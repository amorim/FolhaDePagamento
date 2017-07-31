/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models.Empregado;

/**
 *
 * @author lucas
 */
public class GenericEmployee extends Empregado {

    @Override
    public Double getFee() {
        return -1.0;
    }

    @Override
    public Double getPayment() {
        return -1.0;
    }

    @Override
    public int getType() {
        return -1;
    }
    
}
