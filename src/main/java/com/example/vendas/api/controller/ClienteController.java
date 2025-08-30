package com.example.vendas.api.controller;

import com.example.vendas.api.dto.ClienteInput;
import com.example.vendas.api.dto.ClienteOutput;
import com.example.vendas.domain.model.Cliente;
import com.example.vendas.domain.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public List<ClienteOutput> listar() {
        return clienteService.listar().stream().map(this::toOutput).toList();
    }

    @GetMapping("/{id}")
    public ClienteOutput buscar(@PathVariable Long id) {
        return toOutput(clienteService.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteOutput criar(@RequestBody @Valid ClienteInput input) {
        Cliente c = new Cliente();
        c.setNome(input.nome());
        c.setEmail(input.email());
        c.setDocumento(input.documento());
        return toOutput(clienteService.criar(c));
    }

    @PutMapping("/{id}")
    public ClienteOutput atualizar(@PathVariable Long id, @RequestBody @Valid ClienteInput input) {
        Cliente c = new Cliente();
        c.setNome(input.nome());
        c.setEmail(input.email());
        c.setDocumento(input.documento());
        return toOutput(clienteService.atualizar(id, c));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        clienteService.remover(id);
    }

    private ClienteOutput toOutput(Cliente c) {
        return new ClienteOutput(c.getId(), c.getNome(), c.getEmail(), c.getDocumento(), c.getCriadoEm());
    }
}

