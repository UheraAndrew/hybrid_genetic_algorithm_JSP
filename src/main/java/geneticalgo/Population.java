package geneticalgo;

import static geneticalgo.Dispatcher.N;
import static geneticalgo.Dispatcher.r;

public class Population {
    Chromosome[] chromosomes;

    public Population(int n) {
        this.chromosomes = new Chromosome[n];
    }

    public Population createGeneration() {
        for (int i = 0; i < this.chromosomes.length; i++) {
            chromosomes[i] = new Chromosome().initializedChromosome();
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


        for (int i = 0; i < N * 0.1; i++) {
            nextChromosomes[i] = currentChromosomes[i];
        }


        for (int i = (int) (N * 0.1); i < N * 0.3; i++) {

            int a = r.nextInt(N);
            int b = a;
            while (b == a) {
                b = r.nextInt(N);
            }

            nextChromosomes[i] = Chromosome.cross(currentChromosomes[a], currentChromosomes[b]);

        }

        for (int i = (int) (N * 0.2); i < N; i++) {
            nextChromosomes[i] = new Chromosome().initializedChromosome();
        }

        return next;
    }

    public void sort() {
    }
}
