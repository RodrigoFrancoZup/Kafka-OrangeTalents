package br.com.rodrigo.ecommerce;


import java.util.UUID;
import java.util.concurrent.ExecutionException;

/*
Levantar o Zookeeper;
Levantar o Kafka
 */
public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //Criando um produtor (producer)
        try(var dispatcher = new KafkaDispatcher()) {

            //Vou fazer o produtor enviar 10 mensagens, para dois tópicos
            for (var i = 0; i < 10; i++) {

                //Criando uma chave aleatória para mensagem
                var key = UUID.randomUUID().toString();

                //Criando o valor (conteudo) da mensagem:
                var value = key + ",67523,1234";

                //Fazendo o produtor enviar a mensagem para o tópico ECOMMERCE_NEW_ORDER
                dispatcher.send("ECOMMERCE_NEW_ORDER", key, value);

                //Fazendo o produtor enviar a mensagem para o tópico ECOMMERCE_SEND_EMAIL
                var email = "Thank you for your order! We are processing your order!";
                dispatcher.send("ECOMMERCE_SEND_EMAIL", key, email);
            }
        }
    }
}