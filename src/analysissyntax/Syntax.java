package analysissyntax;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import analysislexical.Regex;

public class Syntax {

	HashMap<String, ArrayList<String>> table;
	ArrayList<String> lineArchive;
	ArrayList<String> words;
	ArrayList<String> tokenWords;
	int lastNumber = 0;
	HashMap<Integer, ArrayList<String>> lineMap;
	HashMap<Integer, ArrayList<String>> tokenMap;
	String pathInput;
	String pathStorage;
	public Syntax(String pathInput, String pathStorage){
		lineMap = new HashMap<Integer, ArrayList<String>>();
		tokenMap = new HashMap<Integer, ArrayList<String>>();
		lineArchive = new ArrayList<String>();
		
		this.pathInput = pathInput;
		this.pathStorage = pathStorage;
		
		table = new HashMap<String, ArrayList<String>>();
		initializeTable();
		
	}
	
	private void initializeTable() {
		
	
		ArrayList<String> type = new ArrayList<String>();
		type.add("float");
		type.add("int");
		type.add("string");
		type.add("bool");
		
		ArrayList<String> bla = new ArrayList<String>();
		
	}
	
	public void parser(String nameArchive){
		readFiles(nameArchive);
	}

	public void variavel(String line){
		
		if(line.contains("final ")){
			
		}
	}
	
	
	private void readFiles(String nameArchive){
		String numberLine ="";
		try {
			FileReader fileRead = new FileReader(pathStorage + nameArchive + "_Compilado.txt");
		    BufferedReader file = new BufferedReader(fileRead);
		    String line;
		    String token;
		    String[] lineSplits;
		    line = file.readLine();
			while (line != null) {
				
				
				if(line.contains(":")){
					line = line.split("-> ")[1];
					lineSplits = line.split("   ");
					line = lineSplits[0];
					numberLine = lineSplits[lineSplits.length - 1];
					numberLine = numberLine.replaceAll("	", "");
					numberLine = numberLine.replaceAll(" ", "");
					int number = Integer.parseInt(numberLine);
					
					token = line.split(":")[0];
					
					if(lineMap.containsKey(number)){
						lineMap.get(number).add(line);
						tokenMap.get(number).add(token);
					}else{
						//System.out.println("  aa" + number);
						words = new ArrayList<String>();
						words.add(line);
						lineMap.put(number, words);
						lastNumber = number;
						
						tokenWords = new ArrayList<String>();
						tokenWords.add(token);
						tokenMap.put(number, tokenWords);
					}
					//System.out.println("SINTATICO " + line + " linha " + number );
					lineMap.get(lineMap.size() -1 + "");
				}
				line = file.readLine(); // le da segunda linha ate a ultima linha
			  	
		    }
			fileRead.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//printLine();
	}
	
	
	
	public void printLine(){
		
		for(int i = 1; i <= lastNumber; i++){
			if(lineMap.containsKey(i)){
				for(int j = 0; j < lineMap.get(i).size(); j++){
					System.out.println("linha: " + i +"  dado linha " + lineMap.get(i).get(j));
					System.out.println("linha: " + i  + tokenMap.get(i).get(j));
				}
			}
		}
	}
}
