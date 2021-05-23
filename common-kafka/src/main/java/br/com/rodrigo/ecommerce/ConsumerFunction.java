package br.com.rodrigo.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/*
Todos consumidores possuem uma função. Essa interface visa generalizar essas funções.
Assim eu posso falar que KafkaService para criar um consumidor precisa receber a referencia de uma função,
e que o tipo dessa função será ConsumerFunction. A referencia de função para executa a função basta dar um .consume().
 */

public interface ConsumerFunction<T> {
    void consume(ConsumerRecord<String, T> record);
}
