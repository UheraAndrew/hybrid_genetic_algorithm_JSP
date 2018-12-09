package geneticalgo;


import java.util.*;


public class Shedule {
    private static int m;//number of machines
    private static int n;//number of jobs
    private static Operation[][] timeTable;// physical representation of table

    // max time
    private int[] t;

    //
    private Chromosome chromosome;
    // read paper
    private ArrayList<Integer> E;
    private HashSet<Integer> S;
    private ArrayList<Integer> T;
//    private ArrayList<Integer> FMC;
//    private ArrayList<Integer> F;


    private int[] pointer;
    private static Operation[] operations;
    private static double maxDuration;


    public Shedule(Chromosome chromosome) {
        this.chromosome = chromosome;
        this.E = new ArrayList<>(n + 1);
        this.S = new HashSet<>(n + 1);
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

        // занулити тестове поле
        for (int i = 0; i < timeTable.length; i++) {
            for (int j = 0; j < timeTable[i].length; j++) {
                timeTable[i][j] = null;
            }
        }

        test();
    }

    public void test() {
        constructShedule();
        System.out.println("+++++++++++++++");
        for (int i = 0; i < operations.length; i++) {
            System.out.println(operations[i]);
        }
        drawShedule();
    }

    public void constructShedule() {
        int g = 0;
        while (g < n) {
            updateE(g);
            while (this.E.size() != 0) {
//              Select Operation with highest priority
                int jj = -1;
                double maxPriority = -1;
                for (int i = 0; i < this.E.size(); i++) {
                    int index = this.E.get(i);
                    if (maxPriority < operations[index].getPrioriti()) {
                        maxPriority = operations[index].getPrioriti();
                        jj = index;
                    }
                }
                pointer[jj / m]++;
                if (pointer[jj / m] >= m) {
                    pointer[jj / m] = -1;
                }
//
                int FMC = 0;
//              If it is first work
                if (jj % m != 0) {
                    FMC = operations[jj - 1].getF();
                }
                FMC += operations[jj].getProcessingTime();
                operations[jj].setFMC(FMC);

                int F = 0;
                if (this.S.size() != 0) {
                    int maxTime = -1;
                    for (int operationIndex : this.S) {
                        if (maxTime < operations[operationIndex].getF()) {
                            maxTime = operations[operationIndex].getF();
                        }
                    }
                    F = maxTime;
                }
                System.out.println("E  =" + (this.E));
                System.out.println("jj = " + jj);

                System.out.println("processTime = " + operations[jj].getProcessingTime());

                F += operations[jj].getProcessingTime();
                operations[jj].setF(F);

                this.S.add(jj);
                this.T.add(F);
                g++;
                updateE(g);
            }
        }
    }

    public void drawShedule() {
        for (int i = 0; i < operations.length; i++) {
            Operation current = operations[i];
            int blockSize = current.getProcessingTime();
            for (int j = 0; j < blockSize; j++) {
                timeTable[current.getMachineIndex()][current.getF() - j - 1] = current;
            }
        }
    }

    void localSearch() {
    }

    private void findCriticalPath() {
    }

    private void findCriticalBlocks() {
    }

    private void updateE(int g) {
        this.E.clear();
        int indexT = 0;

        boolean lol = true;
        while (this.E.size() == 0) {
            for (int i = 0; i < n; i++) {
                if (pointer[i] == -1) continue;

                lol = false;

                int cI = i * m + pointer[i];
                if (operations[cI].getF() < operations[g].getDelay() + this.T.get(indexT)) {
                    this.E.add(cI);
                }
            }
            ++indexT;
            if (lol && indexT >= this.T.size()) {
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
        timeTable = new Operation[m][all_time];
    }

}
