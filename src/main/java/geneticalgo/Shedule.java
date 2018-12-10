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

        boolean currentSolutionUpdated;
        currentSolutionUpdated = false;
        // поки не update
//        do {
        System.out.println("VERY OFTEN");
        //копіювання початок--------------------------------------------------------------------
        // перекопійовуємо операції
        //criticalPath

        Operation[] copiedOperation = copyOperations();
        ArrayList<Integer> criticalPath = findCriticalPath(copiedOperation);

        // оприділяємо критичний блок
        int start, end;
        end = criticalPath.size() - 1;

        int temp;
        // проходимся по блоках
        while (end > -1) {
            // визначаємо початок і кінець блоку наступного
            start = end;
            temp = criticalPath.get(start);

            for (int i = end - 1; i > -1; i--) {
                if (copiedOperation[criticalPath.get(i)].getMachineIndex() == copiedOperation[temp].getMachineIndex()) {
                    end--;
                } else {
                    break;
                }
            }

            // end і start визначають початок і кінець блоку
            // тепер вертимо
            if (start - end + 1 >= 2) {
                //swap operations[start] і operations[start - 1]
                Operation left, right;
                if (start - end + 1 == 2) {
                    System.out.println("start - 1 is " + criticalPath.get(start - 1) + "  start is " + criticalPath.get(start));
                    copiedOperation = copyOperations();
                    swap(copiedOperation, criticalPath.get(start - 1), criticalPath.get(start));
                    currentSolutionUpdated = shift(copiedOperation, criticalPath, start - 1, start);
                    if (currentSolutionUpdated) {
                        return true;
                    }
                } else {
                    copiedOperation = copyOperations();

                    swap(copiedOperation, criticalPath.get(start), criticalPath.get(start - 1));
//                        ТУТ МОЖУТЬ БУТИ ЖОСТКІ ТРАБЛИ
//                      ______________________________________WARNING___________________________________________
                    currentSolutionUpdated = shift(copiedOperation, criticalPath, start, start - 1);

                    if (currentSolutionUpdated) {
                        return true;
                    }

                    copiedOperation = copyOperations();
                    swap(copiedOperation, criticalPath.get(end), criticalPath.get(end + 1));
                    currentSolutionUpdated = shift(copiedOperation, criticalPath, end, end + 1);
                    if (currentSolutionUpdated) {
                        return true;
                    }
                }
                //тепер треба зсунуту
                // шукаємо індекси тих блоків

            }
            end--;
        }


//        } while (currentSolutionUpdated);

        return false;
    }

    private boolean shift(Operation[] copiedOperations, ArrayList<Integer> criticalPath, int start, int end) {
        boolean updated = false;
        int temp = copiedOperations[criticalPath.get(end)].getF();
        System.out.println(copiedOperations[criticalPath.get(start)].getF() + "____" + copiedOperations[criticalPath.get(end)].getF());
        copiedOperations[criticalPath.get(end)].setF(copiedOperations[criticalPath.get(start)].getF());
        copiedOperations[criticalPath.get(start)].setF(temp);
        System.out.println(copiedOperations[criticalPath.get(start)].getF() + "____" + copiedOperations[criticalPath.get(end)].getF());

        for (int i = 0; i < copiedOperations.length; ++i) {
            if (copiedOperations[i].getPrioriti() > copiedOperations[criticalPath.get(start)].getPrioriti()) {
                if (copiedOperations[i].getJobIndex() == copiedOperations[criticalPath.get(start)].getJobIndex()) {
                    int new_f = 0;
                    for (int j = 0; j < copiedOperations.length; ++j) {
                        if (copiedOperations[j].getMachineIndex() == copiedOperations[i].getMachineIndex()) {
                            new_f += copiedOperations[j].getF();
                        }
                    }
                    if (copiedOperations[i].getF() > new_f) updated = true;
                    copiedOperations[i].setF(new_f);
                }
            }
        }
        int currentTime = -1;
        for (int i = 0; i < copiedOperations.length; i++) {
            System.out.println(copiedOperations[i]);
            if (currentTime < copiedOperations[i].getF()) {
                currentTime = copiedOperations[i].getF();
            }
        }

        System.out.println("НІІІІІІІІІІІІІІІІІІІХХХХХХХХХХХХХУУУУУУУУУУУУЯЯЯЯЯЯЯЯЯЯЯЯ");
        System.out.println(currentTime);
        if (currentTime < time()) {
            System.out.println("_____________________________________UPDATED_____________________________________");
            for (int i = 0; i < copiedOperations.length; i++) {
                operations[i] = copiedOperations[i];
            }
        }
        ;

        return updated;
    }

    private boolean procedure(Operation[] copiedOperation, Operation left, Operation right) {
        int currentMaxTime = time();

        int leftIndex = -1;
        int rightIndex = -1;
        for (int i = 0; i < copiedOperation.length; i++) {
            if (copiedOperation[i].equals(left)) {
                leftIndex = i;
            }
            if (copiedOperation[i].equals(right)) {
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
        for (int i = 0; i < copiedOperation.length; i++) {
            if (!copiedS.contains(i)) {
                copiedOperation[i].setFMC(0);
                copiedOperation[i].setF(0);
            }
        }

        for (int i = 0; i < copiedS.size() + 1; i++) {
            copiedT.add(T.get(i));
        }
        // зануляєм F і

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
        System.out.println("current = " + currentMaxTime + " findMaxTime " + findMaxTime);
        if (currentMaxTime > findMaxTime) {
            // комітимо
            System.out.println("CCCCCCCCCCCCCCCCCCCCOOOOOOOOOOOOOOOOOOOOOOOOOOMMMMMMMMMMMMMMMMMMMIIIIIIIIIIIIIIIIIIIIIIIITTTTTTTTTTTTTTTT");
            for (int i = 0; i < copiedOperation.length; i++) {
                operations[i] = copiedOperation[i];
            }
            for (int i = 0; i < copiedT.size(); i++) {
                T.set(i, copiedT.get(i));
            }

            S.clear();
            for (Integer a : copiedS) {
                S.add(a);
            }

            return true;
        }
        return false;
    }

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

    private void swap(Operation[] operations, int a, int b) {
        temp = operations[a].getPrioriti();
        operations[a].setPrioriti(operations[b].getPrioriti());
        operations[b].setPrioriti(temp);
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
        // шукаємо наступний від нього FMC - processingTime
        ArrayList<Integer> criticalPath = new ArrayList<>();
        criticalPath.add(index);

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
                    criticalPath.add(i);
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
                    criticalPath.add(i);
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
//        timeTable = new Operation[m][all_time];
    }

}
