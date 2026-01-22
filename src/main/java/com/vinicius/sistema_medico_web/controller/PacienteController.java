package com.vinicius.sistema_medico_web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.vinicius.sistema_medico_web.dao.PacienteDAO;
import com.vinicius.sistema_medico_web.model.Endereco;
import com.vinicius.sistema_medico_web.model.Paciente;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PacienteController {

    @Autowired
    private PacienteDAO pacienteDAO;
    
    @GetMapping("/")
    public String index(Model model) {
        List<Paciente> pacientes = pacienteDAO.listarPacientes();

        model.addAttribute("pacientes", pacientes);
        return "index";
    }

    @GetMapping("/novo-paciente")
    public String novoPaciente(Model model) {
        Paciente paciente = new Paciente();
        paciente.setEndereco(new Endereco());
        model.addAttribute("paciente", paciente);

        return "pacientes/novo-paciente";
    }

    @PostMapping("/salvar-paciente")
    public String salvarPaciente(Paciente paciente) {
        pacienteDAO.cadastrarPaciente(paciente);

        return "redirect:/";
    }

    @GetMapping("/editar-paciente")
    public String editarPaciente(@RequestParam("id") Integer id, Model model) {
        Paciente paciente = pacienteDAO.buscarPorId(id);
        model.addAttribute("paciente", paciente);

        return "pacientes/editar-paciente";
    }

    @PostMapping("/atualizar-paciente")
    public String atualizarPaciente(Paciente paciente) {
        pacienteDAO.atualizarPaciente(paciente);

        return "redirect:/";
    }

    @GetMapping("/deletar-paciente")
    public String deletarPaciente(@RequestParam("id") Integer id) {
        pacienteDAO.deletarPaciente(id);
        return "redirect:/";
    }
    
}
