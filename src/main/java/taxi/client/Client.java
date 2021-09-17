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

public class Client{
    private double totalTime = 0;
    private int loss = 0;
    private int claimed = 0;
    private int totalDriver = 1000;
    private int typeCar = 3;
    public static void main(String[] args) throws FileNotFoundException {
        Client main = new Client();
        main.run();
    }

    private void run() throws FileNotFoundException {
        Scanner obj = new Scanner(System.in);
        System.out.println("1. Request Driver");
        System.out.println("2. Update Location");
        System.out.println("Lựa chọn: ");
        int choose = obj.nextInt();
        if(choose == 1) {
            int request = 1000; // Tổng số request
            int threads = 3; // Số thread gửi request
            ExecutorService executorService = Executors.newFixedThreadPool(threads);
            for (int i = 0; i < request; i++) {
                int finalI = i;
                executorService.submit(() -> {
                    try {
                        ManagedChannel channel1 = ManagedChannelBuilder.forAddress("localhost", 50001)
                                .usePlaintext()
                                .build();
                        getNearlyCar1(channel1, finalI + 1);
                        claimed += 1;
                    } catch (Exception e) {
                        loss += 1;
                    }
                });
            }
            executorService.shutdown();
            while (!executorService.isTerminated()) {
            }

            System.out.println("-----------------------");
            System.out.println("Total Time: " + totalTime / request + " Millis");
            System.out.println("Loss: " + loss);
            System.out.println("Claimed: " + claimed);
        }
        else if(choose==2){
            double timeStart = System.currentTimeMillis();
            ManagedChannel channel1 = ManagedChannelBuilder.forAddress("localhost", 50001)
                    .usePlaintext()
                    .build();
            updateLocation(channel1);
            double timeEnd = System.currentTimeMillis();
            System.out.println("Total Time: " + (timeEnd-timeStart));
        }
    }

    private void ping(ManagedChannel channel) {
        int n = 0;
        TestServerGrpc.TestServerBlockingStub stub = TestServerGrpc.newBlockingStub(channel);
        HelloResponse response = stub.hello(HelloRequest.newBuilder().build());
        System.out.println(response.getPong());
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
        totalTime = totalTime + timeSEnd -timeStart;
        if(value.getTime() == 0){
            System.out.println("-----------------------");
            System.out.println("Không có tài xế xung quanh!!");
            System.out.println("ID request: " + value.getIdRequest());
            System.out.println("Server Request: " + value.getNameServer());
            System.out.println("Time: " + (timeSEnd -timeStart) + " ms");
        }
        else{
            System.out.println("-----------------------");
            System.out.println("ID request: " + value.getIdRequest());
            System.out.println("Driver: " + value.getDriver().getIdCard());
            System.out.println("Distance: " + value.getDistance());
            System.out.println("Type Car: " + value.getTypecar());
            System.out.println("Server Request: " + value.getNameServer());
            System.out.println("Time: " + (timeSEnd -timeStart) + " ms");
        }
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
        int count = 0;
        double longitude;
        double latitude;
        double timeStart = System.currentTimeMillis();
        while(count < totalDriver){
            System.out.println(count);
            longitude = getRandomLocation(2);
            latitude = getRandomLocation(1);
            UpdateLocationRequest request = UpdateLocationRequest.newBuilder().setTypeCar(count%typeCar+1)
                    .setLongitude(longitude)
                    .setLatitude(latitude)
                    .setIdCard(Integer.toString(count)).build();
            requestStreamObserver.onNext(request);
            count += 1;
        }
        double timeSEnd = System.currentTimeMillis();
        System.out.println("Time: " + (timeSEnd -timeStart) + " ms");
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

