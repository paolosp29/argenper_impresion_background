package pe.com.argenper.bean;

public class Usuario {
	
	String token = new String(); //El token de acceso
	String nombreUsu = new String(); //El nombre del usuario que inicia sesion
	String idEmpresa = new String("0");
	String idAgencia = new String("0");
	String idCaja = new String ("0");


//PabloGo, 17 de mayo de 2013	
	public void setToken(String t){
		this.token = t;
	}
	
	public String getToken(){
		return this.token;
	}
	
	public void setNombreUsu(String nu){
		this.nombreUsu = nu;
	}
	
	public String getNombreUsu(){
		return this.nombreUsu;
	}
	
	public void setIdEmpresa(String idE){
		this.idEmpresa = idE;
	}
	
	public String getIdEmpresa(){
		return this.idEmpresa;
	}
	
	public void setIdAgencia(String idSuc){
		this.idAgencia = idSuc;
	}
	
	public String getIdAgencia(){
		return this.idAgencia;
	}
	
	public void setIdCaja(String idC){
		this.idCaja = idC;
	}
	
	public String getIdCaja(){
		return this.idCaja;
	}

	public Usuario(){
		
	}
}