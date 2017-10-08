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
		for(int linha = 0; linha < lineArchive.size(); linha++){
			if(!"".equals(lineArchive.get(linha).trim())) {
				String[] a = lineArchive.get(linha).split(" ");	
				for(int i = 0; i < a.length; i++) {
					if (a[i].contains("/**/")){
						System.out.println(linha + " Erro de comentario malformado");
						words.add("<ERRO, >" + " Comentario de blocos malformado " + "Linha: " + linha);
						break;
					}
					if (a[i].contains("/*\"*/")) {
						System.out.println(linha + " Erro de cadeia de caracteres malformado");
						words.add("<ERRO, > Cadeia de Caracteres malformado Linha: " + linha);
						a[i] = "";
					}
					if (a[i].contains("\"")) {						
						String r = "\\x22([\\x20-\\x21\\x23-\\x7E]|\\\")*\\x22";
						String aux = a[i];
						if(a[i].contains("\\\"")) a[i] = a[i].replace("\\\"", "");
						if(a[i].lastIndexOf("\"") > a[i].indexOf("\"")){
							String m = a[i].substring(a[i].indexOf("\""), a[i].lastIndexOf("\"")+1);
							if(a[i].charAt(a[i].lastIndexOf("\"")-1) == '\\') {
								words.add("<ERRO, > Cadeia de Caracteres malformado: \\\" Linha: " + linha);
								a[i] = a[i].replaceAll(r, "");
							}
							if (m.matches(r)){
								words.add("<caracteres, >" + " Cadeia de Caracteres: " + aux + " Linha: " + linha);
								a[i] = a[i].replaceAll(r, "");
								System.out.println(linha + " Cadeia de Caracteres: " + m);
							} else {
								words.add("<ERRO, > " + "Cadeia de Caracteres malformado Linha: " + linha);
								a[i] = a[i].replaceAll(r, "");
							}
						} else {
							System.out.println(linha + " Erro de cadeia de caracteres malformado");
							words.add("<ERRO, > Cadeia de Caracteres malformado: nao fechado Linha: " + linha);
							a[i] = a[i].replaceAll(r, "");
						}
					}
					
					if(a[i].indexOf(";") >= 0) {
						System.out.println(linha + " Delimitador: ;");
						words.add("<delimitador, >" + " Delimitador: ;" + " Linha: " + linha);
						a[i] = a[i].replace(";", " ");
					} if(a[i].indexOf(",") >= 0) {
						System.out.println(linha + " Delimitador: ,");
						words.add("<delimitador, >" + " Delimitador: ," + " Linha: " + linha);
						a[i] = a[i].replace(",", " ");
					} if(a[i].indexOf("(") >= 0) {
						System.out.println(linha + " Delimitador: (");
						words.add("<delimitador, >" + " Delimitador: (" + " Linha: " + linha);
						a[i] = a[i].replace("(", " ");
					} if(a[i].indexOf(")") >= 0) {
						System.out.println(linha + " Delimitador: )");
						words.add("<delimitador, >" + " Delimitador: )" + " Linha: " + linha);
						a[i] = a[i].replace(")", " ");
					} if(a[i].indexOf("[") >= 0) {
						System.out.println(linha + " Delimitador: [");
						words.add("<delimitador, >" + " Delimitador: [" + " Linha: " + linha);
						a[i] = a[i].replace("[", " ");
					} if(a[i].indexOf("]") >= 0) {
						System.out.println(linha + " Delimitador: ]");
						words.add("<delimitador, >" + " Delimitador: ]" + " Linha: " + linha);
						a[i] = a[i].replace("]", " ");
					} if(a[i].indexOf("{") >= 0) {
						System.out.println(linha + " Delimitador: {");
						words.add("<delimitador, >" + " Delimitador: {" + " Linha: " + linha);
						a[i] = a[i].replace("{", " ");
					} if(a[i].indexOf("}") >= 0) {
						System.out.println(linha + " Delimitador: }");
						words.add("<delimitador, >" + " Delimitador: }" + " Linha: " + linha);
						a[i] = a[i].replace("}", " ");
					}
					
					String sequence = a[i];
					if(regex.isPalavrasReservadas(sequence)) {
						words = howEstructure(words, linha, sequence);
					} else if (regex.isNumero(sequence)) {
						words = howEstructure(words, linha, sequence);
					} /*else if (regex.isDigito(sequence)) {
						System.out.println(linha + " Digito: " + sequence);
						words.add("<digito, >" + " digito: " + sequence + " Linha: " + linha);
					}*/ else if (regex.isOpAritmeticos(sequence)) {
						words = howEstructure(words, linha, sequence);
					} else if (regex.isOpLogicos(sequence)) {
						words = howEstructure(words, linha, sequence);
					} else if (regex.isOpRelacionais(sequence)) {
						words = howEstructure(words, linha, sequence);
					} else if (regex.isIdentificador(sequence)) {
						if(regex.hasErrorId(sequence) || !regex.isLetra(sequence.substring(0, 1))) {
								System.out.println(linha + " Identificador Malformado: " + sequence);
								words.add("<ERRO, >" + "Identificador Malformado: " + sequence + " Linha: " + linha);
							}else {
								System.out.println(linha + " Identificador: " + sequence);
								words.add("<identificador, >" + " Identificador: " + sequence + " Linha: " + linha);
							}
						}
				}
				
			}			
		}
	}
	
	public ArrayList<String> howEstructure(ArrayList<String> words, int linha, String sequence){		
		if(regex.isPalavrasReservadas(sequence)) words = haveReservedWord(words, linha, sequence);
		if(regex.isOpAritmeticos(sequence)) words = haveAritmethic(words, linha, sequence);
		if(regex.isOpLogicos(sequence.substring(0))) words = haveLogics(words, linha, sequence);
		if(regex.isOpRelacionais(sequence.substring(0))) words = haveRelational(words, linha, sequence);
		if(regex.isNumero(sequence)) words = haveNumber(words, linha, sequence);	
		return words;
	}
	
	private ArrayList<String> haveReservedWord(ArrayList<String> words,
			int linha, String sequence) {
		String parts[] = sequence.split(" ");
		for(int x = 0; x < parts.length; x++){
			if("class".equals("" + parts[x]) ){
				System.out.println(linha + " Palavra reservada: " + parts[x]);
				words.add("<" + parts[x] + ", >" + " Palavra reservada: " + " Linha: " + linha);
			} else if("final".equals("" + parts[x])){
				System.out.println(linha + " Palavra reservada: " + parts[x]);
				words.add("<" + parts[x] + ", >" + " Palavra reservada: " + " Linha: " + linha);
			}else if("if".equals("" + parts[x])){
				System.out.println(linha + " Palavra reservada: " + parts[x]);
				words.add("<" + parts[x] + ", >" + " Palavra reservada: " + " Linha: " + linha);
			} else if("else".equals("" + parts[x])){
				System.out.println(linha + " Palavra reservada: " + parts[x]);
				words.add("<" + parts[x] + ", >" + " Palavra reservada: " + " Linha: " + linha);
			}else if("for".equals("" + parts[x])){
				System.out.println(linha + " Palavra reservada: " + parts[x]);
				words.add("<" + parts[x] + ", >" + " Palavra reservada: " + " Linha: " + linha);
			}else if("scan".equals("" + parts[x])){
				System.out.println(linha + " Palavra reservada: " + parts[x]);
				words.add("<" + parts[x] + ", >" + " Palavra reservada: " + " Linha: " + linha);
			}else if("print".equals("" + parts[x])){
				System.out.println(linha + " Palavra reservada: " + parts[x]);
				words.add("<" + parts[x] + ", >" + " Palavra reservada: " + " Linha: " + linha);
			} else if("int".equals("" + parts[x])){
				System.out.println(linha + " Palavra reservada: " + parts[x]);
				words.add("<" + parts[x] + ", >" + " Palavra reservada: " + " Linha: " + linha);
			}else if("float".equals("" + parts[x])){
				System.out.println(linha + " Palavra reservada: " + parts[x]);
				words.add("<" + parts[x] + ", >" + " Palavra reservada: " + " Linha: " + linha);
			}else if("bool".equals("" + parts[x])){
				System.out.println(linha + " Palavra reservada: " + parts[x]);
				words.add("<" + parts[x] + ", >" + " Palavra reservada: " + " Linha: " + linha);
			} else if("false".equals("" + parts[x])){
				System.out.println(linha + " Palavra reservada: " + parts[x]);
				words.add("<" + parts[x] + ", >" + " Palavra reservada: " + " Linha: " + linha);
			}else if("true".equals("" + parts[x])){
				System.out.println(linha + " Palavra reservada: " + parts[x]);
				words.add("<" + parts[x] + ", >" + " Palavra reservada: " + " Linha: " + linha);
			}else if("string".equals("" + parts[x])){
				System.out.println(linha + " Palavra reservada: " + parts[x]);
				words.add("<" + parts[x] + ", >" + " Palavra reservada: " + " Linha: " + linha);
			}		
		}
		return words;
	}

	private ArrayList<String> haveNumber(ArrayList<String> words, int linha,
			String sequence) {
		for(int x = 0; x < sequence.length(); x++){
			if("0".equals("" + sequence.charAt(x)) ){
				System.out.println(linha + " Operador Numero: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Numero: " + " Linha: " + linha);
			} else if("1".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Numero: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Numero: " + " Linha: " + linha);
			}else if("2".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Numero: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Numero: " + " Linha: " + linha);
			} else if("3".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Numero: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Numero: " + " Linha: " + linha);
			}else if("4".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Numero: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Numero: " + " Linha: " + linha);
			}else if("5".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Numero: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Numero: " + " Linha: " + linha);
			}else if("6".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Numero: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Numero: " + " Linha: " + linha);
			} else if("7".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Numero: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Numero: " + " Linha: " + linha);
			}else if("8".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Numero: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Numero: " + " Linha: " + linha);
			}else if("9".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Numero: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Numero: " + " Linha: " + linha);
			}
		
		}
		return words;
	}

	private ArrayList<String> haveRelational(ArrayList<String> words,
			int linha, String sequence) {
		for(int x = 0; x < sequence.length(); x++){
			if("!=".equals("" + sequence.charAt(x)) ){
				System.out.println(linha + " Operador Relacional: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Relacional: " +" Linha: " + linha);
			} else if("=".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Relacional: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Relacional: " +" Linha: " + linha);
			}else if("<".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Relacional: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Relacional: " +" Linha: " + linha);
			} else if("<=".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Relacional: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Relacional: " +" Linha: " + linha);
			}else if(">".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Relacional: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Relacional: " +" Linha: " + linha);
			}else if(">=".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Relacional: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Relacional: " +" Linha: " + linha);
			}
		
		}
		return words;
	}

	private ArrayList<String> haveLogics(ArrayList<String> words, int linha,
			String sequence) {
		for(int x = 0; x < sequence.length(); x++){
			if("!".equals("" + sequence.charAt(x)) ){
				System.out.println(linha + " Operador Logico: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Logico: " +" Linha: " + linha);
			} else if("\\|{2}".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Logico: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Logico: " +" Linha: " + linha);
			}else if("&&".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Logico: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Operador Logico: " +" Linha: " + linha);
			}
		
		}
		return words;
	}

	public ArrayList<String> haveAritmethic(ArrayList<String> words, int linha, String sequence){
		for(int x = 0; x < sequence.length(); x++){
			if("-".equals("" + sequence.charAt(x)) ){
				System.out.println(linha + " Operador Aritmetico: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Linha: " + linha);
			} else if("+".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Aritmetico: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Linha: " + linha);
			}else if("/".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Aritmetico: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Linha: " + linha);
			}else if("*".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Aritmetico: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Linha: " + linha);
			}else if("%".equals("" + sequence.charAt(x))){
				System.out.println(linha + " Operador Aritmetico: " + sequence.charAt(x));
				words.add("<" + sequence.charAt(x) + ", >" + " Linha: " + linha);
			}
		
		}
		return words;
	}
	
	public void writeLexical(){
		System.out.println(matrix);
		for (Map.Entry<Integer,HashMap<Integer, ArrayList<String>>> pair : matrix.entrySet()) {
		    for(Map.Entry<Integer, ArrayList<String>> pair2: matrix.get(pair.getKey()).entrySet()){		
			   
		    	//System.out.println("for 2 linha: " + pair.getKey() + " "+ pair2.getValue() + " " +pair2.getValue());	
		    }
		}

	}
}
