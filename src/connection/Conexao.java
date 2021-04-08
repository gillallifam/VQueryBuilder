/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author snow
 */
public class Conexao {
    
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost/vqbuilder?verifyServerCertificate=false&useSSL=true",
                    "root", "Gill9177");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    } 
    
    public static void closeConnection(Connection conexao) throws SQLException {
        try {
            if(conexao != null) {
                conexao.close();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
