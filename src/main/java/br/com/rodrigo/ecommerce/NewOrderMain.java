package br.com.rodrigo.ecommerce;


import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/*
Levantar o Zookeeper;
Levantar o Kafka
 */
public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //Criando um produtor (producer)
        try(var oderDispatcher = new KafkaDispatcher<Order>()) {
            try(var emailDispatcher = new KafkaDispatcher<String>()) {
                //Vou fazer o produtor enviar 10 mensagens, para dois tópicos
                for (var i = 0; i < 10; i++) {

                    //Criando um id aleatório para o userId de Pedido
                    var userId = UUID.randomUUID().toString();

                    //Criando um id aleatório para o orderId de Pedido
                    var orderId = UUID.randomUUID().toString();

                    //Criando um valor aleatório do tipo BigDecimal
                    var amount = new BigDecimal(Math.random() * 5000 + 1);

                    //Criando um objeto Order
                    var order = new Order(userId, orderId, amount);

                    //Fazendo o produtor enviar a mensagem para o tópico ECOMMERCE_NEW_ORDER
                    oderDispatcher.send("ECOMMERCE_NEW_ORDER", userId, order);

                    //Fazendo o produtor enviar a mensagem para o tópico ECOMMERCE_SEND_EMAIL
                    var email = "Thank you for your order! We are processing your order!";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userId, email);
                }
            }
        }
    }
}