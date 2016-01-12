package pe.com.argenper.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class AsyncExecDisplay {
  static Display display = new Display();

  public static void main(String[] args) {
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    

    final Button buttonAsyncExec = new Button(shell, SWT.PUSH);
    buttonAsyncExec.setText("start");
    buttonAsyncExec.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void widgetSelected(SelectionEvent e) {
        buttonAsyncExec.setText("Calculation in progress ...");
        getTask2(buttonAsyncExec).start();
      }
    });

    shell.open();
    while (!shell.isDisposed()) { // Event loop.
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }

  public static Thread getTask2(Button button) {
    final Button theButton = button;
    return new Thread() {
      public void run() {

        try {
          Thread.sleep(6000);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        display.asyncExec(new Runnable() {
          public void run() {
            theButton.setText("done");
          }
        });
      }
    };
  }

}

/* Test nro. 1
  
  
  package com.arp;
 

class ThreadDemo
{
   public static void main (String [] args)
   {
      MyThread mt = new MyThread ();
      mt.start ();
      for (int i = 0; i < 50; i++)
           System.out.println ("i = " + i + ", i * i = " + i * i);
   }
}
class MyThread extends Thread
{
   public void run ()
   {
      for (int count = 1, row = 1; row < 20; row++, count++)
      {
           for (int i = 0; i < count; i++)
                System.out.print ('*');
           System.out.print ('\n');
      }
   }
}*/