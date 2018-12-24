package geneticalgo;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static geneticalgo.Configurations.*;

@Getter
public class Population {
    private Chromosome[] chromosomes;
    private int numberOfOperations;

    public Population(int numberOfOperation) {
        this.numberOfOperations = numberOfOperation;
        this.chromosomes = new Chromosome[(int) (numberOfOperation * Configurations.multiplicatorForPopulation)];
    }

    public void createGeneration() {
        for (int i = 0; i < this.chromosomes.length; i++) {
            this.chromosomes[i] = new Chromosome(numberOfOperations * 2);
            this.chromosomes[i].inisializedChromosome();
        }
    }

    public Population newGeneration() {
        Population next = new Population(this.chromosomes.length / 2);

        Chromosome[] nextChromosomes = next.chromosomes;
        Chromosome[] currentChromosomes = this.chromosomes;

        int genesNumber = currentChromosomes[0].getGenes().length;
        int chromosomesNumber = this.chromosomes.length;
        int i = 0;

        double part = partOfBestChromosomes;
        for (; i < chromosomesNumber * part; i++) {
            nextChromosomes[i] = currentChromosomes[i];
        }

        part += partOfCrossChromosomes;
        for (; i < chromosomesNumber * part; i++) {
            int a = Configurations.random.nextInt(chromosomes.length);
            int b = a;
            while (b == a) {
                b = Configurations.random.nextInt(chromosomes.length);
            }
            nextChromosomes[i] = Chromosome.crossChromosome(currentChromosomes[a], currentChromosomes[b]);
        }

        part += partOfNewChromosomes;
        for (; i < chromosomesNumber * part; i++) {
            nextChromosomes[i] = new Chromosome(genesNumber);
            nextChromosomes[i].inisializedChromosome();
        }

        return next;
    }

    public void sort() {
        Arrays.sort(chromosomes, (o1, o2) -> o1.getTimeOfShedule() - o2.getTimeOfShedule());
    }
}
