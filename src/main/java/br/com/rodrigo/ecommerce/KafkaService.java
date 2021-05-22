package br.com.rodrigo.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

class KafkaService implements Closeable {
    //Essa será a referência do consumidor (service)
    private final KafkaConsumer<String, String> consumer;

    //Essa será a referência da função do consumidor (service)
    private final ConsumerFunction parse;

    //Tudo que é obrigatorio para se criar o Consumidor vou receber via parâmetro do construtor:
    KafkaService(String groupId, String topic, ConsumerFunction parse) {
        //Pego a referencia da função específica do consumidor que está sendo criado
        this.parse = parse;

        //Crio um consumidor, com as propriedades genéricas atraves do método properties() e o que é específico esto enviando via parâmetro - exemplo é goupId
        // Recebi o groupId via parâmetro do construtor de KafkaService
        this.consumer = new KafkaConsumer<>(properties(groupId));

        //Faço o consumidor ficar ouvindo o seu tópico - Recebi  o tópico via parâmetro do construtor de KafkaService
        consumer.subscribe(Collections.singletonList(topic));
    }

    //Todos consumidores escutam um tópico e executam uma função.
    // O run() será para isso, para o consumidor ficar escutando seu tópico e quando receber a mensagem executar a função!
    void run() {
        while (true) {
            //poll é para ficar escutando o tópico. Quando ouver mensagme ela cairá em records
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println("Encontrei " + records.count() + " registros");
                for (var record : records) {
                    //Executo a função específica do consumidor (service)
                    parse.consume(record);
                }
            }
        }
    }

    // Propriedades genéricas para se criar um Consumidor. Posso receber via parâmetro propriedades específicas, por ex: goupId
    private static Properties properties(String groupId) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        return properties;
    }

    @Override
    public void close() {

    }
}