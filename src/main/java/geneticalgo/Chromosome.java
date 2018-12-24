package geneticalgo;

import lombok.Getter;
import lombok.Setter;

import static geneticalgo.Configurations.ProbabilityOfHead;


@Getter
class Chromosome {
    private double[] genes;
    @Setter
    private int timeOfShedule = -1;

    public Chromosome(int numberOfGenes) {
        this.genes = new double[numberOfGenes];
    }

    public void inisializedChromosome() {
        for (int i = 0; i < this.genes.length; i++) {
            this.genes[i] = Configurations.random.nextDouble();
        }
    }

    static Chromosome crossChromosome(Chromosome chromosomeHead, Chromosome chromosomeTail) {
        Chromosome newChromosome = new Chromosome(chromosomeHead.genes.length);

        for (int i = 0; i < newChromosome.genes.length; i++) {
            boolean isHead = ((int) (Configurations.random.nextDouble() / ProbabilityOfHead) ^ 1) == 1;
            newChromosome.genes[i] = isHead ? chromosomeHead.genes[i] : chromosomeTail.genes[i];
        }

        return newChromosome;
    }

}
