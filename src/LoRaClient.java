import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;

public class LoRaClient implements WebSocket.Listener {
    String hexTemperature, hexHumidity, hexCo2;
    int decTemperature, decHumidity, decCo2;
    MongoDB mongoDB;

    public LoRaClient() {
        HttpClient client = HttpClient.newHttpClient();
        CompletableFuture<WebSocket> ws = client.newWebSocketBuilder()
                .buildAsync(URI.create("wss://iotnet.teracom.dk/app?token=vnoReQAAABFpb3RuZXQudGVyYWNvbS5kayBQL_XgDRoyHdCNa9UnB1U="), this);
        hexTemperature="";
        hexHumidity="";
        hexCo2="";
        decTemperature=0;
        decHumidity=0;
        decCo2=0;
        mongoDB = new MongoDB();
    }

    //onOpen()
    public void onOpen(WebSocket webSocket) {
        // This WebSocket will invoke onText, onBinary, onPing, onPong or onClose methods on the associated listener (i.e. receive methods) up to n more times
        webSocket.request(1);
        System.out.println("WebSocket Listener has been opened for requests.");
    }

    //onError()
    public void onError​(WebSocket webSocket, Throwable error) {
        System.out.println("A " + error.getCause() + " exception was thrown.");
        System.out.println("Message: " + error.getLocalizedMessage());
        webSocket.abort();
    };
    //onClose()
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        System.out.println("WebSocket closed!");
        System.out.println("Status:" + statusCode + " Reason: " + reason);
        return new CompletableFuture().completedFuture("onClose() completed.").thenAccept(System.out::println);
    };
    //onPing()
    public CompletionStage<?> onPing​(WebSocket webSocket, ByteBuffer message) {
        webSocket.request(1);
        System.out.println("Ping: Client ---> Server");
        System.out.println(message.asCharBuffer().toString());
        return new CompletableFuture().completedFuture("Ping completed.").thenAccept(System.out::println);
    };
    //onPong()
    public CompletionStage<?> onPong​(WebSocket webSocket, ByteBuffer message) {
        webSocket.request(1);
        System.out.println("Pong: Client ---> Server");
        System.out.println(message.asCharBuffer().toString());
        return new CompletableFuture().completedFuture("Pong completed.").thenAccept(System.out::println);
    };
    //onText()
    public CompletionStage<?> onText​(WebSocket webSocket, CharSequence data, boolean last) {
       // System.out.println(data);
        getData(data.toString());
        webSocket.request(1);
        return null; //new CompletableFuture().completedFuture("onText() completed.").thenAccept(System.out::println);
    }

    private void getData(String jsonTelegram) {
        String findBy = "data";
        ArrayList<String> list = new ArrayList<>(Arrays.asList(jsonTelegram.split(",")));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains(findBy)) {
                System.out.println(list.get(i));
                hexTemperature = list.get(i).substring(8, 12);
                decTemperature = Integer.parseInt(hexTemperature,16);
                hexHumidity = list.get(i).substring(12, 16);
                decHumidity = Integer.parseInt(hexHumidity, 16);
                hexCo2 = list.get(i).substring(16, 20);
                decCo2=Integer.parseInt(hexCo2, 16);
                int temperature = decTemperature/10;
                int humidity = decHumidity/10;
                int co2 = decCo2/10;
                mongoDB.insertNewData("measurements", temperature, humidity, co2);
            }

        }
    }
}