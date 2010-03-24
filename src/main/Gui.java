/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author carlos
 */
public class Gui extends JFrame {

    private Controller controller = new Controller(); //controler for the buttons
    private int he = 50; // height of buttons
    private int wi = 100; // width of the buttons
    private int sep = 20; //distance between buttons
    private JButton bOpen = createButton("Open", 1, 1); //the open button
    private JButton bSave = createButton("Save", 3, 1); //the save button
    private JButton bCompile = createButton("Comp", 5, 1); //the compile button
    private JButton bStart = createButton("Sta", 7, 1);// the start button
    private JTextArea textArea = createTextArea(1, 2, 520, 400);//in this textarea you will see the document you try to open
    private JScrollPane areaScroll = createScrollPane(1, 2, 520, 400, textArea); //this is the jcrollpanel for the textarea
    private JTextArea console = createTextArea(0, 0, 0, 0); //the console for output
    private JScrollPane conScroll = createScrollPane(1, 8, 520, 200, console);
    private JFileChooser chooser = new JFileChooser();
    private File file = null; //global variable for the file
    //private JScrollPane scpArea;

    public Gui() {
        this.setTitle("Project MandatoryExercise");
        this.setSize(570, 820);
        this.setLocation(300, 0);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);

        //added all the buttons and textareas
        this.add(bOpen);
        this.add(bSave);
        this.add(bCompile);
        this.add(bStart);
        this.add(areaScroll);
        this.add(conScroll);

        //change default behavor of the
        console.setEditable(false);
        console.setBackground(Color.black);
        console.setForeground(Color.green);

    }

    /**
     * This method calculates the coordinates for buttons based on a
     * frame system
     * @param x
     * @param y
     * @return
     */
    private Point getPoint(int x, int y) {
        Point p = new Point((x * sep + (x - 1) * he), (y * sep + (y - 1) * he));
        return p;
    }

    /**
     * Method to create a button
     * @param name is the display name
     * @param PosX is the X position
     * @param PosY
     * @return an JButton
     */
    private JButton createButton(String name, int PosX, int PosY) {
        JButton b = new JButton(name);
        b.addActionListener(controller);
        b.setSize(wi, he);
        b.setLocation(getPoint(PosX, PosY));
        return b;
    }

    /**
     * Creates an JTextArea
     * @param PosX
     * @param PosY
     * @param sizeX
     * @param sizeY
     * @return JTextArea
     */
    private JTextArea createTextArea(int PosX, int PosY, int sizeX, int sizeY) {
        JTextArea t = new JTextArea();
        //JScrollPane scp = new JScrollPane(t);
        //t.setSize(sizeX, sizeY);
        //t.setLocation(getPoint(PosX, PosY));
        return t;
    }

    private JScrollPane createScrollPane(int PosX, int PosY, int sizeX, int sizeY, Object object) {
        JScrollPane scp = new JScrollPane((Component) object);
        scp.setSize(sizeX, sizeY);
        scp.setLocation(getPoint(PosX, PosY));
        return scp;
    }

    /**
     * This method opens a file an returns it as a string
     * @param file
     * @return String
     */
    private String fileOpener(File file) {
        StringBuffer stringBuffer = new StringBuffer("");
        FileInputStream fis = null;
        try {
            File f = file;
            String buff = null;
            fis = new FileInputStream(f);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);

            while ((buff = dis.readLine()) != null) {
                stringBuffer.append(buff + "\n");
            }

        } catch (IOException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return stringBuffer.toString();
    }

    /**
     * This method opens an file chooser
     * @return the selcted File
     */
    private File openFilechooser() {
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select an .java file");
        chooser.showOpenDialog(Gui.this);
        File f = chooser.getSelectedFile();
        return f;
    }

    private void startRuntime(String str) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(str);
                        
            //Starting to read the inputstream from the procces
            InputStream inputstream =
                    proc.getInputStream();
            InputStreamReader inputstreamreader =
                    new InputStreamReader(inputstream);
            BufferedReader bufferedreader =
                    new BufferedReader(inputstreamreader);


            String line;
            while ((line = bufferedreader.readLine())
                      != null) {
                console.append(line + "\n");
            }

            //
            inputstream = proc.getErrorStream();
            inputstreamreader = new InputStreamReader(inputstream);
            bufferedreader = new BufferedReader(inputstreamreader);
            
            while ((line = bufferedreader.readLine())
                      != null) {
                
                //console.
                console.append(line + "\n");
            }

            try {
                if (proc.waitFor() != 0) {
                    console.append("exit value = " +
                            proc.exitValue()+ "\n");
                    System.err.println("exit value = " +
                            proc.exitValue());
                }
            } catch (InterruptedException e) {
                System.err.println(e);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * writes all text from the textArea in an file
     */
    private void fileWriter() {
        try {
            // Create file
            if (file == null) {
                file = openFilechooser();
            }
            FileWriter fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(textArea.getText());
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            console.append("Error: " + e.getMessage()+ "\n");
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * our controler for the buttons
     */
    private class Controller implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //The Open button
            if (e.getSource() == bOpen) {
                file = openFilechooser();
                textArea.setText(fileOpener(file));
            } else if (e.getSource() == bSave) { //the save button
                fileWriter();
            } else if (e.getSource() == bCompile) { //the compile button
                if(file == null){
                    throw new RuntimeException("Meh ... you need an file to compile");
                }
                startRuntime("/usr/bin/javac "+ file.toString());
            } else if (e.getSource() == bStart) {
                if(file == null){
                    throw new RuntimeException("Meh ... you need an file to compile");
                }
                String splitFile = file.getName().substring(0, file.getName().length()-5);
                startRuntime("/usr/bin/java -cp "+ file.getParent() +" "+ splitFile);
            }
        }
    }
}
