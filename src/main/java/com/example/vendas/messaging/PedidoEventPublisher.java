package com.example.vendas.messaging;

import com.example.contracts.KafkaTopics;
import com.example.contracts.PedidoEvent;
import com.example.vendas.domain.model.ItemPedido;
import com.example.vendas.domain.model.Pedido;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Component
public class PedidoEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final boolean enabled;

    public PedidoEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                @Value("${app.kafka.enabled:true}") boolean enabled) {
        this.kafkaTemplate = kafkaTemplate;
        this.enabled = enabled;
    }

    public void publishAfterCommit(String type, Pedido pedido) {
        if (!enabled) {
            return; // Kafka desabilitado (ex.: ambiente de teste)
        }
        Runnable task = () -> {
            var event = new PedidoEvent(
                    type,
                    pedido.getId(),
                    pedido.getCliente().getId(),
                    pedido.getStatus().name(),
                    pedido.getTotal(),
                    pedido.getCriadoEm(),
                    mapItens(pedido.getItens())
            );
            String key = "pedido-" + pedido.getId();
            kafkaTemplate.send(KafkaTopics.PEDIDOS_EVENTS, key, event);
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { task.run(); }
            });
        } else {
            task.run();
        }
    }

    private List<PedidoEvent.Item> mapItens(List<ItemPedido> itens) {
        return itens.stream()
                .map(i -> new PedidoEvent.Item(
                        i.getProduto().getId(),
                        i.getProduto().getNome(),
                        i.getQuantidade(),
                        i.getPrecoUnitario(),
                        i.getSubtotal()
                ))
                .toList();
    }
}
