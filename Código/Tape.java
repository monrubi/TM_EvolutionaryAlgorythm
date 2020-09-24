/**
 * Una cinta de Turing con funcionalidades.
 **/
public class Tape {

    private static final int BLOCK_SIZE = 8; // Estamos viendo ASCII

    private String string;

    public Tape(String string) {
        this.string = (string == null ? "" : string);
    }

    public static Tape createWithZeros(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            builder.append('0');

        return new Tape(builder.toString());
    }


    public String getString() {
        return string;
    }

    public boolean isEmpty() {
        return string.replace(" ", "").replace("\n","").equals("");
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
        while (offset < (i + 1) * BLOCK_SIZE && offset < string.length() && string.charAt(offset) == '0')
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

    /**
     * Obtiene el número de similitudes entre dos cintas.
     * Se considera que la otra cinta es igual o mas pequena.
     **/
    public int getNumberOfMatches(Tape other) {
        // Cinta nula
        if (other.isEmpty())
            return 0;
        
        // Consigue el primer y ultimo bloque
        int start = 0;
        while (isBlockEmpty(start)) start++;
        int finish = string.length() / BLOCK_SIZE - 1;
        while (isBlockEmpty(finish)) finish--;

        int matches = 0;

        
        // Luego compara bit por bit
        
        
        /*
        for (int i = start * BLOCK_SIZE, j = 0; i < finish * BLOCK_SIZE && j < other.string.length(); i++, j++)
            if (other.string.charAt(j) == string.charAt(i))
                matches++;
        */
        /*
        // Luego compara ascii por ascii
        for (int i = start, j = 0; i < finish && j < other.string.length() / BLOCK_SIZE - BLOCK_SIZE; i++, j++) {
            // Compara bit por bit
            int bit = 0;
            while(string.charAt(i * BLOCK_SIZE + bit) == other.string.charAt(j * BLOCK_SIZE + bit))
                bit++;
            if (bit == 8)
                matches++;
        }
        */

        // Busca el mejor match
        int maxMatches = 0;
        for (int i = 0; i < string.length() - other.string.length(); i++) {
            matches = 0;
            for (int j = 0; j < other.string.length(); j++) {
                if (string.charAt(i + j) == other.string.charAt(j))
                    matches++;
            }
            if (matches > maxMatches)
                maxMatches = matches;
        }

        return maxMatches;
    }

    public String toString() {
        return string;
    }
}