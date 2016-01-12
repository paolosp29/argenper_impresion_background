package pe.com.argenper.bean;

public class DocumentoPrint {
	//PabloGo, 30 de mayo de 2013
	String urlDoc = new String();
	int idDoc = 0;

	//PabloGo, 3 de junio de 2013
	long idImpresion = 0;
	String remitente = new String();
	ConfigDocPrint confDoc = new ConfigDocPrint();
	
	//PabloGo, 13 de agosto de 2013
	String fechaHora = new String();
	String tipoDoc = new String();
	String serieYNro = new String();
	boolean docImpreso = false;
	
	public void setFechaHora(String fH){
		this.fechaHora = fH;
	}
	public String getFechaHora(){
		return this.fechaHora;
	}
	
	public void setTipoDoc(String tD){
		this.tipoDoc = tD;
	}
	public String getTipoDoc(){
		return this.tipoDoc;
	}
	
	public void setSerieNro(String sN){
		this.serieYNro = sN;
	}
	public String getSeriaNro(){
		return this.serieYNro;
	}
	
	public void setDocImpreso(boolean dI){
		this.docImpreso = dI;
	}
	public boolean getDocImpreso(){
		return this.docImpreso;
	}
/*	long idOperacion = 0;
	int idAgenciaEmi = 0;
	int idCajaEmi = 0;
	int idCliente = 0;
	int idAgeRec = 0;

	int usuarioAlta = 0;		//Este me parece que no va¡
	int secAgencia = 0;
	int secCaja = 0;
	String fechaOper = new String(); //ok, deberia ser un Date
	String direccion = new String();
	String telefono = new String();
	int tipoDocumento = 0;
	long nroDocumento = 0;	*/

	public void setRemitente(String remi){
		this.remitente = remi;
	}
	public String getRemitente(){
		return this.remitente;
	}
	
	public void setIdImpresion(long iI){
		this.idImpresion = iI;
	}
	public long getIdImpresion(){
		return this.idImpresion;
	}

	public void setUrlDoc(String uD){
		this.urlDoc = uD;
	}
	public String getUrlDoc(){
		return this.urlDoc;
	}
	
	public void setIdDoc(int iDoc){
		this.idDoc = iDoc;
	}
	public int getIdDoc(){
		return this.idDoc;
	}
	
	public void setConfDoc(ConfigDocPrint cd)
	{ //PabloGo, 9 de julio de 2013
		this.confDoc = cd;
	}
	
	public ConfigDocPrint getConfDoc()
	{
		return this.confDoc;
	}
}