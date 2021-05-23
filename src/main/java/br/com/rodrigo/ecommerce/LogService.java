package br.com.rodrigo.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/*
Levantar o Zookeeper;
Levantar o Kafka
 */
public class LogService {

    public static void main(String[] args) {

        //Crio um objeto do mesmo tipo da classe que estou,
        // para eu poder repassar ao KafkaService a referencia de funçcao que eu quero que seja executado
        var logService = new LogService();

        //Para criar um consumidor LogService vou utilizar o KafkaService,
        //Para ele vou passar via parâmetro: O identificador do consumidor, o tópico que ele deve escutar, e a referencia da função que ele deve executar
        try(var service = new KafkaService(LogService.class.getSimpleName(),
                Pattern.compile("ECOMMERCE.*"),
                logService::parse,
                String.class,
                Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))) {

            //Aqui vou colocar o consumidor para ficar escutando o tópico. E quando ouvir mensagem executar sua função!
            service.run();
        }
    }


    //Essa é a função específica do Consumidor EmailService.
    //Vou passar sua referencia para KafkaService
    private void parse(ConsumerRecord<String,String> record) {
        System.out.println("------------------------------------------");
        System.out.println("Send email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignoring
            e.printStackTrace();
        }
        System.out.println("Email sent");
    }
}
