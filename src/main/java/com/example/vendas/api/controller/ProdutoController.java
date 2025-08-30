package com.example.vendas.api.controller;

import com.example.vendas.api.dto.ProdutoInput;
import com.example.vendas.api.dto.ProdutoOutput;
import com.example.vendas.domain.model.Produto;
import com.example.vendas.domain.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public List<ProdutoOutput> listar() {
        return produtoService.listar().stream().map(this::toOutput).toList();
    }

    @GetMapping("/{id}")
    public ProdutoOutput buscar(@PathVariable Long id) {
        return toOutput(produtoService.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoOutput criar(@RequestBody @Valid ProdutoInput input) {
        Produto p = toEntity(input);
        return toOutput(produtoService.criar(p));
    }

    @PutMapping("/{id}")
    public ProdutoOutput atualizar(@PathVariable Long id, @RequestBody @Valid ProdutoInput input) {
        Produto p = toEntity(input);
        return toOutput(produtoService.atualizar(id, p));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        produtoService.remover(id);
    }

    private Produto toEntity(ProdutoInput in) {
        return Produto.builder()
                .nome(in.nome())
                .sku(in.sku())
                .preco(in.preco())
                .estoque(in.estoque())
                .ativo(in.ativo() == null ? Boolean.TRUE : in.ativo())
                .build();
    }

    private ProdutoOutput toOutput(Produto p) {
        return new ProdutoOutput(p.getId(), p.getNome(), p.getSku(), p.getPreco(), p.getEstoque(), p.getAtivo(), p.getCriadoEm());
    }
}

