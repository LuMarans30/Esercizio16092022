import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * La classe Pietanza contiene il nome, il tipo e il prezzo
 * @author Andrea Marano
 * @version 1.0
 */
public class Pietanza {
    
    private final String nome;
    private final String tipo;
    private final double prezzo;

    public String getNome() {
        return nome;
    }


    public String getTipo() {
        return tipo;
    }


    public double getPrezzo() {
        return prezzo;
    }

    /**
     * Il costruttore della classe Pietanza riceve in input il nome, il tipo e il prezzo
     * @param nome Il nome della pietanza
     * @param tipo Il tipo di pietanza
     * @param prezzo Il prezzo della pietanza
     */
    public Pietanza(String nome, String tipo, double prezzo) {
        this.nome = nome;
        this.tipo = tipo;
        this.prezzo = prezzo;
    }

    /**
     * @param params parametri del costruttore di Pietanza, params: ["Carbonara ", " Primo ", " 12,00 "]
     * @throws Exception se il parsing del prezzo fallisce
     */
    public Pietanza(String[] params) throws Exception
    {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat format = new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.getDefault()));
        format.setDecimalFormatSymbols(symbols);

        this.nome = params[0];
        this.tipo = params[1];
        this.prezzo = format.parse(params[2]).doubleValue();
    }


    @Override
    public String toString() {
        return getNome() + " - " + getPrezzo() + " €";
    }
}
