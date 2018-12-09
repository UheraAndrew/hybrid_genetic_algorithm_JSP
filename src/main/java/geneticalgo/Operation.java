package geneticalgo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
class Operation {
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

}
