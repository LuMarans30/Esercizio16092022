import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;


/**
 * La classe Parser si occupa di leggere il file di input e di generare la lista di ordini
 */
public class Parser {

    private ArrayList<Pietanza> ordini = new ArrayList<Pietanza>();
    private String path;

    /**
     * Il costruttore della classe Parser riceve in input il path del file
     * @param path
     * @throws Exception
     */
    public Parser(String path) throws Exception
    {
        this.path = path;
        this.ordini = new ArrayList<Pietanza>();
    }

    /**
     * Il costruttore di default della classe Parser
     */
    public Parser()
    {
        this.path = "";
        this.ordini = new ArrayList<Pietanza>();
    }
    

    /**
     * Il metodo parse si occupa di leggere il file di input e di generare la lista di ordini
     * @return
     * @throws IOException
     */
    public ArrayList<Pietanza> parse() throws Exception
    {

        File file = new File(getClass().getResource("menu.txt").toURI());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line=null;
        while((line=reader.readLine()) != null)
        {
            String[] splitted = line.split(" ; ");

            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            DecimalFormat format = new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.getDefault()));
            format.setDecimalFormatSymbols(symbols);

            double prezzo = format.parse(splitted[2]).doubleValue();

            ordini.add(new Pietanza(splitted[0], splitted[1], prezzo));
        }
        
        reader.close();
        return ordini;
    }

    
}
