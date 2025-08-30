package com.example.vendas.domain.service;

import com.example.vendas.domain.exception.EntidadeNaoEncontradaException;
import com.example.vendas.domain.exception.EstoqueInsuficienteException;
import com.example.vendas.domain.model.*;
import com.example.vendas.domain.repository.ClienteRepository;
import com.example.vendas.domain.repository.PedidoRepository;
import com.example.vendas.domain.repository.ProdutoRepository;
import com.example.vendas.messaging.PedidoEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoEventPublisher eventPublisher;

    public Pedido buscar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Pedido não encontrado: " + id));
    }

    public List<Pedido> listar() { return pedidoRepository.findAll(); }

    @Transactional
    public Pedido criarPedido(Long clienteId, Map<Long, Integer> itens) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado: " + clienteId));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);

        BigDecimal total = BigDecimal.ZERO;

        // Valida estoque primeiro
        for (Map.Entry<Long, Integer> e : itens.entrySet()) {
            Produto produto = produtoRepository.findById(e.getKey())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto não encontrado: " + e.getKey()));
            int quantidade = e.getValue();
            if (quantidade <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser positiva para produto " + e.getKey());
            }
            if (produto.getEstoque() < quantidade) {
                throw new EstoqueInsuficienteException("Estoque insuficiente para SKU " + produto.getSku());
            }
        }

        // Monta itens e atualiza estoque
        for (Map.Entry<Long, Integer> e : itens.entrySet()) {
            Produto produto = produtoRepository.getReferenceById(e.getKey());
            int quantidade = e.getValue();
            produto.setEstoque(produto.getEstoque() - quantidade);
            BigDecimal preco = produto.getPreco();
            BigDecimal subtotal = preco.multiply(BigDecimal.valueOf(quantidade));
            total = total.add(subtotal);

            ItemPedido item = ItemPedido.builder()
                    .produto(produto)
                    .quantidade(quantidade)
                    .precoUnitario(preco)
                    .subtotal(subtotal)
                    .build();
            pedido.adicionarItem(item);
        }

        pedido.setTotal(total);
        Pedido salvo = pedidoRepository.save(pedido);
        eventPublisher.publishAfterCommit("PEDIDO_CRIADO", salvo);
        return salvo;
    }

    @Transactional
    public Pedido pagar(Long pedidoId) {
        Pedido pedido = buscar(pedidoId);
        if (pedido.getStatus() == PedidoStatus.CRIADO) {
            pedido.setStatus(PedidoStatus.PAGO);
        }
        eventPublisher.publishAfterCommit("PEDIDO_PAGO", pedido);
        return pedido;
    }

    @Transactional
    public Pedido cancelar(Long pedidoId) {
        Pedido pedido = buscar(pedidoId);
        if (pedido.getStatus() == PedidoStatus.CRIADO) {
            // Estorna estoque
            for (ItemPedido item : pedido.getItens()) {
                Produto p = item.getProduto();
                p.setEstoque(p.getEstoque() + item.getQuantidade());
            }
            pedido.setStatus(PedidoStatus.CANCELADO);
        }
        eventPublisher.publishAfterCommit("PEDIDO_CANCELADO", pedido);
        return pedido;
    }
}
