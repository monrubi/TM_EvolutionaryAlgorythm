import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.Random;
import java.util.ArrayList;

/**
 * Clase que genera maquinas de turing a partir de algoritmos geneticos.
 **/
public class TuringMachineGenerator {

    private TuringMachine bestIndividual;
    private int bestFitness = Integer.MIN_VALUE;
    private TuringMachine secondbestIndividual;
    private int secondBestFitness = Integer.MIN_VALUE;
    private int currentGeneration = 0;

    private Random random;
    private final Tape zeroTape;
    private final TuringMachine[] population;
    private final int[] fitness;

    private Tape tapeToAchieve;
    private int populationSize;
    private int maxTransitions;
    private int tapeLength;
    private float crossoverProb;
    private float mutationProb;
    private int generations;


    public TuringMachineGenerator(Tape tapeToAchieve, int seed, int populationSize, int maxTransitions,
        int tapeLength, float crossoverProb, float mutationProb, int generations) {
        
        random = new Random(seed);
        this.tapeToAchieve = tapeToAchieve;
        this.populationSize = populationSize;
        this.maxTransitions = maxTransitions;
        this.tapeLength = tapeLength;
        this.crossoverProb = crossoverProb;
        this.mutationProb = mutationProb;
        this.generations = generations;

        zeroTape = Tape.createWithZeros(tapeLength);
        population = new TuringMachine[populationSize];
        fitness = new int[populationSize];
    }

    public TuringMachine getBestMachine() {
        int best = Integer.MIN_VALUE;
        TuringMachine tm = null;
        computeFitness();
        for (int i = 0; i < populationSize; i++)
            if (fitness[i] > best) {
                best = fitness[i];
                tm = population[i];
            }
        return tm;
    }

    public int getCurrentGen(){
      return currentGeneration;
    }

    public Tape getBestTape() {
        return getTape(getBestMachine());
    }

    public Tape getTapeToAchieve() {
        return tapeToAchieve;
    }

    private void printAllFitness() {
        for (int i = 0; i < populationSize; i++)
            System.out.println(fitness[i]);
    }

    private void printAllMachines() {
        for (int i = 0; i < populationSize; i++)
            System.out.println(population[i].toString());
    }

    /**
     * Procesa el numero previamente indicado de generaciones.
     **/
    public void lastGeneration(boolean debug) {
        for (int i = 0; i < generations; i++) {
            nextGeneration();

            if (debug) {
                System.out.println(String.format("%-10s%-5d%-10s%-5d",
                    "GEN", currentGeneration, "Matches", getBestTape().getNumberOfMatches(tapeToAchieve)));
                //testing check
                /*Tape tape;
                for (int j = 0; j < populationSize; j++)
                {
                  System.out.print(getFitness(population[j]) + " ");
                }
                System.out.println(" ");*/
            }
        }
    }

    /**
     * Actualiza la mejor maquina para conseguir una cinta (genetico).
     **/
    public void nextGeneration() {

        currentGeneration++;

        // En la primera generacion solo generamos una poblacion aleatoria
        if (currentGeneration == 1) {
            // Pero tenemos un individuo de puros ceros
            population[0] = TuringMachine.createWithZeros();
            for (int i = 1; i < populationSize; i++)
                population[i] = TuringMachine.createRandom(random);

            computeFitness();
        }
        // Proximas generaciones
        else {

            // Selecciona las dos mejores maquinas
            selection();

            // Se cruzan estas maquinas
            if (random.nextDouble() < crossoverProb)
                crossover();

            // Se mutan
            if (random.nextDouble() < mutationProb)
                mutation();

            // Luego se agrega la mejor de las dos y se quita la peor
            addBestOffspring();

            //printAllFitness();
        }
    }

    /**
     * Selecciona a los dos mejores individuos.
     **/
    private void selection() {
        
        int bestI = 0;
        bestFitness = fitness[bestI];

        int secondBestI = 1;
        secondBestFitness = fitness[secondBestI];

        if (secondBestFitness > bestFitness)
        {
          secondBestI = 0;
          secondBestFitness = fitness[secondBestI];
        }
        // Selecciona el primer y segundo mejor
        for (int i = 0; i < populationSize; i++) {
            if (fitness[i] > bestFitness) {
                bestI = i;
                bestFitness = fitness[bestI];

            }
            else if (fitness[i] > secondBestFitness) {
                secondBestI = i;
                secondBestFitness = fitness[secondBestI];
            }
        }

        bestIndividual = population[bestI].copy();
        secondbestIndividual = population[secondBestI].copy();

    }

    /**
     * Cruza los genes de los mejores.
     **/
    private void crossover() {

        int crossPoint = random.nextInt(bestIndividual.length());

        // Cambia bit por bit
        for (int i = 0; i < crossPoint; i++) {
            char temp = bestIndividual.bitAt(i);
            bestIndividual.setBit(i, secondbestIndividual.bitAt(i));
            secondbestIndividual.setBit(i, temp);
        }
    }

    /**
     * Muta.
     **/
    private void mutation() {

        // Selecciona un punto random e invierte en los dos mejores
        int mutationPoint = random.nextInt(bestIndividual.length());
        bestIndividual.invertBit(mutationPoint);

        mutationPoint = random.nextInt(secondbestIndividual.length());
        secondbestIndividual.invertBit(mutationPoint);
    }

    /**
     * Agrega al mejor hijo y quita al peor individuo.
     **/
    private void addBestOffspring() {

        // Obtiene el peor individuo
        int worse = 0;
        for (int i = 0; i < populationSize; i++)
            if (fitness[i] < fitness[worse])
                worse = i;

        // Actualiza fitness de los offsprings y se queda con el mejor
        int first = getFitness(bestIndividual);
        int second = getFitness(secondbestIndividual);
        
        if (first > second) {
            population[worse] = bestIndividual;
            fitness[worse] = first;
        }
        else {
            population[worse] = secondbestIndividual;
            fitness[worse] = second;
        }
    }

    /**
     *  Obtiene el fitness de una maquina.
     **/
    private int getFitness(TuringMachine machine) {

        // Compara con la cinta objetivo
        Tape tape = getTape(machine);

        int matches = tape.getNumberOfMatches(tapeToAchieve);
        int val;
        if (tape.isEmpty() || matches == 0)
            val = Integer.MIN_VALUE;
        else val = matches * 1000 -(tape.realLength() - tapeToAchieve.length()) * 10 - machine.getKolmogorovComplexity() * 10;
        return val;
    }

    /**
     * Actualiza el fitness.
     **/
    private void computeFitness() {
        for (int i = 0; i < populationSize; i++)
            fitness[i] = getFitness(population[i]);
    }

    /**
     * Obtiene la cinta que genera una maquina.
     **/
    private Tape getTape(TuringMachine machine) {
        try {
            return new Tape(UTM.newTape(machine.toString(), zeroTape.toString(), maxTransitions, 0));
        }
        catch (Exception ex) { 
            return zeroTape;
        }
    }

    /**
     * Obtiene un generador de maquinas de Turing preguntando al usuario las opciones.
     **/
    public static TuringMachineGenerator getGeneratorByAsking() {
        
        Tape tapeToAchieve = null;
        int seed = 0;
        int populationSize = 100;
        int maxTransitions = 1200;
        int tapeLength = 2500;
        float crossoverProb = 0.95f;
        float mutationProb = 0.1f;
        int generations = 1000;
        

        // Buffer para lectura de archivos
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        // Repite el programa hasta que se indique lo contrario
        String currentCase = "seed";
        while (true) {
            // Dividimos en casos del procedimiento
            switch(currentCase) {
                // Obtiene la semilla de números aleatorios
                case "seed":
                    // Pide el seed para números aleatorios
                    System.out.println("\n");
                    System.out.println("Deme la semilla del generador de números aleatorios:");
                    try {
                        seed = Integer.parseInt(reader.readLine());
                    }
                    catch (Exception ex) {
                        System.out.println("Error al leer el dato.");
                        currentCase = "seed";
                        continue;
                    }
                // Obtiene el archivo de datos
                case "data":
                    // Se acepta la semilla, entonces continuamos leyendo el archivo de datos
                    System.out.println("Deme el nombre del archivo de datos que quiere procesar:");
                    try {
                        // Lee el archivo y cambia las letras por su valor binario en ascii
				        tapeToAchieve = new Tape(toBinaryASCII((new BufferedReader(new InputStreamReader(new FileInputStream(new File(reader.readLine()))))).readLine()));
                    }
                    catch (Exception ex) {
                        System.out.println("Error al leer el archivo.");
                        currentCase = "data";
                        continue;
                    }

                // Acepta la configuración
                case "accept":
                    System.out.println();
                    System.out.println(String.format("%-30s%d", "1) Numero de individuos:", populationSize));
                    System.out.println(String.format("%-30s%d", "2) Numero de transiciones:", maxTransitions));
                    System.out.println(String.format("%-30s%d", "3) Long. de la cinta:", tapeLength));
                    System.out.println(String.format("%-30s%f", "4) Prob. de cruzamiento:", crossoverProb));
                    System.out.println(String.format("%-30s%f", "5) Prob. de mutacion:", mutationProb));
                    System.out.println(String.format("%-30s%d", "6) Numero de generaciones:", generations));
                    System.out.println("\n¿Modificar (S/*)?");

                    try {
                        String val = reader.readLine().toUpperCase();
                        // Queremos modificar algo
                        if (val.equals("S")) {
                            currentCase = "modify";
                            continue;
                        }
                        // Todo bien, seguimos
                        else throw new Exception();
                    }
                    // Con error se toma como que funciona
                    catch (Exception ex) {
                        currentCase = "return";
                        continue;
                    }

                // Modifica los valores del programa
                case "modify":
                    try {
                        System.out.println("Numero de individuos:");
                        populationSize = Integer.parseInt(reader.readLine());
                        System.out.println("Numero de transiciones:");
                        maxTransitions = Integer.parseInt(reader.readLine());
                        System.out.println("Long. de la cinta:");
                        tapeLength = Integer.parseInt(reader.readLine());
                        System.out.println("Prob. de cruzamiento:");
                        crossoverProb = Float.parseFloat(reader.readLine());
                        System.out.println("Prob. de mutacion:");
                        mutationProb = Float.parseFloat(reader.readLine());
                        System.out.println("Numero de generaciones:");
                        generations = Integer.parseInt(reader.readLine());
                    }
                    catch (Exception ex) {
                        System.out.println("Error en el valor.");
                        currentCase = "modify";
                        continue;
                    }
                    // Cuando se modifican los datos, se va hacia la aceptación del programa
                    currentCase = "accept";
                    continue;

                case "return":
                    return new TuringMachineGenerator(tapeToAchieve, seed, populationSize, maxTransitions, tapeLength,
                    crossoverProb, mutationProb, generations);
            }
        }
    }

    /**
     * Transforma texto en una cadena de su valor binario en ASCII.
     **/
    public static String toBinaryASCII(String data) {
        StringBuilder builder = new StringBuilder(data.length() << 3);
        for (int i = 0; i < data.length(); i++) {
            String binary = Integer.toBinaryString((int)data.charAt(i));
            // Agrega ceros a la izquierda para completar un bit
            for (int j = binary.length(); j < 8; j++)
                binary = '0' + binary;
            builder.append(binary);
        }
        return builder.toString();
    }

    /**
     * Regresa el ASCII de una cadena de bits en ASCII.
     **/
    public static String fromBinaryASCII(String binaries) {
        StringBuilder builder = new StringBuilder(binaries.length() >> 3);
        String bit = "";
        for (int i = 1; i <= binaries.length(); i++) {
            bit += binaries.charAt(i - 1);
            if (i % 8 == 0) {
                builder.append((char)Integer.parseInt(bit, 2));
                bit = "";
            }
        }

        return builder.toString();
    }
}