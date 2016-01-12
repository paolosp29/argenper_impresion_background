package com.eway;

public class Error {
    String pSource = "";
    double pNumber = 0;
    String pDescription = "";
    int pLineaErr = 0;
    boolean pException = false;
    /*
     * Getters/Setters
     */
    public void setSource(String s) {
        pSource = s;
    }
    public String getSource() {
        return pSource;
    }
    public void setNumber(double n) {
        pNumber = n;
    }
    public double getNumber() {
        return pNumber;
    }
    public void setDescription(String d) {
        pDescription = d;
    }
    public String getDescription() {
        return pDescription;
    }
    public void setLinea(int l) {
        pLineaErr = l;
    }
    public int getLinea() {
        return pLineaErr;
    }
    public void setException(boolean e) {
        pException = e;
    }
    public boolean getException() {
        return pException;
    }
    /*
     * FIN Getters/Setters
     */
    public boolean clear() {
        boolean _rtn = false;
        try {
            pSource = "";
            pNumber = 0;
            pDescription = "";
            pLineaErr = 0;
            pException = false;

        } catch (Exception ex) {
                // Me.p_errGet.setearError Err.number, " DC3_commons->error->clear() " & Err.description
                _rtn = false;
        }
        return _rtn;
    }
    public Error() {
        this.clear();
    }
}
