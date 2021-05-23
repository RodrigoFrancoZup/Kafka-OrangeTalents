package br.com.rodrigo.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

class KafkaDispatcher<T> implements Closeable {

    //Essa será a referência de um objeto Producer, que irá escrever mensagem com chave e valor String;
    private final KafkaProducer<String, T> producer;

    //Ao criar um um objeto KafkaProducer um producer já é criado, pois colocamos sua criação nesse construtor
    KafkaDispatcher() {
        //Criando um produtor (producer).
        this.producer = new KafkaProducer<>(properties());
    }

    //Propriedades para se criar um produtor (producer). Não temos parâmetros por enquanto.
    //Caso tenha uma configuração específica para o Produtor, vamos recebé-la via parâmetro.
    private static Properties properties() {
        var properties = new Properties();

        //Indico onde está rodando o kafka
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");

        //Indico o serializador da chave da mensagem, como minha chave é string preciso de um serializador de string
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //Indico o serializador do valor (conteúdo) da mensagem, como meu valor é string preciso de um serializador de string
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        return properties;
    }

    //Todos produtores (producer) enviam mensagem. Logo temos esse método para isso, enviar mensagem
    //Via parâmetro tenho para qual tópico enviar, qual chave da mensagem e qual seu conteúdo (valor)
    //Para enviar uma mensagem, eu preciso criar um objeto Record
    //Esse callback criado é para saber se a mensagem foi enviada com sucesso
    void send(String topic, String key, T value) throws ExecutionException, InterruptedException {
        var record = new ProducerRecord<>(topic, key, value);
        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println("sucesso enviando " + data.topic() + ":::partition " + data.partition() + "/ offset " + data.offset() + "/ timestamp " + data.timestamp());
        };

        //O produtor (producer) envia agora de fato a sua mensagem específica.
        producer.send(record, callback).get();
    }

    @Override
    public void close()  {
        producer.close();
    }
}