package com.vinicius.sistema_medico_web.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.vinicius.sistema_medico_web.model.Endereco;

import oracle.jdbc.OraclePreparedStatement;

@Repository
public class EnderecoDAO {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "c##vinicius";
    private static final String PASSWORD = "senha123";

    public Endereco buscarEnderecoPorID(int id) {
        String sql = "SELECT * FROM endereco WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Endereco endereco = new Endereco(
                        rs.getString("logradouro"),
                        rs.getString("numero"),
                        rs.getString("complemento"),
                        rs.getString("bairro"),
                        rs.getString("cidade"),
                        rs.getString("uf"),
                        rs.getString("cep")
                    );
                    endereco.setId(rs.getInt("id"));
                    return endereco;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int cadastrarEndereco(Endereco endereco) {
        String sql = "INSERT INTO endereco (logradouro, numero, complemento, bairro, cidade, uf, cep) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id INTO ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, endereco.getLogradouro());
            stmt.setString(2, endereco.getNumero());
            stmt.setString(3, endereco.getComplemento());
            stmt.setString(4, endereco.getBairro());
            stmt.setString(5, endereco.getCidade());
            stmt.setString(6, endereco.getUf());
            stmt.setString(7, endereco.getCep());

            OraclePreparedStatement oracleStmt = stmt.unwrap(OraclePreparedStatement.class);
            oracleStmt.registerReturnParameter(8, java.sql.Types.INTEGER);
            oracleStmt.executeUpdate();

            try (ResultSet rs = oracleStmt.getReturnResultSet()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void atualizarEndereco(Endereco endereco) {
        String sql = "UPDATE endereco SET logradouro = ?, numero = ?, complemento = ?, bairro = ?, cidade = ?, uf = ?, cep = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, endereco.getLogradouro());
            stmt.setString(2, endereco.getNumero());
            stmt.setString(3, endereco.getComplemento());
            stmt.setString(4, endereco.getBairro());
            stmt.setString(5, endereco.getCidade());
            stmt.setString(6, endereco.getUf());
            stmt.setString(7, endereco.getCep());
            stmt.setInt(8, endereco.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletarEndereco(int id) {
        String sql = "DELETE FROM endereco WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
