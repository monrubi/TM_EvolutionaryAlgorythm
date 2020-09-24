import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;

public class Main {

    public static void main(String[] args) {

    
    //String tapeString = TuringMachineGenerator.toBinaryASCII("El dinosaurio estaba alli");
    //TuringMachineGenerator gen = new TuringMachineGenerator(new Tape(tapeString), (int)Math.round(Math.random() * 100000), 200, 1200, 2500, 0.95f, 0.05f, 1000);
    


    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    TuringMachineGenerator gen = TuringMachineGenerator.getGeneratorByAsking();

    while (true) {
        // Corre el generador y obtiene una maquina
        gen.lastGeneration(false);

        TuringMachine bestMachine = gen.getBestMachine();
        Tape bestTape = gen.getBestTape();
        Tape tapeToAchieve = gen.getTapeToAchieve();

        System.out.println(TuringMachineGenerator.fromBinaryASCII(bestTape.toString()));

        // Imprime informacion util
        System.out.println("a) Numero de coincidencias: " + bestTape.getNumberOfMatches(tapeToAchieve));
        System.out.println("b) Longitud de la cinta de datos: " + tapeToAchieve.length());
        System.out.println("c) Generacion actual: " + gen.getCurrentGen());
        System.out.println("\n\t==> Tasa de coincidencias: " + (bestTape.getNumberOfMatches(tapeToAchieve) + 0.0)/tapeToAchieve.length());
        System.out.println("\n*************************************");
        System.out.println("* La complejidad de Kolmogorov: " + bestMachine.getKolmogorovComplexity() + " *");
        System.out.println("*************************************");

        System.out.println(Printer.printMachine(bestMachine.getReducedMachine().toString()));

        // Guarda en archivo
        try {
            PrintStream printStream = new PrintStream(new FileOutputStream(new File("PackedTM.txt")));
		    printStream.println(bestMachine.getReducedMachine().toString());
            printStream = new PrintStream(new FileOutputStream(new File("NewTape.txt")));
            printStream.println(bestTape.toString());
        }
        catch (Exception ex) {
            // Nunca va a pasar
        }
        System.out.println("Maquina de Turing compactada en PackedTM.txt");

        // Pregunta si se desea finalizar
        System.out.println("¿Desea continuar la busqueda? (S/*)");
        try {
            String val = reader.readLine().toUpperCase();
            // Repite iteraciones
            if (val.equals("S"))
                continue;
            // Fin de las iteraciones
            else throw new Exception();
        }
        // A un error, se toma como que el programa finaliza.
        catch (Exception ex) {
        }
            break;
        }
    }

}