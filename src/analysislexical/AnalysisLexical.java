package analysislexical;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * @author felipe
 *
 */

public class AnalysisLexical {

	ArrayList<String> lineArchive;
	HashMap<Integer, HashMap<Integer, ArrayList<String>>> matrix;//hash para cada linha
	HashMap<Integer, ArrayList<String>> words;
	private Properties configFile;
	
	String pathStorage;
	String nameArchive;
	Regex regex;
	
	public AnalysisLexical() {
		lineArchive = new ArrayList<String>();
		matrix = new HashMap<Integer, HashMap<Integer,ArrayList<String>>>();
		
		regex = new Regex();
		
		configFile = new Properties();
		try {//Leitura das configuracoes
			configFile.load(new FileInputStream("resources/LexicalConfigFile.properties"));
			pathStorage = configFile.getProperty("PATHSTORAGE");
			nameArchive = configFile.getProperty("NAMEARCHIVE");
			
			//Leitura do arquivo fonte
			readFiles();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Leitura do arquivo fonte
	 */
	private void readFiles(){
		int numberLine = 0;
		try {
			FileReader fileRead = new FileReader(pathStorage + nameArchive);
		    BufferedReader file = new BufferedReader(fileRead);
		    String line;
			line = file.readLine();
			String strLine;
			while (line != null) {
			  	String[] object =line.split(";");
			  	if("".equals(object[0].trim())){
			  		strLine = object[0];
				}else{
					if(!object[0].contains("{") && (!object[0].contains("}"))){
						strLine = object[0]+";";
					}else{
						strLine = object[0];
					}
				}
			  	lineArchive.add(numberLine, strLine);
			  	line = file.readLine(); // lê da segunda até a última linha
			  	numberLine++;
		    }
			fileRead.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Exibicao das linhas lidas
	 */
	public void printLines(){
		for(int i = 0; i < lineArchive.size(); i++){
			System.out.println(i + " = " +lineArchive.get(i));
		}
	}
	
	public void parser(){
		
		for(int i = 0; i < lineArchive.size(); i++){
			words = new HashMap<Integer, ArrayList<String>>();//hash da estrutura
			
			ArrayList<String> reserverdWord = new ArrayList<String>();// index 0 hashmap words
			ArrayList<String> id = new ArrayList<String>();
			ArrayList<String> letter = new ArrayList<String>();
			
			String word[] = lineArchive.get(i).split(" ");
			for(int j = 0; j < word.length; j++){
			//	System.out.println(word[j]);
				word[j]= word[j].split(",")[0];
				word[j]= word[j].split("\\(")[0];
				word[j]= word[j].split("\\)")[0];
				word[j]= word[j].split(";")[0];
				if(regex.isPalavrasReservadas(word[j])){
					System.out.println(i + " Reservada: " + word[j]);
					reserverdWord.add(word[j]);
				}else if(regex.isIdentificador(word[j])){
					System.out.println(i +" Identificador: " + word[j]);
					id.add(word[j]);
				}else if(regex.isLetra(word[j])){
					System.out.println(i +" Letra: " + word[j]);
					letter.add(word[j]);
				}else if(regex.isDigito(word[j])){
					System.out.println(i +" Digito: " + word[j]);
				}else if(regex.isOpRelacionais(word[j])){
					System.out.println(i +" Relacionais: " + word[j]);
				}else if(regex.isOpAritmeticos(word[j])){
					System.out.println(i +" Aritmético: " + word[j]);
				}else if(regex.isOpLogicos(word[j])){
					System.out.println(i +" Lógicos: " + word[j]);
				}
			}
			words.put(0, reserverdWord);
			words.put(1, id);
			words.put(2, letter);
			matrix.put(i, words);
		}
	}
	
	public void writeLexical(){
		for (Map.Entry<Integer,HashMap<Integer, ArrayList<String>>> pair : matrix.entrySet()) {
		    for(Map.Entry<Integer, ArrayList<String>> pair2: matrix.get(pair.getKey()).entrySet()){

				
			    System.out.println("linha: " + pair.getKey() +" "+ pair2.getValue() + " "+pair2.getValue());	
		    }
		}

	}
}
