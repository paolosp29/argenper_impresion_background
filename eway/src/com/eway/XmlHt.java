package com.eway;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.jdesktop.http.Method;
import org.jdesktop.http.async.AsyncHttpRequest.ReadyState;
import org.jdesktop.http.async.XmlHttpRequest;
import org.w3c.dom.Document;

public class XmlHt extends Thread 
{
    //Object oErr = null;
	Errores oErr = new Errores();
    Object oUsu = null;

    // la matriz campo / valor
    ArrayList<String> mtxCampoValor = new ArrayList<String>();
 	// los headers de request
    ArrayList<String> mtxHeadersReq = new ArrayList<String>();
    // todos los headers de respuesta
    String pAllHeadersResp = "";

    boolean loaded = false;
    int pStatus = 0;
    String pStatusText = "";
    String pResponseText = "";
    // ver que tipo de variable tiene que ser
    Document pResponseXml;
    String pResponseBody = "";
    boolean pProcCompleto = false;
    String pProcTexto = "";
    String pCharSet = "";

    //PabloGo, 23 de mayo de 2013
    boolean envioOK = false; //establece si el envio fué exitoso
    
    // segundos para el TimeOut de una peticion
    int pEsperaTO = 30;
    // sera o no asycncronica la peticion, por defecto es true
    boolean pAsync = false;
    // setea que NO se debe encodear (??) los datos de envio
    boolean pNoEncode = false;
    // Setea que el metodo a usar sera post o no (si es no, sera GET)
    boolean pMethodPost = false;

	/* Getters / Setters */
    //PabloGo, 23 de mayo de 2013    
    public void setEnvioOK(boolean eo){
    	this.envioOK = eo;
    }
    public boolean getEnvioOK(){
    	return this.envioOK;
    }
    
    public void setError(Errores e) {
        oErr = e;
    }
    public Errores getError(){
        return oErr;
    }
    
    public void setUsu(Object u) {
        oUsu = u;
    }
    public Object getUsu() {
        return oUsu;
    }
    
    public void setProcesoCompleto(boolean b) {
        pProcCompleto = b;
    }
    public boolean getProcesoCompleto() {
        return pProcCompleto;
    }
    
    public void setProcesoTexto(String t) {
        pProcTexto = t;
    }
    public String getProcesoTexto() {
        return pProcTexto;
    }
    
    public void setStatus(int e) {
        pStatus = e;
    }
    public int getStatus() {
        return pStatus;
    }
    
    public void setStatusText(String e) {
        pStatusText = e;
    }
    public String getStatusText() {
        return pStatusText;
    }
    
    public void setResponseText(String r) {
        pResponseText = r;
    }
    public String getResponseText() {
        return pResponseText;
    }
    
    public void setResponseXml(Document r) {
   		pResponseXml = r;
    }
    public Document getResponseXml() {
        return pResponseXml;
    }
    
    public void setResponseBody(String r) {
        pResponseBody = r;
    }
    public String getResponseBody() {
        return pResponseBody;
    }
    
    public void setEsperaTO(int tiempo) {
        pEsperaTO = tiempo;
    }
    public int getEsperaTO() {
        return pEsperaTO;
    }
    
    public void setCharSet(String c) {
        pCharSet = c;
    }
    public String getCharSet() {
        return pCharSet;
    }
    
    public void setAllHeadersResp(String ah) {
        pAllHeadersResp = ah;
    }
    public String getAllHeadersResp() {
        return pAllHeadersResp;
    }
    
    public void setAsync(boolean a) {
        pAsync = a;
    }
    public boolean getAsync() {
        return pAsync;
    }
    
    public void setNoEncode(boolean n) {
        pNoEncode = n;
    }
    public boolean getNoEncode() {
        return pNoEncode;
    }
    
    private void setMethodPost(boolean m) {
        pMethodPost = m;
    }
    private boolean getMethodPost() {
        return pMethodPost;
    }
	/* Fin Getters / Setters */


    public XmlHt() {     //Constructor
    	this.setError(new Errores());
        setMethodPost(true);
        this.setNoEncode(false);
        this.setAsync(true);
        mtxCampoValor = new ArrayList<String>();
        mtxHeadersReq = new ArrayList<String>();
        // es usuario SINGULAR!
        // solo para reconocer quien instancia esta clase
        /*
        Me.errores.setUsu(CreateObject("DC3_usuarios.usuario"));
        */
        this.setCharSet("iso-8859-1");
        this.setEsperaTO(30);
    }
    
    
    public boolean enviarPost(String url) {
        setMethodPost(true);
        return enviar(url);
    }
    public boolean enviarGet(String url) {
        setMethodPost(false);
        return enviar(url);
    }
    //Function enviar(ByVal url As String, ByVal datos As String) As Boolean
    public boolean enviar(String url) 
    {
        boolean _rtn = false;
    
        try {
            _rtn = true;
            setProcesoCompleto(true);
            final XmlHttpRequest req = new XmlHttpRequest();
            
            PropertyChangeListener listener = new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent evt)
                {
                	
                    if (evt.getNewValue() == ReadyState.LOADED)
                    {
                    	loaded = true; //el documento se cargó, ahora hay que ver si es un documento utilizable
                    
                    	XmlHttpRequest nreq = (XmlHttpRequest) evt.getSource();
                    	
                    	setStatus(nreq.getStatus());
                        setStatusText(nreq.getStatusText());
                        setEnvioOK(true);
                        
                        /*if (nreq.getStatus() < 200 || nreq.getStatus() >= 300)
                        {
                            setEnvioOK(false);
                        }else{
                            setEnvioOK(true);*/
                        	setResponseText(nreq.getResponseText());
                        	setResponseXml(nreq.getResponseXML());
                        	setAllHeadersResp(nreq.getAllResponseHeaders());
                        //}
                    }
                }
            };
        
            req.setOnReadyStateChange(listener);
            String post = "";

            if (getMethodPost() == true)
            {
                req.open(Method.POST, new URL(url).toString(), getAsync());
                StringBuilder datos = new StringBuilder();
                establecerParametrosPOST(datos);
                post = datos.toString();
            } else {
                req.open(Method.GET, new URL(url).toString(), getAsync());
                establecerParametrosGET(req);
            }

            establecerReqHeaders(req);

            req.send(post);

            for (int i = 0; i < getEsperaTO() * 2; i++)
            {
            	if (loaded == true) {
            		System.out.print(req.getStatus());
            		break;
            	}
            	Thread.sleep(500);
            }
            
            if (this.getEnvioOK()){
            	//anda
            	return true;
            }else{
            	this.getError().setearErr("Fallo el envio",-100, "enviar", 0);
        		//System.out.print(this.getStatus() + " - "+ this.getStatusText());
            	if (loaded == false) {
	            	((Errores) this.getError()).setDescription("Error al recibir respuesta. Timeout: " + getEsperaTO() + " segundos.");
	            	_rtn = false;
	            }
            }
            /*
            if (_rtn == true)
            {
                Debug.Print req.getallresponseheaders();

                if ("FAIL" == req.getResponseHeader("procesoCompleto").toString())
                {
                    setProcesoCompleto(false);
                }

                this.setProcesoTexto(req.getResponseHeader("procesoTexto").toString());

                if (this.getProcesoCompleto() == false)
                {
                    _rtn = false;
                    ((errores) this.getError()).setDescription(this.getProcesoTexto());
                }
                
                this.setStatus(req.getStatus());

                this.setStatusText(req.getStatusText());

                // cuando el header de respuesta no esta en el rango de los 200
                // se asume que es un error, ya sea de cliente, servidor o una redireccion / informacion
                if (this.getStatus() < 200 || this.getStatus() >= 300)
                {
                    _rtn = false;
                }

                if ((req.getReadyState() != ReadyState.LOADED))
                {
                    _rtn = false;
                    req.abort();
                    ((errores) this.getError()).setDescription("Error de conexi�n.");
                }
            }
            */
        } catch (Exception ex) {
    		((Errores) this.getError()).setDescription("xmlHt->enviar() " + ex.getMessage());
            _rtn = false;
        }
        return _rtn;
    }

    private boolean establecerReqHeaders(XmlHttpRequest req) // por referencia
    {
        boolean _rtn = true;
        try {
            // indica si se especifico el Content-Type
            boolean cT_Spec = false;
            // indica si se especifico el Accept-Charset
            boolean aC_Spec = false;
            // indica si se especifico el Content-Length
            boolean cL_Spec = false;
            // indica si se especifico el Connection
            boolean cN_Spec = false;

            cT_Spec = false;
            aC_Spec = false;
            cL_Spec = false;
            cN_Spec = false;

            String header = "";
            String valor = "";
            String[] mtxAux = null;

            Iterator<String> i = mtxHeadersReq.iterator();

            while (i.hasNext())
            {
                mtxAux = i.next().toString().split("=");
                header = mtxAux[0];
                valor = mtxAux[1];
                if (header.equals("Content-Type")) {
                    cT_Spec = true;
                } else if (header.equals("Accept-Charset")) {
                    aC_Spec = true;
                } else if (header.equals("Content-Length")) {
                    cL_Spec = true;
                } else if (header.equals("Connection")) {
                	cN_Spec = true;
            	}
                req.setRequestHeader(header, valor);
            }
            // Valores por defecto
            // Content-Type
            if (cT_Spec == false)
            {
                if (getMethodPost() == true)
                {
                	req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                }
            }
            // Accept-Charset
            if (aC_Spec == false)
            {
                if (!this.getCharSet().isEmpty())
                {
                	req.setRequestHeader("Accept-Charset", this.getCharSet());
                }
            }
            // Content-Length
            if (cL_Spec == false)
            {
            	//req.setRequestHeader("Content-Length", "" + req.getParameters().length);
            }
            // Connection
            if (cN_Spec == false)
            {
            	//req.setRequestHeader("Connection", "close");
            }
        } catch (Exception ex) {
    		((Errores) this.getError()).setDescription("xmlHt->establecerReqHeaders() " + ((Errores) this.getError()).getDescription());
            _rtn = false;
        }
        return _rtn;
    }

    public void usuSet(double idUsu) {
        // funcion que setea el id de usuario en mi
        // y en todas las clases que instaci?
        // cada clase DEBE tener un 'usuSet' y cada 'usuSet' es diferente
        // en base a las clase que instancia la que lo contiene
    	((Errores) this.getError()).usuSet(idUsu);
        // ninguna de las clases 'singulares' llevan 'usuSet'
    }
    /*
    private void clear() {
        mtxCampoValor = new ArrayList<String>();
        mtxHeadersReq = new ArrayList<String>();
        this.setNoEncode(false);
        this.setAsync(true);
    }
    */
    public boolean addCampoValor(String campo, String valor) 
    {
        boolean _rtn = false;

        try {
            _rtn = true;
            if (campo.toUpperCase().equals("NULL"))
            {
                mtxCampoValor.add(this.escape(valor));
            } else {
                mtxCampoValor.add(campo + "=" + this.escape(valor));
            }
        } catch (Exception ex) {
    		((Errores) this.getError()).setDescription("xmlHt->addCampoValor() " + ((Errores) this.getError()).getDescription());
            _rtn = false;
        }

        return _rtn;
    }

    public boolean addRequestHeader(String header, String valor) {
        boolean _rtn = false;
        try {
            _rtn = true;
            mtxHeadersReq.add(header + "=" + valor);
        } catch (Exception ex) {
        	((Errores) this.getError()).setDescription("xmlHt->addRequestHeader() " + ((Errores) this.getError()).getDescription());
            _rtn = false;
        }
        return _rtn;
    }

    public boolean establecerParametrosPOST(StringBuilder datos) 
    {
        boolean _rtn = false;
        try {
            Iterator<String> i = mtxCampoValor.iterator();
            _rtn = true;
            if (i.hasNext())
			{
            	datos.append(i.next().toString());
				do {
					datos.append("&" + i.next().toString());
				} while (i.hasNext());
            } else {
            	((Errores) this.getError()).setDescription("Debe agregar por lo menos un set de campo / valor a enviar");
                _rtn = false;
            }
        } catch (Exception ex) {
    		((Errores) this.getError()).setDescription("xmlHt->establecerParametros() " + ((Errores) this.getError()).getDescription());
            _rtn = false;
        }
        return _rtn;
    }
    
    public boolean establecerParametrosGET(XmlHttpRequest req) {
        boolean _rtn = false;
        try {
            Iterator<String> i = mtxCampoValor.iterator();
            _rtn = true;

            if (!i.hasNext()) {
            	((Errores) this.getError()).setDescription("Debe agregar por lo menos un set de campo / valor a enviar");
                _rtn = false;
            } else {

                while (i.hasNext())
                {
                    String[] mtxAux = i.next().toString().split("=");
                    String campo = mtxAux[0];
                    String valor = mtxAux[1];
                	req.setParameter(campo, valor);
                }
            }
        } catch (Exception ex) {
    		((Errores) this.getError()).setDescription("xmlHt->establecerParametros() " + ((Errores) this.getError()).getDescription());
            _rtn = false;
        }
        return _rtn;
    }
    
    public String escape(String str) {
        String _rtn = "";
        String s = "";
        char r;
        String strEscape = "";
        int i = 0;
     	// establece que no se debe "encodear"
        if (getNoEncode() == true)
        {
            _rtn = str;
            return _rtn;
        }
        for (i = 0; i < str.length(); i++) {
            r = str.charAt(i);

            if (r == ' ') {
            	s = "%20";
            } else if (r == '!') {
				s = "%21";
            } else if (r == '\"') {
				s = "%22";
            } else if (r == '#') {
				s = "%23";
            } else if (r == '$') {
				s = "%24";
            } else if (r == '%') {
				s = "%25";
            } else if (r == '&') {
				s = "%26";
            } else if (r == '\'') {
				s = "%27";
            } else if (r == '(') {
				s = "%28";
            } else if (r == ')') {
				s = "%29";
            } else if (r == '*') {
				s = "%2A";
            } else if (r == '+') {
				s = "%2B";
            } else if (r == ',') {
				s = "%2C";
            } else if (r == '-') {
				s = "%2D";
            } else if (r == '.') {
				s = "%2E";
            } else if (r == '/') {
				s = "%2F";
            /*
            } else if (r == '0') {
                s = "%30";
            } else if (r == '1') {
                s = "%31";
            } else if (r == '2') {
                s = "%32";
            } else if (r == '3') {
                s = "%33";
            } else if (r == '4') {
                s = "%34";
            } else if (r == '5') {
                s = "%35";
            } else if (r == '6') {
                s = "%36";
            } else if (r == '7') {
                s = "%37";
            } else if (r == '8') {
                s = "%38";
            } else if (r == '9') {
                s = "%39";
            */
            } else if (r == ':') {
				s = "%3A";
            } else if (r == ';') {
				s = "%3B";
            } else if (r == '<') {
				s = "%3C";
            } else if (r == '=') {
				s = "%3D";
            } else if (r == '>') {
				s = "%3E";
            } else if (r == '?') {
				s = "%3F";
            } else if (r == '@') {
				s = "%40";
            /*
            } else if (r == 'A') {
                s = "%41";
            } else if (r == 'B') {
                s = "%42";
            } else if (r == 'C') {
                s = "%43";
            } else if (r == 'D') {
                s = "%44";
            } else if (r == 'E') {
                s = "%45";
            } else if (r == 'F') {
                s = "%46";
            } else if (r == 'G') {
                s = "%47";
            } else if (r == 'H') {
                s = "%48";
            } else if (r == 'I') {
                s = "%49";
            } else if (r == 'J') {
                s = "%4A";
            } else if (r == 'K') {
                s = "%4B";
            } else if (r == 'L') {
                s = "%4C";
            } else if (r == 'M') {
                s = "%4D";
            } else if (r == 'N') {
                s = "%4E";
            } else if (r == 'O') {
                s = "%4F";
            } else if (r == 'P') {
                s = "%50";
            } else if (r == 'Q') {
                s = "%51";
            } else if (r == 'R') {
                s = "%52";
            } else if (r == 'S') {
                s = "%53";
            } else if (r == 'T') {
                s = "%54";
            } else if (r == 'U') {
                s = "%55";
            } else if (r == 'V') {
                s = "%56";
            } else if (r == 'W') {
                s = "%57";
            } else if (r == 'X') {
                s = "%58";
            } else if (r == 'Y') {
                s = "%59";
            } else if (r == 'Z') {
                s = "%5A";
            */
            } else if (r == '[') {
				s = "%5B";
            } else if (r == '\\') {
				s = "%5C";
            } else if (r == ']') {
				s = "%5D";
            } else if (r == '^') {
				s = "%5E";
            } else if (r == '_') {
				s = "%5F";
            } else if (r == '`') {
				s = "%60";
            /*
            } else if (r == 'a') {
                s = "%61";
            } else if (r == 'b') {
                s = "%62";
            } else if (r == 'c') {
                s = "%63";
            } else if (r == 'd') {
                s = "%64";
            } else if (r == 'e') {
                s = "%65";
            } else if (r == 'f') {
                s = "%66";
            } else if (r == 'g') {
                s = "%67";
            } else if (r == 'h') {
                s = "%68";
            } else if (r == 'i') {
                s = "%69";
            } else if (r == 'j') {
                s = "%6A";
            } else if (r == 'k') {
                s = "%6B";
            } else if (r == 'l') {
                s = "%6C";
            } else if (r == 'm') {
                s = "%6D";
            } else if (r == 'n') {
                s = "%6E";
            } else if (r == 'o') {
                s = "%6F";
            } else if (r == 'p') {
                s = "%70";
            } else if (r == 'q') {
                s = "%71";
            } else if (r == 'r') {
                s = "%72";
            } else if (r == 's') {
                s = "%73";
            } else if (r == 't') {
                s = "%74";
            } else if (r == 'u') {
                s = "%75";
            } else if (r == 'v') {
                s = "%76";
            } else if (r == 'w') {
                s = "%77";
            } else if (r == 'x') {
                s = "%78";
            } else if (r == 'y') {
                s = "%79";
            } else if (r == 'z') {
                s = "%7A";
            */
            } else if (r == '{') {
                s = "%7B";
            } else if (r == '|') {
                s = "%7C";
            } else if (r == '}') {
                s = "%7D";
            } else if (r == '~') {
                s = "%7E";
            } /*else if (r == '�') {
                s = "%80";
            } else if (r == '�') {
                s = "%82";
            } else if (r == '�') {
                s = "%83";
            } else if (r == '�') {
                s = "%84";
            } else if (r == '�') {
                s = "%85";
            } else if (r == '�') {
                s = "%86";
            } else if (r == '�') {
                s = "%87";
            } else if (r == '�') {
                s = "%88";
            } else if (r == '�') {
                s = "%89";
            } else if (r == '�') {
                s = "%8A";
            } else if (r == '�') {
                s = "%8B";
            } else if (r == '�') {
                s = "%8C";
            } else if (r == '�') {
                s = "%8E";
            } else if (r == '�') {
                s = "%91";
            } else if (r == '�') {
                s = "%92";
            } else if (r == '�') {
                s = "%93";
            } else if (r == '�') {
                s = "%94";
            } else if (r == '�') {
                s = "%95";
            } else if (r == '�') {
                s = "%96";
            } else if (r == '�') {
                s = "%97";
            } else if (r == '�') {
                s = "%98";
            } else if (r == '�') {
                s = "%99";
            } else if (r == '�') {
                s = "%9A";
            } else if (r == '�') {
                s = "%9B";
            } else if (r == '�') {
                s = "%9C";
            } else if (r == '�') {
                s = "%9E";
            } else if (r == '�') {
                s = "%9F";
            } else if (r == '�') {
                s = "%A1";
            } else if (r == '�') {
                s = "%A2";
            } else if (r == '�') {
                s = "%A3";
            } else if (r == '�') {
                s = "%A5";
            } else if (r == '|') {
                s = "%A6";
            } else if (r == '�') {
                s = "%A7";
            } else if (r == '�') {
                s = "%A8";
            } else if (r == '�') {
                s = "%A9";
            } else if (r == '�') {
                s = "%AA";
            } else if (r == '�') {
                s = "%AB";
            } else if (r == '�') {
                s = "%AC";
            } else if (r == '�') {
                s = "%AD";
            } else if (r == '�') {
                s = "%AE";
            } else if (r == '�') {
                s = "%AF";
            } else if (r == '�') {
                s = "%B0";
            } else if (r == '�') {
                s = "%B1";
            } else if (r == '�') {
                s = "%B2";
            } else if (r == '�') {
                s = "%B3";
            } else if (r == '�') {
                s = "%B4";
            } else if (r == '�') {
                s = "%B5";
            } else if (r == '�') {
                s = "%B6";
            } else if (r == '�') {
                s = "%B7";
            } else if (r == '�') {
                s = "%B8";
            } else if (r == '�') {
                s = "%B9";
            } else if (r == '�') {
                s = "%BA";
            } else if (r == '�') {
                s = "%BB";
            } else if (r == '�') {
                s = "%BC";
            } else if (r == '�') {
                s = "%BD";
            } else if (r == '�') {
                s = "%BE";
            } else if (r == '�') {
                s = "%BF";
            } else if (r == '�') {
                s = "%C0";
            } else if (r == '�') {
                s = "%C1";
            } else if (r == '�') {
                s = "%C2";
            } else if (r == '�') {
                s = "%C3";
            } else if (r == '�') {
                s = "%C4";
            } else if (r == '�') {
                s = "%C5";
            } else if (r == '�') {
                s = "%C6";
            } else if (r == '�') {
                s = "%C7";
            } else if (r == '�') {
                s = "%C8";
            } else if (r == '�') {
                s = "%C9";
            } else if (r == '�') {
                s = "%CA";
            } else if (r == '�') {
                s = "%CB";
            } else if (r == '�') {
                s = "%CC";
            } else if (r == '�') {
                s = "%CD";
            } else if (r == '�') {
                s = "%CE";
            } else if (r == '�') {
                s = "%CF";
            } else if (r == '�') {
                s = "%D0";
            } else if (r == '�') {
                s = "%D1";
            } else if (r == '�') {
                s = "%D2";
            } else if (r == '�') {
                s = "%D3";
            } else if (r == '�') {
                s = "%D4";
            } else if (r == '�') {
                s = "%D5";
            } else if (r == '�') {
                s = "%D6";
            } else if (r == '�') {
                s = "%D8";
            } else if (r == '�') {
                s = "%D9";
            } else if (r == '�') {
                s = "%DA";
            } else if (r == '�') {
                s = "%DB";
            } else if (r == '�') {
                s = "%DC";
            } else if (r == '�') {
                s = "%DD";
            } else if (r == '�') {
                s = "%DE";
            } else if (r == '�') {
                s = "%DF";
            } else if (r == '�') {
                s = "%E0";
            } else if (r == '�') {
                s = "%E1";
            } else if (r == '�') {
                s = "%E2";
            } else if (r == '�') {
                s = "%E3";
            } else if (r == '�') {
                s = "%E4";
            } else if (r == '�') {
                s = "%E5";
            } else if (r == '�') {
                s = "%E6";
            } else if (r == '�') {
                s = "%E7";
            } else if (r == '�') {
                s = "%E8";
            } else if (r == '�') {
                s = "%E9";
            } else if (r == '�') {
                s = "%EA";
            } else if (r == '�') {
                s = "%EB";
            } else if (r == '�') {
                s = "%EC";
            } else if (r == '�') {
                s = "%ED";
            } else if (r == '�') {
                s = "%EE";
            } else if (r == '�') {
                s = "%EF";
            } else if (r == '�') {
                s = "%F0";
            } else if (r == '�') {
                s = "%F1";
            } else if (r == '�') {
                s = "%F2";
            } else if (r == '�') {
                s = "%F3";
            } else if (r == '�') {
                s = "%F4";
            } else if (r == '�') {
                s = "%F5";
            } else if (r == '�') {
                s = "%F6";
            } else if (r == '�') {
                s = "%F7";
            } else if (r == '�') {
                s = "%F8";
            } else if (r == '�') {
                s = "%F9";
            } else if (r == '�') {
                s = "%FA";
            } else if (r == '�') {
                s = "%FB";
            } else if (r == '�') {
                s = "%FC";
            } else if (r == '�') {
                s = "%FD";
            } else if (r == '�') {
                s = "%FE";
            } else if (r == '�') {
                s = "%FF";
            }*/
            if (!s.isEmpty())
            {
                strEscape = strEscape + s;
            } else {
                strEscape = strEscape + r;
            }
            s = "";
        }
        _rtn = strEscape;
        return _rtn;
    }
}