package br.com.rodrigo.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/*
Levantar o Zookeeper;
Levantar o Kafka
 */
public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //Forçar envio de 10 mensagens!
        for(int i =0; i< 100; i++){

        //Criando um producer com chave em string e valor em string (uma mensagem é feita de chave e valor)
        //Precisamos de um properties, ou seja, uma configuração. Podemos criar aqui ou ler de um arquivo.
        var producer = new KafkaProducer<String,String>(properties());

        //Criando esse valor para ser chave da mensagem. Para teste podem ser iguais.
        var key = UUID.randomUUID().toString();

        //Criando esse valor para ser valor (conteudo) da mensagem. Para teste podem ser iguais.
        var value = "123,456,0789";

        //Criando a mensagem, o seu primeiro parâmetro é o tópico: ECOMMERCE_NEW_ORDER, depois vem chave e valor.
        var record = new ProducerRecord<String, String>("ECOMMERCE_NEW_ORDER", key, value);

        //Enviar mensagem
        //O método .send não é blocante (ele executa no futuro), ele é assíncrono, não fica esperando. Por isso colocamos após o .send() o .get()
        //Com .get() forço a espera!
        //Dentro do send coloco a mensagem que quero enviar ao Kafka (record)
        //Dentro do .send() coloco um callback (data,ex), com ele poderemos obter respostas se a mensagem criada deu certo ou não
        Callback callback = (data, ex) -> {
            if (ex != null) {
                return;
            }
            //Se der certo vai rolar o print:
            System.out.println("Sucesso enviando " + data.topic() + ":::partition " + data.partition() + "/ offset " + data.offset() + "/" + data.timestamp());
        };
        producer.send(record, callback).get();

        var email = "Obrigado, sua compra está sendo processada!";
        var emailRecord = new ProducerRecord<>("ECOMMERCE_SEND_EMAIL", email, email);
        producer.send(emailRecord,callback).get();
        }
    }

    //Criando as configurações do Producer
    private static Properties properties() {
        var properties = new Properties();

        //Indico onde vamos nos conectar, onde está rodando o kafka
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");

        //Aqui indico o serializador da chave, se minha chave é String preciso de serializador de String
        //Nesse caso o serializador vai pegar a String da chave e transformar em bytes;
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //Aqui indico o serializador do valor, se valor é String preciso de serializador de String
        //Nesse caso o serializador vai pegar a String do valor e transformar em bytes;
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }
}
