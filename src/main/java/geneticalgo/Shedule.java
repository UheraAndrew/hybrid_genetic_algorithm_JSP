package geneticalgo;


import java.util.*;


public class Shedule {
    private static int m;//number of machines
    private static int n;//number of jobs
    //    private static Operation[][] timeTable;// physical representation of table
    private static Operation[] criticalPath;
    private static int[] blocks;
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
    private static Operation[] operations;
    private static double maxDuration;

    public int time() {
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
//        FMC = new ArrayList<>(n + 1);
//        F = new ArrayList<>(n + 1);T.add(0);

        double[] genes = chromosome.getGenes();

        for (int i = 0; i < genes.length / 2; i++) {
            operations[i].setPrioriti(genes[i]);
            operations[i].setDelay(genes[i + genes.length / 2]);//test_case
//            operations[i].setDelay(genes[i + genes.length / 2] * 1.5 * maxDuration);
        }

        Arrays.sort(operations, (o1, o2) -> {
            if (o1.getJobIndex() == o2.getJobIndex()) {
                return (int) Math.signum(o1.getPrioriti() - o2.getPrioriti());
            } else {
                return o1.getJobIndex() - o2.getJobIndex();
            }
        });


        for (int i = 0; i < operations.length; i++) {
            System.out.println(operations[i]);
        }
        pointer = new int[n];

//        for (int i = 0; i < timeTable.length; i++) {
//            for (int j = 0; j < timeTable[i].length; j++) {
//                timeTable[i][j] = null;
//            }
//        }
    }

    public void test() {
        // початковий shedule
        constructShedule(operations, E, T, pointer, S);

        System.out.println("\n +++++++++++++++++++ \n");
        for (int i = 0; i < operations.length; i++) {
            System.out.println(operations[i]);
        }


//        criticalPath = new Operation[maxTime];
//        blocks = new int[maxTime];
        System.out.println("");

        while (localSearch()) ;

//        int tempTime = maxTime;
//        do {
//            tempTime = localSearch(tempTime);
//        }
//        while (tempTime != maxTime);

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
        System.out.print("");

    }

//    public void drawShedule() {
//        for (int i = 0; i < operations.length; i++) {
//            Operation current = operations[i];
//            int blockSize = current.getProcessingTime();
//            for (int j = 0; j < blockSize; j++) {
//                timeTable[current.getMachineIndex()][current.getF() - j - 1] = current;
//            }
//        }
//    }

    boolean localSearch() {

        boolean currentSolutionUpdated = false;
        Operation[] copiedOperation = copyOperations();
        ArrayList<Integer> criticalPath;
        // поки не update
        do {
            System.out.println("Local search procedure:");
            criticalPath = findCriticalPath(copiedOperation);
            // critical path contains indexes of operations in the current solution that comprise critical path
            int start = criticalPath.size() - 1, end = criticalPath.size() - 1;
            // проходимся по блоках
            while (end > -1) {
                // determining the start and end index of critical block
                for (int i = end - 1; i > -1; --i) {
                    if (copiedOperation[criticalPath.get(i)].getMachineIndex() == copiedOperation[criticalPath.get(start)].getMachineIndex())
                        --end;
                    else break;
                }
                // [end : start] - critical block, where end < start;
                if (start - end + 1 == 2) {
                    //                            SWAPPING LAST TWO
                    Operation[] operations_copy = copiedOperation;
                    // swap redefines the earliest precedence&capacity finish time of two operations
                    swap(operations_copy, criticalPath.get(start - 1), criticalPath.get(start));
                    // shift redefines the earliest precedence&capacity finish time of all operations that goes after criticalPath[start] operation
                    // shift routine also returns the boolean: whether or not make-span has improved with the above swap call
                    currentSolutionUpdated = shift(operations_copy, criticalPath.get(start));
                    if (currentSolutionUpdated) {
                        // local commit of solution update
                        copiedOperation = operations_copy;
                    }
                }
                if (start - end + 1 > 2) {
                    //                            SWAPPING FIRST TWO
                    Operation[] operations_copy = copiedOperation;
                    // swap redefines the earliest precedence&capacity finish time of left and right operations
                    swap(operations_copy, criticalPath.get(end), criticalPath.get(end + 1));
                    // shift redefines the earliest precedence&capacity finish time of all operations that goes after criticalPath[end+1] operation
                    // shift routine also returns the boolean: whether or not make-span has improved with the above swap call
                    currentSolutionUpdated = shift(operations_copy, criticalPath.get(end + 1));
                    if (currentSolutionUpdated) {
                        // local commit of solution update
                        copiedOperation = operations_copy;
                    }
                }
                --end;
            }
        } while (currentSolutionUpdated);
        return false;
    }

    private boolean shift(Operation[] operations, int end) {
        boolean updated = false;
        int previous_make_span = operations[operations.length - 1].getF();
        for (int i = end + 1; i < operations.length; ++i) {
            int new_earliest_precedence_capacity_finish_time = 0;
            for (int j = 0; j < i; ++j) {
                if (operations[j].getJobIndex() == operations[i].getJobIndex() ||
                        operations[j].getMachineIndex() == operations[i].getMachineIndex()) {
                    if (operations[j].getF() > new_earliest_precedence_capacity_finish_time)
                        new_earliest_precedence_capacity_finish_time = operations[j].getF();
                }
            }
            operations[i].setF(new_earliest_precedence_capacity_finish_time + operations[i].getProcessingTime());
        }
        if (previous_make_span > operations[operations.length - 1].getF()) updated = true;
        return updated;
    }

    private void swap(Operation[] operations, int start, int end) {
        operations[end].setF(operations[start].getF() - operations[start].getProcessingTime() + operations[end].getProcessingTime());
        operations[start].setF(operations[end].getF() + operations[start].getProcessingTime());
    }


    private ArrayList<Integer> findCriticalPath(Operation[] operations) {
        Operation lastOperation = operations[0];
        int index = 0;
        for (int i = 0; i < operations.length; i++) {
            if (lastOperation.getF() < operations[i].getF()) {
                lastOperation = operations[i];
                index = i;
            }
        }
        ArrayList<Integer> criticalPath = new ArrayList<>();
        criticalPath.add(index);
        // finding new critical path now of length time with the lastOperation
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

            // switching to other machine
            for (int i = 0; i < operations.length; i++) {
                // searching for the operation that is the part of the same job as the lastOperation
                // and has the earliest finish time equal to time
                if (operations[i].getF() == time &&
                        operations[i].getJobIndex() == lastOperation.getJobIndex()) {
                    lastOperation = operations[i];
                    time = time - lastOperation.getProcessingTime();
                    criticalPath.add(i);
                    break;
                }
            }
        }
        return criticalPath;
    }

    private Operation[] copyOperations() {
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

        System.out.println("\n____________________________\n" + T);
        boolean lol = true;
        while (E.size() == 0) {
            for (int i = 0; i < n; i++) {
                if (pointer[i] == -1) continue;
                lol = false;
                int cI = i * m + pointer[i];
                System.out.println(indexT);
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