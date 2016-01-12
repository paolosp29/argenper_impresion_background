package pe.com.argenper.bean;
/* PabloGo, 9 de julio de 2013
 * Clase que contiene la configuracion para la impresion de un doc. determinado
 * (cantidad de copias, tipo de hoja, orientacion, si es fiscal o no, etc...
 */

public class ConfigDocPrint {
//	Copies, DocumentName, MediaSize, OrientationRequested,  
	private int copias = 1;	//por defecto, una ;-)
	private String nombreDocument = new String("Impresion ARP"); 
	private String tipoHoja = new String("A4"); 
	private int orientacion = 1; //1 vertical, 2 horizontal
//	private boolean fiscal = false;	//por defecto no es impresion fiscal
	private int fiscal = 0;	//PabloGo, 29 de agosto de 2013, no es fiscal (=0) y si es fiscal (>0)
	private String comandoEjec = new String("");	//PabloGo, 11 de julio de 2013
	
	public void setComandoEjec(String ce){
		this.comandoEjec = ce;
	}
	public String getComandoEjec(){
		return this.comandoEjec;
	}
	
	public void setCopias(int cc){
		this.copias = cc;
	}
	public int getCopias(){
		return this.copias;
	}
	
	public void setNombreDoc(String nD){
		this.nombreDocument = nD;
	}
	public String getNombreDoc(){
		return this.nombreDocument;
	}
	
	public void setTipoHoja(String tH){
		this.tipoHoja = tH;
	}
	public String getTipoHoja(){
		return this.tipoHoja;
	}
	
	public void setOrientacion(int ori){
		this.orientacion = ori;
	}
	public int getOrientacion(){
		return this.orientacion;
	}
	
	public void setFiscal(int f){
		this.fiscal = f;
	}
	public int getFiscal(){
		return this.fiscal;
	}
}