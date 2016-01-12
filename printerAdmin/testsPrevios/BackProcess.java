package com.arp;

/* Clase que correrá en background para hacer la busqueda de información
 * desde el servidor (Documentos, avisos de impresión, etc.). 
 * Creada: 8 de agosto de 2013 
 * Autor: PabloGo.*/
public class BackProcess extends Thread 
{
	long intervaloCons = 5;	//Por defecto 10 segundos
	private Home homeRef;		//Referencia a la clase "Home"
	private boolean contRun = true;	//Variable que establece si se debe continuar corriendo o terminar la ejecución
	
	
	public void setInterval(long inter){
		this.intervaloCons = inter;
	}
	public long getInterval(){
		return this.intervaloCons;
	}
	
	public void setHome(Home hR){
		this.homeRef = hR;
	}
	public Home getHome(){
		return this.homeRef;
	}
	
	public void setContRun(boolean cR){
		this.contRun=cR;
	}
	public boolean getContRun()
	{
		return this.contRun;
	}

	public BackProcess(long tiempo){
		BackProcess(); //llamo al constructo sin parámetros que inicia el "run";
	}

	private void BackProcess() {
		//this.run();
	}

	public void run(){
		try {
			while (this.getContRun()){
				/*Home h = this.getHome();
				h.ponerMsgTL("hola :-)");*/
				//System.out.print(":-)");
				//this.wait(this.intervaloCons);
				this.sleep(this.intervaloCons*1000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}