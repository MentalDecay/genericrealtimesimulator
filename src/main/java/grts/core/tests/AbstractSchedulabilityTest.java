package grts.core.tests;

import grts.core.architecture.Architecture;

import java.util.Optional;

public abstract class AbstractSchedulabilityTest implements SchedulabilityTest{
    private final Architecture architecture;

    protected AbstractSchedulabilityTest(Architecture architecture) {
        this.architecture = architecture;
    }

    protected AbstractSchedulabilityTest(){
        architecture = null;
    }

    @Override
    public Optional<Architecture> getArchitecture() {
        return Optional.ofNullable(architecture);
    }

}
