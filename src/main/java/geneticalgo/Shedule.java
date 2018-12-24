package geneticalgo;


import lombok.Getter;

import java.util.*;

import static geneticalgo.Configurations.delayFactor;

public class Shedule {
    private int jobsNumber;
    private int machineNumber;
    private Operation[] operations;
    private double maxDuration;

    private Chromosome chromosome;
    private boolean isEvaluated = false;
    private int timeOfAllWorks;


    public Shedule(int[][] timeTable, Chromosome chromosome) {
        this.chromosome = chromosome;
        this.jobsNumber = timeTable.length;
        this.machineNumber = timeTable[0].length;

        this.operations = new Operation[machineNumber * jobsNumber];

        for (int i = 0; i < timeTable.length; i++) {
            for (int j = 0; j < timeTable[i].length; j++) {
                int index = i * timeTable[i].length + j;
                operations[index] = new Operation(i, j, timeTable[i][j]);

            }
        }

        for (int i = 0; i < operations.length; i++) {
            if (operations[i].getProcessingTime() > maxDuration) {
                this.maxDuration = this.operations[i].getProcessingTime();
            }
        }

        for (int i = 0; i < operations.length; i++) {
            this.operations[i].setPriority(chromosome.getGenes()[i]);
        }

        Arrays.sort(operations, (o1, o2) -> {
            if (o1.getJobIndex() != o2.getJobIndex()) {
                return 0;
            } else {
                return o1.getPriority() > o2.getPriority() ? 1 : -1;
            }

        });

    }

    public int getTimeOfAllWorks() {
        if (!isEvaluated) {
            evaluateShedule();
            isEvaluated = true;
        }
        return timeOfAllWorks;
    }

    private void evaluateShedule() {
        if (isEvaluated) {
            return;
        }
        construcShedule();
        do {
            this.timeOfAllWorks = 0;
            for (int i = 0; i < operations.length; i++) {
                if (this.timeOfAllWorks < operations[i].getF()) {
                    this.timeOfAllWorks = operations[i].getF();
                }
            }
        }
        while (localSerach());

    }

    private boolean localSerach() {
        ArrayList<Integer> criticalPath = findCriticalPath(operations);
        int[][] criticalBlocks = findCriticalBlocks(operations, criticalPath);

        for (int i = 0; i < criticalBlocks.length; i++) {
            if (i == 0 || i == criticalBlocks.length - 1) continue;
            int start, end;
            start = criticalBlocks[i][0];
            end = criticalBlocks[i][1];
            if (end - start < 2) continue;
            if (end - start == 2) {
                Operation[] tempOperation = copyOperation();
                int time = evaluateSwap(tempOperation, criticalPath, start + 1, start);
                if (this.timeOfAllWorks > time) {
                    operations = tempOperation;
                    return true;
                }

            }

            if (end - start > 2) {

                Operation[] tempOperation = copyOperation();
                int time = evaluateSwap(tempOperation, criticalPath, start + 1, start);
                if (this.timeOfAllWorks > time) {
                    operations = tempOperation;
                    return true;
                }
                tempOperation = copyOperation();
                time = evaluateSwap(tempOperation, criticalPath, end, end - 1);
                if (this.timeOfAllWorks > time) {
                    operations = tempOperation;
                    return true;
                }

            }
        }
        return false;
    }

    private int evaluateSwap(Operation[] tempOperation, ArrayList<Integer> criticalPath, int first, int second) {
        Operation firstOperation = tempOperation[criticalPath.get(first)];
        Operation secondOperation = tempOperation[criticalPath.get(second)];
        // dont account the case when one job will be similary executed in few machines
        if (!isAvailableSwap(tempOperation, criticalPath.get(first), criticalPath.get(second))) {
            return Integer.MAX_VALUE;
        }

        secondOperation.setF(firstOperation.getF() - firstOperation.getProcessingTime() + secondOperation.getProcessingTime());
        firstOperation.setF(secondOperation.getF() + firstOperation.getProcessingTime());

        newF(tempOperation, secondOperation);
        newF(tempOperation, firstOperation);
//        OPTIMIZATION PROBLEM OPTIMIZATION PROBLEM OPTIMIZATION PROBLEM OPTIMIZATION PROBLEM OPTIMIZATION PROBLEM OPTIMIZATION PROBLEM OPTIMIZATION PROBLEM
        for (int j = 0; j < operations.length; j++) {
            for (int i = operations.length - 1; i > 0; i--) {
                newF(tempOperation, tempOperation[i]);
            }
        }
        int currentTime = 0;
        for (int i = 0; i < tempOperation.length; i++) {
            if (tempOperation[i].getF() > currentTime) {
                currentTime = tempOperation[i].getF();
            }
        }
        return currentTime;

    }

    private boolean isAvailableSwap(Operation[] tempOperation, int first, int second) {
        for (int i = 0; i < tempOperation.length; i++) {
            if (i == second) continue;

            int leftBound = tempOperation[first].getF() - tempOperation[first].getProcessingTime();
            int rightBound = leftBound + tempOperation[second].getProcessingTime() - 1;

            int leftBoundOfOperation = tempOperation[i].getF() - tempOperation[i].getProcessingTime();
            int rightBoundOfOperation = tempOperation[i].getF() - 1;

            if (operations[i].getJobIndex() == operations[second].getJobIndex()
                    &&
                    (leftBoundOfOperation <= leftBound && rightBound <= rightBoundOfOperation)
                    &&
                    (leftBoundOfOperation <= leftBound && (leftBound <= rightBoundOfOperation && rightBoundOfOperation <= rightBound))
                    &&
                    ((leftBound <= leftBoundOfOperation && leftBoundOfOperation <= rightBound) && rightBound <= rightBoundOfOperation)
                    &&
                    (leftBound <= leftBoundOfOperation && rightBoundOfOperation <= rightBound)
            ) {
                return false;
            }
        }
        for (int i = 0; i < tempOperation.length; i++) {
            if (i == first) continue;

            int leftBound = tempOperation[first].getF() - tempOperation[first].getProcessingTime() + tempOperation[second].getProcessingTime();
            int rightBound = leftBound + tempOperation[first].getProcessingTime() - 1;

            int leftBoundOfOperation = tempOperation[i].getF() - tempOperation[i].getProcessingTime();
            int rightBoundOfOperation = tempOperation[i].getF() - 1;

            if (operations[i].getJobIndex() == operations[first].getJobIndex()
                    &&
                    (leftBoundOfOperation <= leftBound && rightBound <= rightBoundOfOperation)
                    &&
                    (leftBoundOfOperation <= leftBound && (leftBound <= rightBoundOfOperation && rightBoundOfOperation <= rightBound))
                    &&
                    ((leftBound <= leftBoundOfOperation && leftBoundOfOperation <= rightBound) && rightBound <= rightBoundOfOperation)
                    &&
                    (leftBound <= leftBoundOfOperation && rightBoundOfOperation <= rightBound)
            ) {
                return false;
            }
        }
        return true;
    }

    private void newF(Operation[] tempOperation, Operation operation) {
        int time_in_line = 0;
        for (int j = 0; j < tempOperation.length; ++j) {
            if (time_in_line < tempOperation[j].getF()
                    && tempOperation[j].getF() < operation.getF()
                    && (tempOperation[j].getJobIndex() == operation.getJobIndex() || tempOperation[j].getMachineIndex() == operation.getMachineIndex())
            ) {
                time_in_line = tempOperation[j].getF();
            }
        }
        operation.setF(time_in_line + operation.getProcessingTime());
    }


    private Operation[] copyOperation() {

        Operation[] copyOperations = new Operation[this.operations.length];
        for (int i = 0; i < copyOperations.length; i++) {
            copyOperations[i] = (Operation) operations[i].clone();
        }
        return copyOperations;
    }

    private int[][] findCriticalBlocks(Operation[] operations, ArrayList<Integer> criticalPath) {
        int start, end;

        int counter = 1;
        for (int i = 1; i < criticalPath.size(); i++) {

            if (operations[criticalPath.get(i)].getMachineIndex() != operations[criticalPath.get(i - 1)].getMachineIndex()) {
                ++counter;
            }
        }

        int[][] blocks = new int[counter][2];
        counter = 0;
        start = 0;
        end = 1;
        for (int i = 1; i < criticalPath.size(); i++) {
            if (operations[criticalPath.get(i)].getMachineIndex() != operations[criticalPath.get(i - 1)].getMachineIndex()) {
                blocks[counter][0] = start;
                blocks[counter][1] = end;
                start = end;
                ++end;
                counter++;
            } else {
                end++;
            }
        }

        return blocks;
    }

    private ArrayList<Integer> findCriticalPath(Operation[] operations) {
        int lastOperationIndex = 0;
        for (int i = 0; i < operations.length; i++) {
            if (operations[lastOperationIndex].getF() < operations[i].getF()) {
                lastOperationIndex = i;
            }
        }
        ArrayList<Integer> criticalPath = new ArrayList<>();
        criticalPath.add(lastOperationIndex);
        int time = operations[lastOperationIndex].getF() - operations[lastOperationIndex].getProcessingTime();
        while (time != 0) {

            boolean hasInSameMachine = false;
            for (int i = 0; i < operations.length; i++) {
                if (operations[i].getF() == time &&
                        operations[i].getMachineIndex() == operations[lastOperationIndex].getMachineIndex()) {
                    lastOperationIndex = i;
                    hasInSameMachine = true;
                    time -= operations[lastOperationIndex].getProcessingTime();
                    criticalPath.add(lastOperationIndex);

                    break;
                }
            }
            if (hasInSameMachine) continue;

            boolean hasInOther = false;
            for (int i = 0; i < operations.length; i++) {
                if (operations[i].getF() == time &&
                        operations[i].getJobIndex() == operations[lastOperationIndex].getJobIndex()) {
                    lastOperationIndex = i;
                    hasInOther = true;
                    time -= operations[lastOperationIndex].getProcessingTime();
                    criticalPath.add(lastOperationIndex);
                    break;
                }
            }
            if (hasInOther) continue;
            break;
        }
//        if (time != 0) return new ArrayList<>();
        return criticalPath;
    }

    private void construcShedule() {
        int g = 0;

        ArrayList<Integer> T = new ArrayList<>(operations.length + 1);//
        T.add(0);

        ArrayList<Integer> S = new ArrayList<>(operations.length);//indexes

        ArrayList<Integer> E = new ArrayList<>(operations.length);

        // indexes to operation in job must be executed next
        int[] pointer = new int[operations[operations.length - 1].getJobIndex() + 1];


        while (S.size() < operations.length) {
            updateE(g, operations, E, T, pointer);
            while (E.size() != 0) {
                int jj = E.get(0); //j - asterisk
                //get highest priority operation in E
                for (int i = 1; i < E.size(); i++) {
                    if (operations[jj].getPriority() < operations[E.get(i)].getPriority()) {
                        jj = E.get(i);
                    }
                }
                pointer[jj / machineNumber]++;
                if (pointer[jj / machineNumber] >= machineNumber) {
                    pointer[jj / machineNumber] = -1;
                }
                //
                int F = findF(operations, S, jj);
                operations[jj].setF(F);
                S.add(jj);
                T.add(F);
                g = S.size() - 1;
                updateE(g, operations, E, T, pointer);
            }
        }


    }

    private void updateE(int g, Operation[] operations, ArrayList<Integer> E, ArrayList<Integer> T, int[] pointer) {
        E.clear();
        int tIndex = 0;
        boolean allJobsAreDone = true;
        double DELAY = chromosome.getGenes()[jobsNumber * machineNumber + g] * delayFactor * this.maxDuration;

        while (E.size() == 0) {
            for (int i = 0; i < jobsNumber; i++) {
                if (pointer[i] == -1) continue;
                allJobsAreDone = false;
                int currentOperationIndex = i * machineNumber + pointer[i];
                if (operations[currentOperationIndex].getF() < DELAY + T.get(tIndex)) {
                    E.add(currentOperationIndex);
                }
            }
            ++tIndex;
            if (allJobsAreDone && tIndex >= T.size()) {
                break;
            }
        }
    }

    private int findF(Operation[] operations, ArrayList<Integer> S, int jj) {
        int maxTime = 0;
        for (int i = 0; i < S.size(); i++) {
            if ((operations[S.get(i)].getMachineIndex() == operations[jj].getMachineIndex() ||
                    operations[S.get(i)].getJobIndex() == operations[jj].getJobIndex())
                    && operations[S.get(i)].getF() > maxTime) {
                maxTime = operations[S.get(i)].getF();
            }
        }
        return maxTime + operations[jj].getProcessingTime();
    }

}
