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
//    public static void main(String[] args) {
//        final int nServers = 1;
//        ExecutorService executorService = Executors.newFixedThreadPool(nServers);
//        for(int i =0; i< nServers ; i++){
//            String name = "Server_" + i;
//            int port = 50001+i;
//            executorService.submit(()->{
//               try{
//                   startServer(name, port);
//               }catch (IOException | InterruptedException e){
//                   e.printStackTrace();
//               }
//            });
//        }
//    }
//
//    private static void startServer(String name, int port) throws IOException, InterruptedException {
//        Server server = ServerBuilder.forPort(port)
//                .addService(new CarServiceImpl())
//                .addService(new DriverLocationServiceImpl())
//                .addService(new TestServer())
//                .build();
//
//        server.start();
//        System.out.println(name + " server started, listening on port: " + server.getPort());
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            System.out.println("Request shutdown server!");
//            server.shutdown();
//            System.out.println("Successfully stopped server");
//        }));
//        server.awaitTermination();
//    }



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
