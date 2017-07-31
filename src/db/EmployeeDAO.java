/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import enums.UserRole;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import models.Empregado.Assalariado;
import models.Empregado.Comissionado;
import models.Empregado.Empregado;
import models.Empregado.GenericEmployee;
import models.Empregado.Horista;
import models.Endereco;
import models.Usuario;

/**
 *
 * @author lucas
 */
public class EmployeeDAO {
    private static Connection conn;
     
    private static void connect() {
        String username = "hux", password = "huxflooder";
        String url = "jdbc:sqlserver://servidorsql;databaseName=sistema";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void disconnect() {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void insertEmployee(Empregado e) {
        SnapshotDAO.createSnapshot("Before inserting user " + e.getName() + " - " + LocalDateTime.now().toString());
        String strsql = "{call usp_createEmployee(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        connect();
        CallableStatement cstmt;
        try {
            cstmt = conn.prepareCall(strsql);
            cstmt.setString(1, e.getName());
            cstmt.setInt(2, e.getType());
            cstmt.setString(3, e.getAccess_user().getUsername());
            cstmt.setString(4, e.getAccess_user().getPassword());
            cstmt.setInt(5, e.getPaymentType());
            cstmt.setDouble(6, e.getPayment());
            cstmt.setObject(7, e.getFee());
            cstmt.setString(8, e.getEndereco().getLogradouro());
            cstmt.setString(9, e.getEndereco().getNumero());
            cstmt.setString(10, e.getEndereco().getComplemento());
            cstmt.setString(11, e.getEndereco().getCidade());
            cstmt.setString(12, e.getEndereco().getUf());
            cstmt.setString(13, e.getEndereco().getCep());
            cstmt.setObject(14, e.getTuid());
            cstmt.setObject(15, e.getTufee());
            cstmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            disconnect();
        }
    }
    public static List<Empregado> getEmployees(Integer filter) {
        connect();
        String strsql = "select e.id, e.employeeName, e.type, e.username, e.pass, t.id as tuid, t.fee as tufee, a.street, a.number, a.complement, a.city, a.uf, a.cep, p.paymentType, p.paymentValue, p.fee as comission" +
" from employee e left join tradeUnion t on e.id = t.employeeId inner join employeeAddress a on e.id = a.employeeId inner join payment p on p.employeeId = e.id";
        /*if (filter != null) {
            strsql = strsql.replace("@filter", " and e.type = " + filter);
        }
        else {
            strsql = strsql.replace("@filter", "");
        }*/
        try {
            ResultSet rs = conn.createStatement().executeQuery(strsql);
            List<Empregado> list = new ArrayList<>();
            while (rs.next()) {
                Empregado e;
                switch (rs.getInt("type")) {
                    case 0:
                        e = new Horista();
                        ((Horista)e).setSalarioPorHora(rs.getDouble("paymentValue"));
                        break;
                    case 1:
                        e = new Assalariado();
                        ((Assalariado)e).setSalarioMensal(rs.getDouble("paymentValue"));
                        break;
                    default:
                        e = new Comissionado();
                        ((Comissionado)e).setTaxaComissao(rs.getDouble("comission"));
                        ((Comissionado)e).setSalarioMensal(rs.getDouble("paymentValue"));
                        break;
                       
                }
                e.setId(rs.getInt("id"));
                e.setName(rs.getString("employeeName"));
                e.setAccess_user(new Usuario(rs.getString("username"), rs.getString("pass"), UserRole.EMPLOYEE));
                Endereco end = new Endereco();
                end.setLogradouro(rs.getString("street"));
                end.setCep(rs.getString("cep"));
                end.setCidade(rs.getString("city"));
                end.setUf(rs.getString("uf"));
                end.setComplemento(rs.getString("complement"));
                end.setNumero(rs.getString("number"));
                e.setEndereco(end);
                e.setPaymentType(rs.getInt("paymentType"));
                e.setTufee(rs.getDouble("tufee"));
                e.setTuid(rs.getInt("tuid"));
                list.add(e);
            }
            return list;
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            disconnect();
        }
        return null;
    }
    public static boolean login(String user, String password) {
        connect();
        String strsql = "select count(*) from employee where username like ? and pass like ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(strsql);
            pstmt.setString(1, user);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) == 1;
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            disconnect();
        }
        return false;
    }
    
    public static void batePonto(Empregado e) {
        connect();
        String strsql = "{call usp_batePonto(?)}";
        CallableStatement cstmt;
        try {
            cstmt = conn.prepareCall(strsql);
            cstmt.setInt(1, e.getId());
            cstmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            disconnect();
        }
    }
    
    public static void lancaVenda(Comissionado c, double valorVenda) {
        
    }
}
