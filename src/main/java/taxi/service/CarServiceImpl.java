package taxi.service;

import com.proto.taxi.CarServiceGrpc;
import com.proto.taxi.Driver;
import com.proto.taxi.getNearlyCarRequest;
import com.proto.taxi.getNearlyCarResponse;
import io.grpc.internal.JsonUtil;
import io.grpc.stub.StreamObserver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.GeoRadiusParam;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Arrays;
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
            getCar("type1", request, responseObserver, podName, typeCar);
        }
        else if(typeCar==2){
            getCar("type2", request, responseObserver, podName, typeCar);
        }
        else  if(typeCar==3){
            getCar("type3", request, responseObserver, podName, typeCar);
        }

    }

    private void getCar(String typeC, getNearlyCarRequest request, StreamObserver<getNearlyCarResponse> responseObserver, String podName, int typeCar){
        try(Jedis jedis = new Jedis("127.0.0.1", 6379)) {
            getNearlyCarRequest value = request;
            double longitude = value.getLongitude();
            double latitude = value.getLatitude();
            int idRequest = value.getIdRequest();
            double timeStart = System.nanoTime();
            List<GeoRadiusResponse> responses = jedis.georadiusReadonly(typeC, longitude, latitude, 1, GeoUnit.KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(5).withCoord());;
            int distance = 2;
            int total = responses.size();
            while(distance < 7 && total < 5){
                responses = jedis.georadiusReadonly(typeC, longitude, latitude, distance, GeoUnit.KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(5).withCoord());
                total = responses.size();
                distance+=1;
            }
            getNearlyCarResponse.Builder listDirver = getNearlyCarResponse.newBuilder();
            if(responses.size()==0){
                Driver driver = Driver.newBuilder()
                        .setIdCard("")
                        .setLongitude(0)
                        .setLatitude(0)
                        .build();
                getNearlyCarResponse response = listDirver.setDriver(driver).setDistance(0).setTypecar(typeCar).setIdRequest(Integer.toString(idRequest)).setTime(0).setNameServer(podName).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }

            String des = longitude+ "," + latitude;
            String table = "";
            int count = 0 ;
            for (GeoRadiusResponse respons : responses) {
                if(count != 4){
                    table += respons.getCoordinate().getLongitude() + "," + respons.getCoordinate().getLatitude() + ";";
                }
                else{
                    table += respons.getCoordinate().getLongitude() + "," + respons.getCoordinate().getLatitude();
                }
                count += 1;
            }
            while(count < 5){
                table += "1" + "," + "1" + ";";
                if(count == 4){
                    table += "1" + "," + "1";
                }
                count += 1;
            }
            String rq = "http://52.139.254.49:5000/table/v1/driving/" + des + ";" + table + "?sources=0&destinations=1;2;3;4;5";
            JSONObject json = readJsonFromUrl(rq);
            double minDistance = Double.MAX_VALUE;
            JSONArray arr = json.getJSONArray("destinations");
            int index = 0;
            for(int i = 0; i < arr.length(); i++){
                if(minDistance > arr.getJSONObject(i).getDouble("distance")){
                    index = i;
                    minDistance = arr.getJSONObject(i).getDouble("distance");
                }
            }
            double timeEnd = System.nanoTime();
            GeoRadiusResponse respons = responses.get(index);
            Driver driver = Driver.newBuilder()
                    .setIdCard(respons.getMemberByString())
                    .setLongitude(respons.getCoordinate().getLongitude())
                    .setLatitude(respons.getCoordinate().getLatitude())
                    .build();
            getNearlyCarResponse response = listDirver.setDriver(driver).setDistance(minDistance).setTypecar(typeCar).setIdRequest(Integer.toString(idRequest)).setTime(timeEnd - timeStart).setNameServer(podName).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IOException e) {
            e.printStackTrace();
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
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
}
