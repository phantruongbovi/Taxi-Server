package taxi.service;

import com.proto.taxi.*;
import io.grpc.stub.StreamObserver;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.stream.Stream;

public class DriverLocationServiceImpl extends DriverLocationServiceGrpc.DriverLocationServiceImplBase {
    @Override
    public StreamObserver<UpdateLocationRequest> updateLocation(StreamObserver<UpdateLocationResponse> responseObserver) {
        StreamObserver<UpdateLocationRequest> requestStreamObserver = new StreamObserver<UpdateLocationRequest>() {
            int count = 0;
            @Override
            public void onNext(UpdateLocationRequest value) {
                int typeCar = value.getTypeCar();
                double longi = value.getLongitude();
                double lati = value.getLatitude();
                String idCar = value.getIdCard();
                /*typeCar
                1: bike
                2: car
                3: extra car
                */
                if (typeCar==1){
                    updateCar("type1", idCar, longi, lati);
                }
                else if(typeCar==2){
                    updateCar("type2", idCar, longi, lati);
                }
                else  if(typeCar==3){
                    updateCar("type3", idCar, longi, lati);
                }
                count +=1;
                System.out.println(count);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                UpdateLocationResponse response = UpdateLocationResponse.newBuilder().setResult(count).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
        return requestStreamObserver;
    }
    private void updateCar(String typeC, String idCard, double longi, double lati){
        try(Jedis jedis = new Jedis("20.195.100.123", 6379)) {
            jedis.geoadd(typeC, longi, lati, idCard);
        }
    }
}
