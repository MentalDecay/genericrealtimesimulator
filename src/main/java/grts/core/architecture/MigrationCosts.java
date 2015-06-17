package grts.core.architecture;

import java.util.LinkedList;

public class MigrationCosts {

    private static class MigrationCost {
        private final int from;
        private final int to;
        private final long cost;

        /**
         * Creates a new Migration Cost
         * @param from The processor id from where the migration is done.
         * @param to The processor id to where the migration is done.
         * @param cost The cost of the migration.
         */
        public MigrationCost(int from, int to, long cost) {
            this.from = from;
            this.to = to;
            this.cost = cost;
        }

        /**
         * Get the processor id from where the migration is done.
         * @return The processor id from where the migration is done.
         */
        public int getFrom() {
            return from;
        }

        /**
         * Get the processor id to where the migration is done.
         * @return The processor id from to the migration is done.
         */
        public int getTo() {
            return to;
        }

        /**
         * Get cost of the migration.
         * @return The cost of the migration.
         */
        public long getCost() {
            return cost;
        }

        @Override
        public String toString() {
            return "from : " + from + " to : " + to + " cost : " + cost;
        }
    }

    private final LinkedList<MigrationCost> migrationCosts = new LinkedList<>();

    public void addMigrationCost(int from, int to, long cost){
        migrationCosts.add(new MigrationCost(from, to, cost));
    }

    public int nbMigrationCosts(){
        return migrationCosts.size();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Migration Costs : \n");
        migrationCosts.forEach(migrationCost -> stringBuilder.append("\t").append(migrationCost).append("\n"));
        return stringBuilder.toString();
    }
}
