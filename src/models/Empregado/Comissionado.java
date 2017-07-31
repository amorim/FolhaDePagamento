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
public class Comissionado extends Assalariado {
    private double taxaComissao;

    public double getTaxaComissao() {
        return taxaComissao;
    }

    public void setTaxaComissao(double taxaComissao) {
        this.taxaComissao = taxaComissao;
    }
    
    @Override
    public Double getFee() {
        return taxaComissao;
    }
    @Override
    public int getType() {
        return 2;
    }
}
