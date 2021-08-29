package taxi.client;

import com.proto.taxi.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.sql.Time;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class Client {
    public static void main(String[] args) throws FileNotFoundException {
        Client main = new Client();
        main.run();
    }

    private void run() throws FileNotFoundException {
        //for(int i =0; i < 10; i++){

        //ping(channel1);
        //updateLocation(channel1);
        for(int i =0; i< 100 ;i++) {
            ManagedChannel channel1 = ManagedChannelBuilder.forAddress("20.198.149.232", 50001)
                    .usePlaintext()
                    .build();

            getNearlyCar1(channel1, i+1);

        }
            //getNearlyCar(channel1);

       // }

    }

    private void ping(ManagedChannel channel) {
        int n = 0;
      // while(n < 500){
        TestServerGrpc.TestServerBlockingStub stub = TestServerGrpc.newBlockingStub(channel);
        HelloResponse response = stub.hello(HelloRequest.newBuilder().build());
        System.out.println(response.getPong());
        //    n+=1;
        //}

    }

    // unary
    private void getNearlyCar1(ManagedChannel channel, int idRequest) {
        CarServiceGrpc.CarServiceBlockingStub stub = CarServiceGrpc.newBlockingStub(channel);
        double longitude;
        double latitude;
        int typeCar = idRequest%3;
        longitude = getRandomLocation(2);
        latitude = getRandomLocation(1);
        getNearlyCarRequest request = getNearlyCarRequest.newBuilder()
                .setIdRequest(idRequest)
                .setLongitude(longitude)
                .setLatitude(latitude)
                .setTypeCar(typeCar+1)
                .build();
        double timeStart = System.currentTimeMillis();
        getNearlyCarResponse value = stub.getNearlyCar1(request);
        double timeSEnd = System.currentTimeMillis();
        System.out.println("-----------------------");
        System.out.println("ID request: " + value.getIdRequest());
        System.out.println("Server Response: " + value.getNameServer());
        for(int i = 0 ; i < value.getDriverCount() ; i++){
            System.out.println("Driver: " + value.getDriver(i).getIdCard());
            System.out.println("    Longitude: " + value.getDriver(i).getLongitude());
            System.out.println("    Latitude: " + value.getDriver(i).getLatitude());
        }
        System.out.println("Time claim response: " + (timeSEnd - timeStart) + " miliS");



    }

    private void updateLocation(ManagedChannel channel) throws FileNotFoundException {
        DriverLocationServiceGrpc.DriverLocationServiceStub stub = DriverLocationServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<UpdateLocationRequest> requestStreamObserver = stub.updateLocation(new StreamObserver<UpdateLocationResponse>() {
            @Override
            public void onNext(UpdateLocationResponse value) {
                System.out.println("Total location have been updated: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Completed claim Response!");
                latch.countDown();
            }
        });
        File myObj = new File("Location.txt");
        Scanner myReader = new Scanner(myObj);
        while(myReader.hasNextLine()){
            String value = myReader.nextLine();
            String[] splited = value.split("\\s+");
            UpdateLocationRequest request = UpdateLocationRequest.newBuilder().setTypeCar(Integer.parseInt(splited[3]))
                    .setLongitude(Double.parseDouble(splited[0]))
                    .setLatitude(Double.parseDouble(splited[1]))
                    .setIdCard(splited[2]).build();
            requestStreamObserver.onNext(request);
        }
        requestStreamObserver.onCompleted();

        try {
            latch.await(1000L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private double getRandomLocation(int num){
        double response;
        if(num == 1){ // lat
            while(true){
                response = ThreadLocalRandom.current().nextDouble();
                double y1 = 8.563582;
                double y2 = 23.418351;
                response =(Math.round(response * 1000000.0) / 1000000.0) + Math.floor(Math.random() * (y2 - y1 + 1) + y1);
                if(response < y2 && response >= y1){
                    return response;
                }
            }
        }
        else{ // lat
            while(true){
                response = ThreadLocalRandom.current().nextDouble();
                double x1 = 102.103044;
                double x2 = 109.472468;
                response =(Math.round(response * 1000000.0) / 1000000.0) + Math.floor(Math.random() * (x2 - x1 + 1) + x1);
                if(response < x2 && response >= x1){
                    return response;
                }
            }
        }
    }
}
