package com.example.vendas.api.controller;

import com.example.vendas.api.dto.PedidoInput;
import com.example.vendas.api.dto.PedidoOutput;
import com.example.vendas.domain.model.ItemPedido;
import com.example.vendas.domain.model.Pedido;
import com.example.vendas.domain.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public List<PedidoOutput> listar() {
        return pedidoService.listar().stream().map(this::toOutput).toList();
    }

    @GetMapping("/{id}")
    public PedidoOutput buscar(@PathVariable Long id) {
        return toOutput(pedidoService.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoOutput criar(@RequestBody @Valid PedidoInput input) {
        Map<Long, Integer> itens = new HashMap<>();
        for (PedidoInput.ItemInput i : input.itens()) {
            itens.merge(i.produtoId(), i.quantidade(), Integer::sum);
        }
        Pedido p = pedidoService.criarPedido(input.clienteId(), itens);
        return toOutput(p);
    }

    @PostMapping("/{id}/pagar")
    public PedidoOutput pagar(@PathVariable Long id) {
        return toOutput(pedidoService.pagar(id));
    }

    @PostMapping("/{id}/cancelar")
    public PedidoOutput cancelar(@PathVariable Long id) {
        return toOutput(pedidoService.cancelar(id));
    }

    private PedidoOutput toOutput(Pedido p) {
        List<PedidoOutput.ItemOutput> itens = p.getItens().stream().map(this::toItemOutput).toList();
        return new PedidoOutput(
                p.getId(),
                p.getCliente().getId(),
                p.getStatus(),
                p.getTotal(),
                p.getCriadoEm(),
                itens
        );
    }

    private PedidoOutput.ItemOutput toItemOutput(ItemPedido i) {
        return new PedidoOutput.ItemOutput(
                i.getProduto().getId(),
                i.getProduto().getNome(),
                i.getQuantidade(),
                i.getPrecoUnitario(),
                i.getSubtotal()
        );
    }
}

