/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package folhadepagamento;

import db.DatabaseOperationFailedException;
import db.EmployeeDAO;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import models.Empregado.Empregado;
import util.Util;

/**
 *
 * @author lucas
 */
public class SelectUser extends JFrame {

    /**
     * Creates new form SelectUser
     *
     * @param origin
     */
    List<Empregado> empregados;
    ISelectUser origin;
    Action a;
    public SelectUser(ISelectUser origin) {
        this(null, origin);
    }

    public SelectUser(Integer filter, ISelectUser origin) {
        this(filter, origin, null);
    }
    
    public SelectUser(ISelectUser origin, Action a) {
        this(null, origin, a);
    }
    
    public SelectUser(Integer filter, ISelectUser origin, Action a) {
        initComponents();
        this.a = a;
        this.origin = origin;
        try {
            empregados = EmployeeDAO.getEmployees(filter);
        } catch (DatabaseOperationFailedException ex) {
            Util.displayDatabaseError(ex.getMessage());
            dispose();
        }
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Identification");
        columnNames.add("Name");
        Empregado[] array = empregados.toArray(new Empregado[0]);
        Vector<Vector<Object>> data = new Vector<>();
        empregados.stream().forEach(e -> {
            Vector<Object> vector = new Vector<>();
            vector.add(e.getId());
            vector.add(e.getName());
            data.add(vector);
        });
        jTable1.setModel(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 490, 320));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Select an User");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, -1, -1));

        setSize(new java.awt.Dimension(544, 430));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
        int row = jTable1.getSelectedRow();
        if (row == -1)
            return;
        if (evt.getClickCount() == 2) {
            origin.callback(empregados.get(row), a);
            dispose();
        }
    }//GEN-LAST:event_jTable1MousePressed

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
