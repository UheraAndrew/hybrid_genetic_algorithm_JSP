package geneticalgo;


import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import static geneticalgo.Configurations.numberOfPopulation;

public class Dispatcher {


    public static void main(String[] args) {
        try {
            String text = new String(Files.readAllBytes(Paths.get("src/main/resources/dataset.json")), StandardCharsets.UTF_8);
            JSONArray tests = new JSONArray(text);
            JSONObject test;
            for (int i = 0; i < tests.length(); i++) {

                test = (JSONObject) tests.get(i);
                JSONArray matrix = (JSONArray) test.get("data");
                int[][] dataSet = new int[matrix.length()][];
                for (int j = 0; j < matrix.length(); j++) {
                    JSONArray array = (JSONArray) matrix.get(j);
                    dataSet[j] = new int[array.length() / 2];
                    for (int k = 0; k < array.length(); k = k + 2) {
                        dataSet[j][array.getInt(k)] = array.getInt(k + 1);
                    }

                }
                System.out.println("test name: " + test.get("description") + " time = " + runTest(dataSet));
                System.exit(47);
            }

        } //      Считуєм джейсон
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static int runTest(int[][] dataSet) {
        Population population = new Population(dataSet.length * dataSet[0].length);
        population.createGeneration();
        int bestTime = Integer.MAX_VALUE;
        Chromosome bestChromosome = null;
        for (int i = 0; i < numberOfPopulation; i++) {
            for (int j = 0; j < population.getChromosomes().length; j++) {
                Shedule temp = new Shedule(dataSet, population.getChromosomes()[j]);
                population.getChromosomes()[j].setTimeOfShedule(temp.getTimeOfAllWorks());
                if (bestTime > population.getChromosomes()[j].getTimeOfShedule()) {
                    bestTime = population.getChromosomes()[j].getTimeOfShedule();
                    bestChromosome = population.getChromosomes()[j];
                }
            }
            System.out.println("Population #" + i + "[Done] Best time = " + bestTime);
            population = population.newGeneration();
        }
        System.out.println("Best Chromosome = " + Arrays.toString(bestChromosome.getGenes()));
        return bestTime;

    }


}
