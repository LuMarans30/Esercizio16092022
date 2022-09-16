import javax.swing.*;
import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static javax.swing.JOptionPane.showMessageDialog;

public class Form1 extends JFrame{
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JButton btnInvio;
    private JComboBox cmbTipo;
    private JComboBox cmbPietanza;
    private JList lstOrdini;
    private JButton btnTotale;
    private JTextField txtTotale;
    private ArrayList<Pietanza> menu;
    private ArrayList<Pietanza> ordini;


    public Form1() {

        ordini = new ArrayList<Pietanza>();
        DefaultListModel<Pietanza> dfl = new DefaultListModel<Pietanza>();

        try {
            createMenu();
        }catch(Exception ex)
        {
            showMessageDialog(this,  "Errore di lettura del file!", "Errore", JOptionPane.ERROR_MESSAGE);
        }

        btnInvio.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             * Salva i dati dell'utente e dell'ordinazione.
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
                cmbPietanza.removeAllItems();
                menu.stream().forEach(o -> {
                    if(o.getTipo().equals(cmbTipo.getSelectedItem().toString())) {
                        cmbPietanza.addItem(o);
                    }
                });
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
                txtTotale.setText(String.valueOf(totale) + " €");
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
     * @throws Exception
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

}
