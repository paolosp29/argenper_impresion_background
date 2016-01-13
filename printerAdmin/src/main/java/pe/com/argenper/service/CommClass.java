package pe.com.argenper.service;

import java.util.ArrayList;

import com.eway.*;

import pe.com.argenper.bean.ConfigDocPrint;
import pe.com.argenper.bean.DocumentoPrint;
import pe.com.argenper.bean.Usuario;
import pe.com.argenper.util.MyUtils;

import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.*;
/*
 * Clase en la cual se encuentra los datos comunes a todas las demas
 * asi como metodos utils para las mismas
 */

public class CommClass {
	
	String urlSis = new String(); 					// la url de destino del sistema
	String urlVisorDoc = new String(); 				// la url por defecto para visulizar documentos
	Usuario usu = new Usuario();  					// Objeto usuario
	String salida = new String();  					// salida 

//PabloGo, 29 de mayo 2013
	com.eway.Errores oErr;
	
	public void setError(Errores er){
		this.oErr = er;
	}
	public Errores getError(){
		return this.oErr;
	}
	
//PabloGo, 15 de mayo de 2013	
	public void setUrlSis(String url){
		this.urlSis = url;
	}
	public String getUrlSis(){
		return this.urlSis;
	}

//PabloGo, 21 de mayo de 2013
	public void setSalida(String sal){
		this.salida = sal;
	}
	public String getSalida(){
		return this.salida;
	}

//PabloGo, 04 de mayo de 2013
	public void setUrlVisorDoc(String vd){
		this.urlVisorDoc = vd;	
	}
	public String getUrlVisorDoc(){
		return this.urlVisorDoc;
	}

// Constructor
	public CommClass(){
		//this.setUrlSis("http://192.168.0.231/");
		//this.setUrlSis("http://arp.datacod.com.ar/");
		this.setUrlSis(MyUtils.getProperty("url.sis.default"));
		this.setError(new com.eway.Errores());
		this.setUrlVisorDoc(MyUtils.getProperty("url.visordoc"));
	}
	
// Metodos	
	public boolean loginDo(String usuario, String password)
	{	//Validar usuario y password
		//Url de destino
		String url = new String(this.getUrlSis()+"verifLogin.asp");
		
		XmlHt xmlHt = new XmlHt();
	
		xmlHt.addCampoValor("acc", "VERIF");
		xmlHt.addCampoValor("app", "app");
		xmlHt.addCampoValor("usuario", usuario);
		xmlHt.addCampoValor("clave", password);
		
		if (xmlHt.enviarPost(url) == false){
			this.getError().heredarErr(xmlHt.getError());
			return false;
		}else{
			Document resp = xmlHt.getResponseXml();
			//xmlHt = null;
			// normalizar documento
			resp.getDocumentElement().normalize();
			NodeList respuesta = resp.getElementsByTagName("respuesta");
			Node Nodo = respuesta.item(0);
			if (Nodo.getNodeType() == Node.ELEMENT_NODE)
			{
				Element elemento = (Element)Nodo;
	
				NodeList resultList = elemento.getElementsByTagName("result");
				String result = resultList.item(0).getChildNodes().item(0).getNodeValue();
	
				if (result.equals("true"))
				{
					NodeList tokenList = elemento.getElementsByTagName("token");
					//tokenList.item(0).getChildNodes();
					String token = tokenList.item(0).getChildNodes().item(0).getNodeValue();
		        	this.usu.setToken(token);

					NodeList empresaList = elemento.getElementsByTagName("idempresa");
					String empresa = empresaList.item(0).getChildNodes().item(0).getNodeValue();
		        	this.usu.setIdEmpresa(empresa);
		        	
					NodeList agenciaList= elemento.getElementsByTagName("idagencia");
					String agencia = agenciaList.item(0).getChildNodes().item(0).getNodeValue();
		        	this.usu.setIdAgencia(agencia);
		        	
					NodeList cajaList = elemento.getElementsByTagName("idcaja");
					String caja = cajaList.item(0).getChildNodes().item(0).getNodeValue();
		        	this.usu.setIdCaja(caja);
		        	
		        	return true;
				} else {
					//Se procesa la respuesta de error
					procesarMensajeError(elemento,"loginDo");
		        	return false;
				}
			} else {
				this.getError().setearErr("Error de conexión", -101, "loginDo", 0);
				//commClass.setSalida("Error de conexion");
				return false;
			}
	
	/*		xmlHt.addCampoValor("user", "pablo");
			xmlHt.enviarPost(url);
			
			if (a!=0){
				Errores err = new Errores();
				err.setearError(100, "No valido",false);
				throw(err);
			}else{
			
				return true;
			}*/
		}
	}
	
	public boolean setDocImpreso(long idImpresion)
	{	/* PabloGo, 14 de agosto de 2013, 
		para marcar un documento como impreso */ 
		String url = new String(this.getUrlSis()+MyUtils.getProperty("url.remoteAcc"));
		
		XmlHt xmlHt = new XmlHt();
		
		Long l = idImpresion;
		
		xmlHt.addCampoValor("acc", "SETDOCPRINTED");
		xmlHt.addCampoValor("app", "app");
		xmlHt.addCampoValor("token", this.usu.getToken());		// PabloGo, 4 de Noviembre de 2014
		xmlHt.addCampoValor("idimpresion", l.toString());
		
		if (xmlHt.enviarPost(url) == false){
			this.getError().heredarErr(xmlHt.getError());
			return false;
		}else{
			Document resp = xmlHt.getResponseXml();
			//xmlHt = null;
			// normalizar documento
			resp.getDocumentElement().normalize();
			NodeList respuesta = resp.getElementsByTagName("respuesta");
			Node Nodo = respuesta.item(0);
			if (Nodo.getNodeType() == Node.ELEMENT_NODE)
			{
				Element elemento = (Element)Nodo;
	
				NodeList resultList = elemento.getElementsByTagName("result");
				String result = resultList.item(0).getChildNodes().item(0).getNodeValue();
	
				if (result.equals("true"))
				{
					//NodeList tokenList = elemento.getElementsByTagName("docImpreso");
					//String strDocImp = tokenList.item(0).getChildNodes().item(0).getNodeValue();
					
		        	return true;
				} else {
					//Se procesa la respuesta de error
					procesarMensajeError(elemento,"loginDo");
		        	return false;
				}
			} else {
				this.getError().setearErr("Error de conexión", -101, "loginDo", 0);
				//commClass.setSalida("Error de conexion");
				return false;
			}
		}
	}

	public ArrayList<DocumentoPrint> loadPrintDocs() throws Errores
	{	//PabloGo, 30 de mayo de 2013, cargar los documentos pendientes de impresion

		//try	{
			String url = new String(this.getUrlSis()+MyUtils.getProperty("url.remoteAcc"));
			ArrayList<DocumentoPrint> lista = new ArrayList<DocumentoPrint>();
		
			XmlHt xmlHt = new XmlHt();
	
			xmlHt.addCampoValor("acc", "LOADDOCS");
			xmlHt.addCampoValor("app", "app");
			xmlHt.addCampoValor("token", this.usu.getToken());
			xmlHt.addCampoValor("idAg", this.usu.getIdAgencia());
			xmlHt.addCampoValor("idEmpr", this.usu.getIdEmpresa());
			xmlHt.addCampoValor("idCaja", this.usu.getIdCaja());
			
			if (xmlHt.enviarPost(url) == false){
				this.getError().heredarErr(xmlHt.getError());
				throw this.getError();
			}else{
				Document resp = xmlHt.getResponseXml();
				// normalizar documento
				resp.getDocumentElement().normalize();
				NodeList respuesta = resp.getElementsByTagName("respuesta");
				Node Nodo = respuesta.item(0);
				if (Nodo.getNodeType() == Node.ELEMENT_NODE)
				{
					Element elemento = (Element)Nodo;
		
					NodeList resultList = elemento.getElementsByTagName("result");
					String result = resultList.item(0).getChildNodes().item(0).getNodeValue();
		
					if (result.equals("true"))
					{
						//NodeList docsList = resp.getElementsByTagName("docsPrint");

						int i = 0;
						NodeList operacion = resp.getElementsByTagName("operacion");
						DocumentoPrint docP = null;
						
						for (i=0;i<operacion.getLength();i++)
						{
							docP = new DocumentoPrint();
							
							NodeList opAct = operacion.item(i).getChildNodes();
							ConfigDocPrint confDoc = null;
							Node nodo = null; 

							if (opAct.item(0) != null)
							{
								nodo = opAct.item(0);
								while (nodo != null)
								{
	/*	urlDoc	-	 urlpath
		idDoc	-	idoperacion
		idImpresion	-	idimpresion
		remitente  	-	remitente	*/
									
									String nombreNodo = nodo.getNodeName();
									//id. de operacion (idDoc)
									if (nombreNodo.equalsIgnoreCase("idoperacion")){
										docP.setIdDoc(new Integer(nodo.getChildNodes().item(0).getNodeValue()));
									}
	
									//id. de  impresion	(idImpresion)
									if (nombreNodo.equalsIgnoreCase("idimpresion")){
										docP.setIdImpresion(new Integer(nodo.getChildNodes().item(0).getNodeValue()));
									}
	
									//urlDoc
									if (nombreNodo.equalsIgnoreCase("urlpath")){
										docP.setUrlDoc(this.obtStrVFromXmlNode(nodo));
									}
	
									//remitente
									if (nombreNodo.equalsIgnoreCase("remitente")){
										docP.setRemitente(this.obtStrVFromXmlNode(nodo));
									}

									//PabloGo, 13 de agosto de 2013
									// FechaYHora
									if (nombreNodo.equalsIgnoreCase("fechaDoc")){
										docP.setFechaHora(this.obtStrVFromXmlNode(nodo));
									}
									//tipo de documento
									if (nombreNodo.equalsIgnoreCase("tipoDoc")){
										docP.setTipoDoc(this.obtStrVFromXmlNode(nodo));
									}
									//serie y nro.
									if (nombreNodo.equalsIgnoreCase("serieNro")){
										docP.setSerieNro(this.obtStrVFromXmlNode(nodo));
									}
									//documento impreso
									if (nombreNodo.equalsIgnoreCase("impreso")){
										String dI = new String(this.obtStrVFromXmlNode(nodo));
										if (dI.compareTo(new String("1")) != 0){ //es diferente de "1", no se imprimio previamente
											docP.setDocImpreso(false);
										}else{	//es igual a "1", se indica que se imprimio previamente
											docP.setDocImpreso(true);
										}
									}
									
									
									//Parametros de impresion (PabloGo, 9 de julio de 2013)
									if (nombreNodo.equalsIgnoreCase("params")){
										confDoc = procesarConfDoc(this.obtStrVFromXmlNode(nodo));
										if (confDoc == null){	//No se pudo cargar la configuracion del documento
											confDoc = new ConfigDocPrint(); //Generar obj. con conf. por defecto.
										}
										//serie y nro(fragmento)										
										if (confDoc.getFiscal()>0){//es fiscal
											String serieFiscal=docP.getSeriaNro().substring(0,3), command=confDoc.getComandoEjec();
											confDoc.setComandoEjec(command+ " "+ serieFiscal);
										}
										
										docP.setConfDoc(confDoc);
									}				
									
//									System.out.print(" - "+opAct.item(0).getChildNodes().item(0).getNodeValue().toString());								
//									System.out.print(nodo.getNodeValue()+" - "+nodo.getNodeName()+"\r\n");
									nodo = nodo.getNextSibling();
								}
	
								lista.add(docP);
							}
							//String textoTemp = new String();
/*							textoTemp = docsList.item(0).getChildNodes().item(0).getNodeName()+" - "+docsList.item(0).getChildNodes().item(0).getNodeValue();
							System.out.println(textoTemp);*/
						}
			        	return lista;
					} else {
						//Se procesa la respuesta de error
						procesarMensajeError(elemento,"loadPrintDocs");

						throw this.getError();
						/*Errores errr = this.getError(); 
			        	throw errr;*/
					}
				} else {
					this.getError().setearErr("Error de conexión", -101, "loadPrintDocs", 0);
					//commClass.setSalida("Error de conexion");
					throw this.getError();
				}
			}
		/*}catch(Exception e){
			Errores error = new Errores();
			error.setearErr("loadPrintDocs error de excepcion. "+e.getMessage(), -100, "loadPrintDocs", 0);
			throw error;
		}*/
	}

	private boolean procesarMensajeError(Element elemento,String fnSource) 
	{
		/* PabloGo, 16 de agosto de 2013
		 * Funcion para parsear (o intentar de) los mensajes de errores 
		 * brindados por el servidor en respuesta a la accion solicitada */
		String salida = new String("Error obteniendo descripcion de error"); 
		String codigoSal = new String("-10101010101");
		
		try {
			// Recuperar texto descriptivo del error			
			NodeList salidaList = elemento.getElementsByTagName("salida");
			try {
				if (salidaList != null){
					salida = salidaList.item(0).getChildNodes().item(0).getNodeValue();
				}
			}catch(NullPointerException e){
				salida = "Error recuperando mensaje de error (nodo no informado)";
			}catch(Exception e){
				salida = "Error recuperando mensaje de error. "+e.getMessage(); 
			}
	
			//aca evaluo que fue lo que salio mal (puede ser un token no valido)
			//NodeList codigoSalList = elemento.getElementsByTagName("codigo");
			try {
				codigoSal = salidaList.item(0).getChildNodes().item(0).getNodeValue();	
			}catch(NullPointerException e){
				codigoSal = "-2020202020";
			}catch(Exception e){
				codigoSal = "-0101010101"; 
			}
			
			this.getError().setearErr(salida, new Double(codigoSal), fnSource, 0);
			return true;
		}catch (Exception e){
			//Opa !
			this.getError().setearErr(salida, new Double(codigoSal), fnSource, 0);
			return false;
		}
	}

	private ConfigDocPrint procesarConfDoc(String datos)
	{	/*PabloGo, 9 de julio de 2013, procesar la informaciÃ³n 
			de configuracion del documento proveniente desde el servidor.*/
		try{
			ConfigDocPrint cd = new ConfigDocPrint();
			String[] mtxDatos = datos.split("\\|");	//justo el | (pipe) requiere ser exceptuado

			for (int i=0;i<mtxDatos.length;i++)
			{
				switch(i){
					case (0):	//documento fiscal?	(1 = si(true), 0 (o cualquier cosa que no sea 1) = no (false))
					{
						int val = new Integer(mtxDatos[i].toString());
						cd.setFiscal(val);
/*						if (val == 1){	//solo si es 1 es true
							cd.setFiscal(true);
						}else{
							cd.setFiscal(false);
						}*/
						break;
					}

					case (1):	//comando
					{
						cd.setComandoEjec(mtxDatos[i].toString());
						break;
					}

					case (2):	//Cantidad de copias
					{
						cd.setCopias(new Integer(mtxDatos[i].toString()));
						break;
					}
					
					case (3):	//Nombre del documento
					{
						cd.setNombreDoc(mtxDatos[i].toString());
						break;
					}
					
					case (4):	//Tipo de hoja
					{
						cd.setTipoHoja(mtxDatos[i].toString());
						break;
					}
					
					case (5):	//Orientacion (1 vertical, 2 horizontal)
					{
						cd.setOrientacion(new Integer(mtxDatos[i].toString()));
						break;
					}
				}
			}
			return cd;
		}catch(Exception e){
			this.getError().setearErr("Error cargando configuración de documento. "+e.getMessage(), -101, "procesarConfDoc", 0);
			return null;			
		}
	}	

	public void centrarVentana(Shell shell) 
	{	//PabloGo, 7 de junio de 2013
		
        org.eclipse.swt.graphics.Rectangle bds = shell.getDisplay().getBounds();

        org.eclipse.swt.graphics.Point p = shell.getSize();

        int nLeft = (bds.width - p.x) / 2;
        int nTop = (bds.height - p.y) / 2;

        shell.setBounds(nLeft, nTop, p.x, p.y);
    }

	public String obtStrVFromXmlNode(Node nodo)
	{	//PabloGo, 11 de julio de 2013
		try{
			String str = new String(nodo.getChildNodes().item(0).getNodeValue());
			return str;
		}catch(Exception e){
			return new String("");
		}
	}
	public Usuario getUsu() {
		return usu;
	}
	public void setUsu(Usuario usu) {
		this.usu = usu;
	}
	
	
}