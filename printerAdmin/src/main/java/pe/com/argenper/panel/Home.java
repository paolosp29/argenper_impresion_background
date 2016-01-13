package pe.com.argenper.panel;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
//PabloGo, 4 de junio de 2013
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.eway.Errores;

import pe.com.argenper.bean.DocumentoPrint;
import pe.com.argenper.service.CommClass;
import pe.com.argenper.service.PrintWork;
import pe.com.argenper.service.PrintWorkFiscal;
import pe.com.argenper.util.MyUtils;

public class Home {
	static Display display; 
	protected Shell shell;
	CommClass commClass;
	
 	ArrayList<DocumentoPrint> listadoDoc = new ArrayList<DocumentoPrint>();
	ArrayList<PrinterData> listaImpresoras = new ArrayList<PrinterData>();	//PabloGo, 11 de junio de 2013
	ArrayList<PrintWork> trabajosDeImpresion = new ArrayList<PrintWork>(); //PabloGo, 1 de julio de 2013
	private Label mensajeTxt;
	private Label threadLabel; 
	private List listaDocumentos;
	private List listComandos;				// PabloGo, 15 de agosto de 2013 
	private Browser browser;
	private int ultIdDoc = -1;				// PabloGo, 12 de julio de 2013, el indice del listado del ultimo doc. cargado
	private boolean appEnd = false;
	private boolean mostrarPreview = false; 	// Pablogo, 16 de agosto de 2013, indica si el worker de impresion se debe mostrar o no 
	//PabloGo, 4 de julio de 2013
	com.eway.Errores oErr;
	private Table tablaDocs;
	private boolean autoRefresh = false;
	
	//PabloGo, 8 de agosto de 2013
	public void setAppEnd(boolean a){
		this.appEnd=a;
	}
	public boolean getAppEnd(){
		return this.appEnd;
	}
	//PabloGo, 12 de julio de 2013
	public void setUltIdDoc(int idD){
		this.ultIdDoc = idD;
	}
	public int getUltIdDoc(){
		return this.ultIdDoc;
	}
	//PabloGo, 10 de julio de 2013, el documento actual seleccionado para imprimir
	private DocumentoPrint docAct;
	private String strUrl; 
	public void setDocAct(DocumentoPrint da){
		this.docAct = da;
	}
	public DocumentoPrint getDocAct(){
		return this.docAct;
	}
	public void setError(Errores er){
		this.oErr = er;
	}
	public Errores getError(){
		return this.oErr;
	}
	public void setCommClass(CommClass cM){
		this.commClass = cM;
	}
	public CommClass getCommClass(){
		return this.commClass;
	}	
	public void setListadoDocs(ArrayList<DocumentoPrint> aL){
		this.listadoDoc = aL;		
	}
	public ArrayList<DocumentoPrint> getListadoDocs(){
		return this.listadoDoc;
	}
	//PabloGo, 11 de junio de 2013
	public void setListadoImpresoras(ArrayList<PrinterData> pd){
		this.listaImpresoras = pd;
	}
  	public ArrayList<PrinterData> getListadoImpresoras(){
		return this.listaImpresoras;
	}
	public Home(CommClass cM, Inicio ini){
		this.commClass = cM;
		ini.esconder();
	}
	public Home()
	{
		
	}
	/**
	 * Launch the application.
	 * @param args	 */
	public static void main(String[] args)
	{
		try {
			Home window = new Home();

			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/* Open the window.	 */
	public void open() {
		try {
			display = Display.getDefault();
			createContents();
			createTray(display);	//system tray
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				this.setAppEnd(true);
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			terminarPWs(); //cerrar todos los trabajos de impresion que hay dando vueltas por ahi.
		}catch(Exception e){
			e.printStackTrace();
			System.out.print(e.getMessage());
		}
	}
	/* Create contents of the window. */
	protected void createContents() throws Exception
	{
		shell = new Shell();
		shell.setSize(755, 592);
		shell.setText("Impresor - Documentos v1.2");
		
		this.commClass.centrarVentana(shell);
		
		listComandos = new List(shell, SWT.BORDER | SWT.H_SCROLL | SWT.COLOR_WHITE);
		listComandos.setBounds(72, 22, 628, 498);
		listComandos.setVisible(false);

//Boton que carga los documentos		
		Button btnLoaddocs = new Button(shell, SWT.NONE);
		btnLoaddocs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnLoaddocs.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent arg0) {
				loadDocs(false, false);
			}
		});
		btnLoaddocs.setBounds(10, 20, 200, 26);
		btnLoaddocs.setText("Cargar documentos a imprimir");
		
//Browser donde se muestra el documento descargado
		browser = new Browser(shell, SWT.BORDER);
		browser.setBounds(10, 289, 726, 231);
		browser.addProgressListener(new ProgressListener() 
		{
			public void changed(ProgressEvent event) {
				// Cuando cambia.	
			}
			public void completed(ProgressEvent event) {
				/* Cuando se completo 
				  Se usa para parsear el documento una vez cargado, el documento
				  puede ser un doc xml que contiene un mensaje de error
				  o un doc html que dara error de parseo Xml,es decir,
				  ante este error entendemos que se cargo bien el doc HTML.
				  Paradojico verdad? */
				leerDocumentoCargado();
			}
		 });

///Boton para iniciar impresion		
		Button btnIniPrintW = new Button(shell, SWT.NONE);
		btnIniPrintW.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				try {
					iniciarImpresion(null);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnIniPrintW.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				try {
					iniciarImpresion(null);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnIniPrintW.setBounds(337, 532, 144, 26);
		btnIniPrintW.setText("Iniciar impresion");
		
		Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 52, 885, 2);
		
		Button btnSalir = new Button(shell, SWT.NONE);
		btnSalir.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				shell.dispose();
			}
		});
		btnSalir.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				shell.dispose();
			}
		});
		btnSalir.setBounds(659, 532, 77, 26);
		btnSalir.setText("Salir");
		
		threadLabel = new Label(shell, SWT.BORDER);
		threadLabel.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				//mostrar / ocultar el historial de mensajes
				MOCmsgHist();
			}
		});
		threadLabel.setAlignment(SWT.CENTER);
		threadLabel.setBackground(new Color(display, 255,255,255));
		threadLabel.setBounds(10, 0, 891, 18);
		threadLabel.setText("");
		
		Label label_1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setBounds(10, 281, 726, 2);

		tablaDocs = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tablaDocs.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				//PabloGo, 12 de julio de 2013
				//verifUltDocSelected(e);
				seleccionarItemMouse(e);
			}
		});
		tablaDocs.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				seleccionarItem(e);
			}
		});
		tablaDocs.setBounds(10, 52, 726, 215);
		tablaDocs.setHeaderVisible(true);
		tablaDocs.setLinesVisible(true);
		crearColumnas(tablaDocs);
		
//Label que muestra los mensajes
		mensajeTxt = new Label(shell, SWT.NONE);
		mensajeTxt.setAlignment(SWT.CENTER);
		mensajeTxt.setBounds(251, 146, 200, 26);
		mensajeTxt.setText("Espere...");
		
//Listado donde se muestran los documentos cargados
		listaDocumentos = new List(shell, SWT.BORDER);
		listaDocumentos.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				//PabloGo, 12 de julio de 2013
				//verifUltDocSelected(e);
				seleccionarItemMouse(e);
			}
		});
		listaDocumentos.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				seleccionarItem(e);
			}
		});
		listaDocumentos.setBounds(10, 82, 726, 77);
		
		Button btnShowPreview = new Button(shell, SWT.CHECK);
		btnShowPreview.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				if (((Button)e.getSource()).getSelection()){
					mostrarPreview = true;
				}else{
					mostrarPreview = false;
				}
			}
		});
		btnShowPreview.setBounds(215, 28, 118, 18);
		btnShowPreview.setText("Mostrar preview");
		
		Button btnAutoRefresh = new Button(shell, SWT.CHECK);
		btnAutoRefresh.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				if (((Button)e.getSource()).getSelection()){
					autoRefresh = true;
					beginTarea();
				}else{
					autoRefresh = false;
					ponerMsg("Carga automática desactivada");
					finishTarea();
				}
			}
		});
		btnAutoRefresh.setBounds(353, 28, 128, 18);
		btnAutoRefresh.setText("Recarga Automática");
		
		tareaBack(this, autoRefresh).start();
		loadDocs(false, false);
	}
	private void beginTarea(){
		tareaBack(this, true).start();
	}
	private void finishTarea(){
		tareaBack(this, false).interrupt();
	}
	private boolean loadDocFromServer(int indice, boolean autoPrint)
	{	//	private boolean loadDocFromServer(List l) 
		//Seleccionar en base al indice del listado el documento a imprimir 
//		DocumentoPrint docP = listadoDoc.get(l.getSelectionIndex());
		DocumentoPrint docP = listadoDoc.get(indice);
		
		//La url de destino
		String urlDoc = new String();
		urlDoc = this.commClass.getUrlSis() + this.commClass.getUrlVisorDoc();
		
		//agregar las variables via queryString
		String urlVars = new String();
		//Token
		urlVars = "?token="+this.commClass.getUsu().getToken();
		//acc
		urlVars = urlVars + "&acc_imp=LOADDOCIMPRE";
		//app y app_imp
		urlVars = urlVars + "&app=app";
		urlVars = urlVars + "&app_imp=app";
		//idOperacion
		urlVars = urlVars + "&Imp_idOperacion="+docP.getIdDoc();
		//idImpresion
		urlVars = urlVars + "&Imp_idImpre="+docP.getIdImpresion();
		
		urlDoc = urlDoc + urlVars;
		strUrl="";
		browser.setUrl(urlDoc);
		System.out.println("browser: "+browser.getVisible());
		if(!autoPrint){
			this.setDocAct(docP);
			this.setUltIdDoc(indice);
			browser.setVisible(true);
		}else{
			strUrl=urlDoc;
		}
		System.out.println("browser: "+browser.getUrl());
		System.out.println("browser: "+browser.getText());
		return true;
	}
	
	private boolean leerDocumentoCargado()
	{
		String doc = new String();

		doc = browser.getText();
		
		DocumentBuilderFactory domFact = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder;			
			builder = domFact.newDocumentBuilder();
			Document xmlDoc = null;
			try {
				xmlDoc = builder.parse(new InputSource(new StringReader(doc)));
			}catch(SAXParseException e){
				//no es un doc XML
			}
			
			if (xmlDoc != null){ //si es diferente de null, es porque efectivamente es un obj. xml
				xmlDoc.getDocumentElement().normalize();
//PabloGo, 3 de septiembre de 2013, verificar si es respuesta desde el server				
				NodeList respuesta = xmlDoc.getElementsByTagName("respuesta");
				Node nodo = respuesta.item(0);
				if (nodo != null){ //es respuesta del server
					if (nodo.getNodeType() == Node.ELEMENT_NODE){
						Element Elemento = (Element)nodo;
						
						NodeList resultList = Elemento.getElementsByTagName("result");
						String result = resultList.item(0).getChildNodes().item(0).getNodeValue();
			
						if (result.equals("false")){
							//No anda che
							ponerMsg("Documento no válido");
							return false;
						}
					}
				}else{ //PabloGo, 3 de septiembre de 2013, verificar si es un doc xml (tipo fiscal)
					respuesta = xmlDoc.getElementsByTagName("documento");
					nodo = respuesta.item(0);
					if (nodo != null){ //es un doc tipo fiscal (ver como hacer para mostrar eso es en browser
						browser.setText("<html><body>Documento fiscal</body></html>");
					}
				}
			}
			
		}catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ponerMsg("Documento cargado");
		return false;
	}
	private boolean iniciarImpresion(DocumentoPrint docAutoPrint) throws IOException
	{ //PabloGo, 11 de junio de 2013
		boolean imprimio = false;
		DocumentoPrint docImpr=null;
		String strHtml=null;
		if(docAutoPrint==null){
			docImpr = this.getDocAct();
			strHtml=browser.getText();
		}else{
			docImpr = docAutoPrint;
			strHtml=MyUtils.getHtmlFromUrl(strUrl);
		}
		strUrl="";
		if (docImpr == null){
			ponerMsg("Documento de impresión no seleccionado.");
		}else{
			PrintWork pw;
			if (docImpr.getConfDoc().getFiscal()>0){	//Es fiscal
				pw = new PrintWorkFiscal(docImpr.getConfDoc().getComandoEjec());
			}else{ //documento normal de impresion
				pw = new PrintWork(strHtml);
				pw.setConfDoc(docImpr.getConfDoc());
			}
			
			imprimio = pw.getIniOK();	//iniciar
			if (imprimio){
				pw.main(mostrarPreview); //crear el obj.
				
				imprimio = pw.iniciarImpresion(); //imprimir
				if (imprimio){
					thSetDocPrint(this,docImpr.getIdImpresion()).start();	/* PabloGo, 15 de agosto de 2013, 
																			 * iniciar thread para el envio de la señal al servidor 
s																			 * de que este documento se envia */
					this.trabajosDeImpresion.add((PrintWork)pw); 			//guardar el trabajo (para reimprimir, uso futuro)
				}
			}
			
			if (!imprimio){
				//PabloGo, 4 de julio de 2013, ver donde mostrar el error
				this.setError(pw.getError());
				return false;
			}
		}
		return true;
	}
	private boolean terminarPWs()
	{ 	//PabloGo, 1 de julio de 2013, terminar trabajos de impresion abiertos.
		
		for (PrintWork pwAct: this.trabajosDeImpresion){
			pwAct.cerrar();
		}
		return true;
	}
	private void createTray(Display display) 
	{
	//PabloGo , 10 de julio e 2013, muestra un icono en le systray
        Tray tray;
        TrayItem item;
        tray = display.getSystemTray();

        if (tray == null) {
            System.out.println("The system tray is not available");
        } else {
            item = new TrayItem(tray, SWT.NONE);
            item.setToolTipText("Arper Express");
            item.addListener(SWT.Show, new Listener() {
                public void handleEvent(Event event) {
                    System.out.println("show");
                }
            });

            item.addListener(SWT.Hide, new Listener() {
                public void handleEvent(Event event) {
                    System.out.println("hide");
                }
            });

            item.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    System.out.println("selection");
                }
            });

            item.addListener(SWT.DefaultSelection, new Listener() {
                public void handleEvent(Event event) {
                    System.out.println("default selection");
                }
            });

            final Menu menu = new Menu(shell, SWT.POP_UP);

            MenuItem openMenuItem = new MenuItem(menu, SWT.PUSH);
            openMenuItem.setText("Mostrar");
            openMenuItem.addListener(SWT.Selection, new Listener() {

                public void handleEvent(Event event) {
                    shell.setVisible(true);
                    shell.setMaximized(true);
                }
            });

            MenuItem exitMenuItem = new MenuItem(menu, SWT.PUSH);
            exitMenuItem.setText("Salir");
            exitMenuItem.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    System.exit(0);
                }
            });

            item.addListener(SWT.MenuDetect, new Listener() {
                public void handleEvent(Event event) {
                    menu.setVisible(true);
                }
            });

            // image = SWTResourceManager.getImage(MakeBreak.class, "Backup-Green-Button-icon.png");
 //           image = SWTResourceManager.getImage(WpCommenter.class, "images/mb4.png");
//            ImageData imageData = new ImageData("lib/impresora.png");
            ImageData imageData = new ImageData(Home.class.getClassLoader().getResourceAsStream("impresora.png"));
            //ImageData imageData = new ImageData(getClass().getResourceAsStream("impresora.png"));

            item.setImage(new Image(display,imageData));
                        
            shell.addShellListener(new ShellListener() {
            	public void shellActivated(ShellEvent event) {

            	}

            	public void shellClosed(ShellEvent event) {
            		event.doit = false;  //!! for this code i looked long time
                    shell.setVisible(false);
                }

                public void shellDeactivated(ShellEvent event) {
             
                }

                public void shellDeiconified(ShellEvent event) {

                }

                public void shellIconified(ShellEvent event) {
                            //shell.setVisible(false);
                }
            });
            
        }
    }
	public boolean verifUltDocSelected(MouseEvent e)
	{
		/* PabloGo, 12 de julio de 2013 
		 * Verificar si el id. del listado actual 
		 * es el mismo al Ãºlimo documento cargado seleccionado */
		
		return true;
	}
	private boolean loadDocs(boolean addToThread, boolean isFromThread)
	{ //PabloGo, 30 de mayo de 2013
		try {
			if(addToThread && !autoRefresh)return false;
			limpiarTablaDocs(tablaDocs);
			listaDocumentos.removeAll();
			tablaDocs.setVisible(false);
			listaDocumentos.setVisible(false); //ocultar listado
			listadoDoc = this.commClass.loadPrintDocs();
			Iterator<DocumentoPrint> li = listadoDoc.listIterator();
			boolean docAutoImpresoEjec=false;
			int ind=0;
			while (li.hasNext()){ //recorre todos los documentos cargados de "listadoDoc" el cual contiene objetos tipo "DocumentoPrint"
				DocumentoPrint dp = li.next();
				//si se invoco desde el hilo automatico y no esta impreso
				if(isFromThread && !dp.getDocImpreso() && !docAutoImpresoEjec){
					loadDocFromServer(ind, true);
					//System.out.println("browser2: "+browser.getUrl());
					//System.out.println("browser2: "+browser.getText());
					iniciarImpresion(dp);
					docAutoImpresoEjec=true;
				}
				listaDocumentos.add(dp.getIdDoc() + " - "+ dp.getRemitente()); //agrega al combo el texto del documento actual
				agregarRegistroATabla(tablaDocs, dp);
				ind++;
			}
			tablaDocs.setVisible(true);
			listaDocumentos.setVisible(true); //mostrar listado
			ponerMsg("Carga finalizada");

			seleccionarItemEnListado(); //PabloGo, 6 de Septiembre de 2013, seleccionar de nuevo el ultimo item escogido
			
			if (addToThread){ /*PabloGo, 2 de enero de 2014, se agrega esta validacion 
								para evitar crear un hilo demas al hacer click en el boton
								cargar documentos */
				tareaBack(this, autoRefresh).start();	//Generar la nueva llamada
			}
			return true;
		}catch(Errores e){
			ponerMsg(e.getDescription());
			return false;
		}catch(Exception e){
			ponerMsg(e.getMessage());
			return false;
		}
	}	
	// Hilos	
	public static Thread thSetDocPrint(Home h,long idImpre)
	{ /*PabloGo, 15 de agosto de 2013
	 	Marcar un documento como impreso*/
		final Home miHome = h;
		final long idImpres = idImpre;
		
	    return new Thread() { 
	    	public void run() { 
	    		try {
		          Thread.sleep(1000); //en Delay por un segundo 
		        } catch (InterruptedException e) {
		          // TODO Auto-generated catch block
		          e.printStackTrace();
		        }
		        
		        display.asyncExec(new Runnable()  
		        {
		        	public void run() {
		    			miHome.ponerMsg("Enviado para marcar");
		        		if (!miHome.getCommClass().setDocImpreso(idImpres)){
		        			//Fallo el marcado del documento
		        			miHome.ponerMsg("Error marcando documento. "+miHome.getCommClass().getError().getDescription());
		        		}else{
		        			miHome.ponerMsg("Documento marcado como impreso correctamente.");
		        		}
		          }
		        });
	    	}
	    };
	}
	public static Thread tareaBack(Home h, final boolean autoRefresh) 
	{	/* PabloGo ? de agosto de 2013
	 		Ejecuta la tarea de recarga en background de los documentos.*/
		if(!autoRefresh)return new Thread();
		final Home miHome = h;
		
	    return new Thread() {
	    	public void run() {
	    		try {
		          Thread.sleep(Long.parseLong(MyUtils.getProperty("time.recarga"))); // cinco minutos
		        } catch (InterruptedException e) {
		          // TODO Auto-generated catch block
		          e.printStackTrace();
		        }
		        
		        display.asyncExec(new Runnable() 
		        {
		        	public void run() {
		        		if(miHome.autoRefresh)
		        		miHome.ponerMsg("Carga automática de documentos en curso");
		        		miHome.loadDocs(true, true);
		        		//laLabel.setText("done");
		          }
		        });
	    	}
	    };
	}
	// Eventos
	private boolean seleccionarItemMouse(MouseEvent moE)
	{ /* PabloGo, 12 de julio de 2013
	   * Cargar el documento haciendo click*/
		//List l = (List) moE.getSource();
		int indice = -1;
		
		if (moE.getSource() instanceof List){
			List l = (List) moE.getSource();
			indice = l.getSelectionIndex();
		}else{
			Table t = (Table) moE.getSource();
			indice = t.getSelectionIndex();
		}		
		
		return loadDocFromServer(indice, false);
	}
	private boolean seleccionarItem(KeyEvent kp)
	{ //PabloGo, 3 de junio de 2013, para cargar el documento en particular
		//Obtener el obj. originario del evento.
		//List l = (List) moE.getSource();
		
		if (kp.keyCode != 13 && kp.keyCode != 16777296){
			//no es enter, ver que hacer
			try{
				this.setDocAct(null);
				browser.setVisible(false);
//				browser.setText("<!DOCTYPE html><html><head><title>no doc</title></head><body>no doc</body></html>");
			}catch(Exception e){
				//que paso?
			}
			this.setUltIdDoc(-1);
		}else{ //es Enter
			int indice=0;
			if (kp.getSource() instanceof List){
				List l = (List) kp.getSource();
				indice = l.getSelectionIndex();
			}else{
				Table t = (Table) kp.getSource();
				indice = t.getSelectionIndex();
			}
			return loadDocFromServer(indice, false);
		}
		return true;
	}
	// Funciones auxiliares
	private void MOCmsgHist()
	{
		listComandos.setVisible(!listComandos.getVisible());
	}
	public boolean ponerMsg(String texto)
	{	// PabloGo, 8 de agosto de 2013, poner un mensaje 
		// del thread que esta corriendo con la tareas en background.
		if (texto.length() == 0){
			threadLabel.setBackground(new Color(display, 255,255,255));
			return true; //salir
		}else{
			threadLabel.setBackground(new Color(display, 255,222,173));
		}

		agregarAHistMsgs(texto);
		//obtener un lÃ­mite de 100 cars
		String msgAct = new String (texto + " >> " + threadLabel.getText());
		if (msgAct.length() > 120){
			msgAct = new String(msgAct.substring(0, 119));
			msgAct = msgAct + "...";
		}else{
			// Â¿?
		}
		threadLabel.setText(msgAct);
		//threadLabel.setText(texto);
		return true;
	}
	private boolean crearColumnas(Table t)
	{ /* PabloGo, 13 de agosto de 2013
		Crea la columnas de la tabla	*/
		try {
		    TableColumn tc1 = new TableColumn(t, SWT.CENTER);
		    TableColumn tc2 = new TableColumn(t, SWT.CENTER);
		    TableColumn tc3 = new TableColumn(t, SWT.CENTER);
		    TableColumn tc4 = new TableColumn(t, SWT.CENTER);
		    TableColumn tc5 = new TableColumn(t, SWT.CENTER);
		    tc1.setText("Fecha - Hora");
		    tc2.setText("Tipo Doc.");
		    tc3.setText("Serie - nro.");
		    tc4.setText("Remitente");
		    tc5.setText("Ya impreso");

		    tc1.setWidth(100);
		    tc2.setWidth(100);
		    tc3.setWidth(120);
		    tc4.setWidth(200);
		    tc5.setWidth(80);
		    t.setHeaderVisible(true);
	    	return true;
		}catch(Exception e){
			
			return false;
		}
	}
	private boolean limpiarTablaDocs(Table t)
	{ //PabloGo, 14 de agosto de 2013
		t.removeAll(); //borra todo?
		return true;
	}
	private boolean agregarRegistroATabla(Table t,DocumentoPrint dp)
	{	 /* PabloGo, 14 de agosto de 2013 
		  * */
	    TableItem item1 = new TableItem(t, SWT.NONE);
	    String impreso = new String("NO");
	    if (dp.getDocImpreso()){
	    	impreso = "SI";
	    }
	    item1.setText(new String[] {dp.getFechaHora(),dp.getTipoDoc(),dp.getSeriaNro(),dp.getRemitente(),impreso} );
		return true;
	}
	private boolean agregarAHistMsgs(String comando)
	{ /* PabloGo, 15 de agosto de 2013
		agrega al historial de mensajes que se recibieron */
		try {
			Calendar c = new GregorianCalendar();
			String hora = new String(new Integer(c.get(Calendar.HOUR_OF_DAY)) + ":"+new Integer(c.get(Calendar.MINUTE))+":"+new Integer(c.get(Calendar.SECOND)));

			listComandos.add(hora+" - "+comando, 0); //Agrego el nuevo comando al principio
			if (listComandos.getItemCount() >= 200){
				listComandos.remove(199);
			}
			listComandos.select(0);
			return true;
		}catch(Exception e){
			// AHHHH! donde lo ponemos?
			return false;
		}
	}
	private boolean seleccionarItemEnListado(){
	/* PabloGo, 6 de septiembre de 2013
	 * Colocar en el listado (que sigo manteniendo) 
	 * y en la tabla el item seleccionado
	 * al recargarlo desde el server */
		
		int indice = this.getUltIdDoc();
		if (indice > -1){ //solo si se eligiÃ³ un doc
			if ((indice+1) > tablaDocs.getItemCount()){ //se pasa de la cantidad de elemento en la lista, poner el de mas arriba en su lugar
				this.setUltIdDoc(tablaDocs.getItemCount()-1);
				indice = this.getUltIdDoc(); 
			}
			tablaDocs.setSelection(indice); 
			listaDocumentos.setSelection(indice);
		}
		return true;
	}
}