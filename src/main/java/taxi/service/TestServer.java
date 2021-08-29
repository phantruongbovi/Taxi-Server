package taxi.service;

import com.proto.taxi.HelloRequest;
import com.proto.taxi.HelloResponse;
import com.proto.taxi.TestServerGrpc;
import io.grpc.stub.StreamObserver;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestServer extends TestServerGrpc.TestServerImplBase {
    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        String podName = getHostName();
        System.out.println("Client call Test Server");
        responseObserver.onNext(HelloResponse.newBuilder().setPong("pong").build());
        responseObserver.onCompleted();
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
