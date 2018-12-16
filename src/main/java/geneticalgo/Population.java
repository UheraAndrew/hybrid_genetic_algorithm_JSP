package geneticalgo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static geneticalgo.Dispatcher.N;
import static geneticalgo.Dispatcher.r;

public class Population {
    Chromosome[] chromosomes;

    public Population(int n) {
        this.chromosomes = new Chromosome[n];
    }

    public Population createGeneration(int genesNumber) {
        for (int i = 0; i < this.chromosomes.length; i++) {
            chromosomes[i] = new Chromosome(genesNumber).initializedChromosome();
        }
        return this;
    }

    public Population newGeneration() {

//        10% - еліта перекопійовуємо
//        наступні 20% схрещуємо
//        наступні рандомно накидуємо
        Population next = new Population(this.chromosomes.length);

        Chromosome[] nextChromosomes = next.chromosomes;
        Chromosome[] currentChromosomes = this.chromosomes;

        int genesNumber = currentChromosomes[0].getGenes().length;
        int chromosomesNumber = this.chromosomes.length;

        for (int i = 0; i < chromosomesNumber * 0.1; i++) {
            nextChromosomes[i] = currentChromosomes[i];
        }


        for (int i = (int) (chromosomesNumber * 0.1); i < chromosomesNumber * 0.8; i++) {
            int a = r.nextInt(N);
            int b = a;
            while (b == a) {
                b = r.nextInt(N);
            }
            nextChromosomes[i] = Chromosome.cross(currentChromosomes[a], currentChromosomes[b]);
        }

        for (int i = (int) (chromosomesNumber * 0.8); i < chromosomesNumber; i++) {
            nextChromosomes[i] = new Chromosome(genesNumber).initializedChromosome();
        }

        return next;
    }

    public void sort() {
        Arrays.sort(chromosomes, (o1, o2) -> o1.getTime() - o2.getTime());
    }
}
