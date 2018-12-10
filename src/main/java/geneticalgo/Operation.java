package geneticalgo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
class Operation implements Cloneable {
    private int jobIndex;
    private int machineIndex;
    private double prioriti;
    private double delay;
    private int processingTime;
    private int F;
    private int FMC;

    Operation(int jobIndex, int machineIndex, int processingTime) {
        this.jobIndex = jobIndex;
        this.machineIndex = machineIndex;
        this.processingTime = processingTime;
    }

    @Override
    protected Object clone() {
        Object o = null;
        try {
            o =  super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return jobIndex == operation.jobIndex &&
                machineIndex == operation.machineIndex &&
                Double.compare(operation.prioriti, prioriti) == 0 &&
                Double.compare(operation.delay, delay) == 0 &&
                processingTime == operation.processingTime &&
                F == operation.F &&
                FMC == operation.FMC;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobIndex, machineIndex, prioriti, delay, processingTime, F, FMC);
    }
}
