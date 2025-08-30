package com.example.vendas.domain.service;

import com.example.vendas.domain.exception.EntidadeNaoEncontradaException;
import com.example.vendas.domain.exception.NegocioException;
import com.example.vendas.domain.model.Cliente;
import com.example.vendas.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<Cliente> listar() { return clienteRepository.findAll(); }

    public Cliente buscar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado: " + id));
    }

    @Transactional
    public Cliente criar(Cliente cliente) {
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new NegocioException("Email já cadastrado");
        }
        if (clienteRepository.existsByDocumento(cliente.getDocumento())) {
            throw new NegocioException("Documento já cadastrado");
        }
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente dados) {
        Cliente atual = buscar(id);
        if (!atual.getEmail().equalsIgnoreCase(dados.getEmail()) && clienteRepository.existsByEmail(dados.getEmail())) {
            throw new NegocioException("Email já cadastrado");
        }
        if (!atual.getDocumento().equalsIgnoreCase(dados.getDocumento()) && clienteRepository.existsByDocumento(dados.getDocumento())) {
            throw new NegocioException("Documento já cadastrado");
        }
        atual.setNome(dados.getNome());
        atual.setEmail(dados.getEmail());
        atual.setDocumento(dados.getDocumento());
        return clienteRepository.save(atual);
    }

    @Transactional
    public void remover(Long id) {
        Cliente atual = buscar(id);
        clienteRepository.delete(atual);
    }
}

