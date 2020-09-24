import java.util.Random;

/**
 * Una clase que define una máquina de Turing de 64 estados en formato de bits-ASCII.
 **/
public class TuringMachine {

    public static final int MAX_STATE = 0b111111; // 64 estados (000 0000 - 1111)
    public static final int BLOCK_SIZE = 16; // Bits por bloque
    public static final int LENGTH = (MAX_STATE + 1) * BLOCK_SIZE;

    private StringBuilder string;

    public TuringMachine(String string) {

        this.string = new StringBuilder(string.length());
        this.string.append(string);
    }

    /**
     * Crea una maquina de Turing con puros ceros.
     **/
    public static TuringMachine createWithZeros() {
        StringBuilder builder = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++)
            builder.append('0');
        
        return new TuringMachine(builder.toString());
    }

    /**
     * Crea una maquina de Turing random.
     **/
    public static TuringMachine createRandom(Random random) {
        StringBuilder builder = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) 
            builder.append(random.nextDouble() + (i / LENGTH) < 0.5 ? '1' : '0');

        return new TuringMachine(builder.toString());
    }

    public String getString() {
        return string.toString();
    }

    public TuringMachine copy() {
        return new TuringMachine(string.toString());
    }

    public char bitAt(int i) {
        return string.charAt(i);
    }

    public void setBit(int i, char bit) {
        string.setCharAt(i, bit);
    }

    public void invertBit(int i) {
        setBit(i, bitAt(i) == '0' ? '1' : '0');
    }

    public int getNumberOfStates() {
        return realLength() / BLOCK_SIZE;
    }

    /**
     * Checa si en un bloque hay puros ceros.
     **/
    private boolean isBlockEmpty(int i) {
        int offset = i * BLOCK_SIZE;

        // Limit
        if (offset >= string.length() || offset < 0)
            return false;

        int nextOffset = (i + 1) * BLOCK_SIZE;
        while (offset < (i + 1) * BLOCK_SIZE && string.charAt(offset) == '0')
            offset++;

        return offset >= nextOffset;
    }

    public int length() {
        return string.length();
    }

    /**
     * Obtiene la longitud de la maquina eliminando bloques de puros ceros.
     **/
    public int realLength() {
        // Quita todos los bloques de la izquierda y derecha que sean puro cero
        int start = 0;
        while (isBlockEmpty(start)) start++;
        int finish = string.length() / BLOCK_SIZE - 1;
        while (isBlockEmpty(finish)) finish--;

        // Y regresa la longitud
        return Math.abs(finish - start) * BLOCK_SIZE;
    }

    
    
    public int usedStates() {
        // Quita todos los bloques de la izquierda y derecha que sean puro cero
        
        boolean[] states = new boolean[MAX_STATE+1];
        int numUsedStates = 1;

        states[0] = true;

        for (int i = 2; i < LENGTH; i += 8)
        {
            int nextState = Integer.parseInt(string.substring(i,i+6), 2);
            if (!states[nextState])
            {
              states[nextState] = true;
              numUsedStates++;
            }

        }

        return numUsedStates;
    }
    

    public String toString() {
        return string.toString();
    }

    public int getKolmogorovComplexity() {
        return usedStates() * BLOCK_SIZE;
    }

    public TuringMachine getReducedMachine()
    {
              
        boolean[] states = new boolean[MAX_STATE+1];

        states[0] = true;

        for (int i = 2; i < LENGTH; i += 8)
        {
            int nextState = Integer.parseInt(string.substring(i,i+6), 2);
            if (!states[nextState])
            {
              states[nextState] = true;
            }

        }
        

        int[] newStates = new int[MAX_STATE+1];

        for (int i = 0; i < MAX_STATE; i++)
        {
          if(states[i])
          {
            int newState = 0;
            for (int j = 0; j < i; j++)
            {
              if (states[j])
              {
                  newState++;
              }
            }
            newStates[i] = newState;
          }
        }

        newStates[MAX_STATE] = MAX_STATE; 
        
        StringBuilder newMachine = new StringBuilder();

        for (int i = 0; i < MAX_STATE; i++)
        {
          if (states[i])
          {
            newMachine.append(string.substring(16*i,16*i+2));
            //System.out.println(string.substring(16*i,16*i+2));
            int oldState = Integer.parseInt(string.substring(16*i+2,16*i+8), 2);
            int newState = newStates[oldState];
            newMachine.append(paddedString(newState));
            //System.out.println(paddedString(newState));
            
            newMachine.append(string.substring(16*i+8,16*i+10));
            //System.out.println(string.substring(16*i+8,16*i+10));
            oldState = Integer.parseInt(string.substring(16*i+10,16*i+16), 2);
            newState = newStates[oldState];
            newMachine.append(paddedString(newState));
            //System.out.println(paddedString(newState));
          }
        }

        return new TuringMachine(newMachine.toString());
    }

    static private String paddedString(int state)
    {
      String unpadded = Integer.toBinaryString(state);
      return "000000".substring(unpadded.length()) + unpadded;
    }

}