package com.arp;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
 
public class TestHTML extends JFrame {
    public TestHTML() {
        super("Test HTML");
 
        String htmlText = "<html><head><script language='JavaScript'>function a(){window.alert('hola Undo');}</script></head><body>"
                + "<input name='btn' value='hola' onchange='a()' onkeydown='a()'>"
        		+ "<img src='http://physpics.com/images/twomugs-small.gif'/><br/>"
                +"<h2>h2</h2>"
                + "<div class='mytag'>div class=mytag,  .mytag selector</div>"
                + "<div id='mytag'>div id=mytag,  #mytag selector</div>"
// these two don't work
                + "<mytag>mytag,  selector mytag</mytag>"   //   displays the tags
                + "<div mytag='mytag'>div mytag=mytag, selector [mytag]</div>"
 
                + "<ul><li><div align='center'>"
                + "<font color='red' size='6' face='verdana,helvetica'>red</font>"
                + "</div></li>"
                + "<li><div style='text-align: right; color: blue; font-weight: bold;'>"
                + "blue"
                + "<hr>"
                + "</div></li>"
                + "<li><table border='1px'><tr><td bgcolor='yellow'>"
                + "<font color='green' size='4' face='verdana,helvetica'>green on yellow</font>"
                + "</td></tr></table></li></ul></body>"
                + "</html>";
        JLabel label = new JLabel("<html><body>aaaa</body></html>");
 
//// http://www.devdaily.com/blog/post/jfc-swing/how-add-style-stylesheet-jeditorpane-example-code
 
        HTMLEditorKit hed = new HTMLEditorKit();
        StyleSheet ss = hed.getStyleSheet();
        ss.addRule("h2 {color : green; font-weight: bold; position: absolute; left: 500px;}");
        ss.addRule("#mytag {color :  #bb0022; font-style:italic;}");
        ss.addRule(".mytag {color : rgb(0,128,25); font-weight: bold;}");
        ss.addRule("[mytag]{color : purple; font-family: monospace;}"); // not selected
        ss.addRule("mytag {color : purple; text-size:14pt;}");  // not selected
        Document doc = hed.createDefaultDocument();
 
        JEditorPane editor = new JEditorPane();
        editor.setEditorKit(hed);
        editor.setDocument(doc);
        editor.setText(htmlText);
        
        Box box = Box.createHorizontalBox();
        box.add(label);
        editor.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, Color.BLACK));
        box.add(editor);
        getContentPane().add(box);
 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 270));
        pack();
        setLocationRelativeTo(null);
    }
 
    public static void main(String[] args) throws Exception {
        (new TestHTML()).setVisible(true);
    }

	public void main() {
		(new TestHTML()).setVisible(true);		
	}
}