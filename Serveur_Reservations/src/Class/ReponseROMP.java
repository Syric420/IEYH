package Class;

import Interfaces.Reponse;
import java.io.*;

public class ReponseROMP implements Reponse, Serializable
{
    public static int SUCCESS = 1;
    public static int FAILED = 2;
    
    private int code;
    private String chargeUtile;
    
    public ReponseROMP(int code)
    {
        this.code = code;
        
    }
    
    public ReponseROMP(int code, String cu)
    {
        this.code = code;
        this.chargeUtile = cu;
    }
    
    @Override
    public int getCode() 
    {
        return code;
    }

    /**
     * @return the chargeUtile
     */
    public String getChargeUtile() {
        return chargeUtile;
    }

    /**
     * @param chargeUtile the chargeUtile to set
     */
    public void setChargeUtile(String chargeUtile) {
        this.chargeUtile = chargeUtile;
    }
}
