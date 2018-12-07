package geneticalgo;

import static geneticalgo.Dispatcher.N;
import static geneticalgo.Dispatcher.r;

public class Chromosome {
    double[] gene;
    int time;

    public Chromosome() {
        this.gene = new double[2 * N];
    }


    public static Chromosome cross(Chromosome a, Chromosome b) {
        Chromosome m = new Chromosome();
        for (int i = 0; i < 2 * N; i++) {
            // Tosing Tail
            if ((int) (Dispatcher.r.nextDouble() / Dispatcher.P) == 0) {
                m.gene[i] = a.gene[i];
            } else {
                m.gene[i] = b.gene[i];
            }
        }
        return m;
    }

    public Chromosome initializedChromosome() {
        for (int i = 0; i < this.gene.length; i++) {
            this.gene[i] = r.nextDouble();
        }
        return this;
    }


}
