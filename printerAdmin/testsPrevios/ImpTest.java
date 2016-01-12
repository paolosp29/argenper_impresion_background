package com.arp;

import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import org.w3c.dom.stylesheets.StyleSheet;

public class ImpTest {

    static MessageFormat head = new MessageFormat("");
    static MessageFormat foot = new MessageFormat("");

    public static void main(String[] args) throws Exception {

        PrinterJob pj = PrinterJob.getPrinterJob();
        if(pj.printDialog()) {
            JEditorPane text = new JEditorPane("text/html", "text");
            HTMLEditorKit kit = new HTMLEditorKit();

            // make it read-only
            text.setEditable(false);
            
            // now add it to a scroll pane
            JScrollPane scrollPane = new JScrollPane(text);
            
            javax.swing.text.html.StyleSheet styleSheet = kit.getStyleSheet();
            styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
            styleSheet.addRule("h1 {color: blue;}");
            styleSheet.addRule("h2 {position: absolute; top: 100px; left: 300px; color: #ff0000;}");
            styleSheet.addRule("span {font : 10px monaco; color : black; background-color : #fafafa; }");            
            
//            text.setText("Return this page to Shyarmal.");
//            text.read(new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/pablo/Desktop/a.html")))), "");
//            text.repaint();
            Document doc = kit.createDefaultDocument();
            text.setEditorKit(kit);
            text.setDocument(doc);            
//            text.setText("<html><head><script language='javascript'>function a(){ window.alert('hola');}</script></head><body><input type='button' name='btn' value='hola' onclick='a()'><h1>HOLA</h1><h2>Javon</h2><span class='st1'>cdlololololo/c d</span></body></html>");
            text.setText("<html><head><script language='javascript'>function a(){ window.alert('hola');}</script></head><body><input type='button' name='btn' value='hola' onclick='a()'></body></html>");
            pj.setPrintable(text.getPrintable(head, foot));
            pj.print();
            System.out.println("done .............. ");
        }
    }
}
/*
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ImpTest {
  Display display = new Display();
  Shell shell = new Shell(display);

  public ImpTest() {
    shell.pack();
    shell.open();

    PrintDialog dialog = new PrintDialog(shell);
    // Opens a dialog and let use user select the 
    // target printer and configure various settings.
    PrinterData printerData = dialog.open();
    if(printerData != null) { // If a printer is selected
      // Creates a printer.
      Printer printer = new Printer(printerData);
      // Starts the print job.
      if(printer.startJob("Text")) {
        GC gc = new GC(printer);

        // Starts a new page.
        if(printer.startPage()) {
          gc.drawString("Eclipse", 400, 600);
          // Finishes the page. 
          printer.endPage();
        }
        gc.dispose();
        // Ends the job.
        printer.endJob();
      }
      // Disposes the printer object after use. 
      printer.dispose();
      System.out.println("Print job done.");
    }
    // Set up the event loop.
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        // If no more entries in event queue
        display.sleep();
      }
    }
    display.dispose();
  }

  private void init() {

  }

  public static void main(String[] args) {
    new ImpTest();
  }
}*/