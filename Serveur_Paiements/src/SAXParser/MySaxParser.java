package SAXParser;

import java.util.Vector;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class MySaxParser extends DefaultHandler
{
    protected String nomFichierXML;
    int cptTags = 0;
    
    String tag = ""; String attribute = "";
    
    private Vector<String> vecNoms_serveurs = new Vector<String>();
    private Vector<String> vecInfos_root = new Vector<>();
    private Vector<String> vecConnector= new Vector<>();
    private Vector<String> vecArchitecture= new Vector<>();
    private Vector<String> vecBDOracle= new Vector<>();
    private Vector<String> vecBDMysql= new Vector<>();

    public MySaxParser()
    {
        this.setNomFichierXML(null);
    }
    
    public MySaxParser(String s)
    {
        this.setNomFichierXML(s);
    }
    /**
     * @return the nomFichierXML
     */
    public String getNomFichierXML()
    {
        return nomFichierXML;
    }

    /**
     * @param nomFichierXML the nomFichierXML to set
     */
    public void setNomFichierXML(String nomFichierXML)
    {
        this.nomFichierXML = nomFichierXML;
    }
    
    static protected void trace (String s)
    {
        System.out.println(s);
    }
    static protected void trace (String sCte, String s)
    {
        System.out.println(sCte + " : " + s);
    } 
    
    static protected void trace (String sCte, int i)
    {
       System.out.println(sCte + " " + i);
    } 

    // Quelques méthodes du ContentHandler
    @Override
    public void characters(char[] ch, int start,int length) throws SAXException
    {
        String chaine = new String(ch, start, length).trim();
        if (chaine.length() > 0)
        {
            trace("@Chaine", chaine);
            
            switch(tag) //en fonction du tag principal, on ajoute ses éléments dans le bon vecteur
            {
                case "noms_serveurs":
                    vecNoms_serveurs.add(chaine);
                    break;
                case "infos_root":
                    vecInfos_root.add(chaine);
                    break;
                case "connector":
                    vecConnector.add(chaine);
                    break;
                case "architecture":
                    vecArchitecture.add(chaine);
                    break;
                case "databases":
                    if(attribute.equals("oracle")) vecBDOracle.add(chaine);
                    else vecBDMysql.add(chaine);
                    break;
            }
        }
    }

    @Override
    public void startDocument()throws SAXException
    {
        trace("** Début du document **");
    }
    
    @Override
    public void endDocument()throws SAXException
    {
        trace("** Fin du document **");
    }

    @Override
    public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qName, Attributes attr) throws SAXException
    {
        trace("-Tag-", cptTags + " " + qName + " " + tag);
        
        if(cptTags == 1 || cptTags == 7 || cptTags == 13 || cptTags == 19 || cptTags == 25) //à chaque tag principaux - incréments de 6
            tag = qName; //on stocke le tag concerné
        
        cptTags++;
        if (uri != null && uri.length()>0) 
            trace(" uri", uri);
        if (uri != null && uri.length()>0) 
            trace(" nom complet", qName);
        
        int nAttr = attr.getLength();
        trace(" nombre d'attributs", nAttr);
        if (nAttr == 0) return; // Denys like
        for (int i=0; i<nAttr; i++)
        {
            trace(" attribut n°" + i + " = " + attr.getLocalName(i) + " avec valeur : " + attr.getValue(i));
            attribute = attr.getValue(i); //on stocke l'attribut
        }
    }

    @Override
    public void endElement(java.lang.String uri, java.lang.String localName,java.lang.String qName) throws SAXException
    {
        trace("* Fin de l'élément " + qName);
        cptTags++;
        trace("++ compteur de tags", cptTags);
    }

    /**
     * @return the vecNoms_serveurs
     */
    public Vector<String> getVecNoms_serveurs()
    {
        return vecNoms_serveurs;
    }

    /**
     * @param vecNoms_serveurs the vecNoms_serveurs to set
     */
    public void setVecNoms_serveurs(Vector<String> vecNoms_serveurs)
    {
        this.vecNoms_serveurs = vecNoms_serveurs;
    }

    /**
     * @return the vecInfos_root
     */
    public Vector<String> getVecInfos_root()
    {
        return vecInfos_root;
    }

    /**
     * @param vecInfos_root the vecInfos_root to set
     */
    public void setVecInfos_root(Vector<String> vecInfos_root)
    {
        this.vecInfos_root = vecInfos_root;
    }

    /**
     * @return the vecConnector
     */
    public Vector<String> getVecConnector()
    {
        return vecConnector;
    }

    /**
     * @param vecConnector the vecConnector to set
     */
    public void setVecConnector(Vector<String> vecConnector)
    {
        this.vecConnector = vecConnector;
    }

    /**
     * @return the vecArchitecture
     */
    public Vector<String> getVecArchitecture()
    {
        return vecArchitecture;
    }

    /**
     * @param vecArchitecture the vecArchitecture to set
     */
    public void setVecArchitecture(Vector<String> vecArchitecture)
    {
        this.vecArchitecture = vecArchitecture;
    }

    /**
     * @return the vecBDOracle
     */
    public Vector<String> getVecBDOracle()
    {
        return vecBDOracle;
    }

    /**
     * @param vecBDOracle the vecBDOracle to set
     */
    public void setVecBDOracle(Vector<String> vecBDOracle)
    {
        this.vecBDOracle = vecBDOracle;
    }

    /**
     * @return the vecBDMysql
     */
    public Vector<String> getVecBDMysql()
    {
        return vecBDMysql;
    }

    /**
     * @param vecBDMysql the vecBDMysql to set
     */
    public void setVecBDMysql(Vector<String> vecBDMysql)
    {
        this.vecBDMysql = vecBDMysql;
    }
}
