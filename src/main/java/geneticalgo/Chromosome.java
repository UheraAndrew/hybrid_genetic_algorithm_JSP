package geneticalgo;

import lombok.Getter;
import lombok.Setter;

import static geneticalgo.Dispatcher.N;
import static geneticalgo.Dispatcher.r;

@Getter
class Chromosome {
    private double[] genes;
    @Setter
    private int time;

    static long H = 0;
    static long T = 0;

    Chromosome(int n) {
        this.genes = new double[n];
    }


    static Chromosome cross(Chromosome a, Chromosome b) {
        Chromosome m = new Chromosome(a.genes.length);
        for (int i = 0; i < 2 * N; i++) {
            // Tosing Tail
            if ((int) (Dispatcher.r.nextDouble() / Dispatcher.P) == 0) {
                ++H;
                m.genes[i] = a.genes[i];
            } else {
                ++T;
                m.genes[i] = b.genes[i];
            }
        }
        return m;
    }

    Chromosome initializedChromosome() {
        for (int i = 0; i < this.genes.length; i++) {
            this.genes[i] = r.nextDouble();
        }
        return this;
    }


}
