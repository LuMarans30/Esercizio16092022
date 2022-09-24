import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Form1, sottoclasse di JFrame, contiene i componenti grafici e i metodi per la scrittura del file.
 * @author Andrea Marano
 * @version 1.0
 */
public class Form1 extends JFrame{

    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JButton btnInvio;
    private JComboBox<String> cmbTipo;
    private JComboBox<Pietanza> cmbPietanza;
    private JList<String> lstOrdini;
    private JTextField txtTotale;
    private JButton btnSalvaSuFile;
    private JButton btnSalvaCome;
    private ArrayList<Pietanza> menu;
    private final ArrayList<Pietanza> ordini;
    private final DefaultListModel dfl;
    private File file;


    public Form1() {

        ordini = new ArrayList<>();
        dfl = new DefaultListModel<>();
        file = null;

        try {
            createMenu();
            createPietanze();
        }catch(Exception ex)
        {
            showMessageDialog(this,  "Errore di lettura del file!", "Errore", JOptionPane.ERROR_MESSAGE);
        }

        btnInvio.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             * Salva i dati dell'ordinazione.
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Pietanza tmp = (Pietanza) cmbPietanza.getSelectedItem();
                ordini.add(tmp);
                lstOrdini.setModel(dfl);
                dfl.addElement(tmp);
                calcoloTotale();
            }
        });
        cmbTipo.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             * Aggiorna la combobox delle pietanze
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                createPietanze();
            }
        });

        lstOrdini.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             * Se si esegue un doppio click su un elemento della lista, l'elemento viene eliminato e il totale viene aggiornato
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JList list = (JList) e.getSource();
                if (e.getClickCount() == 2) {

                    // Double-click detected
                    int index = list.locationToIndex(e.getPoint());
                    ordini.remove((Pietanza) dfl.getElementAt(index));
                    dfl.removeElementAt(index);
                    calcoloTotale();
                }
            }
        });
        btnSalvaSuFile.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             * Salva le ordinazioni su un file
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(dfl.isEmpty())
                        showMessageDialog(null, "Non sono state fatte ordinazioni", "Avviso", JOptionPane.WARNING_MESSAGE);
                    else{
                        calcoloTotale();
                        if(file.exists())
                            salvaFile("save");
                        else
                            salvaFile("saveAs");
                    }
                }catch(Exception ex)
                {
                    showMessageDialog(null, "Errore di scrittura del file!", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnSalvaCome.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             * Salva le ordinazioni su un file nel percorso specificato da una finestra di dialogo
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(dfl.isEmpty())
                        showMessageDialog(null, "Non sono state fatte ordinazioni", "Avviso", JOptionPane.WARNING_MESSAGE);
                    else{
                        calcoloTotale();
                        salvaFile("saveAs");
                        if(file.exists())
                            btnSalvaSuFile.setEnabled(true);
                    }
                }catch(Exception ex)
                {
                    showMessageDialog(null, "Errore di scrittura del file!", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Aggiunta delle pietanze alla combobox
     */
    private void createPietanze()
    {
        cmbPietanza.removeAllItems();
        menu.forEach(o -> {

            String tipo = Objects.requireNonNull(cmbTipo.getSelectedItem()).toString();

            if(o.getTipo().equals(tipo)) {
                cmbPietanza.addItem(o);
            }
        });
    }

    /**
     * Calcola il prezzo totale delle ordinazioni
     */
    private void calcoloTotale()
    {
        double totale = ordini.stream().mapToDouble(Pietanza::getPrezzo).sum();
        txtTotale.setText(totale + " €");
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        JFrame frame = new Form1();
        frame.setTitle("Ristorante");
        frame.setContentPane(new Form1().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(new Dimension(360,480));
        frame.setResizable(false);
        frame.setVisible(true);
    }

    /**
     * Popola le combobox con i valori da file
     * @throws Exception Lancia un eccezzione se il parser riscontra un'errore
     */
    private void createMenu() throws Exception
    {
        Parser parser = new Parser();
        menu = parser.parse();

        menu.stream().filter(distinctByKey(Pietanza::getTipo)).forEach(o -> cmbTipo.addItem(o.getTipo()));
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Selezione del file tramite JFileChooser
     * @throws Exception se la selezione del file fallisce
     */
    private void salvaFile(String opzione) throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Salva le ordinazioni");
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("TXT files", "txt"));
        fileChooser.setAcceptAllFileFilterUsed(true);
        if(opzione.equals("saveAs")) {
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                scriviSuFile();
            }
        }else
            scriviSuFile();
    }

    /**
     * Scrive le ordinazioni sul file selezionato
     * @throws Exception se la scrittura del file fallisce
     */
    private void scriviSuFile() throws Exception
    {
        FileWriter fileWriter = new FileWriter(file);
        ArrayList<Pietanza> ordinazioni = new ArrayList<>();
        fileWriter.write("ORDINI:\n\n");

        for(int i=0;i<dfl.getSize();i++)
            ordinazioni.add((Pietanza) dfl.getElementAt(i)); //Leggo le ordinazioni dalla JList lstOrdini

        String[] tipologie = ordinazioni.stream() //Leggo tutti i tipi di pietanza dalla lista delle ordinazioni
            .map(Pietanza::getTipo)
            .distinct()
            .toArray(String[]::new);

        for (String tipo: tipologie) //Per ogni tipo di pietanza scrivo sul file la tipologia e tutte le ordinazioni corrispondenti
        {
            fileWriter.write("\n\n" + tipo.toUpperCase(Locale.ROOT) + ": \n");
            ordinazioni.stream().filter(o -> o.getTipo().equals(tipo)).forEach(o ->{
                try
                {
                    fileWriter.write(o + "\n");

                }catch (Exception ex) {

                    JOptionPane.showMessageDialog(null, "Impossibile scrivere l' ordinazione" + o + " su file", "Errore", JOptionPane.ERROR_MESSAGE );
                }
            });
        }

        fileWriter.write("\n\n-------------------------------------------------" + "\nTOTALE: " + txtTotale.getText());
        fileWriter.close();
    }

}
