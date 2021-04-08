/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import connection.Conexao;
import exception.DAOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ItemTabela;

/**
 *
 * @author gill
 */
public class DbDAO {

    private Connection conexao;
    String db = "default";

    public DbDAO(String db) {
        this.db = db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getDb() {
        return db;
    }

    public List<List<String>> testarConsulta(String consulta) throws DAOException, SQLException {
        conexao = null;
        List<List<String>> lista1 = new ArrayList<>();

        try {
            conexao = Conexao.getConnection();
            PreparedStatement stmt = conexao.prepareStatement(consulta);
            ResultSet resultado = stmt.executeQuery();
            resultado.last();
            int lines = resultado.getRow();
            resultado.beforeFirst();
            int columns = resultado.getMetaData().getColumnCount();
            //System.out.println();
            //List<String[columns]> lista = new ArrayList<>();

            while (resultado.next()) {
                ArrayList<String> lista2 = new ArrayList<>();
                for (int r = 1; r <= columns; r++) {
                    lista2.add(resultado.getString(r));
                }
                lista1.add(lista2);
            }

            stmt.close();
            return lista1;

        } catch (SQLException ex) {
            throw new DAOException("Erro ao testar consulta:\n\n" + ex.getMessage(), ex);
        } finally {
            Conexao.closeConnection(conexao);
        }
    }

    public List<String[]> testarConsultaxxx(String consulta) throws DAOException, SQLException {
        conexao = null;

        try {
            conexao = Conexao.getConnection();
            PreparedStatement stmt = conexao.prepareStatement(consulta);
            ResultSet resultado = stmt.executeQuery();
            resultado.last();
            int lines = resultado.getRow();
            resultado.beforeFirst();
            int columns = resultado.getMetaData().getColumnCount();
            System.out.println();
            //List<String[columns]> lista = new ArrayList<>();
            ArrayList<String[]> lista = new ArrayList<>();
            while (resultado.next()) {
                String[] strs = new String[columns];
                //for (int l = 0; l < lines; l++) {
                for (int r = 1; r <= columns; r++) {
                    strs[r - 1] = resultado.getString(r);
                }
                //}
                //System.out.println(lista);
                lista.add(strs);
            }
            stmt.close();
            return lista;

        } catch (SQLException ex) {
            throw new DAOException("Erro ao fazer consulta:\n\n" + ex.getMessage(), ex);
        } finally {
            Conexao.closeConnection(conexao);
        }
    }

    // Databases
    public List<ItemTabela> getDataBases() throws DAOException, SQLException {
        List<ItemTabela> lista = new ArrayList<>();
        String consulta = "show databases";
        try {
            conexao = Conexao.getConnection();
            PreparedStatement stmt = conexao.prepareStatement(consulta);
            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                lista.add(new ItemTabela(resultado.getString("Database")));
            }
            return lista;
        } catch (SQLException ex) {
            throw new DAOException("Erro ao obter databases:\n\n" + ex.getMessage(), ex);
        } finally {
            Conexao.closeConnection(conexao);
        }
    }

    public List<String> getTableNames(String table) throws DAOException, SQLException {
        List<String> lista = new ArrayList<>();
        String consulta = "desc " + table;
        try {
            conexao = Conexao.getConnection();
            PreparedStatement stmt = conexao.prepareStatement(consulta);
            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                lista.add(resultado.getString("Field"));
            }
            return lista;
        } catch (SQLException ex) {
            throw new DAOException("Erro ao obter campos:\n\n" + ex.getMessage(), ex);
        } finally {
            Conexao.closeConnection(conexao);
        }
    }

    // Tabelas
    public List<ItemTabela> getTables() throws DAOException, SQLException {
        String sp_call = "{call sp_vertabelas(?)}";
        List<ItemTabela> lista = new ArrayList<>();
        try {
            conexao = Conexao.getConnection();
            CallableStatement call = conexao.prepareCall(sp_call);
            call.setString(1, db);
            ResultSet resultado = call.executeQuery();
            while (resultado.next()) {
                lista.add(new ItemTabela(resultado.getString("tabelas")));
            }
            return lista;
        } catch (SQLException ex) {
            throw new DAOException("Erro ao obter tabelas:\n\n" + ex.getMessage(), ex);
        } finally {
            Conexao.closeConnection(conexao);
        }
    }

    // Campos
    public List<ItemTabela> getFields(String table) throws DAOException, SQLException {

        String sp_call = "{call sp_listarcampos(?,?)}";
        List<ItemTabela> lista = new ArrayList<>();
        try {
            conexao = Conexao.getConnection();
            CallableStatement call = conexao.prepareCall(sp_call);
            call.setString(1, db);
            call.setString(2, table);
            ResultSet resultado = call.executeQuery();
            while (resultado.next()) {
                lista.add(new ItemTabela(resultado.getString("campos")));
            }
            return lista;
        } catch (SQLException ex) {
            throw new DAOException("Erro ao obter campos:\n\n" + ex.getMessage(), ex);
        } finally {
            Conexao.closeConnection(conexao);
        }
    }

    // Chaves
    public List<String> getKeys(String table) throws DAOException, SQLException {

        String sp_call = "{call sp_listarchaves(?,?)}";
        List<String> lista = new ArrayList<>();
        try {
            conexao = Conexao.getConnection();
            CallableStatement call = conexao.prepareCall(sp_call);
            call.setString(1, db);
            call.setString(2, table);
            ResultSet resultado = call.executeQuery();
            while (resultado.next()) {
                lista.add(resultado.getString("keys"));
            }
            return lista;
        } catch (SQLException ex) {
            throw new DAOException("Erro ao obter chaves:\n\n" + ex.getMessage(), ex);
        } finally {
            Conexao.closeConnection(conexao);
        }
    }

    // Relacionamentos
    public List<String> getRelations(String table) throws DAOException, SQLException {

        String sp_call = "{call sp_listarrelacoes(?,?)}";
        List<String> lista = new ArrayList<>();
        try {
            conexao = Conexao.getConnection();
            CallableStatement call = conexao.prepareCall(sp_call);
            call.setString(1, db);
            call.setString(2, table);
            ResultSet resultado = call.executeQuery();
            while (resultado.next()) {
                lista.add(resultado.getString("relacionamentos"));
            }
            return lista;
        } catch (SQLException ex) {
            throw new DAOException("Erro ao obter relacionamentos:\n\n" + ex.getMessage(), ex);
        } finally {
            Conexao.closeConnection(conexao);
        }
    }

    public List<String> getRelators(String table) throws DAOException, SQLException {
        String sp_call = "{call sp_cometome(?,?)}";
        List<String> lista = new ArrayList<>();
        try {
            conexao = Conexao.getConnection();
            CallableStatement call = conexao.prepareCall(sp_call);
            call.setString(1, db);
            call.setString(2, table);
            ResultSet resultado = call.executeQuery();
            while (resultado.next()) {
                lista.add(resultado.getString("relators"));
            }
            return lista;
        } catch (SQLException ex) {
            throw new DAOException("Erro ao obter relatores:\n\n" + ex.getMessage(), ex);
        } finally {
            Conexao.closeConnection(conexao);
        }
    }

    public List<String> getPriKeys(String table) throws DAOException, SQLException {
        String sp_call = "{call sp_getpk(?,?)}";
        List<String> lista = new ArrayList<>();
        try {
            conexao = Conexao.getConnection();
            CallableStatement call = conexao.prepareCall(sp_call);
            call.setString(1, db);
            call.setString(2, table);
            ResultSet resultado = call.executeQuery();
            while (resultado.next()) {
                lista.add(resultado.getString("prikeys"));
            }
            return lista;
        } catch (SQLException ex) {
            throw new DAOException("Erro ao obter chaves:\n\n" + ex.getMessage(), ex);
        } finally {
            Conexao.closeConnection(conexao);
        }
    }

    // Par chave primaria-estrangeira
    public String getKeyPairs(String pTable, String sTable) throws DAOException, SQLException {

        String sp_call = "{call sp_keytest(?,?,?)}";
        try {
            conexao = Conexao.getConnection();
            CallableStatement call = conexao.prepareCall(sp_call);
            call.setString(1, db);
            call.setString(2, pTable);
            call.setString(3, sTable);
            ResultSet resultado = call.executeQuery();
            while (resultado.next()) {
                return resultado.getString("rsp");
            }
            return null;
        } catch (SQLException ex) {
            throw new DAOException("Erro ao obter chaves:\n\n" + ex.getMessage(), ex);
        } finally {
            Conexao.closeConnection(conexao);
        }
    }

    // Sao relacionadas
    public String isRelated(String tablea, String tableb) throws DAOException, SQLException {

        String sp_call = "{call sp_tstrelacao(?,?,?)}";
        try {
            conexao = Conexao.getConnection();
            CallableStatement call = conexao.prepareCall(sp_call);
            call.setString(1, db);
            call.setString(2, tablea);
            call.setString(3, tableb);
            ResultSet resultado = call.executeQuery();
            if (resultado.next()) {
                return resultado.getString("rsp");//'##' = sem relaçao
            }
            return null;
        } catch (SQLException ex) {
            throw new DAOException("Erro ao obter relacionamentos:\n\n" + ex.getMessage(), ex);
        } finally {
            Conexao.closeConnection(conexao);
        }
    }

}
