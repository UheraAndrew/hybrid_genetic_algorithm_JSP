package geneticalgo;

import java.util.Random;

public class Configurations {
    public final static double ProbabilityOfHead = 0.7;//PROBABILITY
    public final static Random random = new Random();
    public final static double multiplicatorForPopulation = 2.0;// in paper was 2

    public final static double partOfBestChromosomes = 0.1;
    public final static double partOfCrossChromosomes = 0.4;
    public final static double partOfNewChromosomes = 0.5;

    public final static double delayFactor = 1.5;

    public final static int numberOfPopulation = 400;

    static {
        random.setSeed(20);
    }
}
