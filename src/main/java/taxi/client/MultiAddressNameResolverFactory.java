package taxi.client;


import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.SocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class MultiAddressNameResolverFactory extends NameResolverProvider {
    final List<EquivalentAddressGroup> address;
    MultiAddressNameResolverFactory(SocketAddress... addresses){
        this.address = Arrays.stream(addresses)
                .map(EquivalentAddressGroup::new)
                .collect(Collectors.toList());
    }

    public NameResolver newNameResolver(URI notUsedUri, NameResolver.Args args){
        return new NameResolver() {
            @Override
            public String getServiceAuthority() {
                return "fakeAuthority";
            }
            public void start(Listener2 listener){
                listener.onResult(ResolutionResult.newBuilder().setAddresses(address).setAttributes(Attributes.EMPTY).build());
            }

            @Override
            public void shutdown() {

            }
        };
    }

    @Override
    public String getDefaultScheme(){
        return "multiaddress";
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 0;
    }
}