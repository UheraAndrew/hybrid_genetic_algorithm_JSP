package geneticalgo;


import lombok.Getter;

import java.util.*;


public class Shedule {
    private static int m;//number of machines
    private static int n;//number of jobs
    // max time
    private int[] t;

    //
    private Chromosome chromosome;
    // read paper
    private LinkedHashSet<Integer> S;
    private ArrayList<Integer> E;
    private ArrayList<Integer> T;
//    private ArrayList<Integer> FMC;
//    private ArrayList<Integer> F;


    private int[] pointer;
    @Getter
    private static Operation[] operations;
    private static double maxDuration;

    public int time(Operation[] operations) {
        int maxtTime = 0;
        for (int i = 0; i < operations.length; i++) {
            if (maxtTime < operations[i].getF()) {
                maxtTime = operations[i].getF();
            }
        }
        return maxtTime;
    }

    public Shedule(Chromosome chromosome) {
        this.chromosome = chromosome;
        this.E = new ArrayList<>(n + 1);
        this.S = new LinkedHashSet<>(n + 1);
        T = new ArrayList<>(n + 1);
        T.add(0);
        double[] genes = chromosome.getGenes();

        for (int i = 0; i < genes.length / 2; i++) {
            operations[i].setPrioriti(genes[i]);
            operations[i].setDelay(genes[i + genes.length / 2] * 1.5 * maxDuration);
        }

        Arrays.sort(operations, (o1, o2) -> {
            if (o1.getJobIndex() == o2.getJobIndex()) {
                return (int) Math.signum(o1.getPrioriti() - o2.getPrioriti());
            } else {
                return o1.getJobIndex() - o2.getJobIndex();
            }
        });


//        for (int i = 0; i < operations.length; i++) {
//            System.out.println(operations[i]);
//        }
        pointer = new int[n];
    }
    static ArrayList<Integer> criticalPath;
    public void test() {
        constructShedule(operations, E, T, pointer, S);
        criticalPath = findCriticalPath(operations);
        //        System.out.println("\n +++++++++++++++++++ \n");
//        for (int i = 0; i < operations.length; i++) {
//            System.out.println(operations[i]);
//        }
//        System.out.println("CURRENT MAKESPAN IS " + time());

        whyNot();
        while (localSearch()) {
            whyNot();
            //            System.out.println(1);
        }

//        System.out.println();
//        for (int i = 0; i < operations.length; i++) {
//            System.out.println(operations[i]);
//        }
//        System.out.println("NEW MAKESPAN IS " + time());
    }

    public void constructShedule(Operation[] operations,
                                 ArrayList<Integer> E,
                                 ArrayList<Integer> T,
                                 int[] pointer,
                                 LinkedHashSet<Integer> S) {
        int g = S.size();
//        int g = S.size();// що таке g
        int forDebag = n;
        while (g < n * m) {
            updateE(g, operations, E, T, pointer);
            while (E.size() != 0) {
//              Select Operation with highest priority
                int jj = -1;
                double maxPriority = -1;
                for (int i = 0; i < E.size(); i++) {
                    int index = E.get(i);
                    if (maxPriority < operations[index].getPrioriti()) {
                        maxPriority = operations[index].getPrioriti();
                        jj = index;
                    }
                }
                pointer[jj / m]++;
                if (pointer[jj / m] >= m) {
                    pointer[jj / m] = -1;
                }

//              set FMCg
                int FMC = 0;
                if (jj % m != 0) {
                    FMC = operations[jj - 1].getF();
                }
                FMC += operations[jj].getProcessingTime();
                operations[jj].setFMC(FMC);
//              set Fg
                int F = 0;
                if (S.size() != 0) {
                    int maxTime = -1;
                    for (int operationIndex : this.S) {
                        if (maxTime < operations[operationIndex].getF()) {
                            maxTime = operations[operationIndex].getF();
                        }
                    }
                    F = maxTime;
                }
                F += operations[jj].getProcessingTime();

                //Updating
                operations[jj].setF(F);
                S.add(jj);
                T.add(F);
                g = S.size();
                updateE(g, operations, E, T, pointer);
            }
        }

    }

    void whyNot() {
        Arrays.sort(operations, (o1, o2) -> {
            return o1.getF() - o2.getF();
        });
        for (int i = 0; i < operations.length; i++) {
            newF(operations, i);
        }
    }

    boolean localSearch() {
        int swapTime = Integer.MAX_VALUE;
        Operation[] copiedOperation = copyOperations(operations);
//        ArrayList<Integer> criticalPath = findCriticalPath(copiedOperation);

        // critical path contains indexes of operations in the current solution that comprise critical path
        int start, end = criticalPath.size() - 1;

        // проходимся по блоках
        while (end > -1) {
            start = end;
            // determining the start and end index of critical block
            for (int i = end - 1; i > -1; --i) {
                if (copiedOperation[criticalPath.get(i)].getMachineIndex() == copiedOperation[criticalPath.get(start)].getMachineIndex())
                    --end;
                else break;
            }
            // [end : start] - critical block, where end < start;

            if (start - end + 1 == 2) {
                //                            SWAPPING LAST TWO
                Operation[] tempOperations = copyOperations(copiedOperation);
                swapTime = swapShift(tempOperations, criticalPath, criticalPath.get(start - 1), criticalPath.get(start));

                if (swapTime < this.time(operations)) {
                    // local commit of solution update
                    operations = tempOperations;

                    return true;

                }
            }
            if (start - end + 1 > 2) {
                //                            SWAPPING FIRST TWO
                Operation[] tempOperations = copyOperations(copiedOperation);
                // swap redefines the earliest precedence&capacity finish time of left and right operations
                swapTime = swapShift(tempOperations, criticalPath, end, end + 1);

                if (swapTime < this.time(operations)) {
                    // local commit of solution update
                    operations = tempOperations;
                    return true;
                }
                //                            SWAPPING LAST TWO
                tempOperations = copyOperations(copiedOperation);
                swapTime = swapShift(tempOperations, criticalPath, start - 1, start);

                if (swapTime < this.time(operations)) {
                    // local commit of solution update
                    operations = tempOperations;

                    return true;
                }
            }
            --end;
        }
        operations = copiedOperation;
        return false;
    }


    private int swapShift(Operation[] operations, ArrayList<Integer> criticalPath, int first, int second) {

        swap(operations[first], operations[second]);
        for (int i = first; i < criticalPath.size(); i++) {
            newF(operations, criticalPath.get(i));
        }
        int newMakeSpan = 0;
        for (int i = 0; i < operations.length; i++) {
            if (newMakeSpan < operations[i].getF()) {
                newMakeSpan = operations[i].getF();
            }
        }
        return newMakeSpan;
    }

    private void swap(Operation first, Operation second) {
//        просто змінити F
        int start = first.getF() - first.getProcessingTime();
        second.setF(start + second.getProcessingTime());
        first.setF(second.getF() + first.getProcessingTime());
    }

    private void newF(Operation[] operations, int i) {
        int minTimeInLine = 0;
        int currentJob = operations[i].getJobIndex();
        int currentMachine = operations[i].getMachineIndex();
        int finishTimeOfJob = operations[i].getF();

        //min час по машині
        for (int j = 0; j < operations.length; j++) {

            if (operations[j].getMachineIndex() == currentMachine &&
                    operations[j].getF() < finishTimeOfJob &&
                    operations[j].getF() > minTimeInLine
            ) {
                minTimeInLine = operations[j].getF();
            }
        }
        // мін час по роботі
        for (int j = 0; j < operations.length; j++) {
            if (operations[j].getJobIndex() == currentJob &&
                    operations[j].getF() < finishTimeOfJob &&
                    operations[j].getF() > minTimeInLine
            ) {
                minTimeInLine = operations[j].getF();
            }
        }

        operations[i].setF(minTimeInLine + operations[i].getProcessingTime());
    }

    private ArrayList<Integer> findCriticalPath(Operation[] operations) {
        ArrayList<Integer> ends = new ArrayList<>();
        int maxTime = 0;
        for (int i = 0; i < operations.length; i++) {
            if (maxTime < operations[i].getF()) {
                maxTime = operations[i].getF();
            }
        }
        for (int i = 0; i < operations.length; i++) {
            if (maxTime == operations[i].getF()) {
                ends.add(i);
            }
        }
        ArrayList<Integer> criticalPath = new ArrayList<>();
        // finding new critical path now of length time with the lastOperation
        for (int operationIndex = 0; operationIndex < ends.size(); operationIndex++) {

            Operation lastOperation = operations[ends.get(operationIndex)];
            criticalPath = new ArrayList<>();
            criticalPath.add(ends.get(operationIndex));

            int time = lastOperation.getF() - lastOperation.getProcessingTime();
            while (time > 0) {
                boolean endBlock = true;
                // searching on the same machine
                for (int i = 0; i < operations.length; i++) {
                    if (operations[i].getMachineIndex() == lastOperation.getMachineIndex()
                            && operations[i].getF() == time) {
                        lastOperation = operations[i];
                        time = time - lastOperation.getProcessingTime();
                        criticalPath.add(i);
                        endBlock = false;
                        break;
                    }
                }

                if (!endBlock) continue;

                boolean hasOperationInOtherMachune = false;
                // switching to other machine
                for (int i = 0; i < operations.length; i++) {
                    // searching for the operation that is the part of the same job as the lastOperation
                    // and has the earliest finish time equal to time
                    if (operations[i].getF() == time &&
                            operations[i].getJobIndex() == lastOperation.getJobIndex()) {
                        lastOperation = operations[i];
                        time = time - lastOperation.getProcessingTime();
                        criticalPath.add(i);
                        hasOperationInOtherMachune = true;
                        break;
                    }
                }
                if (hasOperationInOtherMachune) continue;

//                time = 0;
                break;

            }
            if (time == 0) {
                break;
            }
        }

        ArrayList<Integer> outArray = new ArrayList<>();
        for (int i = criticalPath.size() - 1; i > -1; i--) {
            outArray.add(criticalPath.get(i));
        }

        return outArray;
    }

    private Operation[] copyOperations(Operation[] operations) {
        Operation[] tempOperations = new Operation[operations.length];
        for (int i = 0; i < tempOperations.length; i++) {
            tempOperations[i] = (Operation) operations[i].clone();
        }
        return tempOperations;
    }

    private void updateE(int g, Operation[] operations, ArrayList<Integer> E,
                         ArrayList<Integer> T, int[] pointer) {
        E.clear();
        int indexT = 0;

//        System.out.println("\n____________________________\n" + T);
        boolean lol = true;
        while (E.size() == 0) {
            for (int i = 0; i < n; i++) {
                if (pointer[i] == -1) continue;
                lol = false;
                int cI = i * m + pointer[i];
//                System.out.println(indexT);
                if (operations[cI].getF() < operations[g].getDelay() + T.get(indexT)) {
                    E.add(cI);
                }
            }
            ++indexT;
            if (lol && indexT >= T.size()) {
                break;
            }

        }
    }


    static void setOperations(int[][] dataSet) {
        n = dataSet.length;
        m = dataSet[0].length;

        operations = new Operation[dataSet.length * dataSet[0].length];

        int all_time = 0;
        for (int i = 0; i < dataSet.length; i++) {
            for (int j = 0; j < dataSet[i].length; j++) {
                all_time += dataSet[i][j];
                operations[i * dataSet[i].length + j] = new Operation(i, j, dataSet[i][j]);
                if (maxDuration < dataSet[i][j]) {
                    maxDuration = dataSet[i][j];
                }
            }
        }
    }
}