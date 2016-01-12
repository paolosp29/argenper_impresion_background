package com.arp;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.browser.*;
import com.eway.*;

public class BrowserPrintTest 
{
    public static void main(String [] args) 
    {
    	com.eway.XmlHt xmlHt = new com.eway.XmlHt();
        // Display, la conexión con la pantalla
    	Display display = new Display();
        
        // Shell, la window que se inicia en base a un display
        final Shell shell = new Shell(display);

        // GridLayout extiende de Layout por ende se puede pasar a "setLayout" de shell
        GridLayout gridLayout = new GridLayout(); 
        gridLayout.numColumns = 3;
        
        //Se setea el layout del grid
        shell.setLayout(gridLayout);
        
        //una toolbar
        ToolBar toolbar = new ToolBar(shell, SWT.NONE);
        //Un botón que se crea sobre un "ToolItem" y recibe el estilo "SWT.PUSH"
        ToolItem itemPrint = new ToolItem(toolbar, SWT.PUSH);
        itemPrint.setText("Print");

        //Un grilla para configurar la toolbar
        GridData data = new GridData();
        data.horizontalSpan = 3;
        toolbar.setLayoutData(data);	// Se configura la toolbar

        //Agrega el label que indica donde va la dirección
        Label labelAddress = new Label(shell, SWT.NONE);
        labelAddress.setText("Address");

        // El input donde se coloca la dirección
        final Text location = new Text(shell, SWT.BORDER);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 2;
        data.grabExcessHorizontalSpace = true;
        location.setLayoutData(data); // se configura el text "location"

        //El obj. browser
        final Browser browser;
        
        try {
            browser = new Browser(shell, SWT.NONE);
        } catch (SWTError e) {
            System.out.println("Could not instantiate Browser: " + e.getMessage());
            display.dispose();	//Así se sale
            return;
        }
        
        data = new GridData();	//Otra configuración
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.horizontalSpan = 3;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        browser.setLayoutData(data); // aca se configura el browser

        //El label de status
        final Label status = new Label(shell, SWT.NONE);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 2;
        status.setLayoutData(data);

        final ProgressBar progressBar = new ProgressBar(shell, SWT.NONE);
        data = new GridData();
        data.horizontalAlignment = GridData.END;
        progressBar.setLayoutData(data);

        /* event handling */
        Listener listener = new Listener() 
        {
            public void handleEvent(Event event) {
                ToolItem item = (ToolItem)event.widget;
                String string = item.getText();
                if (string.equals("Print")) browser.execute("javascript:window.print();");
           }
        };
        
        browser.addProgressListener(new ProgressListener() 
        {
            public void changed(ProgressEvent event) {
                    if (event.total == 0) return;                            
                    int ratio = event.current * 100 / event.total;
                    progressBar.setSelection(ratio);
            }
            public void completed(ProgressEvent event) {
                progressBar.setSelection(0);
            }
        });
        
        browser.addStatusTextListener(new StatusTextListener() 
        {
            public void changed(StatusTextEvent event) {
                status.setText(event.text); 
            }
        });
        
        browser.addLocationListener(new LocationListener() 
        {
            public void changed(LocationEvent event) {
                if (event.top) location.setText(event.location);
            }
            public void changing(LocationEvent event) {
            }
        });
        
        itemPrint.addListener(SWT.Selection, listener);
        location.addListener(SWT.DefaultSelection, new Listener() 
        {
            public void handleEvent(Event e) {
                browser.setUrl(location.getText());
            }
        });

        shell.open();
        //browser.setUrl("http://eclipse.org");

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}