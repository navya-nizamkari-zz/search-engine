//importing swing and awt packages

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.*;
import java.math.*;

public class Gui extends JFrame{
	
	JTextField input; JTextArea output ;
	 InvertedIndex idx;
	
	public Gui() {
	
	//first build index
	
	 try {
			long startTime = System.currentTimeMillis();
			idx = new InvertedIndex();
			for (int i = 0; i < 768; i++) {
				idx.indexFile(new File("corpus/split"+i+".txt"));
			}
			
			//time to index
			long stopTime = System.currentTimeMillis();  
			System.out.println("Time taken for indexing " + (stopTime - startTime) + " ms");
			// Document frequency list to store document frequency
			idx.docFreq();
			
			//idx.search(Arrays.asList(page.input.getText().split(" ")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	// GUi code : setting layot, adding components like text box, text area, button
	
	getContentPane().setLayout(new FlowLayout());
	input = new JTextField(" ", 10);
	//textField2 = new JTextField(" ", 100);
	JLabel name = new JLabel("Results:");
	output = new JTextArea("", 20,25);
	JButton butt = new JButton("search");
	getContentPane().add(input);
	getContentPane().add(butt);
	getContentPane().add(name);
	getContentPane().add(output);
	setSize(300, 170);
        setVisible(true);
        this.setSize(400,400);
        this.setTitle("IR-search engine");
	this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	
	// idx = new InvertedIndex();
	
	//code that is executed on button click	
	butt.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         output.setText("");
           String query = input.getText();
           idx.search(Arrays.asList(input.getText().split(" ")));
           // Apeending new docs to the text area
           for (String f : idx.answer) {
			output.append( f + "\n");
			
		}     
           }
           });
	}
		
	
	public static void main(String argv[]) {
			
		new Gui();
	
	}
}

