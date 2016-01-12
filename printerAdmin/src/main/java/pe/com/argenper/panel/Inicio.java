package pe.com.argenper.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.eway.Errores;

import pe.com.argenper.service.CommClass;

public class Inicio {
	// variables locales
	protected Shell shell;
	private Text usuario;
	private Text password;
	private Label mensajeTxt;
	com.eway.Errores oErr;
	CommClass commClass = new CommClass();
	// FIN - variables locales
	
	// Getters y Setters	
	public void setCommClass(CommClass data){
		this.commClass = data;
	}
	public CommClass getCommClass(){
		return this.commClass;
	}
	
	public void setError(Errores er){
		this.oErr = er;
	}
	public Errores getError(){
		return this.oErr;
	}
	// FIN - Getters y Setters

	// Constructor
	public Inicio(String[] params){ 
		loadParams(params);
		this.setError(new com.eway.Errores());
	}
	
	// Metodos publicos
	public void esconder()
	{
		shell.setVisible(false);	//PabloGo, 7 de junio de 2013
	}
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try {
			Inicio window = new Inicio(args);
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* loadParams: 
	 * PabloGo, 4 de Noviembre de 2014
	 * parsea la lista de parametros de inicio */
	public boolean loadParams(String[] params)
	{
		try {
			int i=0;
			String p;

			for(i=0;i<params.length;i++) {
				p = new String(params[i].substring(0, 1));
				if (p.equals("-")){
					p = new String(params[i].substring(1, 2));
					char charP = p.charAt(0);
					// Es un indicador de parametro
					switch(charP)
					{
						case ('h'):	// El host de destino
							 this.getCommClass().setUrlSis(params[i+1]);							
						break;
					}
				}
			}
			return true;
		}catch(Exception e){
			return false;
		}		
	}

	/** Open the window. */
	public void open() 
	{
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	// Create contents of the window.
	protected void createContents() {
		shell = new Shell();
		shell.setSize(289, 200);

// PabloGo, 28 de mayo de 2013, para ubicar la ventana
		shell.setText("Impresor - Inicio v1.2");
		this.commClass.centrarVentana(shell);

/*		org.eclipse.swt.graphics.Point p = shell.computeSize(SWT.DEFAULT,SWT.DEFAULT);
		int x = new Integer(p.x/2);
		int y = new Integer (p.y/2);*/
		
// Input - Nombre de usuario
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(10, 23, 58, 14);
		lblNewLabel.setText("Usuario");

		usuario = new Text(shell, SWT.BORDER);
		usuario.setBounds(105, 23, 110, 24);
		usuario.setTextLimit(16);
		usuario.setText("pe");

// Input - Password		
		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setBounds(10, 53, 68, 14);
		lblNewLabel_1.setText("Contraseña");

		password = new Text(shell, SWT.PASSWORD | SWT.BORDER);
		password.setBounds(105, 55, 108, 24);
		password.setTextLimit(20);
		password.setText("enVios04");

// Label de mensaje
		mensajeTxt = new Label(shell, SWT.NONE);
		mensajeTxt.setAlignment(SWT.CENTER);
		mensajeTxt.setBounds(10, 102, 251, 16);
		mensajeTxt.setText(" ");
		
// El boton	
		Button btnNewButton = new Button(shell, SWT.NONE);
	// Setear evento "KeyPressed"
		btnNewButton.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent arg0) {
				if(!ingresar())ponerMsg("Ocurrio un problema, por favor reintente luego");
			}
		});
	// Setear el evento "onclick"		
		btnNewButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent arg0) {
				if(!ingresar())ponerMsg("Ocurrio un problema, por favor reintente luego");
			}
		});
	// Ubicar el boton
		btnNewButton.setBounds(105, 122, 77, 26);
	// Setear el "label" del El boton		
		btnNewButton.setText("Ingresar");

		
		Label label_1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setBounds(0, 89, 271, 14);
	}
	// FIN - Metodos publicos

	// Metodos privados
	private boolean ingresar()
	{
		try {
			if (verifUser()){
				Home h = new Home(commClass, this);
				h.open();
				shell.dispose();
				return true;
			}
		}catch(Exception e) {
			System.out.print(e.getMessage());
			return false;
		}
		return false;
	}
	
	private boolean verifUser() throws Errores, Exception
	{ //Verificar la password y el usuario vs el servidor

		if (validarDatos() == false){
			return false;
		}else{
			ponerMsg("Verificando datos, espere...");
			if (!this.commClass.loginDo(usuario.getText(), password.getText())){
				this.getError().heredarErr(this.commClass.getError());
				ponerMsg(this.getError().getDescription());
				return false;
			}
		}
		return true;
	}	

	private boolean validarDatos()
	{ //PabloGo, 27 de mayo de 2013, verifica si los datos estan completos
		try{
			if (usuario.getText().compareTo("")==0){
				ponerMsg("Ingrese el usuario");
				usuario.setFocus();
				return false;
			}

			if (password.getText().compareTo("")==0){
				ponerMsg("Ingrese la clave");
				password.setFocus();
				return false;
			}
			return true;
		}catch(Exception e){
			this.getError().setearErr("Error validando datos. " + e.getMessage(),-100, "validarDatos()",0);
			return false;
		}
	}
	
	private boolean ponerMsg(String t)
	{
		mensajeTxt.setText(t);
		return true;
	}
	// FIN - Metodos privados
}