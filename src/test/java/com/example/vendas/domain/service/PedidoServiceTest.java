package com.example.vendas.domain.service;

import com.example.vendas.SistemaVendasApplication;
import com.example.vendas.domain.exception.EstoqueInsuficienteException;
import com.example.vendas.domain.model.Pedido;
import com.example.vendas.domain.model.Produto;
import com.example.vendas.domain.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SistemaVendasApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PedidoServiceTest {

    @Autowired
    PedidoService pedidoService;

    @Autowired
    ProdutoRepository produtoRepository;

    @Test
    void criarPedido_deveDiminuirEstoqueECalcularTotal() {
        Produto produto = produtoRepository.findById(1L).orElseThrow();
        int estoqueAntes = produto.getEstoque();

        Pedido pedido = pedidoService.criarPedido(1L, Map.of(1L, 2));

        assertNotNull(pedido.getId());
        assertEquals(new BigDecimal("13000.00"), pedido.getTotal());

        Produto apos = produtoRepository.findById(1L).orElseThrow();
        assertEquals(estoqueAntes - 2, apos.getEstoque());
    }

    @Test
    void criarPedido_quandoEstoqueInsuficiente_deveLancarExcecao() {
        Produto produto = produtoRepository.findById(2L).orElseThrow();
        int muito = produto.getEstoque() + 1;
        assertThrows(EstoqueInsuficienteException.class,
                () -> pedidoService.criarPedido(1L, Map.of(2L, muito)));
    }
}

