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
					} else {
						line = "";
					}
				}
				lineArchive.add(numberLine, line);
			  	line = file.readLine(); // le da segunda linha até a ultima linha
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
			System.out.println(i+1 + " = " +lineArchive.get(i));
		}
	}
	
	public void parser(){
		ArrayList<String> words = new ArrayList<String>();
		for(int linha = 0; linha < lineArchive.size(); linha++){
			if(!"".equals(lineArchive.get(linha).trim())) {
				String[] a = lineArchive.get(linha).split(" ");				
				for(int i = 0; i < a.length; i++) {
					if (a[i].contains("/**/")){
						words.add("<ERRO, >" + " Comentario de blocos malformado: /* " + "Linha: " + linha);
						break;
					}
					if(a[i].indexOf(";") >= 0) {
						words.add("<delimitador, > " + " Delimitador: ;" + " Linha: " + linha);
						a[i] = a[i].replace(";", " ");
					} if(a[i].indexOf(",") >= 0) {
						words.add("<delimitador, > " + " Delimitador: ," + " Linha: " + linha);
						a[i] = a[i].replace(",", " ");
					} if(a[i].indexOf("(") >= 0) {
						words.add("<delimitador, > " + " Delimitador: (" + " Linha: " + linha);
						a[i] = a[i].replace("(", " ");
					} if(a[i].indexOf(")") >= 0) {
						words.add("<delimitador, > " + " Delimitador: )" + " Linha: " + linha);
						a[i] = a[i].replace(")", " ");
					} if(a[i].indexOf("[") >= 0) {
						words.add("<delimitador, > " + " Delimitador: [" + " Linha: " + linha);
						a[i] = a[i].replace("[", " ");
					} if(a[i].indexOf("]") >= 0) {
						words.add("<delimitador, > " + " Delimitador: ]" + " Linha: " + linha);
						a[i] = a[i].replace("]", " ");
					} if(a[i].indexOf("{") >= 0) {
						words.add("<delimitador, > " + " Delimitador: {" + " Linha: " + linha);
						a[i] = a[i].replace("{", " ");
					} if(a[i].indexOf("}") >= 0) {
						words.add("<delimitador, > " + " Delimitador: }" + " Linha: " + linha);
						a[i] = a[i].replace("}", " ");
					}
					if(regex.isPalavrasReservadas(a[i])) {
						System.out.println(linha + " Reservada: " + a[i]);
						words.add("<" + a[i] + " ,>" + " Palavra Reservada: " + a[i] + " Linha: " + linha);
					} else if (regex.isIdentificador(a[i])) {
						if(regex.hasErrorId(a[i].substring(0)) || regex.isOpAritmeticos(a[i].substring(0)) 
							|| regex.isOpLogicos(a[i].substring(0)) || regex.isOpRelacionais(a[i].substring(0))) {
							System.out.println(linha + " Identificador Malformado: " + a[i]);
							words.add("<ERRO, >" + "Identificador Malformado: " + a[i] + " Linha: " + linha);
						} else {
							System.out.println(linha + " Identificador: " + a[i]);
							words.add("<identificador, >" + " Identificador: " + a[i] + " Linha: " + linha);
						}
					} else if (regex.isNumero(a[i])) {
						System.out.println(linha + " Numero: " + a[i]);
						words.add("<numero, >" + " numero: " + a[i] + " Linha: " + linha);
					}  else if (regex.isOpAritmeticos(a[i])) {
						System.out.println(linha + " Operador Aritmetico: " + a[i]);
						words.add("<" + a[i] + ", >" + " Linha: " + linha);
					} else if (regex.isOpLogicos(a[i])) {
						System.out.println(linha + " Operador Logico: " + a[i]);
						words.add("<" + a[i] + ", >" + " Linha: " + linha);
					} else if (regex.isOpRelacionais(a[i])) {
						System.out.println(linha + " Operador Relacional: " + a[i]);
						words.add("<" + a[i] + ", >" + " Linha: " + linha);
					}
				}
				
			}			
		}
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
