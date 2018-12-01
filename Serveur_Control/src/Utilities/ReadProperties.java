/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadProperties {
    Properties prop;
    InputStream input = null;
    public ReadProperties(String Conf) throws FileNotFoundException, IOException {
        prop = new Properties();
        input = ReadProperties.class.getResourceAsStream(Conf);
        if(input==null)
    	            System.out.println("Sorry, unable to find " + Conf);
        prop.load(input);
    }
        
    public String getProp(String Chaine)
    {
        return(prop.getProperty(Chaine));
    }
}
