package com.vinicius.sistema_medico_web.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vinicius.sistema_medico_web.model.Endereco;
import com.vinicius.sistema_medico_web.model.Paciente;

import oracle.jdbc.OraclePreparedStatement;

@Repository
public class PacienteDAO {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "c##vinicius";
    private static final String PASSWORD = "senha123";

    @Autowired
    private EnderecoDAO enderecoDAO;

    public void cadastrarPaciente(Paciente paciente) {
        String sqlEndereco = "INSERT INTO endereco (logradouro, numero, complemento, bairro, cidade, uf, cep) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id INTO ?";
        String sqlPaciente = "INSERT INTO paciente (nome, cpf, telefone, endereco_id, data_nascimento) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            int enderecoId;
            try (PreparedStatement stmt = conn.prepareStatement(sqlEndereco)) {
                Endereco endereco = paciente.getEndereco();
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
                        enderecoId = rs.getInt(1);
                    } else {
                        throw new SQLException("Falha ao obter ID do endereço.");
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlPaciente)) {
                stmt.setString(1, paciente.getNome());
                stmt.setString(2, paciente.getCpf().replace(".", "").replace("-", ""));
                stmt.setString(3, paciente.getTelefone());
                stmt.setInt(4, enderecoId);
                stmt.setDate(5, Date.valueOf(paciente.getDataNascimento()));
                stmt.executeUpdate();
            }

            System.out.println("Paciente " + paciente.getNome() + " cadastrado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Paciente> listarPacientes() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM paciente";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Integer id = rs.getInt("id");
                String nome = rs.getString("nome");
                String cpf = rs.getString("cpf");
                String telefone = rs.getString("telefone");
                Date dataNascimento = rs.getDate("data_nascimento");
                Endereco endereco = enderecoDAO.buscarEnderecoPorID(rs.getInt("endereco_id"));

                Paciente paciente = new Paciente(nome, cpf, telefone, endereco, dataNascimento.toLocalDate());
                paciente.setId(id);
                pacientes.add(paciente);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pacientes;
    }

    public Paciente buscarPorId(int id) {
        String sql = "SELECT * FROM paciente WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Endereco endereco = enderecoDAO.buscarEnderecoPorID(rs.getInt("endereco_id"));

                    Paciente paciente = new Paciente(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("telefone"),
                        endereco,
                        rs.getDate("data_nascimento").toLocalDate()
                    );
                    paciente.setId(rs.getInt("id"));
                    return paciente;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void atualizarPaciente(Paciente paciente) {
        String sql = "UPDATE paciente SET nome = ?, cpf = ?, telefone = ?, endereco_id = ?, data_nascimento = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getCpf().replace(".", "").replace("-", ""));
            stmt.setString(3, paciente.getTelefone());
            if (paciente.getEndereco() != null && paciente.getEndereco().getId() != null) {
                stmt.setInt(4, paciente.getEndereco().getId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.setDate(5, Date.valueOf(paciente.getDataNascimento()));
            stmt.setInt(6, paciente.getId());

            stmt.executeUpdate();
            System.out.println("Paciente atualizado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletarPaciente(int pacienteId) {
        String sql = "DELETE FROM paciente WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pacienteId);
            stmt.executeUpdate();
            System.out.println("Paciente deletado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
