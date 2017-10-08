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
 * @author Felipe Cordeiro
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
		    int lineError = 0;
		    boolean looking = false;
			line = file.readLine();
			while (line != null) {
				if (line.contains("//")) { //verificando comentario de linha
					//adiciona no array a linha contendo da primeira posicao ate a posicao inicial do //
					line = line.substring(0, line.indexOf("//"));
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
				lineArchive.add(numberLine, line);
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
			System.out.println(i + " = " + lineArchive.get(i));
		}
	}
	
	public void parser(){
		ArrayList<String> words = new ArrayList<String>();
		boolean notFind = true;
		int count;
		for(int linha = 0; linha < lineArchive.size(); linha++){
			if(!"".equals(lineArchive.get(linha).trim())) {
				String aux2;
				if(lineArchive.get(linha).contains("\"")){
					int beginIndex = lineArchive.get(linha).indexOf("\""), endIndex = lineArchive.get(linha).lastIndexOf("\"");
					aux2 = lineArchive.get(linha).substring(beginIndex, endIndex);
				}
				String sequence = lineArchive.get(linha);
				if (sequence.contains("/**/")){
					System.out.println(linha + " Erro de comentario malformado");
					words.add("<ERRO, >" + " Comentario de blocos malformado " + "Linha: " + linha);
					break;
				}
				if (sequence.contains("/*\"*/")) {
					System.out.println(linha + " Erro de cadeia de caracteres malformado" + ": " + sequence);
					words.add("<ERRO, > Cadeia de Caracteres malformado Linha: " + linha);
					sequence = "";
				}
				if (sequence.contains("\"")) {						
					String r = "\\x22([\\x20-\\x21\\x23-\\x7E]|\\\")*\\x22";
					String aux = sequence;					
					if(sequence.contains("\\\"")) sequence = sequence.replace("\\\"", "");
					count = sequence.length()-sequence.replace("\"", "").length(); //qtd de "
					String[] cadeia;
					if(count%2 != 0) {
						words.add("<ERRO, > Cadeia de Caracteres malformado Linha: " + linha);
						sequence = sequence.replaceAll("\"", "");
					} else if(count%2 == 0 && count > 2){
						cadeia = sequence.split("\"");
						for(int j = 0; j < cadeia.length; j++) { //o split vai quebrar a string criando strings vazias nas posicoes das aspas
							if(j%2!=0){
								cadeia[j] = "\"" + cadeia[j] + "\""; //Readicionando as aspas									
								words.add("<caracteres, > " + "Cadeia de Caracteres: " + cadeia[j] + " Linha: " + linha);
								System.out.println(linha + " Cadeia de Caracteres: " + cadeia[j]);
							}							
						}
						notFind = false;
						sequence = sequence.replaceAll("\".*\"", "");
					}
					if(sequence.lastIndexOf("\"") > sequence.indexOf("\"")){
						String m = sequence.substring(sequence.indexOf("\""), sequence.lastIndexOf("\"")+1);
						if(sequence.charAt(sequence.lastIndexOf("\"")-1) == '\\') {
							words.add("<ERRO, > Cadeia de Caracteres malformado: \\\" Linha: " + linha);
							sequence = sequence.replaceAll(r, "");
						}else if (m.matches(r)){
							words.add("<caracteres, >" + " Cadeia de Caracteres: " + aux + " Linha: " + linha);
							sequence = sequence.replaceAll(r, "");
							System.out.println(linha + " Cadeia de Caracteres: " + m);
						} else {
							words.add("<ERRO, > " + "Cadeia de Caracteres malformado Linha: " + linha);
							sequence = sequence.replaceAll(r, "");
						}
					} else if(sequence.lastIndexOf("\"") <= sequence.indexOf("\"") && notFind) {
						System.out.println(linha + " Erro de cadeia de caracteres malformado" + ": " + sequence);
						words.add("<ERRO, > Cadeia de Caracteres malformado: nao fechado Linha: " + linha);
						sequence = sequence.replaceAll(r, "");
					}
				}
				if(sequence.indexOf(";") >= 0) {
					count = sequence.length()-sequence.replace(";", "").length();
					System.out.println(linha + " Delimitador: ;");
					for(int i = 0; i < count; i++) words.add("<delimitador, >" + " Delimitador: ;" + " Linha: " + linha);						
					sequence = sequence.replaceAll(";", " ");
				} if(sequence.indexOf(",") >= 0) {
					count = sequence.length()-sequence.replace(",", "").length();
					System.out.println(linha + " Delimitador: ,");
					for(int i = 0; i < count; i++) words.add("<delimitador, >" + " Delimitador: ," + " Linha: " + linha);
					sequence = sequence.replaceAll(",", " ");
				} if(sequence.indexOf("(") >= 0) {
					count = sequence.length()-sequence.replace("(", "").length();
					System.out.println(linha + " Delimitador: (");
					for(int i = 0; i < count; i++) words.add("<delimitador, >" + " Delimitador: (" + " Linha: " + linha);
					sequence = sequence.replaceAll("\\(", " ");
				} if(sequence.indexOf(")") >= 0) {
					count = sequence.length()-sequence.replace(")", "").length();
					System.out.println(linha + " Delimitador: )");
					for(int i = 0; i < count; i++) words.add("<delimitador, >" + " Delimitador: )" + " Linha: " + linha);
					sequence = sequence.replaceAll("\\)", " ");
				} if(sequence.indexOf("[") >= 0) {
					count = sequence.length()-sequence.replace("[", "").length();
					System.out.println(linha + " Delimitador: [");
					for(int i = 0; i < count; i++) words.add("<delimitador, >" + " Delimitador: [" + " Linha: " + linha);
					sequence = sequence.replaceAll("[", " ");
				} if(sequence.indexOf("]") >= 0) {
					count = sequence.length()-sequence.replace("]", "").length();
					System.out.println(linha + " Delimitador: ]");
					for(int i = 0; i < count; i++) words.add("<delimitador, >" + " Delimitador: ]" + " Linha: " + linha);
					sequence = sequence.replaceAll("]", " ");
				} if(sequence.indexOf("{") >= 0) {
					count = sequence.length()-sequence.replace("{", "").length();
					System.out.println(linha + " Delimitador: {");
					for(int i = 0; i < count; i++) words.add("<delimitador, >" + " Delimitador: {" + " Linha: " + linha);
					sequence = sequence.replaceAll("\\{", " ");
				} if(sequence.indexOf("}") >= 0) {
					count = sequence.length()-sequence.replace("}", "").length();
					System.out.println(linha + " Delimitador: }");
					for(int i = 0; i < count; i++) words.add("<delimitador, >" + " Delimitador: }" + " Linha: " + linha);
					sequence = sequence.replaceAll("\\}", " ");
				}
				// Segunda parte 
				// relacional
				if(sequence.indexOf("!=") >= 0){
					count = sequence.length()-sequence.replace("!=", "").length();
					System.out.println(linha + " Operador Relacional: !=");
					for(int i = 0; i < count; i++) words.add("<!=, >" + " Operador Relacional: !=" + " Linha: " + linha);
					sequence = sequence.replaceAll("!=", " ");
				}
				if(sequence.indexOf("=") >= 0){
					count = sequence.length()-sequence.replace("=", "").length();
					System.out.println(linha + " Operador Relacional: =");
					for(int i = 0; i < count; i++) words.add("<=, >" + " Operador Relacional: =" + " Linha: " + linha);
					sequence = sequence.replaceAll("=", " ");
				}
				if(sequence.indexOf("<") >= 0){
					count = sequence.length()-sequence.replace("<", "").length();
					System.out.println(linha + " Operador Relacional: <");
					for(int i = 0; i < count; i++) words.add("<<, >" + " Operador Relacional: <" + " Linha: " + linha);
					sequence = sequence.replaceAll("<", " ");
				}
				if(sequence.indexOf(">") >= 0){
					count = sequence.length()-sequence.replace(">", "").length();
					System.out.println(linha + " Operador Relacional: >");
					for(int i = 0; i < count; i++) words.add("<>, >" + " Operador Relacional: " + " Linha: " + linha);
					sequence = sequence.replaceAll(">", " ");
				}
				if(sequence.indexOf("<=") >= 0){
					count = sequence.length()-sequence.replace("<=", "").length();
					System.out.println(linha + " Operador Relacional: <=");
					for(int i = 0; i < count; i++) words.add("<<=, >" + " Operador Relacional: <=" + " Linha: " + linha);
					sequence = sequence.replaceAll("<=", " ");
				}
				if(sequence.indexOf(">=") >= 0){
					count = sequence.length()-sequence.replace(">=", "").length();
					System.out.println(linha + " Operador Relacional: >=");
					for(int i = 0; i < count; i++) words.add("<>=, >" + " Operador Relacional: >=" + " Linha: " + linha);
					sequence = sequence.replaceAll(">=", " ");
				}
				//logico
				if(sequence.indexOf("!") >= 0){
					count = sequence.length()-sequence.replace("!", "").length();
					System.out.println(linha + " Operador Logico: !");
					for(int i = 0; i < count; i++) words.add("<!, >" + " Operador Logico: !" + " Linha: " + linha);
					sequence = sequence.replaceAll("!", " ");
				}
				if(sequence.indexOf("&&") >= 0){
					count = sequence.length()-sequence.replace("&&", "").length();
					System.out.println(linha + " Operador Logico: &&");
					for(int i = 0; i < count; i++) words.add("<&&, >" + " Operador Logico: &&" + " Linha: " + linha);
					sequence = sequence.replaceAll("&&", " ");
				}
				if(sequence.indexOf("||") >= 0){
					count = sequence.length()-sequence.replace("||", "").length();
					System.out.println(linha + " Operador Logico: ||");
					for(int i = 0; i < count; i++) words.add("<||, >" + " Operador Logico: ||" + " Linha: " + linha);
					sequence = sequence.replaceAll("||", " ");
				}
				//aritmeticos
				if(sequence.indexOf("+") >= 0){
					count = sequence.length()-sequence.replace("+", "").length();
					System.out.println(linha + " Operador Aritmeticos: +");
					for(int i = 0; i < count; i++) words.add("<+, >" + " Operador Aritmeticos: +" + " Linha: " + linha);
					sequence = sequence.replaceAll("\\+", " ");
				}
				if(sequence.indexOf("*") >= 0){
					count = sequence.length()-sequence.replace("*", "").length();
					System.out.println(linha + " Operador Aritmeticos: *");
					for(int i = 0; i < count; i++) words.add("<*, >" + " Operador Aritmeticos: *" + " Linha: " + linha);
					sequence = sequence.replaceAll("\\*", " ");
				}
				if(sequence.indexOf("/") >= 0){
					count = sequence.length()-sequence.replace("/", "").length();
					System.out.println(linha + " Operador Aritmeticos: /");
					for(int i = 0; i < count; i++) words.add("</, >" + " Operador Aritmeticos: /" + " Linha: " + linha);
					sequence = sequence.replaceAll("/", " ");
				}if(sequence.indexOf("%") >= 0){
					count = sequence.length()-sequence.replace("%", "").length();
					System.out.println(linha + " Operador Aritmeticos: %");
					for(int i = 0; i < count; i++) words.add("<%, >" + " Operador Aritmeticos: %" + " Linha: " + linha);
					sequence = sequence.replaceAll("&", " ");
				}
				if(sequence.indexOf("-") >= 0){					
					String[] r;
					r = sequence.split("-");
					for(int i = 0; i < r.length; i++){
						if(!r[i].isEmpty()) {
							r[i] = "-" + r[i];
							int countDots = r[i].length()-r[i].replace(".", "").length();
							if(countDots>1){
								words.add("<ERRO, > Numero malformado: " + r[i] + " Linha: " + linha);
								System.out.println(linha + " ERRO numero malformado " + sequence);
								sequence = sequence.replace(r[i].substring(1), "");
							}
							else if(countDots == 1) {
								if(!"".equals(r[i].charAt(r[i].indexOf(".")+1))){
									words.add("<" + r[i] + ", > Numero Linha: " + linha);
									sequence = sequence.replaceFirst("-?[\\x09|\\x0A|\\x0D|\\x20]*?\\b[0-9]+(\\x2E[0-9]+)?\\b", " ");
									System.out.println(linha + " Numero: " + sequence);
								}else {
									words.add("<ERRO, > Numero malformado Linha: " + linha);
									sequence = sequence.replace(r[i].substring(1), "");
									System.out.println(linha + " ERRO Numero malformado " + sequence);
								}
							} else if(regex.isNumero(r[i])){
								words.add("<" + r[i] + ", > Numero Linha: " + linha);
								sequence = sequence.replaceFirst("-?[\\x09|\\x0A|\\x0D|\\x20]*?\\b[0-9]+\\b", " ");
								System.out.println(linha + " Numero: " + r[i]);
							}
						}
					}
					count = sequence.length()-sequence.replace("-", "").length();
					for(int i = 0; i < count; i++) {
						System.out.println(linha + " Operador Aritmetico: -");
						words.add("<-, >" + " Operador Aritmetico: -" + " Linha: " + linha);
					}
					sequence = sequence.replaceAll("-", " ");		
				}
				//palavra reservada
				if(regex.isPalavrasReservadas(sequence)) {
					String[] r = sequence.split(" ");
					for(int i = 0; i < r.length; i++){
						if(r[i].isEmpty()) continue;
						System.out.println(linha + "Palavra Reservada: " + r[i] + " Linha: " + linha);
						words.add("<" + r[i] + ", >" + " Palavra Reservada Linha: " + linha);
						sequence = sequence.replaceFirst(r[i], "");						
					}
				}
				if(regex.isIdentificador(sequence)){
					String[] r = sequence.split(" ");
					for(int i = 0; i < r.length; i++){
						if(r[i].isEmpty()) continue;
						else if(regex.hasErrorId(r[i])){ //contem uma caractere nao aceitavel
							System.out.println(linha + " Identificador malformado: " + r[i]);
							words.add("<ERROR, > Identificador malformado: " + r[i] + " Linha: " + linha);
							sequence = sequence.replaceFirst(r[i], "");
						} else if(!r[i].substring(0, 1).matches("[a-zA-Z]")){ //primeiro digito nao eh uma letra 
							System.out.println(linha + " Identificador malformadoxxxx: " + r[i]);
							words.add("<ERROR, > Identificador malformado: " + r[i] + " Linha: " + linha);
							sequence = sequence.replaceFirst(r[i], "");
						} else {
							System.out.println(linha + " Identificador: " + r[i]);
							words.add("<id, >" + " Identificador: " + r[i] + " Linha: " + linha);
							sequence = sequence.replaceFirst(r[i], "");
						}
					}
				}
				if(regex.isNumero(sequence)){					
					String[] r = sequence.split(" ");		
					for(int i = 0; i < r.length; i++){
						if(!r[i].isEmpty()) {
							int countDots = r[i].length()-r[i].replace(".", "").length();
							if(countDots>1){
								words.add("<ERRO, > Numero malformado: " + r[i] + " Linha: " + linha);
								System.out.println(linha + " ERRO numero malformado " + sequence);
								sequence = sequence.replace(r[i].substring(1), "");
							}
							else if(countDots == 1) {
								if(r[i].indexOf(".")+1 < r[i].length()){
									words.add("<" + r[i] + ", > Numero Linha: " + linha);
									sequence = sequence.replaceFirst("-?[\\x09|\\x0A|\\x0D|\\x20]*?\\b[0-9]+(\\x2E[0-9]+)?\\b", " ");
									System.out.println(linha + " Numero: " + r[i]);
								}else {
									words.add("<ERRO, > Numero malformado Linha: " + linha);
									sequence = sequence.replace(r[i], " ");
									System.out.println(linha + " ERRO Numero malformado");
								}
							}
						}
						else continue;
					}
				}					
			}			
		}
	}

/*	public void writeLexical(){
		System.out.println(matrix);
		for (Map.Entry<Integer,HashMap<Integer, ArrayList<String>>> pair : matrix.entrySet()) {
		    for(Map.Entry<Integer, ArrayList<String>> pair2: matrix.get(pair.getKey()).entrySet()){		
			   
		    	//System.out.println("for 2 linha: " + pair.getKey() + " "+ pair2.getValue() + " " +pair2.getValue());	
		    }
		}

	}*/
}
