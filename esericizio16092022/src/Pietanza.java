/**
 * La classe Pietanza contiene il nome, il tipo e il prezzo
 */
public class Pietanza {
    
    private String nome;
    private String tipo;
    private double prezzo;


    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getTipo() {
        return tipo;
    }


    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public double getPrezzo() {
        return prezzo;
    }


    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }


    /**
     * Il costruttore della classe Pietanza riceve in input il nome, il tipo e il prezzo
     * @param nome
     * @param tipo
     * @param prezzo
     */
    public Pietanza(String nome, String tipo, double prezzo) {
        this.nome = nome;
        this.tipo = tipo;
        this.prezzo = prezzo;
    }


    @Override
    public String toString() {
        return getNome() + " - " + getPrezzo() + " €";
    }
}
