package grts.core.taskset;

import java.util.List;

public class GlobalNetwork {
    private final List<CANNetwork> networks;

    public GlobalNetwork(List<CANNetwork> networks) {
        this.networks = networks;
    }

    @Override
    public String toString() {
        return "Global Network : \n" + networks.stream().map(canNetwork -> canNetwork.toString()+"\n");
    }
}
