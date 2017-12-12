package analysislexical;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * 
 * @author Felipe Cordeiro && Lucas Morais
 *
 */

public class AnalysisLexical {

	ArrayList<String> lineArchive;
	HashMap<Integer, HashMap<Integer, ArrayList<String>>> matrix;//hash para cada linha
	HashMap<Integer, ArrayList<String>> words;
	
	
	String pathInput;
	String pathStorage;
	String nameArchive;
	Regex regex;
	
	public AnalysisLexical(String pathInput, String pathStorage) {
		lineArchive = new ArrayList<String>();
		matrix = new HashMap<Integer, HashMap<Integer,ArrayList<String>>>();
		
		regex = new Regex();
		
		
		this.pathInput = pathInput;
		this.pathStorage = pathStorage;
	}
	
	/**
	 * Leitura do arquivo fonte
	 * @param nameArchive 
	 */
	private void readFiles(String nameArchive){
		int numberLine = 0;
		try {
			FileReader fileRead = new FileReader(pathInput + nameArchive);
		    BufferedReader file = new BufferedReader(fileRead);
		    String line;
		    int lineError = 0;
		    boolean looking = false;
			line = file.readLine();
			while (line != null) {
				if (line.contains("//")) { //verificando comentario de linha
					//adiciona no array a linha contendo da primeira posicao ate a posicao inicial do //
					if(!line.contains("\"w*\\w*\"")){
						
					}else{
						line = line.substring(0, line.indexOf("//"));
					}
				}
				if (line.contains("/*")){
					lineError = numberLine;
					looking = true;
					if (line.contains("*/") && line.indexOf("*/") > line.indexOf("/*")) {
						line = line.substring(0, line.indexOf("/*")) + 
								line.substring(line.indexOf("*/")+2, line.length());
						looking = false;
					} else if (line.contains("*/")) { // Exemplo: */ int a /* nao eh comentario
						lineArchive.add(numberLine, line);
					} else { //o */ nao se encontra na mesma linha, portanto, tem que fazer a leitura ate encontrar o final do comentario ou final do arquivo
						//if (line.indexOf("/*") > 0)	aux.add(line.substring(0, line.indexOf("/*")));
						line = line.substring(0, line.indexOf("/*"));						
					}
				}
				if (looking) {
					if (line.contains("*/")) {
						line = line.substring(line.indexOf("*/")+2, line.length());
						looking = false;
					} else line = "";
				}
				if(!looking && line.contains("\"") && line.lastIndexOf("\"") == line.indexOf("\"")) line = "/*\"*/";				
				lineArchive.add(numberLine, line.trim());
			  	line = file.readLine(); // le da segunda linha ate a ultima linha
			  	numberLine++;
		    }
			fileRead.close();
			if (looking) lineArchive.add(lineError, "/**/"); //adicionando a forma de comentario em bloco no codigo para identificar como comentario de bloco malformado (nao fechado)
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
			//System.out.println(i + " = " + lineArchive.get(i));
		}
	}
	
	public void parser(String nameArchive){
		this.nameArchive = nameArchive;
		
		//Leitura do arquivo fonte
		readFiles(nameArchive);
		ArrayList<String> words = new ArrayList<String>();
		boolean notFind = true;
		int count;
		int initPos = 0;
		int lastPos = 0;
		for(int linha = 0; linha < lineArchive.size(); linha++){
			if(!"".equals(lineArchive.get(linha).trim())) {
				initPos = words.size();
				String sequence = lineArchive.get(linha);
				if (sequence.contains("/**/")){
					//System.out.println(linha + " Erro de comentario mal formado");
					words.add("<-     ERRO      -> Comentario de blocos mal formado "
							+ "                Linha: " + linha);
					break;
				}
				if (sequence.contains("/*\"*/")) {
					//System.out.println(linha + " Erro de cadeia de caracteres mal formado" + ": " + sequence);
					words.add("<-     ERRO      -> Cadeia de Caracteres mal formado "
							+ "                Linha: " + linha);
					sequence = "";
				}
				if (sequence.contains("\"")) {						
					String r = "\\x22([\\x20-\\x21\\x23-\\x7E]|\\\")*\\x22";
					String aux = sequence;					
					boolean quotes = false;
					if(sequence.contains("\\\"")){
						sequence = sequence.replace("\\\"", "");
						quotes = true;
					}
					count = sequence.length()-sequence.replace("\"", "").length(); //qtd de "
					String[] cadeia;
					if(count%2 != 0) {
						words.add("<-     ERRO      -> Cadeia de Caracter mal formado "
								+ "                Linha: " + linha);
						sequence = sequence.replaceAll("\"", "");
					} else if(count%2 == 0 && count > 2){
						cadeia = sequence.split("\"");
						for(int j = 0; j < cadeia.length; j++) { //o split vai quebrar a string criando strings vazias nas posicoes das aspas
							if(j%2!=0){
								cadeia[j] = "\"" + cadeia[j] + "\""; //Readicionando as aspas
								if(regex.isNotSimbolo(cadeia[j])){
									words.add("<-     ERRO      -> Cadeia de Caracter mal formado "
											+ "                Linha: " + linha);
								}
								else {
									words.add("<-  Caracteres   -> Cadeia de Caracteres: " + cadeia[j] + " "
											+ "                Linha: " + linha);
								}								
								//System.out.println(linha + " Cadeia de Caracteres: " + cadeia[j]);
							}							
						}
						notFind = false;
						sequence = sequence.replaceAll("\".*\"", "");
					}
					if(sequence.lastIndexOf("\"") > sequence.indexOf("\"")){
						String m = sequence.substring(sequence.indexOf("\""), sequence.lastIndexOf("\"")+1);
						if(sequence.charAt(sequence.lastIndexOf("\"")-1) == '\\') {
							words.add("<-     ERRO      -> Cadeia de Caracteres mal formado: \\\" "
									+ "                Linha: " + linha);
							sequence = sequence.replaceAll(r, "");
						}else if (m.matches(r)){
							if (quotes) {
								//System.out.println(linha + " Cadeia de Caracteres: " + aux);								
								words.add("<-  Caracteres   -> Cadeia de Caracteres: " + aux + " "
										+ "                            Linha: " + linha);
							}
							else {
								//System.out.println(linha + " Cadeia de Caracteres: " + m);
								words.add("<-  Caracteres   -> Cadeia de Caracteres: " + m + " "
										+ "                            Linha: " + linha);
							}
							sequence = sequence.replaceAll(r, "");
						} else {
							words.add("<-     ERRO      -> Cadeia de Caracteres mal formado Linha: " + linha);
							sequence = sequence.replaceAll(r, "");
						}
					} else if(sequence.lastIndexOf("\"") <= sequence.indexOf("\"") && notFind) {
						//System.out.println(linha + " Erro de cadeia de caracteres mal formado" + ": " + sequence);
						words.add("<-     ERRO      -> Cadeia de Caracteres mal formado: nao fechado, Linha: " + linha);
						sequence = sequence.replaceAll(r, "");
					}
				}
				if(sequence.indexOf(";") >= 0) {
					count = sequence.length()-sequence.replace(";", "").length();
					//System.out.println(linha + " Delimitador: ;");
					for(int i = 0; i < count; i++) words.add("<-  Delimitador  ->" + " Delimitador: ; " + " "
							+ "                               Linha: " + linha);						
					sequence = sequence.replaceAll(";", " ");
				} if(sequence.indexOf(",") >= 0) {
					count = sequence.length()-sequence.replace(",", "").length();
					//System.out.println(linha + " Delimitador: ,");
					for(int i = 0; i < count; i++) words.add("<-  Delimitador  ->" + " Delimitador: , " + " "
							+ "                               Linha: " + linha);
					sequence = sequence.replaceAll(",", " ");
				} if(sequence.indexOf("(") >= 0) {
					count = sequence.length()-sequence.replace("(", "").length();
					//System.out.println(linha + " Delimitador: (");
					for(int i = 0; i < count; i++) words.add("<-  Delimitador  ->" + " Delimitador: ( " + " "
							+ "                               Linha: " + linha);
					sequence = sequence.replaceAll("\\(", " ");
				} if(sequence.indexOf(")") >= 0) {
					count = sequence.length()-sequence.replace(")", "").length();
					//System.out.println(linha + " Delimitador: )");
					for(int i = 0; i < count; i++) words.add("<-  Delimitador  ->" + " Delimitador: ) " + " "
							+ "                               Linha: " + linha);
					sequence = sequence.replaceAll("\\)", " ");
				} if(sequence.indexOf("[") >= 0) {
					count = sequence.length()-sequence.replace("[", "").length();
					//System.out.println(linha + " Delimitador: [");
					for(int i = 0; i < count; i++) words.add("<-  Delimitador  ->" + " Delimitador: [ " + " "
							+ "                               Linha: " + linha);
					sequence = sequence.replaceAll("\\[", " ");
				} if(sequence.indexOf("]") >= 0) {
					count = sequence.length()-sequence.replace("]", "").length();
					//System.out.println(linha + " Delimitador: ]");
					for(int i = 0; i < count; i++) words.add("<-  Delimitador  ->" + " Delimitador: ] " + " "
							+ "                               Linha: " + linha);
					sequence = sequence.replaceAll("]", " ");
				} if(sequence.indexOf("{") >= 0) {
					count = sequence.length()-sequence.replace("{", "").length();
					//System.out.println(linha + " Delimitador: {");
					for(int i = 0; i < count; i++) words.add("<-  Delimitador  ->" + " Delimitador: { " + " "
							+ "                               Linha: " + linha);
					sequence = sequence.replaceAll("\\{", " ");
				} if(sequence.indexOf("}") >= 0) {
					count = sequence.length()-sequence.replace("}", "").length();
					//System.out.println(linha + " Delimitador: }");
					for(int i = 0; i < count; i++) words.add("<-  Delimitador  ->" + " Delimitador: } " + " "
							+ "                               Linha: " + linha);
					sequence = sequence.replaceAll("\\}", " ");
				}
				// Segunda parte 
				// relacional
				if(sequence.indexOf("!=") >= 0){
					count = sequence.length()-sequence.replace("!=", "").length();
					//System.out.println(linha + " Operador Relacional: !=");
					for(int i = 0; i < count; i++) words.add("<- Op Relacional -> " + "Operador Relacional: !=" + " "
							+ "                          Linha: " + linha);
					sequence = sequence.replaceAll("!=", " ");
				}
				if(sequence.indexOf("=") >= 0){
					count = sequence.length()-sequence.replace("=", "").length();
					//System.out.println(linha + " Operador Relacional: =");
					for(int i = 0; i < count; i++) words.add("<- Op Relacional -> " + "Operador Relacional: = " + " "
							+ "                          Linha: " + linha);
					sequence = sequence.replaceAll("=", " ");
				}
				if(sequence.indexOf("<") >= 0){
					count = sequence.length()-sequence.replace("<", "").length();
					//System.out.println(linha + " Operador Relacional: <");
					for(int i = 0; i < count; i++) words.add("<- Op Relacional -> " + "Operador Relacional: < " + " "
							+ "                          Linha: " + linha);
					sequence = sequence.replaceAll("<", " ");
				}
				if(sequence.indexOf(">") >= 0){
					count = sequence.length()-sequence.replace(">", "").length();
					//System.out.println(linha + " Operador Relacional: >");
					for(int i = 0; i < count; i++) words.add("<- Op Relacional -> " + "Operador Relacional: > " + " "
							+ "                          Linha: " + linha);
					sequence = sequence.replaceAll(">", " ");
				}
				if(sequence.indexOf("<=") >= 0){
					count = sequence.length()-sequence.replace("<=", "").length();
					//System.out.println(linha + " Operador Relacional: <=");
					for(int i = 0; i < count; i++) words.add("<- Op Relacional -> " + "Operador Relacional: <= " + " "
							+ "                          Linha: " + linha);
					sequence = sequence.replaceAll("<=", " ");
				}
				if(sequence.indexOf(">=") >= 0){
					count = sequence.length()-sequence.replace(">=", "").length();
					//System.out.println(linha + " Operador Relacional: >=");
					for(int i = 0; i < count; i++) words.add("<- Op Relacional -> " + "Operador Relacional: >= " + " "
							+ "                          Linha: " + linha);
					sequence = sequence.replaceAll(">=", " ");
				}
				//logico
				if(sequence.indexOf("!") >= 0){
					count = sequence.length()-sequence.replace("!", "").length();
					//System.out.println(linha + " Operador Logico: !");
					for(int i = 0; i < count; i++) words.add("<-  Op Logico  -> " + "Operador Logico: ! " + " "
							+ "                          Linha: " + linha);
					sequence = sequence.replaceAll("!", " ");
				}
				if(sequence.indexOf("&&") >= 0){
					count = sequence.length()-sequence.replace("&&", "").length();
					//System.out.println(linha + " Operador Logico: &&");
					for(int i = 0; i < count; i++) words.add("<-  Op Logico  -> " + "Operador Logico: && " + " "
							+ "                          Linha: " + linha);
					sequence = sequence.replaceAll("&&", " ");
				}
				if(sequence.indexOf("||") >= 0){
					count = sequence.length()-sequence.replace("||", "").length();
					//System.out.println(linha + " Operador Logico: ||");
					for(int i = 0; i < count; i++) words.add("<-  Op Logico  -> " + "Operador Logico: || " + " "
							+ "                          Linha: " + linha);
					sequence = sequence.replaceAll("||", " ");
				}
				//aritmeticos
				if(sequence.indexOf("+") >= 0){
					count = sequence.length()-sequence.replace("+", "").length();
					//System.out.println(linha + " Operador Aritmeticos: +");
					for(int i = 0; i < count; i++) words.add("<- Op Aritmetico -> " + "Operador Aritmeticos: + " + " "
							+ "                          Linha: " + linha);
					sequence = sequence.replaceAll("\\+", " ");
				}
				if(sequence.indexOf("*") >= 0){
					count = sequence.length()-sequence.replace("*", "").length();
					//System.out.println(linha + " Operador Aritmeticos: *");
					for(int i = 0; i < count; i++) words.add("<- Op Aritmetico -> " + "Operador Aritmeticos: * " + " "
							+ "                          Linha: " + linha);
					sequence = sequence.replaceAll("\\*", " ");
				}
				if(sequence.indexOf("/") >= 0){
					count = sequence.length()-sequence.replace("/", "").length();
					//System.out.println(linha + " Operador Aritmeticos: /");
					for(int i = 0; i < count; i++) words.add("<- Op Aritmetico -> " + "Operador Aritmeticos: / " + " "
							+ "                          Linha: " + linha);
					sequence = sequence.replaceAll("/", " ");
				}if(sequence.indexOf("%") >= 0){
					count = sequence.length()-sequence.replace("%", "").length();
					//System.out.println(linha + " Operador Aritmeticos: %");
					for(int i = 0; i < count; i++) words.add("<- Op Aritmetico -> " + "Operador Aritmeticos: % " + " "
							+ "                          Linha: " + linha);
					sequence = sequence.replaceAll("&", " ");
				}
				if(sequence.indexOf("-") >= 0){			
					String[] r;
					r = sequence.split("-");
					for(int i = 0; i < r.length; i++){
						r[i] = r[i].trim();
						if(regex.isPalavrasReservadas(r[i])){
							String[] aux = r[i].split(" ");
							for(int j = 0; j < aux.length; j++){
								if(aux[j].isEmpty()) continue;
								else if(regex.isPalavrasReservadas(aux[j])){
									//System.out.println(linha + " Palavra Reservada: " + aux[j]);
									words.add("<- Pal Reservada ->" + " Palavra Reservada: " +  aux[j] + " "
											+ "                       Linha: " + linha);
									sequence = sequence.replaceFirst(aux[j], "");
									r[i] = r[i].replaceFirst(aux[j], "");
								} else continue;
							}
						}else if(regex.isIdentificador(r[i])){
							String[] aux = r[i].split(" ");
							for(int j = 0; j < aux.length; j++){
								if(aux[j].isEmpty()) continue;
								else if(regex.isIdentificador(aux[j])){
									//System.out.println(linha + " Identificador: " + aux[j]);
									words.add("<- Identificador ->" + " Identificador: " + aux[j] + " "
											+ "                                    Linha: " + linha);
									sequence = sequence.replaceFirst(aux[j], "");
									r[i] = r[i].replaceFirst(aux[j], "");
								} else continue;
							}
						}if(!r[i].isEmpty()) {
							r[i] = r[i].replace(" ", ""); //removendo o espaco q pode existir entre o - e o primeiro digito
							sequence = sequence.replace(" ", ""); //removendo o espaco q pode existir entre o - e o primeiro digito
							if(i != 0) r[i] = "-" + r[i];
							if(!regex.isDigito(r[i])) {
								words.add("<-     ERRO      -> Numero mal formado: " + r[i] + " "
										+ "                  Linha: " + linha);
								//System.out.println(linha + " ERRO numero mal formado " + sequence);
								sequence = sequence.replace(r[i], " ");								
							} else {								
								int countDots = r[i].length()-r[i].replace(".", "").length();
								if(countDots>1){
									words.add("<-     ERRO      -> Numero mal formado: " + r[i] + " "
											+ "                  Linha: " + linha);
									//System.out.println(linha + " ERRO numero mal formado " + sequence);
									sequence = sequence.replace(r[i].substring(1), "");
								}
								else if(countDots == 1) {
									if(!"".equals(r[i].charAt(r[i].indexOf(".")+1))){
										words.add("<-    Numero     -> Numero " + r[i] + " "
												+ "                                  Linha: " + linha);
										sequence = sequence.replaceFirst("-?[\\x09|\\x0A|\\x0D|\\x20]*?\\b[0-9]+(\\x2E[0-9]+)?\\b", " ");
										//System.out.println(linha + " Numero: " + sequence);
									}else {
										words.add("<-     ERRO      -> Numero mal formado "
												+ "                  Linha: " + linha);
										sequence = sequence.replace(r[i].substring(1), "");
										//System.out.println(linha + " ERRO Numero mal formado " + sequence);
									}
								} else if(regex.isNumero(r[i])){
									words.add("<-    Numero     -> Numero: " + r[i] + " "
											+ "                                   Linha: " + linha);
									sequence = sequence.replaceFirst("-?[\\x09|\\x0A|\\x0D|\\x20]*?\\b[0-9]+\\b", " ");
									//System.out.println(linha + " Numero: " + r[i]);
								}
							}
						}
					}
					count = sequence.length()-sequence.replace("-", "").length();
					for(int i = 0; i < count; i++) {
						//System.out.println(linha + " Operador Aritmetico: -");
						words.add("<- Op Aritmetico -> " + "Operador Aritmetico: - "  + " "
								+ "                       Linha: " + linha);
					}
					sequence = sequence.replaceAll("-", " ");		
				}
				//palavra reservada
				if(regex.isPalavrasReservadas(sequence)) {
					String[] r = sequence.split(" ");
					for(int i = 0; i < r.length; i++){
						if(r[i].isEmpty()) continue;
						else if(regex.isPalavrasReservadas(r[i])){
							//	System.out.println(linha + " Palavra Reservada: " + r[i]);
							words.add("<- Pal Reservada -> " + "Palavra Reservada: " +  r[i] +" "
									+ "                       Linha: " + linha);
							sequence = sequence.replaceFirst(r[i], "");	
						} else continue;				
					}
				}
				if(regex.isNumero(sequence)){				
					String[] r = sequence.split(" ");		
					for(int i = 0; i < r.length; i++){
						if(!r[i].isEmpty()) {
							int countDots = r[i].length()-r[i].replace(".", "").length();
							if(countDots>1){
								words.add("<-     ERRO      -> Numero mal formado: " + r[i] + " "
										+ "                  Linha: " + linha);
								//	System.out.println(linha + " ERRO numero mal formado " + sequence);
								sequence = sequence.replace(r[i].substring(1), "");
							}
							else if(countDots == 1) {
								 if(r[i].indexOf(".") == 0 || regex.isNumero(r[r[i].indexOf(".")-1])){
									 words.add("<-     ERRO      -> Numero mal formado "
												+ "                  Linha: " + linha);
									 sequence = sequence.replace(r[i], " ");
									 // System.out.println(linha + " ERRO Numero mal formado");
								 }
								 else if(r[i].indexOf(".")+1 < r[i].length()){
									words.add("<-    Numero     -> Numero: " + r[i] + " "
											+ "                                   Linha: " + linha);
									sequence = sequence.replaceFirst("-?[\\x09|\\x0A|\\x0D|\\x20]*?\\b[0-9]+(\\x2E[0-9]+)?\\b", " ");
									//System.out.println(linha + " Numero: " + r[i]);
								} else {
									words.add("<-     ERRO      -> Numero mal formado "
											+ "                  Linha: " + linha);
									sequence = sequence.replace(r[i], " ");
									//	System.out.println(linha + " ERRO Numero mal formado");
								}
							} else {
								if (r[i].matches("\\d*")){
									words.add("<-    Numero     -> Numero: " + r[i] + " "
											+ "                                   Linha: " + linha);
									sequence = sequence.replaceFirst(r[i], " ");
									//	System.out.println(linha + " Numero: " + r[i]);
								}
							}
						}
						else continue;
					}
				}		
				if(regex.isIdentificador(sequence)){
					String[] r = sequence.split(" ");
					for(int i = 0; i < r.length; i++){
						if(r[i].isEmpty()) continue;
						else if(regex.hasErrorId(r[i])){ //contem uma caractere nao aceitavel
						//	System.out.println(linha + " Identificador mal formado:" + r[i]);
							words.add("<-     ERRO      -> Identificador mal formado: " + r[i] + " "
									+ "                  Linha: " + linha);
							sequence = sequence.replaceFirst(r[i], "");
						} else if(!r[i].substring(0, 1).matches("[a-zA-Z]")){ //primeiro digito nao eh uma letra 
					//		System.out.println(linha + " Identificador mal formado: " + r[i]);
							words.add("<-     ERRO      -> Identificador mal formado: " + r[i] + " "
									+ "                  Linha: " + linha);
							sequence = sequence.replaceFirst(r[i], "");
						} else {
				//			System.out.println(linha + " Identificador: " + r[i]);
							words.add("<- Identificador ->" + " Identificador: " + r[i] + " "
									+ "                                    Linha: " + linha);
							sequence = sequence.replaceFirst(r[i], "");
						}
					}
				}
				lastPos = words.size();
				order(lineArchive.get(linha), initPos, lastPos, words);
			}	
		}
		writeLexical(words);
		generateSheet(words);
		printLines();
		words.clear();
		lineArchive.clear();
	}

	private void order(String sequenceLine, int initPos, int lastPos,
			ArrayList<String> words) {
		int pos = 0;
		String word = "";
		//System.out.println("init " + initPos + " last " + lastPos);
		String[] aux = new String[100];
		for(int i = initPos; i < lastPos; i++){
			word = words.get(i).split(": ")[1];
			word = word.split("   ")[0];
			word = word.replace(" ", "");
			pos = sequenceLine.indexOf(word);
			//System.out.println(pos + " palavra " + word);
			if(pos == -1){
				
			}else{
				aux[pos] = words.get(i);
			}
			//System.out.println(pos + " substring " + word + " linha " + sequenceLine);
		}
		String aux2[] = new String[lastPos - initPos];
		int index = 0;
		for(int i = 0; i< aux.length; i++){
			if(aux[i] != null){
				aux2[index] = aux[i];
				index++;
				
			}
		}
		index = 0;
		for(int i = initPos; i < lastPos; i++){
			//System.out.println(i + " string " + words.get(i));
			words.set(i, aux2[index]);
			index++;
			//System.out.println(i + " string " + words.get(i));
		}
		
		
	}

	public void writeLexical(ArrayList<String> words){
		try {
			ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();
			  FileOutputStream fos   = new FileOutputStream(pathStorage+ nameArchive +"_Compilado.txt");
		      OutputStreamWriter osw = new OutputStreamWriter(fos);
		      BufferedWriter bw      = new BufferedWriter(osw);   
		     boolean isSuccessful = true;  
		     bw.write("    Padrao      -             Token                      -            Linha\n");
            for(int i = 0; i < words.size(); i++){
          	  //System.out.println(words.get(i));
            	if(words.get(i) != null){
	              String tokens[] = words.get(i).split("Linha:");
	              tokens[1] = tokens[1].replaceAll(" ", ""); 
	              int line = Integer.parseInt(tokens[1]);
	              line += 1;
	          	  bw.write(tokens[0] + "\t " + line  + "\n");
	          	  if(words.get(i).contains("ERRO")){
	          		  isSuccessful = false;
	          	  }
            	}
            }
            if(isSuccessful){
		      bw.write("\n\nSucesso - Compilou sem erros!");	 
		    }
            bw.close();
		    }catch (IOException e) {
		     
		      e.printStackTrace();
		        
		  } // printVariablesToFile
		
	}
	
	public void generateSheet(ArrayList<String> words) {
		
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet worksheet = workbook.createSheet("matching_measures");
			int i = 0;
			boolean isSuccessful = true;  
			Row row = worksheet.createRow(i);
			Cell patternCell = row.createCell(2);
			Cell tokenCell = row.createCell(4);
			Cell lineCell = row.createCell(6);
			patternCell.setCellValue("Padrao");
			tokenCell.setCellValue("Token");
			lineCell.setCellValue("Linha");
			while(i < words.size()) {
				row = worksheet.createRow(i + 1);
				patternCell = row.createCell(2);
				tokenCell = row.createCell(4);
				lineCell = row.createCell(6);
				if(words.get(i) != null){
				String pattern = words.get(i).split("->")[0];
				
				patternCell.setCellValue(pattern + "->");
				String token = words.get(i).replace(pattern+"->", "");
				tokenCell.setCellValue(token.split("Linha:")[0]);
				token = token.split("Linha:")[1];
				lineCell.setCellValue(Integer.parseInt(token.replaceAll(" ", "")) + 1);
				if(words.get(i).contains("ERRO")){
	          		  isSuccessful = false;
	          	  }
				}
				i++;
				
				
			}
			
			if(isSuccessful){
				String message = "Sucesso - Compilou sem erros!";
				row = worksheet.createRow(i+ 2);
				patternCell = row.createCell(2);
				patternCell.setCellValue(message);
				System.out.println(message);
		    }
			
			FileOutputStream output = new FileOutputStream(new File(pathStorage + File.separator + nameArchive+"_Compilado.xls"));
			workbook.write(output);
			output.close();
			workbook.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
