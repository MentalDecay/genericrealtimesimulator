package grts.core.network;

import grts.core.schedulable.AbstractRecurrentTask;

import java.util.Collections;
import java.util.List;

public class GlobalNetwork {
    private final List<CANNetwork> networks;

    public GlobalNetwork(List<CANNetwork> networks) {
        this.networks = networks;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Global Network :\n");
        networks.forEach(network -> stringBuilder.append(network.toString()).append("\n"));
        return stringBuilder.toString();
//        return "Global Network : \n" + networks.stream().map(canNetwork -> canNetwork.toString()+"\n");
    }

    public List<CANNetwork> getNetworks() {
        return Collections.unmodifiableList(networks);
    }

    public boolean checkValidity(){
        return !networks.stream().anyMatch(canNetwork -> canNetwork.getExternalSchedulables().entrySet().stream().anyMatch(abstractRecurrentTaskCANNetworkEntry -> {
            AbstractRecurrentTask task = abstractRecurrentTaskCANNetworkEntry.getKey();
            CANNetwork network = abstractRecurrentTaskCANNetworkEntry.getValue();
            return !network.getInternalSchedulables().contains(task);
        }));
    }
}