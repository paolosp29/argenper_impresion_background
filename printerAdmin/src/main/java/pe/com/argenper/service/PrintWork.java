package pe.com.argenper.service;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
//import java.io.IOException;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.UnmodifiableSetException;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import com.eway.Errores;

import pe.com.argenper.bean.ConfigDocPrint;
 
public class PrintWork extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	com.eway.Errores oErr;		//PabloGo, 4 de julio de 2013
	
	private JEditorPane editor;
	private Box box;
	
	private HTMLEditorKit hed;
	private boolean iniOK;	//PabloGo, 4 de julio de 2013
	private ConfigDocPrint confDoc;	//PabloGo, 10 de julio de 2013
	
	public void setConfDoc(ConfigDocPrint cd){
		this.confDoc = cd;
	}
	public ConfigDocPrint getConfDoc(){
		return this.confDoc;
	}
	
	//PabloGo, 4 de julio de 2013
	public void setError(Errores er){
		this.oErr = er;
	}
	public Errores getError(){
		return this.oErr;
	}
	
	public void setIniOK(boolean po){
		this.iniOK = po;
	}
	
	public boolean getIniOK(){
		return this.iniOK;
	}
	
    public PrintWork(String htmlDoc) {
        super("Trabajo de impresion");
        if (!htmlDoc.equalsIgnoreCase("impFisc")){	//si no es una impresion fiscal, entonces iniciar.
        	this.setIniOK(iniciar(htmlDoc));
        	this.setConfDoc(new ConfigDocPrint()); //configuracion por defecto (por si el que usa la clase no se acuerda de pasarle la configuraci贸n de impresi贸n)
        }
    }
 
    public static void main(String[] args) throws Exception {
//        (new PrintWork()).setVisible(true);
    }

	public void main(boolean mostrar) {
	//	(new PrintWork()).setVisible(true);
		this.setError(new com.eway.Errores());
		this.setVisible(mostrar);
	}
	
	public void cerrar()
	{	//pablogo, 1 de julio de 2013
		this.dispose();
	}
	
	private boolean iniciar(String htmlDoc)
	{
		try {
			//// http://www.devdaily.com/blog/post/jfc-swing/how-add-style-stylesheet-jeditorpane-example-code
			box = Box.createHorizontalBox();
			editor = new JEditorPane();
			Insets m = new Insets(0,0,0,0);
			
			hed = new HTMLEditorKit();
			//StyleSheet ss = hed.getStyleSheet();
			/*
				ss.addRule("h2 {color : green; font-weight: bold; position: absolute; left: 500px;}");			*/
			Document doc = hed.createDefaultDocument();
			
			editor.setEditorKit(hed);
			editor.setDocument(doc);
			editor.setMargin(m);
			editor.setContentType("text/html");
			editor.setText(htmlDoc);
			//System.out.println("editor1: "+editor.getText());
			editor.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, Color.BLACK));
			box.add(editor);        
			//System.out.println("editor2: "+editor.getText());
			//System.out.println("box: "+((JEditorPane)box.getComponent(0)).getText());
			getContentPane().add(box);
			//System.out.println("getContentPane: "+((JEditorPane)((Box)getContentPane().getComponent(0)).getComponent(0)).getText());

			setPreferredSize(new Dimension(600, 270));
			pack();
			setLocationRelativeTo(null);
			
			return true;
		}catch(Exception e){
			//Se fue por excepcion
			this.getError().setearErr("Error de iniciando trabajo de impresion. "+e.getMessage(), -100, "iniciar()", 0);
			return false;
		}
	}

	public boolean iniciarImpresion()
	{	// PabloGo, 4 de julio de 2013
		try {
			boolean imprimio = true;
			//Servicio de impresion
		    PrintService pss = PrintServiceLookup.lookupDefaultPrintService();
			
			//Atributos
			PrintRequestAttributeSet attr_set = new HashPrintRequestAttributeSet();
			//PabloGo, 10 de julio de 2013, obtener los valores desde la configuracion del documento.
			attr_set.add(new Copies(this.getConfDoc().getCopias()));

			//Recuperar el tipo de hoja a utilizar "this.getConfDoc().getTipoHoja()"
			if (this.getConfDoc().getTipoHoja().equalsIgnoreCase("A4")){
				attr_set.add(MediaSizeName.ISO_A4);
			}else if (this.getConfDoc().getTipoHoja().equalsIgnoreCase("EJEC")){
				attr_set.add(MediaSizeName.EXECUTIVE);
			}else{
				//Tama駉 personalizado 
				float x;
				float y;
				String xy = new String(this.getConfDoc().getTipoHoja());
				String[] mtxXY = xy.split(";"); //se separa por punto y coma porque pueden ser valores con decimales y si viene una coma, se rompe todo.
				x = new Float(mtxXY[0]);
				y = new Float(mtxXY[1]);
				
				attr_set.add(new MediaSize(x,y,MediaSize.MM));
			}

			//PabloGo, 10 de julio de 2013, indicar la orientacion del documento
			switch(this.getConfDoc().getOrientacion())
			{
				case(2):	//horizontal
				{
					attr_set.add(OrientationRequested.LANDSCAPE);
					break;
				}
			
				default:	// vertical
				case(1):	
				{
					attr_set.add(OrientationRequested.PORTRAIT);
					break;
				}
			}
			
			//Margenes
			attr_set.add(new MediaPrintableArea(10, 10, 210, 297, MediaPrintableArea.MM));

//			attr_set.add(Sides.DUPLEX);
			try{
				imprimio = editor.print(new MessageFormat(""), new MessageFormat(""), false, pss, attr_set, true);
				//imprimio = editor.print(new MessageFormat(""), new MessageFormat(""), true, null, attr_set, true);
			}catch(PrinterException e){
				this.getError().setearErr("Error imprimiendo. "+e.getMessage(), -101, "iniciarImpresion()", 0);
			}

			if (!imprimio){
				this.getError().setearErr("Error imprimiendo. Error no especificado. ", -102, "iniciarImpresion()", 0);
			}
			return true;
		}catch(UnmodifiableSetException e){
			this.getError().setearErr("Error iniciando impresi贸n. "+e.getMessage(), -100, "iniciarImpresion()", 0);
			return false;
		}catch(ClassCastException e){
			this.getError().setearErr("Error iniciando impresi贸n. "+e.getMessage(), -100, "iniciarImpresion()", 0);
			return false;
		}catch(NullPointerException e){
			this.getError().setearErr("Error iniciando impresi贸n. "+e.getMessage(), -100, "iniciarImpresion()", 0);
			return false;
		}catch(Exception e){
			this.getError().setearErr("Error iniciando impresi贸n. "+e.getMessage(), -100, "iniciarImpresion()", 0);
			return false;
		}
	}

/*	public boolean setDoc(String htmlDoc){
		// PabloGo, 26 de junio de 2013, le pasa el texto que es el documento HTML
		// para realizar la impresi贸n

        //Document doc = hed.createDefaultDocument();
 
		System.out.print(editor.getText());
		
		Document doc = hed.createDefaultDocument();
        editor.setEditorKit(hed);
        editor.setDocument(doc);

        //editor.setText(htmlDoc);
        try {
			editor.read(new java.io.StringReader(htmlDoc),editor.getDocument());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   
        System.out.print(editor.getText());
		return true;
	}
	
	 try {
      PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
      pras.add(new Copies(1));

      DocFlavor doc_flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
      PrintRequestAttributeSet attr_set = new HashPrintRequestAttributeSet();
      attr_set.add(new Copies(2));
      attr_set.add(Sides.DUPLEX);
      PrintService pss = PrintServiceLookup.lookupDefaultPrintService();
      //PrintService pss[] = PrintServiceLookup.lookupPrintServices(doc_flavor,attr_set); 
      
      //PrintService pss[] = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.PDF, pras);
      //if (pss.length == 0)
      //  throw new RuntimeException("No printer services available.");
      PrintService ps = pss;
      System.out.println("Printing to " + ps);
      DocPrintJob job = ps.createPrintJob();
      FileInputStream fin = new FileInputStream("printtest.txt");
      Doc doc = new SimpleDoc(fin,  DocFlavor.INPUT_STREAM.AUTOSENSE, null);
      job.print(doc, attr_set);
      fin.close();
    } catch (IOException ie) {
      ie.printStackTrace();
    } catch (PrintException pe) {
      pe.printStackTrace();
    }

http://docs.oracle.com/javase/6/docs/api/javax/print/attribute/Attribute.html

Copies, DocumentName, MediaSize, OrientationRequested,  

Copies,  
DocumentName, 
JobName, 
JobState, 
JobStateReason, 
JobStateReasons, 
Media, 
MediaName, 
MediaPrintableArea, 
MediaSize, 
MediaSizeName, 
MediaTray, 
OrientationRequested, 
PrinterInfo, 
PrinterLocation, 
PrinterName, 
PrinterResolution, 
PrinterState, 
PrinterStateReason, 
PrinterStateReasons, 
PrintQuality	*/
}