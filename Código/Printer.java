/*
 *	Alimenta una Maquina de Turing
 */
import java.io.* ;

class Printer {

    public static String printMachine(String machine){
    StringBuilder sb = new StringBuilder();
    int numStates = (int)machine.length() / 16;
    int estado, inicioEstado=0, actual = 1;
    String binaryState, espacio;

    sb.append("Hay " + numStates + " estados en la Maquina de Turing\n");
    sb.append(" EA | O | M | SE || O | M | SE |\n");
    sb.append(" -------------------------------");
    for (int i = 0; i < numStates*2; i++) {
      if(actual<10) espacio = "  ";
      else espacio = " ";
      //end if 

            //agrega el output
      if(inicioEstado % 16 == 0){
        sb.append("\n"+ espacio + actual + " |");
        actual++;
      }
      else sb.append("||");
      int paro = 7;
      //agrega el siguiente estado
      sb.append(" "+machine.charAt(inicioEstado)+" |");
      if (machine.charAt(inicioEstado) == '0') sb.append(" R |");
      else sb.append(" L |");
      binaryState = machine.substring(inicioEstado + 2, inicioEstado + 8);
      estado = Integer.parseInt(binaryState,2);
      if (estado < 10) espacio = "  ";
      else espacio = " ";
      //endif
      
      if (estado == 63) {
        sb.append("  H ");
      } else {
        sb.append(espacio + estado + " ");
      } //endif
      
      if (inicioEstado % 16 != 0)sb.append("|");
      inicioEstado += 8;
    } //endFor
    return sb.toString();
  }
}