package grts.core.architecture;

import java.util.LinkedList;

public class Resources {

    private static class Resource {
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

    /**
     * Adds a resource.
     * @param type The type of the resource (String).
     * @param id The id of the resource (String). Should be single.
     */
    public void addResource(String type, String id){
        if(resources.stream().anyMatch(resource -> resource.id.equals(id))){
            throw new IllegalArgumentException("A resource with this id is already in the resources list.");
        }
        resources.add(new Resource(type, id));
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Resources :\n");
        resources.forEach(resource -> stringBuilder.append("\t").append(resource).append("\n"));
        return stringBuilder.toString();
    }
}
