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

        int maxTime = -1;
        for (int i = 0; i < operations.length; i++) {
            if (maxTime < operations[i].getF()) {
                maxTime = operations[i].getF();
            }
        }

//        criticalPath = new Operation[maxTime];
//        blocks = new int[maxTime];
        System.out.println("");
        localSearch(maxTime);

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
        while (g < n) {
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
                g++;
                updateE(g, operations, E, T, pointer);
            }
        }
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

    boolean localSearch(int currentMaxTime) {

        boolean currentSolutionUpdated;

        do {
            currentSolutionUpdated = false;
            //копіювання початок--------------------------------------------------------------------
            Operation[] copiedOperation = new Operation[operations.length];
            // перекопійовуємо операції
            for (int i = 0; i < operations.length; i++) {
                copiedOperation[i] = (Operation) operations[i].clone();
            }
            //criticalPath
            ArrayList<Operation> criticalPath = findCriticalPath(copiedOperation);
            // оприділяємо критичний блок

            int start, end;
            end = criticalPath.size() - 1;

            Operation temp;
            // проходимся по блоках
            while (end > -1) {
                // визначаємо початок і кінець блоку
                start = end;
                temp = criticalPath.get(start);

                for (int i = end - 1; i > -1; i--) {
                    if (criticalPath.get(i).getMachineIndex() == temp.getMachineIndex()) {
                        end--;
                    } else {
                        break;
                    }
                }

                // end і start визначають початок і кінець блоку
                // тепер вертимо
                if (start - end + 1 >= 2) {
                    //swap operations[start] і operations[start - 1]
                    Operation left,right;
                    if(start - end + 1 == 2){
                        left = copiedOperation[start - 1];
                        right = copiedOperation[start];

                    }
                    else{

                    }

                    swap(left, right);
                    //тепер треба зсунуту
                    // шукаємо індекси тих блоків
                    int leftIndex = -1;
                    int rightIndex = -1;
                    for (int i = 0; i < operations.length; i++) {
                        if (operations[i].equals(left)) {
                            leftIndex = i;
                        }
                        if (operations[i].equals(right)) {
                            rightIndex = i;
                        }
                    }
                    // перекопійовуємо S, T,
                    LinkedHashSet<Integer> copiedS = new LinkedHashSet<>();
                    ArrayList<Integer> copiedT = new ArrayList<>();
                    for (Integer s : S) {
                        if (s != rightIndex && s != leftIndex) {
                            copiedS.add(s);
                        } else {
                            break;
                        }
                    }
                    for (int i = 0; i < copiedS.size(); i++) {
                        copiedT.add(T.get(i));
                    }
                    // ще треба якимось чином перекопіювати pointer бо без них не працює
                    int[] copiedPointer = new int[pointer.length];
                    for (Integer s : copiedS) {
                        copiedPointer[copiedOperation[s].getJobIndex()]++;
                    }
                    ArrayList<Integer> copiedE = new ArrayList<>();
                    constructShedule(copiedOperation, copiedE, copiedT, copiedPointer, copiedS);

                    // визначаємо час максимальний час t
                    // якщо менший то знайшли рішення нє по новій
                    int findMaxTime = -1;
                    for (int i = 0; i < copiedOperation.length; i++) {
                        if (findMaxTime < copiedOperation[i].getF()) {
                            findMaxTime = copiedOperation[i].getF();
                        }
                    }
                    if (currentMaxTime > findMaxTime) {
                        currentSolutionUpdated = true;
                        // комітимо

                    }
                }
                end--;
            }


        } while (currentSolutionUpdated);

        return false;
    }
    private void procedure(Operation[] copiedOperation){}

//    private void swap(Operation[][] tempTaskTable, Operation left, Operation right) {
//        //тут мало б бути все ок
//        int leftBound = left.getF() - left.getProcessingTime();
//        int rightBound = right.getF();
//        int m = left.getMachineIndex();
//
//        for (int i = leftBound; i < leftBound + right.getProcessingTime(); i++) {
//            tempTaskTable[m][i] = right;
//        }
//
//        for (int i = leftBound + right.getProcessingTime(); i < rightBound; i++) {
//            tempTaskTable[m][i] = left;
//        }
//
//        //swap F
//        int temp = rightBound;
//        right.setF(left.getF());
//        left.setF(temp);
//
//
//
//    }

    private void swap(Operation a, Operation b) {
        double temp;
        temp = a.getPrioriti();
        a.setPrioriti(b.getPrioriti());
        b.setPrioriti(temp);
    }


    private ArrayList<Operation> findCriticalPath(Operation[] operations) {
//        criticalPath = new Operation[timeTable[0].length];
//        blocks = new int[timeTable[0].length];
//        int counter = 1;
//        int end;
//        //find the ending
//        int i = 0;
//        int j;
//        // тут може зациклитись бо не знайде критичного шляху
//        // пошук кінця
//        for (j = timeTable[0].length - 1; j > -1; j--) {
//            for (i = 0; i < timeTable.length; i++) {
//                if (timeTable[i][j] != null) {
//                    break;
//                }
//                blocks[j] = -1;
//            }
//            if (timeTable[i][j] != null) {
//                break;
//            }
//        }
//
//        int endJobIndex = timeTable[i][j].getJobIndex();
//        while (j > -1) {
//            // знайшли кінець
//            for (i = 0; i < timeTable.length; ++i) {
//                if (timeTable[i][j] != null && timeTable[i][j].getJobIndex() == endJobIndex) {
//                    break;
//                }
//            }
//
//            //cчитали критичний блок
//            while (j > -1 && timeTable[i][j] != null) {
//                //стягнули вниз блок
//                endJobIndex = timeTable[i][j].getJobIndex();
//                for (int k = 0; k < timeTable[i][j].getProcessingTime(); k++) {
//                    //записали в criticalPath і criticalBlocks
//                    criticalPath[j - k] = timeTable[i][j];
//                    blocks[j - k] = counter;
//                }
//                j -= timeTable[i][j].getProcessingTime();
//            }
//            counter++;
//            //перейшли до наступного блоку якщо такий є
//        }
        //маємо доступні

        Operation lastOperation = operations[0];
        for (int i = 0; i < operations.length; i++) {
            if (lastOperation.getF() < operations[i].getF()) {
                lastOperation = operations[i];
            }
        }
        // шукаємо наступний від нього FMC - processingTime
        ArrayList<Operation> criticalPath = new ArrayList<>();
        criticalPath.add(lastOperation);

        int time = lastOperation.getF() - lastOperation.getProcessingTime();

        while (time > 0) {
            boolean endBlock = true;
            //шукаємо по блоку
            for (int i = 0; i < operations.length; i++) {
                // шукаємо критичний блок якщо такий є
                if (operations[i].getMachineIndex() == lastOperation.getMachineIndex()
                        && operations[i].getF() == time) {
                    lastOperation = operations[i];
                    time = time - lastOperation.getProcessingTime();
                    criticalPath.add(lastOperation);
                    endBlock = false;
                    break;
                }
            }
            if (!endBlock) {
                continue;
            }
            // переходимо на іншу машину

            for (int i = 0; i < operations.length; i++) {
                // шукаємо критичний блок якщо такий є
                if (operations[i].getF() == time &&
                        operations[i].getJobIndex() == lastOperation.getJobIndex()) {
                    lastOperation = operations[i];
                    time = time - lastOperation.getProcessingTime();
                    criticalPath.add(lastOperation);
                    break;
                }
            }
        }
//        ________________________WARNING_____________________
//        ERORR кастингування
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

        boolean lol = true;
        while (E.size() == 0) {
            for (int i = 0; i < n; i++) {
                if (pointer[i] == -1) continue;
                lol = false;
                int cI = i * m + pointer[i];
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
//        timeTable = new Operation[m][all_time];
    }

}
