
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;


public class DB {
    
    private String path = "jdbc:sqlite:db/WaterSales.db";
    public Connection conn = null;
    public static List<String> ls = new ArrayList<>();

    public DB() {
        try {
            // connection
            conn = DriverManager.getConnection(path);
            System.out.println("Connect Success");
        } catch (Exception e) {
            System.err.println("Connect Error : " + e);
        }
    }
    
    // admin login fnc
   public List<String> login(String userName, String pass) { // Kullanıcıdan aldığımız değerleri DB'de var mı yok mu işlemini yaptırıyoruz.

        try {
            String query = "SELECT * FROM admin WHERE mail = ? and pass = ? "; // Varlığını kontrol etmeliyiz pass & email'in.
            PreparedStatement pre = conn.prepareStatement(query); // preden dönen prametreleri ata!
            pre.setString(1, userName);
            pre.setString(2, pass);
            ResultSet rs = pre.executeQuery();

            if (rs.next()) { // True dönüyorsa, admin doğru giriş yapmıştır demek.
                ls.add("" + rs.getInt("aid")); // list içi data ile dolduruldu.
                ls.add(rs.getString("name"));
                ls.add(rs.getString("mail"));
                ls.add(rs.getString("pass"));
            }
            pre.close();
            conn.close();

        } catch (Exception e) {
            System.err.println("Login error: " + e);
        }
        return ls;
    }
    
   
   // Data Customer function
   public DefaultTableModel fncAllCustomer(String txt) {
       DefaultTableModel dtm = new DefaultTableModel();
       
       String q = "";
       if( txt.equals("") ) {
           q = "select * from customer";
       }else {
           q = "SELECT * FROM customer WHERE name like '%"+txt+"%' or mail like '%"+txt+"%' or tel like '%"+txt+"%'";
       }
       
       dtm.addColumn("Cid");
       dtm.addColumn("Name");
       dtm.addColumn("Mail");
       dtm.addColumn("Tel");
       dtm.addColumn("Address");
       
       try {
           PreparedStatement pre = conn.prepareStatement(q);
           ResultSet rs = pre.executeQuery();
           while(rs.next()) {
               int cid = rs.getInt("cid");
               String nm = rs.getString("name");
               String mail = rs.getString("mail");
               String tl = rs.getString("tel");
               String adr = rs.getString("address");
               Object[] row = { cid, nm, mail, tl, adr };
               dtm.addRow(row);
           }
           pre.close();
           conn.close();
       } catch (Exception e) {
           System.err.println("fncAllCustomer error : " + e);
       }
       return dtm;
   }
   
    
   //  Customer insert
   public int customerInsert( String name, String mail, String tel, String address ) {
       int statu = 0;
       try {
           String query = "insert into customer values ( null, ?, ?, ? , ? )";
           PreparedStatement pre = conn.prepareStatement(query);
           pre.setString(1, name);
           pre.setString(2, mail);
           pre.setString(3, tel);
           pre.setString(4, address);
           statu = pre.executeUpdate();
           
           pre.close();
           conn.close();
           
       } catch (Exception e) {
           System.err.println("customerInsert Error : " +  e);
       }
       return statu;
   }
   
   // Customer Delete item
   public int customerDelete( int cid ) {
       int statu = 0;
       try {
           String query = "delete from customer where cid = ?";
           PreparedStatement pre = conn.prepareStatement(query);
           pre.setInt(1, cid);
           statu = pre.executeUpdate();
           
           pre.close();
           conn.close();
       } catch (Exception e) {
           System.err.println("customerDelete Error : " +  e);
       }
       return statu;
   }
   
   
      //  Customer insert
   public int customerUpdate( int cid, String name, String mail, String tel, String address ) {
       int statu = 0;
       try {
           String query = "update customer set name = ?, mail = ?, tel = ?, address = ? where cid = ? ";
           PreparedStatement pre = conn.prepareStatement(query);
           pre.setString(1, name);
           pre.setString(2, mail);
           pre.setString(3, tel);
           pre.setString(4, address);
           pre.setInt(5, cid);
           statu = pre.executeUpdate();
           
           pre.close();
           conn.close();
           
       } catch (Exception e) {
           System.err.println("customerInsert Error : " +  e);
       }
       return statu;
   }
   
   
   
   // Data Customer function
   public DefaultTableModel fncAllOrder() {
       DefaultTableModel dtm = new DefaultTableModel();
       
       String q = "SELECT * FROM orderWater INNER JOIN customer on  orderWater.cid = customer.cid where statu != 3 ";
       
       dtm.addColumn("OID");
       dtm.addColumn("Name");
       dtm.addColumn("Tel");
       dtm.addColumn("Add");
       dtm.addColumn("Size");
       dtm.addColumn("Statu");
       dtm.addColumn("Date");
       
       try {
           PreparedStatement pre = conn.prepareStatement(q);
           ResultSet rs = pre.executeQuery();
           while(rs.next()) {
               int oid = rs.getInt("oid");
               String name = rs.getString("name");
               String tel = rs.getString("tel");
               String address = rs.getString("address");
               int size = rs.getInt("size");
               int statu = rs.getInt("statu");
               String statuString = "";
               if (statu == 1) {
                   statuString = "preparing";
               }
               if (statu == 2) {
                   statuString = "delivery";
               }
               
               String date = rs.getString("date");
               Object[] row = { oid, name, tel, address, size, statuString, date };
               dtm.addRow(row);
           }
           pre.close();
           conn.close();
       } catch (Exception e) {
           System.err.println("fncAllOrder error : " + e);
       }
       return dtm;
   }
   
   
   // new order
   public int newOrder( int cid, int size ) {
       int statu = -1;
       try {
           
           SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
           Timestamp timestamp = new Timestamp(System.currentTimeMillis());
           String ts = sdf.format(timestamp);
           
           String query = "insert into orderWater values ( null, ?, ?, 1 , '"+ts+"' )";
           PreparedStatement pre = conn.prepareStatement(query);
           pre.setInt(1, cid);
           pre.setInt(2, size);
           statu = pre.executeUpdate();
           
           pre.close();
           conn.close();
       } catch (Exception e) {
           System.err.println("newOrder error : " + e);
       }
       return statu;
   }
   
   
   // order statu change
   public int orderStatuChange( int oid, int newStatu ) {
       int statu = -1;
       try {
           String query = "update orderWater set statu = ? where oid = ? ";
           PreparedStatement pre = conn.prepareStatement(query);
           pre.setInt(1, newStatu);
           pre.setInt(2, oid);
           statu = pre.executeUpdate();
           pre.close();
           conn.close();
           
       } catch (Exception e) {
           System.err.println("orderStatuChange : " + e);
       }
       return statu;
   } 
   
   
}
