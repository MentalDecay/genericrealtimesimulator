package grts.core.architecture;

import java.util.LinkedList;

public class Resources {

    public static class Resource {
        private final String id;
        private final String type;


        public Resource(String type, String name) {
            this.id = name;
            this.type = type;
        }

        @Override
        public String toString() {
            return type + " : " + id;
        }
    }

    private final LinkedList<Resource> resources = new LinkedList<>();

    public void addResource(String type, String id){
        resources.add(new Resource(type, id));
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Resources :\n");
        resources.forEach(resource -> stringBuilder.append("\t").append(resource).append("\n"));
        return stringBuilder.toString();
    }
}
