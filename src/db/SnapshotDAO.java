/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Snapshot;

/**
 *
 * @author lucas
 */
public class SnapshotDAO {
    private static Connection conn;
    private static void connect() {
        String username = "hux", password = "huxflooder";
        String url = "jdbc:sqlserver://casaamorim.no-ip.biz;databaseName=snapshot_control";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SnapshotDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            Logger.getLogger(SnapshotDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void disconnect() {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SnapshotDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static ArrayList<Snapshot> getSnapshots() {
        connect();
        String strSQL = "select * from db_snapshot";
        try {
            PreparedStatement pstmt = conn.prepareStatement(strSQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = pstmt.executeQuery();
            ArrayList<Snapshot> snapshots = new ArrayList<>();
            while (rs.next()) {
                Snapshot snap = new Snapshot();
                snap.setId(rs.getInt("id"));
                snap.setUuid(rs.getString("uuid"));
                snap.setOcurrence(rs.getString("ocurrence"));
                snapshots.add(snap);
            }
            return snapshots;
            
        } catch (SQLException ex) {
            Logger.getLogger(SnapshotDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            disconnect();
        }
       return null; 
    }
    
    private static void rollback() {
        connect();
        String strDisc = "alter database sistema set multi_user with rollback immediate";
        try {
            conn.createStatement().execute(strDisc);
        } catch (SQLException ex) {
            Logger.getLogger(SnapshotDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
          disconnect();
        }
       
    }
    
    public static void createSnapshot(String snapDesc) {
        rollback();
        connect();
        String insert = "insert db_snapshot values (?, ?, 0, 0)";
        String uuid = UUID.randomUUID().toString();
        try {
            PreparedStatement pst = conn.prepareStatement(insert);
            pst.setString(1, uuid);
            pst.setString(2, snapDesc);
            pst.execute();
            String backup = "backup database sistema to disk = 'C:\\Users\\lucas\\Desktop\\sharebonito\\projbkps\\" + uuid + ".bkp'";
            pst = conn.prepareStatement(backup);
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(SnapshotDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            disconnect();
        }
        
    }
    
    public static void recoverFromSnapshot(Snapshot snap) {
        rollback();
        connect();
        String strrecover = "restore database sistema from disk = 'C:\\Users\\lucas\\Desktop\\sharebonito\\projbkps\\" + snap.getUuid() + ".bkp' with replace";
        try {
            conn.createStatement().execute("update db_snapshot set actual = 0");
            conn.createStatement().execute("update db_snapshot set actual = 1 where id = " + snap.getId());
            conn.createStatement().execute(strrecover);
        } catch (SQLException ex) {
            Logger.getLogger(SnapshotDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            disconnect();
        }
    }
}
