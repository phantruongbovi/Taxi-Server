package taxi.service;

import com.proto.taxi.CarServiceGrpc;
import com.proto.taxi.Driver;
import com.proto.taxi.getNearlyCarRequest;
import com.proto.taxi.getNearlyCarResponse;
import io.grpc.internal.JsonUtil;
import io.grpc.stub.StreamObserver;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.geo.GeoRadiusParam;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class CarServiceImpl extends CarServiceGrpc.CarServiceImplBase {
    @Override
    public void getNearlyCar1(getNearlyCarRequest request, StreamObserver<getNearlyCarResponse> responseObserver) {
        String podName = getHostName();
        int typeCar = request.getTypeCar();
        /*typeCar
        1: bike
        2: car
        3: extra car
        */
        if (typeCar==1){
            getCar("20.184.57.114", request, responseObserver, podName, typeCar);
        }
        else if(typeCar==2){
            getCar("20.184.59.153", request, responseObserver, podName, typeCar);
        }
        else  if(typeCar==3){
            getCar("20.184.59.186", request, responseObserver, podName, typeCar);
        }

    }

    private void getCar(String host, getNearlyCarRequest request, StreamObserver<getNearlyCarResponse> responseObserver, String podName, int typeCar){
        System.out.println("yes");
        try(Jedis jedis = new Jedis(host, 6379)) {
            getNearlyCarRequest value = request;
            double longitude = value.getLongitude();
            double latitude = value.getLatitude();
            int idRequest = value.getIdRequest();
            double timeStart = System.nanoTime();
            List<GeoRadiusResponse> responses;
            responses = jedis.georadius("LocationDriver", longitude, latitude, 500, GeoUnit.KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(5).withCoord());
            getNearlyCarResponse.Builder listDirver = getNearlyCarResponse.newBuilder();
            Driver driver;
            for (GeoRadiusResponse respons : responses) {
                driver = Driver.newBuilder()
                        .setIdCard(respons.getMemberByString())
                        .setLongitude(respons.getCoordinate().getLongitude())
                        .setLatitude(respons.getCoordinate().getLatitude())
                        .build();
                listDirver.addDriver(driver);
            }
            double timeEnd = System.nanoTime();
            getNearlyCarResponse response = listDirver.setIdRequest(idRequest + "(" + typeCar + ")").setTime(timeEnd - timeStart).setNameServer(podName).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
    private String getHostName(){
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
