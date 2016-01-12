package com.eway;

public class Errores extends Exception{

	private static final long serialVersionUID = 1L;
	
	double errNro = 0;
    String errDesc = "";
    boolean warn = false;
    String[] warnColl = null;
    int totWarns = 0;

    Object oErr = null;
    Object oUsu = null;

    public Errores() {
    	this.setError(new Error());
        // MODELO NUEVO DE ERRORES
        /*
        es usuario SINGULAR!
        solo para reconocer quien instancia esta clase
        setUsu(CreateObject("DC3_usuarios.usuario"));
        */
        setWarnSet(false);
        setTotWarnsSet(-1);
        warnColl = null;
    }
    /*
     *  Inicio Getters / Setters
     */
    private void setWarnSet(boolean estadoWarn) {
        warn = estadoWarn;
    }
    public boolean getWarnGet() {
        return warn;
    }
    private void setTotWarnsSet(int total) {
        totWarns = total;
    }
    public int getTotWarnsGet() {
        return totWarns;
    }
    public String[] getWarnCollGet() {
        return warnColl;
    }
    public void setErrorNroSet(double queErrNro) {
        errNro = queErrNro;
    }
    public void setErrorDescSet(String queErrDesc) {
        errDesc = queErrDesc;
    }
    public double getErrorNroGet() {
        return errNro;
    }
    public String getErrorDescGet() {
        return errDesc;
    }
    /*
     *  Desde aca propiedades del modelo nuevo de errores
	 */
    public void setError(Object oe) {
        oErr = oe;
    }
    public Object getError() {
        return oErr;
    }
    public void setUsu(Object u) {
        oUsu = u;
    }
    public Object getUsu() {
        return oUsu;
    }
    public void setNumber(double n) {
        ((Error) this.getError()).setNumber(n);
    }
    public void setDescription(String d) {
    	((Error) this.getError()).setDescription(d);
    }
    public void setSource(String s) {
    	((Error) this.getError()).setSource(s);
    }
    public void setLinea(int l) {
    	((Error) this.getError()).setLinea(l);
    }
    public void setException(boolean e) {
    	((Error) this.getError()).setException(e);
    }
    // Public Property Get number() As Double
    public double getNumber() {
    	return ((Error) this.getError()).getNumber();
    }
    public String getDescription() {
        return ((Error) this.getError()).getDescription();
    }
    public String getSource() {
        return ((Error) this.getError()).getSource();
    }
    public int getLinea() {
        return ((Error) this.getError()).getLinea();
    }
    public boolean getException() {
        return ((Error) this.getError()).getException();
    }

    /*
     *  FIN GETTERS
     *  hasta aca propiedades del modelo nuevo de errores 
     */

    /*    public void setearError(double errNro, String errDesc, boolean warn) {
        // setear las propiedades de error
        if (warn == false) {
            this.setErrorDescSet(errDesc);
            // COMPATIBILIDAD MODELO NUEVO
            this.setDescription(errDesc);
            this.setErrorNroSet(errNro);
            // COMPATIBILIDAD MODELO NUEVO
            this.setNumber(errNro);
        } else {
            // NO es error, ES warning
            addWarn(errDesc);
        }
    }*/
    
    public void obtError(double queErrNro, String queErrDesc) {
        queErrNro = this.getErrorNroGet();
        queErrDesc = this.getErrorDescGet();
    }
    
    private void addWarn(String textoWarn) {
        // agraga un warning a la coleccion de warning del objeto y setea la propiendad warn a true
        setWarnSet(true);
        setTotWarnsSet(getTotWarnsGet() + 1);
        warnColl[getTotWarnsGet()] = textoWarn;
    }
    
    public String obtWarn(int indice) {
        // regresa el warning especificado
        try {
        	return warnColl[indice];
        } catch (Exception ex) {
        	return "";
        }
    }

    public void resetAll() {
        errNro = 0;
        errDesc = "";
        warn = false;
        totWarns = -1;
        // this.clear();
        warnColl = null;
    }

    /*
     * Desde aca metodos del modelo nuevo de errores
     */
    public boolean heredarError(Errores e) {
        // sinonimo a heredarErr
        return this.heredarErr(e);
    }

    public boolean heredarErr(Errores oE) {
    	/*
         * lo que hace esta funcion es recibir un e (error)
         * ya sea un Err (excepcion) o un 'error' (logico)
         * y recuperar los valores de forma indistinta
         */
    	
    	Error e = (Error)oE.getError();
    	this.cachear(e);
        this.setLinea(e.getLinea());
        this.setException(e.getException());

        return true;
    }

    public void cachear(Error e) {

        this.setNumber(e.getNumber());
        this.setDescription(e.getDescription());

        // compatibilidad modelo viejo
        this.setErrorNroSet(e.getNumber());
        this.setErrorDescSet(e.getDescription());

        this.setSource(e.getSource());
        this.setException(true);
    }
    /*
    public boolean setearErr(String descrip) {
    	return this.setearErr(descrip, 0, "", 0);
    }
    */
    public boolean setearErr(String descrip, double numero, String origen, int linea) {
        boolean _rtn = false;
        // setear las propiedades de error
        try {
            this.setDescription(descrip);
            // COMPATIBILIDAD MODELO VIEJO
            this.setErrorDescSet(descrip);
            this.setNumber(numero);
            // COMPATIBILIDAD MODELO VIEJO
            this.setErrorNroSet(numero);
            this.setSource(origen);
            this.setLinea(linea);
            this.setException(false);
            _rtn = true;
        } catch (Exception ex) {
        	// this.p_errGet.setearError Err.number, " DC3_commons->errores->setearErr() Error seteando error " & Err.description
        	_rtn = false;
        }
        return _rtn;
    }

    public void usuSet(double idUsu)
    {
    	/*
        funcion que setea el id de usuario en mi
        y en todas las clases que instacia
        cada clase DEBE tener un 'usuSet' y cada 'usuSet' es diferente
        en base a las clase que instancia la que lo contiene
    	*/
        // aca me seteo el ID a this (clase acutal)
        this.setUsu(idUsu);
        // ninguna de las clases 'singulares' llevan 'usuSet'
    }
}