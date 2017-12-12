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
	ArrayList<String> errors;
	
	int lastNumber = 0;
	Regex grammar;
	
	HashMap<Integer, ArrayList<String>> lineMap;
	HashMap<Integer, ArrayList<String>> tokenMap;
	String pathInput;
	String pathStorage;
	
	public Syntax(String pathInput, String pathStorage){
		lineMap = new HashMap<Integer, ArrayList<String>>();
		tokenMap = new HashMap<Integer, ArrayList<String>>();
		lineArchive = new ArrayList<String>();
		grammar = new Regex();
		
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
					System.out.println("linha: " + i + "  dado linha " + lineMap.get(i).get(j).split(":")[1]);
					System.out.println("linha: " + i  + " " + tokenMap.get(i).get(j));
					System.out.println("===============" + lineMap.get(i).get(j).split(":")[1].trim());
					System.out.println(">>>>>>>>>>>> " + lineMap.get(i));
				}
			}
		}
	}
	

	
	public boolean checkGrammar(){
		int i = 1;
		int j = 0;
		int lines = tokenMap.get(i).size();
		errors = new ArrayList<String> ();
		
		if(tokenMap.containsKey(i)) {
			for (int l = 0; l < lines; l++) {
				if (lineMap.get(i).get(j).split(":")[1].trim() == "final") {
					// variavel constante ou objeto
					j++;
					if (grammar.getTipo(lineMap.get(i).get(j).split(":")[1].trim())) {
						//final tipo tratamento constante ; <variavel constante ou objeto>
						j++;
						
					} else {
						errors.add("Erro: falta tipo");
					}
				} else if (lineMap.get(i).get(j).split(":")[1] == "class") {
					// classe 
				}
			}
		}
		return false;	
	}
	
	public boolean varConstObj(int i, int j) {
		if (lineMap.get(i).get(j).split(":")[1].trim() == "final") {
			j++;
			if (grammar.getTipo(lineMap.get(i).get(j).split(":")[1].trim())) {
				j++;
				if (tratamentoConstante(i, j)) {
					j++;
					if(lineMap.get(i).get(j).split(":")[1].trim() == ";") {
						j++;
						varConstObj(i, j);
					} else return false;					
				} else return false;
			} else return false;
		} else if (grammar.getTipo(lineMap.get(i).get(j).split(":")[1].trim())){
			//tratamento variavel
			j++;
			if(tratamentoVariavel(i, j)) return true;
		} else if(criarObjetosLinha(i, j)) {
			j++;
			if(varConstObj(i, j)) return true;
		} else if(tokenMap.get(i).get(j).isEmpty()) return true;
		return false;
	}
	
	
	public boolean criarObjetosLinha(int i, int j) {
		if (tokenMap.get(i).get(j) == "Identificador") {
			j++;
			if (tokenMap.get(i).get(j) == "Identificador") {
				j++;
				if (variosObjetos(i, j)) {
					j++;
					if(lineMap.get(i).get(j).split(":")[1].trim() == ";") return true;
				}
			}
		}
		return false;
	}
	
	public boolean variosObjetos(int i, int j) {
		if(lineMap.get(i).get(j).split(":")[1].trim() == ",") {
			j++;
			if (tokenMap.get(i).get(j) == "Identificador") {
				j++;
				if(variosObjetos(i, j)) return true;
			}
		} else if (tokenMap.get(i).get(j).isEmpty()) return true;
		return false;
	}
	
	public boolean tratamentoVariavel(int i, int j) {
		if(variaveis(i, j)) {
			j++;
			if(lineMap.get(i).get(j).split(":")[1].trim() == ";") {
				j++;
				if(varConstObj(i, j)) return true;
			}
		}
		return false;
	}
	
	public boolean variaveis(int i, int j){
		if (tokenMap.get(i).get(j) == "Identificador") {
			j++;
			if(fatoracaoVariaveis(i, j)) return true;
		} return false;
	}
	
	public boolean fatoracaoVariaveis(int i, int j) {
		if(acrescentar(i, j)) return true;
		else if(lineMap.get(i).get(j).split(":")[1].trim() == "[") {
			j++;
			if(tokenMap.get(i).get(j) == "Numero") {
				j++;
				if (lineMap.get(i).get(j).split(":")[1].trim() == "]") {
					j++;
					if (fatoracaoFatoracaoVariaveis(i, j)) return true;
				}
			}
		}
		return false;
	}
	
	public boolean fatoracaoFatoracaoVariaveis(int i, int j) {
		if (acrescentar(i, j)) return true;
		else if(lineMap.get(i).get(j).split(":")[1].trim() == "[") {
			j++;
			if(tokenMap.get(i).get(j) == "Numero") {
				j++;
				if (lineMap.get(i).get(j).split(":")[1].trim() == "]") {
					j++;
					if (acrescentar(i, j)) return true;
				}
			}
		}
		return false;
	}
	
	public boolean acrescentar(int i, int j) {
		if (lineMap.get(i).get(j).split(":")[1].trim() == ",") {
			j++;
			if(variaveis(i, j)) return true;
		} else if(tokenMap.get(i).get(j).isEmpty()) return true;
		return false;
	}
	
	public boolean tratamentoConstante(int i, int j) {
		if (tokenMap.get(i).get(j) == "Identificador") {
			//tratamento constante
			j++;
			if (lineMap.get(i).get(j).split(":")[1].trim() == "=") {
				//Numero <gerador constante>
				j++;
				if (tokenMap.get(i).get(j) == "Numero") {
					//<gerador constante>
					j++;
					geradorConstante(i, j);
					return true;
				} else return false;
			} else return false;			
		} else return false;
	}
	
	public boolean geradorConstante(int i, int j) {
		if (lineMap.get(i).get(j).split(":")[1].trim() == ",") {
			j++;
			tratamentoConstante(i, j);
		} else if(tokenMap.get(i).get(j).isEmpty()) return true;
		return false;
	}
	
}
