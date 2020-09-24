
public class UTM {
    public static String newTape(String TT, String Cinta, int N, int P)
    {
        StringBuilder tape = new StringBuilder(Cinta);
        
        int state = 0;
        
        for (int i = 0; i < N; i++)
        {   
            char S = tape.charAt(P);
            int rulepos = (S == '0')? 16*state : 16*state + 8;
            
            char newS = TT.charAt(rulepos);
            tape.setCharAt(P,newS);
            
            P = (TT.charAt(rulepos+1) == '0')? P+1 : P-1;
            
            state = Integer.parseInt(TT.substring(rulepos+2,rulepos+8), 2);
            
            if (state == 63)
            {
                //System.out.println("\nHALT state was reached");
                //System.out.println("\t" + (i+1) + " transitions were performed");
                //System.out.println("The productivity of this machine is " + prod);
                return tape.toString();
            }
            
            if (P < 0)
            {
                //System.out.println("\nTape underflow");
                return "";
            }
            
            if (P >= tape.length())
            {
                //System.out.println("\nTape overflow");
                return "";
            }
        }
        
        //System.out.println("\nMaximum number of transitions was reached");
        return tape.toString();
        
    }
}
