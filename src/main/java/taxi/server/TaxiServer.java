package taxi.server;

import io.grpc.DecompressorRegistry;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import taxi.service.CarServiceImpl;
import taxi.service.DriverLocationServiceImpl;
import taxi.service.TestServer;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaxiServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello, This is Taxi Server!");

        Server server = ServerBuilder.forPort(50001)
                .addService(new DriverLocationServiceImpl())
                .addService(new CarServiceImpl())
                .addService(new TestServer())
                .build();
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Request shutdown server!");
            server.shutdown();
            System.out.println("Successfully stopped server");
        }));
        server.awaitTermination();
    }
}
