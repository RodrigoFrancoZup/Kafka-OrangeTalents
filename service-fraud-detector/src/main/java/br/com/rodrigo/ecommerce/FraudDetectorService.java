package br.com.rodrigo.ecommerce;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import java.util.HashMap;

/*
Levantar o Zookeeper;
Levantar o Kafka
 */
public class FraudDetectorService {

    public static void main(String[] args) {

        //Crio um objeto do mesmo tipo da classe que estou,
        // para eu poder repassar ao KafkaService a referencia de funçcao que eu quero que seja executado
        var fraudDetectorService = new FraudDetectorService();

        //Para criar um consumidor FraudDetectorService vou utilizar o KafkaService,
        //Para ele vou passar via parâmetro: O identificador do consumidor, o tópico que ele deve escutar, e a referencia da função que ele deve executar
        try(var service = new KafkaService(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudDetectorService::parse,
                Order.class,
                new HashMap<>())) {

            //Aqui vou colocar o consumidor para ficar escutando o tópico. E quando ouver mensagem executar sua função!
            service.run();
        }
    }


    //Essa é a função específica do Consumidor FraudDetectorService.
    //Vou passar sua referencia para KafkaService
    private void parse(ConsumerRecord<String,Order> record) {
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
