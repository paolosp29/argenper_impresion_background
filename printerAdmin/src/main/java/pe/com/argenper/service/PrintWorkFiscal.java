package pe.com.argenper.service;
/* PabloGo, 8 de julio de 2013
 * Se copia desde printWork para hacer 
 * la ejecución del comando para la impresión fiscal 
 * */


import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.Box;

import com.eway.Errores;
 
public class PrintWorkFiscal extends PrintWork {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	com.eway.Errores oErr;		//PabloGo, 4 de julio de 2013
	
	//private JEditorPane editor;
	private Box box;
	
	//private HTMLEditorKit hed;
	private boolean iniOK;	//PabloGo, 4 de julio de 2013
	private String cmdText = new String();	//PabloGo, 11 de julio de 2013
	
	public void setCmd(String cmd){
		this.cmdText = cmd;
	}
	public String getCmd(){
		return this.cmdText;
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
	
	public PrintWorkFiscal(String cmd) {
	 	super("impFisc");  //al enviar este texto, se evita el "setIniOK" del padre.
	 	this.setCmd(cmd);
		this.setIniOK(iniciar());
	}
 
	public static void main(String[] args) throws Exception {
		// 	(new PrintWork()).setVisible(true);
	}

	public void main() {
	//	(new PrintWork()).setVisible(true);
		this.setVisible(true);
	}
	
	public void cerrar()
	{	//pablogo, 1 de julio de 2013
		this.dispose();
	}
	
	private boolean iniciar()
	{
		try {
			/* PabloGo, 9 de julio de 2013
			Display de la ventana (poner un label para mostrar mensaje indicando que el comando fue correcto o no)*/
			box = Box.createHorizontalBox();
			
			getContentPane().add(box);
					 
			setPreferredSize(new Dimension(600, 270));
			pack();
			setLocationRelativeTo(null);
			
			return true;
		}catch(Exception e){
			//Se fué por excepción
			this.getError().setearErr("Error de iniciando trabajo de impresi�n. "+e.getMessage(), -100, "iniciar()", 0);
			return false;
		}
	}

	public boolean iniciarImpresion()
	{	// PabloGo, 4 de julio de 2013
		try {
			boolean imprimio = true;

			Runtime rt = Runtime.getRuntime();
			//Process pr = rt.exec("cmd /c dir");
			Process pr = rt.exec(this.getCmd());
			 
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			
			String line=null;
			
			while((line=input.readLine()) != null) {
				System.out.println(line);
			}
			
			int exitVal = pr.waitFor();
			System.out.println("Exited with error code "+exitVal);

			return imprimio;
		}catch(Exception e){
			e.printStackTrace();
			this.getError().setearErr("Error iniciando impresi�n. "+e.getMessage(), -100, "iniciarImpresion()", 0);
			return false;
		}
	}
/*
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