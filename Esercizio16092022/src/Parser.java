import java.io.*;
import java.util.ArrayList;

/**
 * La classe Parser si occupa di leggere il file di input e di generare la lista menu
 * @author Andrea Marano
 * @version 1.0
 */
public class Parser {

    private final ArrayList<Pietanza> menu;

    /**
     * Costruttore di default
     */
    public Parser()
    {
        this.menu = new ArrayList<>();
    }

    /**
     * Il metodo parse si occupa di leggere il file di input e di generare la lista menu
     * @return l'ArrayList di pietanze menu
     * @throws IOException se la lettura del file fallisce
     */
    public ArrayList<Pietanza> parse() throws Exception {

        InputStreamReader inputStreamReader = new InputStreamReader(getClass().getResourceAsStream("menu.txt"));

        BufferedReader reader = new BufferedReader(inputStreamReader);

        String line;

        while ((line = reader.readLine()) != null)
        {
            menu.add(new Pietanza(line.split(" ; ")));
        }

        reader.close();
        return menu;
    }

}
