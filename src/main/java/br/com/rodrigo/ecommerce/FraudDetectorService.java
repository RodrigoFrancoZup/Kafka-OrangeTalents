package br.com.rodrigo.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/*
Levantar o Zookeeper;
Levantar o Kafka
 */
public class FraudDetectorService {

    public static void main(String[] args) {
        //Criando um consumer, que consumirá mensagem com chave em string e valor em string
        //Precisamos de um properties, ou seja, uma configuração. Podemos criar aqui ou ler de um arquivo.
        var consumer = new KafkaConsumer<String, String>(properties());

        //Indico  qual tópico vou consumir as mensagens, normalmente escolhemos só UM,
        // Por parâmetro passo uma Collection qualquer e o Nome do Tópico que vou escutar
        consumer.subscribe(Collections.singletonList("ECOMMERCE_NEW_ORDER"));

        // Vou manter isso em looping infinito para ficar ouvindo sempre!
        while(true) {

            //.poll() é para meu consumidor ficar perguntando se tem mensagem, ficar ouvindo, passo um tempo de duração,
            //as mensagens que eu escutar vai cair na variavel records
            var records = consumer.poll(Duration.ofMillis(100));

            //Verifico se há mensagens, se tiver vou mostrá-las!
            if (!records.isEmpty()) {
                System.out.println("Encontrei " + records.count() +  " registros");
                for (var record : records) {
                    System.out.println("=========================================");
                    System.out.println("Processando novo pedido, checando fraude!");
                    System.out.println(record.key());
                    System.out.println(record.value());
                    System.out.println(record.partition());
                    System.out.println(record.offset());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Pedido processador");
                }
            }
        }
    }

    //Criando as configurações do Consumer
    private static Properties properties() {
        var properties = new Properties();

        //Indico onde vamos nos conectar, onde está rodando o kafka, onde vamos escutar
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");

        //Aqui indico o deserializador da chave, se minha chave era String preciso de deserializador de String
        //Nesse caso o deserializador vai pegar a chave que esta em bytes e transformar em String;
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        //Aqui indico o deserializador do valor, se meu valor era String preciso de deserializador de String
        //Nesse caso o deserializador vai pegar o valor que esta em bytes e transformar em String;
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        //Estou colocando esse serviço em um grupo (dei ao grupo o nome da classe)
        //Adicionar um serviço em grupo, garante que esse serviço receberá todas as mensagens que ele está ouvindo
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, FraudDetectorService.class.getSimpleName());

        return properties;
    }
}
