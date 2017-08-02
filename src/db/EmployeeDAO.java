/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import enums.UserRole;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import models.Empregado.Assalariado;
import models.Empregado.Comissionado;
import models.Empregado.Empregado;
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
        String url = "jdbc:sqlserver://casaamorim.no-ip.biz;databaseName=sistema";
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

    public static void insertOrUpdateEmployee(Empregado e) {

        String strsql = "{call usp_createUpdateEmployee(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        connect();
        CallableStatement cstmt;
        try {
            cstmt = conn.prepareCall(strsql);
            if (e.getId() == null) {
                cstmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                cstmt.setObject(1, e.getId());
            }
            cstmt.setString(2, e.getName());
            cstmt.setInt(3, e.getType());
            cstmt.setString(4, e.getAccess_user().getUsername());
            cstmt.setString(5, e.getAccess_user().getPassword());
            cstmt.setInt(6, e.getPaymentType());
            cstmt.setDouble(7, e.getPayment());
            cstmt.setObject(8, e.getFee());
            cstmt.setString(9, e.getEndereco().getLogradouro());
            cstmt.setString(10, e.getEndereco().getNumero());
            cstmt.setString(11, e.getEndereco().getComplemento());
            cstmt.setString(12, e.getEndereco().getCidade());
            cstmt.setString(13, e.getEndereco().getUf());
            cstmt.setString(14, e.getEndereco().getCep());
            cstmt.setObject(15, e.getTuid());
            cstmt.setObject(16, e.getTufee());
            cstmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            disconnect();
        }
    }

    public static void deleteEmployee(Empregado e) {
        connect();
        String strsql = "delete from employee where id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(strsql);
            pstmt.setInt(1, e.getId());
            pstmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            disconnect();
        }
    }

    public static List<Empregado> getEmployees(Integer filter) {
        connect();
        String strsql = "select e.id, e.employeeName, e.type, e.username, e.pass, t.id as tuid, t.fee as tufee, a.street, a.number, a.complement, a.city, a.uf, a.cep, p.paymentType, p.paymentValue, p.fee as comission"
                + " from employee e left join tradeUnion t on e.id = t.employeeId inner join employeeAddress a on e.id = a.employeeId inner join payment p on p.employeeId = e.id@filter";
        if (filter != null) {
            strsql = strsql.replace("@filter", " where e.type = " + filter);
        } else {
            strsql = strsql.replace("@filter", "");
        }
        try {
            ResultSet rs = conn.createStatement().executeQuery(strsql);
            List<Empregado> list = new ArrayList<>();
            while (rs.next()) {
                Empregado e;
                switch (rs.getInt("type")) {
                    case 0:
                        e = new Horista();
                        ((Horista) e).setSalarioPorHora(rs.getDouble("paymentValue"));
                        break;
                    case 1:
                        e = new Assalariado();
                        ((Assalariado) e).setSalarioMensal(rs.getDouble("paymentValue"));
                        break;
                    default:
                        e = new Comissionado();
                        ((Comissionado) e).setTaxaComissao(rs.getDouble("comission"));
                        ((Comissionado) e).setSalarioMensal(rs.getDouble("paymentValue"));
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
        } finally {
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
        } finally {
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
        } finally {
            disconnect();
        }
    }

    public static void lancaVenda(Comissionado c, double valorVenda) {
        connect();
        String strsql = "insert selling values (getdate(), ?, ?)";
        try {
            PreparedStatement psmt = conn.prepareStatement(strsql);
            psmt.setDouble(1, valorVenda);
            psmt.setInt(2, c.getId());
            psmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            disconnect();
        }
    }

    public static void lancaTaxa(Empregado e, double valorTaxa) {
        connect();
        String strsql = "insert serviceFees values (getdate(), ?, ?)";
        try {
            PreparedStatement psmt = conn.prepareStatement(strsql);
            psmt.setDouble(1, valorTaxa);
            psmt.setInt(2, e.getId());
            psmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            disconnect();
        }
    }

    public static void rodaFolha() {
        connect();
        String html = "";
        html += "<h1>Folha de Pagamento</h1><br><table border='1'><tr><th>Nome</th><th>Salario Bruto</th><th>Descontos</th><th>Salario Liquido</th></tr>";
        String strhoristas = "select * from employee e inner join agenda a on e.id = a.employeeId where e.type = 0";
        try {
            ResultSet rshoristas = conn.createStatement().executeQuery(strhoristas);
            while (rshoristas.next()) {
                int type = rshoristas.getInt(8);
                if (type == 0 || type == 2) {
                    LocalDate lastpaid;
                    try {
                        lastpaid = rshoristas.getDate("lastPaid").toLocalDate();
                    } catch (Exception e) {
                        lastpaid = LocalDate.of(2000, Month.JANUARY, 1);
                    }
                    LocalDate now = LocalDate.now();
                    long difference = DAYS.between(lastpaid, now);
                    long expected;
                    if (rshoristas.getInt(8) == 0) {
                        expected = 7;
                    } else {
                        expected = 15;
                    }
                    if (expected <= difference) {
                        html += "<tr>";
                        html += "<td>" + rshoristas.getString("employeeName") + "</td>";
                        String puxaHorasTrabalhadas = "select * from access_card where employeeId = ? and access_time > ? and access_time <= ?";
                        PreparedStatement prepara = conn.prepareStatement(puxaHorasTrabalhadas);
                        prepara.setInt(1, rshoristas.getInt("id"));
                        prepara.setDate(2, java.sql.Date.valueOf(lastpaid));
                        prepara.setDate(3, java.sql.Date.valueOf(now));
                        ResultSet rshoras = prepara.executeQuery();
                        String sqlrapid = "select * from tradeUnion where employeeId = " + rshoristas.getInt("id");
                        ResultSet infotrade = conn.createStatement().executeQuery(sqlrapid);
                        double taxaTradeUnion = 0;
                        if (infotrade.next()) {
                            taxaTradeUnion = infotrade.getDouble("fee");
                        }
                        sqlrapid = "select * from payment where employeeId = " + rshoristas.getInt("id");
                        ResultSet infoPayment = conn.createStatement().executeQuery(sqlrapid);
                        infoPayment.next();
                        double salario = infoPayment.getDouble("paymentValue");
                        sqlrapid = "select * from serviceFees where employeeId = " + rshoristas.getInt("id");
                        ResultSet taxas = conn.createStatement().executeQuery(sqlrapid);
                        double taxasservicos = 0;
                        while (taxas.next()) {
                            taxasservicos += taxas.getDouble("feeValue");
                        }
                        long horas = 0, horasextras = 0;
                        while (rshoras.next()) {
                            LocalDateTime entrada = rshoras.getTimestamp("access_time").toLocalDateTime();
                            rshoras.next();
                            LocalDateTime saida = rshoras.getTimestamp("access_time").toLocalDateTime();
                            long horastrabs = HOURS.between(entrada, saida);
                            if (horastrabs > 8) {
                                horasextras += horastrabs - 8;
                                horas += 8;
                            } else {
                                horas += horastrabs;
                            }
                        }
                        double bruto = 0;
                        bruto += (salario * horasextras) * 1.5;
                        bruto += (salario * horas);
                        double descontos = 0;
                        descontos = bruto * (taxaTradeUnion / 100.0);
                        descontos += taxasservicos;
                        double liquido = bruto - descontos;
                        html += "<td>" + bruto + "</td><td>" + descontos + "</td><td>" + liquido + "</td>";
                        html += "</tr>";
                        updateLastPaid(rshoristas.getInt("id"));
                    }
                }
            }
            String sqlcom = "select * from employee e inner join agenda a on e.id = a.employeeId where e.type = 2";
            ResultSet comissionados = conn.createStatement().executeQuery(sqlcom);
            while (comissionados.next()) {
                LocalDate lastpaid;
                try {
                    lastpaid = comissionados.getDate("lastPaid").toLocalDate();
                } catch (Exception e) {
                    lastpaid = LocalDate.of(2000, Month.JANUARY, 1);
                }
                LocalDate now = LocalDate.now();
                long difference = DAYS.between(lastpaid, now);
                long expected;
                if (comissionados.getInt(8) == 0) {
                    expected = 7;
                } else {
                    expected = 15;
                }
                if (expected <= difference) {
                    html += "<tr>";
                    html += "<td>" + comissionados.getString("employeeName") + "</td>";
                    String sqlrapid = "select * from tradeUnion where employeeId = " + comissionados.getInt("id");
                    ResultSet infotrade = conn.createStatement().executeQuery(sqlrapid);
                    double taxaTradeUnion = 0;
                    if (infotrade.next()) {
                        taxaTradeUnion = infotrade.getDouble("fee");
                    }
                    sqlrapid = "select * from payment where employeeId = " + comissionados.getInt("id");
                    ResultSet infoPayment = conn.createStatement().executeQuery(sqlrapid);
                    infoPayment.next();
                    double salario = infoPayment.getDouble("paymentValue");
                    double comissao = infoPayment.getDouble("fee");
                    sqlrapid = "select * from serviceFees where employeeId = " + comissionados.getInt("id");
                    ResultSet taxas = conn.createStatement().executeQuery(sqlrapid);
                    double taxasservicos = 0;
                    while (taxas.next()) {
                        taxasservicos += taxas.getDouble("feeValue");
                    }
                    sqlrapid = "select * from selling where employeeId = ? and saleDate > ?";
                    PreparedStatement pstmt = conn.prepareStatement(sqlrapid);
                    pstmt.setInt(1, comissionados.getInt("id"));
                    pstmt.setDate(2, java.sql.Date.valueOf(lastpaid));
                    ResultSet vendas = conn.createStatement().executeQuery(sqlrapid);
                    double totcomissao = 0;
                    while (vendas.next()) {
                        totcomissao += (comissao / 100) * vendas.getDouble("saleValue");
                    }
                    double bruto = salario + totcomissao;
                    double descontos = ((taxaTradeUnion / 100) * bruto) + taxasservicos;
                    double liquido = bruto - descontos;
                    html += "<td>" + bruto + "</td><td>" + descontos + "</td><td>" + liquido + "</td>";
                    html += "</tr>";
                    updateLastPaid(comissionados.getInt("id"));
                }
            }
            String sqlass = "select * from employee e inner join agenda a on e.id = a.employeeId where e.type = 1";
            ResultSet ass = conn.createStatement().executeQuery(sqlass);
            while (ass.next()) {
                String sqlrapid = "select * from tradeUnion where employeeId = " + ass.getInt("id");
                ResultSet infotrade = conn.createStatement().executeQuery(sqlrapid);
                double taxaTradeUnion = 0;
                if (infotrade.next()) {
                    taxaTradeUnion = infotrade.getDouble("fee");
                }
                sqlrapid = "select * from payment where employeeId = " + ass.getInt("id");
                ResultSet infoPayment = conn.createStatement().executeQuery(sqlrapid);
                infoPayment.next();
                double salario = infoPayment.getDouble("paymentValue");
                sqlrapid = "select * from serviceFees where employeeId = " + ass.getInt("id");
                ResultSet taxas = conn.createStatement().executeQuery(sqlrapid);
                double taxasservicos = 0;
                while (taxas.next()) {
                    taxasservicos += taxas.getDouble("feeValue");
                }
                if (ass.getInt(8) == 0 || ass.getInt(8) == 2) {
                    LocalDate lastpaid;
                    try {
                        lastpaid = ass.getDate("lastPaid").toLocalDate();
                    } catch (Exception e) {
                        lastpaid = LocalDate.of(2000, Month.JANUARY, 1);
                    }
                    LocalDate now = LocalDate.now();
                    long difference = DAYS.between(lastpaid, now);
                    long expected;
                    if (ass.getInt(8) == 0) {
                        expected = 7;
                    } else {
                        expected = 15;
                    }
                    if (expected <= difference) {
                        html += "<tr>";
                        html += "<td>" + ass.getString("employeeName") + "</td>";
                        if (ass.getInt(8) == 0) {
                            salario /= 4.0;
                        } else {
                            salario /= 2.0;
                        }
                        double bruto = salario;
                        double descontos = ((taxaTradeUnion / 100) * bruto) + taxasservicos;
                        double liquido = bruto - descontos;
                        html += "<td>" + bruto + "</td><td>" + descontos + "</td><td>" + liquido + "</td>";
                        html += "</tr>";
                        updateLastPaid(ass.getInt("id"));
                    }
                }
                else {
                    LocalDate now = LocalDate.now();
                    int day = ass.getInt("payDay");
                    if (day == now.getDayOfMonth() || (day == -1 && now.getDayOfMonth() == 30)) {
                        html += "<tr>";
                        html += "<td>" + ass.getString("employeeName") + "</td>";
                        double bruto = salario;
                        double descontos = ((taxaTradeUnion / 100) * bruto) + taxasservicos;
                        double liquido = bruto - descontos;
                        html += "<td>" + bruto + "</td><td>" + descontos + "</td><td>" + liquido + "</td>";
                        html += "</tr>";
                        updateLastPaid(ass.getInt("id"));
                    }
                }
            }
            html += "</table>";
            PrintWriter writer = null;
            try {
                writer = new PrintWriter("folha.html", "UTF-8");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            writer.println(html);
            writer.close();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            disconnect();
        }

    }
    
    private static void updateLastPaid(int idEmp) throws SQLException {
        String strsql = "update agenda set lastPaid = getdate() where employeeId = " + idEmp;
        conn.createStatement().execute(strsql);
    }
}
