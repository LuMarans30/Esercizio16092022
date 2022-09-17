import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Form1, sottoclasse di JFrame, contiene i componenti grafici e i metodi per la scrittura del file
 */
public class Form1 extends JFrame{
    private JPanel panel1;
    private JButton btnInvio;
    private JTabbedPane tabbedPane1;
    private JComboBox<String> cmbTipo;
    private JComboBox<Pietanza> cmbPietanza;
    private JList lstOrdini;
    private JButton btnTotale;
    private JTextField txtTotale;
    private JButton btnSalvaSuFile;
    private JButton btnOrdina;
    private ArrayList<Pietanza> menu;
    private ArrayList<Pietanza> ordini;
    private DefaultListModel dfl;
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
        btnTotale.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             * Calcola il costo totale
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {

                double totale = ordini.stream().mapToDouble(o -> o.getPrezzo()).sum();
                txtTotale.setText(totale + " €");
            }
        });

        lstOrdini.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JList list = (JList)e.getSource();
                if (e.getClickCount() == 2) {

                    // Double-click detected
                    int index = list.locationToIndex(e.getPoint());
                    ordini.remove(dfl.getElementAt(index));
                    dfl.removeElementAt(index);
                    btnTotale.doClick();
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
                    salvaFile();
                }catch(Exception ex)
                {
                    showMessageDialog(null,  "Errore di scrittura del file!", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void createPietanze()
    {
        cmbPietanza.removeAllItems();
        menu.forEach(o -> {
            if(o.getTipo().equals(cmbTipo.getSelectedItem().toString())) {
                cmbPietanza.addItem(o);
            }
        });
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
        Parser parser = new Parser("menu.txt");
        menu = parser.parse();

        menu.stream().filter(distinctByKey(Pietanza::getTipo)).forEach(o -> {
            cmbTipo.addItem(o.getTipo());
        });
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private void salvaFile() throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Salva le ordinazioni");
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("TXT files", "txt"));
        fileChooser.setAcceptAllFileFilterUsed(true);
        if(file==null) {
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                scriviSuFile();
            }
        }else
            scriviSuFile();

    }

    private void scriviSuFile() throws Exception
    {
        FileWriter fileWriter = new FileWriter(file);
        int i;
        fileWriter.write("ORDINI:\n\n");

        for(i=0;i<dfl.getSize();i++)
            fileWriter.write(dfl.getElementAt(i) + "\n");

        fileWriter.write("\n\n-------------------------------------------------" + "\nTOTALE: " + txtTotale.getText());
        fileWriter.close();
    }

}
