package vibrofeeldebug;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class InterfaceWnd {
    private int MIN = 0;
    private int MAX = 255;

    private JComboBox comboBox = new JComboBox<Object>();
    private ArrayList<Object> comboBoxOptions;
    private boolean comboBoxVisible = false; 
    private JFrame frame = new JFrame();
    private String[] buttonTexts = {"Connect", "Disconnect"};
    private JButton btnConnect = new JButton(buttonTexts[0]);
    private JTextArea textArea = new JTextArea();
    JSlider smallMotorSlider = new JSlider(JSlider.HORIZONTAL, MIN, MAX, MIN);
    JSlider bigMotorSlider = new JSlider(JSlider.HORIZONTAL, MIN, MAX, MIN);


    private JLabel noPorts = new JLabel("No ports found");

    @FunctionalInterface
    public interface CallbackFn {
        void call();
    }
    @FunctionalInterface
    public interface CallbackOnConnect {
        Boolean call(String portName);
    }



    public InterfaceWnd(CallbackFn onRefresh, CallbackOnConnect onConnect, CallbackFn onSliderChanged, CallbackFn onExit) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Vibrofeel debug");
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowListener(){

            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (!comboBox.isEnabled()) {
                    onExit.call();

                }
            }

            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
            
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                frame.dispose();
            }

            @Override
            public void windowIconified(java.awt.event.WindowEvent e) {

            }

            @Override
            public void windowDeiconified(java.awt.event.WindowEvent e) {

            }

            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowDeactivated(java.awt.event.WindowEvent e) {
                // TODO Auto-generated method stub
                
            }
        });
        
        JButton btn = new JButton("Refresh");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRefresh.call();
            }
        });

        comboBox.setPreferredSize(new Dimension(200, 50));
        comboBox.setVisible(false);
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object item = comboBox.getSelectedItem();
                boolean result = onConnect.call(item.toString());
                bigMotorSlider.setValue(0);
                smallMotorSlider.setValue(0);
                if (result) {
                    comboBox.setEnabled(false);
                    bigMotorSlider.setVisible(true);
                    smallMotorSlider.setVisible(true);
                    btnConnect.setText(buttonTexts[1]);
                } else {
                    comboBox.setEnabled(true);
                    bigMotorSlider.setVisible(false);
                    smallMotorSlider.setVisible(false);
                    btnConnect.setText(buttonTexts[0]);
                }
                update();
            }
        });

        textArea.setEditable(false);
        textArea.setPreferredSize(new Dimension(200, 100));

        smallMotorSlider.setVisible(false);
        bigMotorSlider.setVisible(false);

        smallMotorSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
              onSliderChanged.call();
            }
        });
      
        bigMotorSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
              onSliderChanged.call();
            }
        });
      

        JPanel[] groups = new JPanel[5];

        for (int i = 0; i < groups.length; i++) {
            groups[i] = new JPanel();
            panel.add(groups[i]);
        }

        groups[0].add(comboBox);
        groups[0].add(noPorts);
        groups[1].add(btn);
        groups[1].add(btnConnect);
        groups[2].add(textArea);
        groups[3].add(smallMotorSlider);
        groups[4].add(bigMotorSlider);

    }

    public void show(){
        frame.setVisible(true);
    }
    public void hide(){
        frame.setVisible(false);
    }

    public void addComboItem(String item) {
        Object objectifiedItem = makeComboBoxSelection(item);
        comboBox.addItem(objectifiedItem);
        comboBox.setVisible(true);
        btnConnect.setVisible(true);
        noPorts.setVisible(false);
        
        update();
    }
    
    public void reset() {
        comboBox.removeAllItems();
        noPorts.setVisible(true);
        btnConnect.setVisible(false);
        comboBox.setVisible(false);
        comboBox.setEnabled(true);
        btnConnect.setText(buttonTexts[0]);
        smallMotorSlider.setVisible(false);
        smallMotorSlider.setValue(0);
        bigMotorSlider.setVisible(false);
        bigMotorSlider.setValue(0);
        update();
    }

    public int[] getValues() {
        int s = smallMotorSlider.getValue();
        int l = bigMotorSlider.getValue();
        int[] sl = { s, l };
        return sl;
    }

    private void update() {
        //SwingUtilities.updateComponentTreeUI(frame);
        frame.invalidate();
        frame.validate();
        frame.repaint();
    }

    public void log(String text) {
        String[] lines = textArea.getText().split("\n");
        int maxLines = 20;
        int totalLines = lines.length > maxLines ? maxLines : lines.length;
        String newText = text + "\n";
        for (int i = 0; i < totalLines; i++) {
            newText += lines[i] + "\n"; 
        }
        textArea.setText(newText);
        System.out.println(text);
        this.update();
    }

    private Object makeComboBoxSelection(final String item)  {
        return new Object() { 
            public String toString() { 
                return item; 
            } 
        };
    }
}
