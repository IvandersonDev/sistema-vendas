package com.example.vendas.domain.service;

import com.example.vendas.domain.exception.EntidadeNaoEncontradaException;
import com.example.vendas.domain.exception.NegocioException;
import com.example.vendas.domain.model.Produto;
import com.example.vendas.domain.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public List<Produto> listar() { return produtoRepository.findAll(); }

    public Produto buscar(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto não encontrado: " + id));
    }

    @Transactional
    public Produto criar(Produto produto) {
        if (produtoRepository.existsBySku(produto.getSku())) {
            throw new NegocioException("SKU já cadastrado");
        }
        if (produto.getEstoque() == null || produto.getEstoque() < 0) {
            throw new NegocioException("Estoque inválido");
        }
        return produtoRepository.save(produto);
    }

    @Transactional
    public Produto atualizar(Long id, Produto dados) {
        Produto atual = buscar(id);
        if (!atual.getSku().equalsIgnoreCase(dados.getSku()) && produtoRepository.existsBySku(dados.getSku())) {
            throw new NegocioException("SKU já cadastrado");
        }
        atual.setNome(dados.getNome());
        atual.setSku(dados.getSku());
        atual.setPreco(dados.getPreco());
        atual.setEstoque(dados.getEstoque());
        atual.setAtivo(dados.getAtivo());
        return produtoRepository.save(atual);
    }

    @Transactional
    public void remover(Long id) {
        produtoRepository.delete(buscar(id));
    }

    @Transactional
    public void ajustarEstoque(Long produtoId, int delta) {
        Produto p = buscar(produtoId);
        int novo = p.getEstoque() + delta;
        if (novo < 0) {
            throw new NegocioException("Estoque ficaria negativo para o produto: " + produtoId);
        }
        p.setEstoque(novo);
        produtoRepository.save(p);
    }
}

