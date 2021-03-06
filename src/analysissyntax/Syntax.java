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
	int checkFirstIndex = 0;
	int checkSecondIndex = 0;
	int numberLines = 0;
	
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
		String numberLine = "";
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
			  	this.numberLines++;
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
		int i = 0;
		int lines = tokenMap.get(1).size();
		errors = new ArrayList<String> ();
		
		if(tokenMap.containsKey(i)) {
			for (int l = 1; l < lines; i++) {
				//inicio
				if(varConstObj(i, checkSecondIndex)) {
					if(!classe(i, checkSecondIndex)) {
						errors.add("Linha: " + i + " Classe mal formada");
					}
				}
				checkSecondIndex = 0;
				checkFirstIndex++;
			}
		}
		if(errors.isEmpty())System.out.println("ANALISADO COM SUCESSO");
		else {
			for(int a = 0; a < errors.size(); a++) {
				System.out.println("ERRORS: " + errors.get(a));
			}
		}
		return false;	
	}
	
	public boolean classe(int i, int j) {
		if(lineMap.get(i).get(j).split(":")[1].trim() == "class") {
			j++;
			checkSecondIndex++;
			if(tokenMap.get(i).get(j) == "Identificador") {
				j++;
				checkSecondIndex++;
				if(heranca(i, j)){
					j++;
					checkSecondIndex++;
					if(lineMap.get(i).get(j).split(":")[1].trim() == "{"){
						for(int chave = 1; chave < tokenMap.get(i).size(); chave++){
							if(varConstObj(i, j)){
								j++;
								checkSecondIndex++;
								if(metodo(i, j)){
									j++;
									checkSecondIndex++;
									if(lineMap.get(checkFirstIndex).get(checkSecondIndex).split(":")[1].trim() == "}"){
										j++;
										checkSecondIndex++;
										if(variasClasses(i, j)) return true;
									}
								}
							}
						}
					}
				}
			}
		}
		errors.add("Linha: " + checkFirstIndex + " Classe mal formada");
		return false;
	}
	
	public boolean variasClasses(int i, int j) {
		if(classe(i, j)){
			j++;
			checkSecondIndex++;
			return true;
		} else if(tokenMap.get(i).get(j).isEmpty()) {
			checkFirstIndex++;
			return true;
		}
		return false;
	}
	
	public boolean metodo(int i, int j) {
		if(lineMap.get(i).get(j).split(" ")[1] == ":") {
			j++;
			checkSecondIndex++;
			if(lineMap.get(i).get(j).split(" ")[1] == ":") {
				j++;
				checkSecondIndex++;
				if(comOuSemRetorno(i, j))return true;
			}
		} else if(tokenMap.get(i).get(j).isEmpty()) {
			checkFirstIndex++;
			return true;
		}
		return false;
	}
	
	public boolean comOuSemRetorno(int i, int j) {
		if(ehMain(i, j)){
			j++;
			checkSecondIndex++;
			if(grammar.getTipo(lineMap.get(i).get(j).split(":")[1].trim())){
				j++;
				checkSecondIndex++;
				if(tokenMap.get(i).get(j) == "Identificador"){
					j++;
					checkSecondIndex++;
					if(lineMap.get(i).get(j).split(":")[1].trim() == "("){
						j++;
						checkSecondIndex++;
						if(parametros(i, j)) {
							j++;
							checkSecondIndex++;
							if(lineMap.get(i).get(j).split(":")[1].trim() == ")"){
								j++;
								checkSecondIndex++;
								if(lineMap.get(i).get(j).split(":")[1].trim() == "{"){
									j++;
									checkSecondIndex++;
									if(ehProgram(i, j)){
										j++;
										checkSecondIndex++;
										if(lineMap.get(i).get(j).split(":")[1].trim() == "}"){
											j++;
											checkSecondIndex++;
											if(metodo(i, j)){
												checkSecondIndex++;
												return true;
											}
										}
									}
								}
							}
						}
					}
				}			
			} else if(tokenMap.get(i).get(j) == "Identificador"){
				j++;
				checkSecondIndex++;
				if(lineMap.get(i).get(j).split(":")[1].trim() == "("){
					j++;
					checkSecondIndex++;
					if(parametros(i, j)) {
						j++;
						checkSecondIndex++;
						if(lineMap.get(i).get(j).split(":")[1].trim() == ")"){
							j++;
							checkSecondIndex++;
							if(lineMap.get(i).get(j).split(":")[1].trim() == "{"){
								j++;
								checkSecondIndex++;
								if(ehProgram(i, j)){
									j++;
									checkSecondIndex++;
									if(lineMap.get(i).get(j).split(":")[1].trim() == "}"){
										j++;
										checkSecondIndex++;
										if(metodo(i, j)){
											checkSecondIndex++;
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean parametros(int i, int j) {
		if(grammar.getTipo(lineMap.get(i).get(j).split(":")[1].trim())){
			j++;
			checkSecondIndex++;
			if(tokenMap.get(i).get(j) == "Identificador"){
				j++;
				checkSecondIndex++;
				if(acrescentarParametros(i, j)){
					checkSecondIndex++;
					return true;
				}
			}
		} else if(tokenMap.get(i).get(j).isEmpty()) {
			checkFirstIndex++;
			return true;
		}
		errors.add("Linha: " + checkFirstIndex + " parametros mal formado");
		return false;
	}
	
	public boolean acrescentarParametros(int i, int j) {
		if(lineMap.get(i).get(j).split(":")[1].trim() == ",") {
			j++;
			checkSecondIndex++;
			if(parametros(i, j)){
				checkSecondIndex++;
				return true;
			}
		} else if(tokenMap.get(i).get(j).isEmpty()) {
			checkFirstIndex++;
			return true;
		}
		return false;
	}
	
	public boolean ehMain(int i, int j){
		if(lineMap.get(i).get(j).split(":")[1].trim() == "bool") {
			j++;
			checkSecondIndex++;
			if(lineMap.get(i).get(j).split(":")[1].trim() == "main") {
				j++;
				checkSecondIndex++;
				if(lineMap.get(i).get(j).split(":")[1].trim() == "(") {
					j++;
					checkSecondIndex++;
					if(lineMap.get(i).get(j).split(":")[1].trim() == ")") {
						j++;
						checkSecondIndex++;
						if(lineMap.get(i).get(j).split(":")[1].trim() == "{") {
							j++;
							checkSecondIndex++;
							if(ehProgram(i, j)) {
								j++;
								checkSecondIndex++;
								if(lineMap.get(i).get(checkSecondIndex).split(":")[1].trim() == "}") {
									j++;
									checkSecondIndex++;
									if(metodo(i, j)) return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean ehProgram(int i, int j){
		if(checkSecondIndex >= tokenMap.get(i).size()){
			checkSecondIndex++;
			checkFirstIndex++;
		}
		if(lineMap.get(i).get(j).split(":")[1].trim() == "for"){
			j++;
			checkSecondIndex++;
			if (ehFor(i, j)) return true;
		} else if(ehIf(i, j)) return true;
		else if(classificarVariavel(i, j)) return true;
		else if(criarVariavel(i, j)) return true;
		else if(scan(i, j)) return true;
		else if(print(i, j)) return true;
		else if(instancia(i, j)) return true;
		else if(chamadaMetodosPontoVirgula(i, j)) return true;
		else if(returnR(i, j)) return true;
		else if(tokenMap.get(i).get(j).isEmpty()){
			checkFirstIndex++;
			return true;
		}
		return false;
	}
	
	//ESSe
	public boolean returnR(int i, int j) {
		if (lineMap.get(i).get(j).split(":")[1].trim() == "<<") {
			j++;
			checkSecondIndex++;
			if(tiposReturn(i, j)){
				if (lineMap.get(i).get(j).split(":")[1].trim() == ";") {
					checkSecondIndex++;
					return true;	
				}				 
			}
		}
		return false;
	}
	//ESSE
	private boolean tiposReturn(int i, int j) {
		if(ehOperation(i, j)) return true;
		return false;
	}
	
	public boolean chamadaMetodosPontoVirgula(int i, int j) {
		if(chamadaMetodos(i, j)){
			j++;
			checkSecondIndex++;
			if(lineMap.get(i).get(j).split(":")[1].trim() == ";"){
				j++;
				checkSecondIndex++;
				if(ehProgram(i, j)) return true;
			}
		}
		errors.add("Linha: " + checkFirstIndex + " Chamada de metodo mal formada");
		return false;
	}
	
	//ESSe
	public boolean multiplasImpressoes(int i, int j) {		
		if (lineMap.get(i).get(j).split(":")[1].trim() == ",") {
			j++;
			checkSecondIndex++;
			if (impressao(i, j)) {
				j++;
				checkSecondIndex++;
				if(tokenMap.get(i).size() <= j){
					multiplasImpressoes(i, j);
				}
			}
		}
		
		return false;
	}
	
	//esse
	public boolean impressao(int i, int j) {
		if(ehOperation(i, j)) return true;
		return false;
	}
	
	public boolean instancia(int i, int j) {
		if(tokenMap.get(i).get(j) == "Identificador"){
			j++;
			checkSecondIndex++;
			if(lineMap.get(i).get(j).split(":")[1].trim() == "="){
				j++;
				checkSecondIndex++;
				if(lineMap.get(i).get(j).split(":")[1].trim() == ">"){
					j++;
					checkSecondIndex++;
					if(tokenMap.get(i).get(j) == "Identificador"){
						j++;
						checkSecondIndex++;
						if(lineMap.get(i).get(j).split(":")[1].trim() == ">"){
							j++;
							checkSecondIndex++;
							if(passagemParametros(i, j)){
								j++;
								checkSecondIndex++;
								if(lineMap.get(i).get(j).split(":")[1].trim() == ")"){
									j++;
									checkSecondIndex++;
									if(lineMap.get(i).get(j).split(":")[1].trim() == ";"){
										j++;
										checkSecondIndex++;
										if(ehProgram(i, j)) return true;
									}
								}
							}
						}
					}
				}
			}
		}
		errors.add("Linha: " + checkFirstIndex + " Instancia mal formada");
		return false;
	}
	
	public boolean passagemParametros(int i, int j) {
		if(ehOperation(i, j)){
				j++;
				checkSecondIndex++;
				if(maisPassagens(i, j)) return true;
		} else if(tokenMap.get(i).get(j).isEmpty()) return true;
		return false;
	}
	
	public boolean maisPassagens(int i, int j) {
		if(lineMap.get(i).get(j).split(":")[1].trim() == ","){
			j++;
			checkSecondIndex++;
			if(passagemParametros(i, j)) return true;
		}
		return false;
	}
	
	public boolean criarVariavel(int i, int j) {
		if(grammar.getTipo(lineMap.get(i).get(j).split(":")[1].trim())){
			j++;
			checkSecondIndex++;
			if(variaveis(i, j)){
				j++;
				checkSecondIndex++;
				if(lineMap.get(i).get(j).split(":")[1].trim() == ";"){
					j++;
					checkSecondIndex++;
					if(ehProgram(i, j)) return true;
				}
			}
		} else if(criarObjetos(i, j)) return true;
		return false;
	}
	
	public boolean criarObjetos(int i, int j) {
		if(criarObjetosLinha(i, j)){
			j++;
			checkSecondIndex++;
			if (ehProgram(i, j)) return true;
		}
		return false;
	}
	
	
	
	public boolean classificarVariavel(int i, int j) {
		if(lineMap.get(i).get(j).split(":")[1].trim() == "-"){
			j++;
			checkSecondIndex++;
			if(lineMap.get(i).get(j).split(":")[1].trim() == "-"){
				j++;
				checkSecondIndex++;
				if(lineMap.get(i).get(j).split(":")[1].trim() == ">"){
					j++;
					checkSecondIndex++;
					if(operationLinhe(i, j)){
						j++;
						checkSecondIndex++;
						return true;
					}
				}
			} else if(lineMap.get(i).get(j).split(":")[1].trim() == ">"){
				j++;
				checkSecondIndex++;
				if(operationLinhe(i, j)){
					j++;
					checkSecondIndex++;
					return true;
				}
			}
		} else if(operationLinhe(i, j)){
			j++;
			checkSecondIndex++;
			return true;
		}
		errors.add("Linha: " + checkFirstIndex + "Classificar variavel mal formado");
		return false;
	}
	
	public boolean operationLinhe(int i, int j){
		if(operationFor(i, j)){
			j++;
			checkSecondIndex++;
			if(lineMap.get(i).get(j).split(":")[1].trim() == ";"){
				j++;
				checkSecondIndex++;
				if(ehProgram(i, j)) return true;
			}
		}
		return false;
	}
	
	public boolean ehIf(int i, int j) {
		if(lineMap.get(i).get(j).split(":")[1].trim() == "if"){
			j++;
			checkSecondIndex++;
			if(lineMap.get(i).get(j).split(":")[1].trim() == "("){
				j++;
				checkSecondIndex++;
				if(expressaoLogicaRelacional(i, j)){
					j++;
					checkSecondIndex++;
					if(lineMap.get(i).get(j).split(":")[1].trim() == ")"){
						j++;
						checkSecondIndex++;
						if(lineMap.get(i).get(j).split(":")[1].trim() == "{"){
							j++;
							checkSecondIndex++;
							if(ehProgram(i, j)){
								j++;
								checkSecondIndex++;
								if(elseOpcional(i, j)) {
									j++;
									checkSecondIndex++;
									return true;
								}								
							}
						}
					}
				}
			}
		}
		errors.add("Linha: " + checkFirstIndex + " If mal formado");
		return false;
	}
	
	public boolean elseOpcional(int i, int j) {
		if(lineMap.get(i).get(j).split(":")[1].trim() == "else"){
			j++;
			checkSecondIndex++;
			if(lineMap.get(i).get(j).split(":")[1].trim() == "{"){
				j++;
				checkSecondIndex++;
				if(ehProgram(i, j)){
					j++;
					checkSecondIndex++;
					if(lineMap.get(i).get(j).split(":")[1].trim() == "}"){
						j++;
						checkSecondIndex++;
						if(ehProgram(i, j)){
							j++;
							checkSecondIndex++;
						}
					}
				}
			}
		} else if(ehProgram(i, j)) return true;
		errors.add("Linha: " + checkFirstIndex + " else mal formado");
		return false;
	}
	
	public boolean ehFor(int i, int j) {
		if(lineMap.get(i).get(j).split(":")[1].trim() == "(") {
			j++;
			checkSecondIndex++;
			if(operationFor(i, j)){
				j++;
				checkSecondIndex++;
				if(lineMap.get(i).get(j).split(":")[1].trim() == ";") {
					j++;
					checkSecondIndex++;
					if(expressaoLogicaRelacional(i, j)) {
						j++;
						checkSecondIndex++;
						if(lineMap.get(i).get(j).split(":")[1].trim() == ";") {
							j++;
							checkSecondIndex++;
							if(operationFor(i, j)) {
								j++;
								checkSecondIndex++;
								if(lineMap.get(i).get(j).split(":")[1].trim() == ")") {
									j++;
									checkSecondIndex++;
									if(lineMap.get(i).get(j).split(":")[1].trim() == "{") {
										j++;
										checkSecondIndex++;
										if(ehProgram(i, j)) {
											j++;
											checkSecondIndex++;
											if(lineMap.get(checkFirstIndex).get(checkSecondIndex).split(":")[1].trim() == "}") {
												if(ehProgram(i, j)) {
													j++;
													checkSecondIndex++;
													if(ehProgram(i, j)) return true;												
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		errors.add("Linha: " + checkFirstIndex + " For mal formado");
		return false;		
	}
	
	public boolean operationFor(int i, int j) {
		if(tokenMap.get(i).get(j) == "Identificador") {
			j++;
			checkSecondIndex++;
			if(lineMap.get(i).get(j).split(":")[1].trim() == "=") {
				j++;
				checkSecondIndex++;
				if(ehOperation(i, j)) return true;
			}
		} else if(acessoVetorMatriz	(i, j)){
			j++;
			checkSecondIndex++;
			if(lineMap.get(i).get(j).split(":")[1].trim() == "=") {
				j++;
				checkSecondIndex++;
				if(ehOperation(i, j)) return true;
			}
		}
		return false;
	}
	
	public boolean ehOperation(int i, int j) {
		if(expressaoAritmetica(i, j)) return true;
		else if(expressaoLogicaRelacional(i, j)) return true;
		return false;
	}
	
	public boolean expressaoAritmetica(int i, int j) {
		if(relacionalAritmetico(i, j)) return true;
		return false;
	}
	
	public boolean expressaoLogicaRelacional(int i, int j) {
		if(ehRelacional(i, j)) {
			j++;
			checkSecondIndex++;
			if(variasExpressoes(i, j)) {
				j++;
				checkSecondIndex++;
				return true;
			}
		}
		return false;
	}
	
	public boolean ehRelacional(int i, int j) {
		if(addValor(i, j)){
			j++;
			checkSecondIndex++;
			if(tokenMap.get(i).get(j).toLowerCase() == "operador relacional"){
				j++;
				checkSecondIndex++;
				if(addValor(i, j))return true;
			}
		} else if(lineMap.get(i).get(j).split(":")[1].trim() == "("){
			j++;
			checkSecondIndex++;
			if(expressaoLogicaRelacional(i, j)){
				j++;
				checkSecondIndex++;
				if(lineMap.get(i).get(j).split(":")[1].trim() == ")") return true;
			}
		} else if(lineMap.get(i).get(j).split(":")[1].trim() == "!"){
			j++;
			checkSecondIndex++;
			if(lineMap.get(i).get(j).split(":")[1].trim() == "("){
				if(expressaoLogicaRelacional(i, j)){
					j++;
					checkSecondIndex++;
					if(lineMap.get(i).get(j).split(":")[1].trim() == ")") return true;
				}
			}
		}
		return false;
	}
	
	public boolean variasExpressoes(int i, int j) {
		if(tokenMap.get(i).get(j) == "Operador Logico") {
			j++;
			checkSecondIndex++;
			if(expressaoLogicaRelacional(i, j)) {
				j++;
				checkSecondIndex++;
				return true;
			}
		} else if(tokenMap.get(i).get(j).isEmpty()) return true;
		return false;
	}
		
	//ESSE
	public boolean scan(int i, int j){
		if (lineMap.get(i).get(j).split(":")[1].trim() == "scan") {
			j++;
			checkSecondIndex++;
			if (lineMap.get(i).get(j).split(":")[1].trim() == "(") {
				j++;
				checkSecondIndex++;
				if (tokenMap.get(i).get(j) == "Identificador") {
					j++;
					checkSecondIndex++;
					if(!multiplasLeituras(i, j)){
						if (lineMap.get(i).get(j).split(":")[1].trim() == ")") {
							j++;
							checkSecondIndex++;
							if (lineMap.get(i).get(j).split(":")[1].trim() == ";") {
								j++;
								checkSecondIndex++;
								if(tokenMap.get(i).get(j).isEmpty() || tokenMap.get(i).get(j) == null) 
									return true;
							}
						}
					}else{
						System.out.println("Erro no scan linha: " + i);
					}
				}
			}
		} 
		return false;
	}
	
	//ESSE
	public boolean acessoVetorMatriz(int i, int j) {
		
		if (tokenMap.get(i).get(j) == "Identificador") {
			j++;
			checkSecondIndex++;
			if(lineMap.get(i).get(j).split(":")[1].trim() == "[") {
				j++;
				checkSecondIndex++;
				if(tokenMap.get(i).get(j) == "Numero") {
					j++;
					checkSecondIndex++;
					if (lineMap.get(i).get(j).split(":")[1].trim() == "]") {
						j++;
						checkSecondIndex++;
						fatoracaoAcessoVetorMatriz(i, j);
						return true;
					}
				}
			}
		}
		return false;
	}

	//ESSE
	public boolean fatoracaoAcessoVetorMatriz(int i, int j) {
		
		if(lineMap.get(i).get(j).split(":")[1].trim() == "[") {
			j++;
			checkSecondIndex++;
			if(tokenMap.get(i).get(j) == "Numero") {
				j++;
				checkSecondIndex++;
				if (lineMap.get(i).get(j).split(":")[1].trim() == "]") {
					j++;
					checkSecondIndex++;
					if(fatoracaoAcessoVetorMatriz(i, j))
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	//ESSE
	public boolean print(int i, int j){
		if (lineMap.get(i).get(j).split(":")[1].trim() == "print") {
			j++;
			checkSecondIndex++;
			if (lineMap.get(i).get(j).split(":")[1].trim() == "(") {
				j++;
				checkSecondIndex++;
				if (tokenMap.get(i).get(j) == "Identificador") {
					j++;
					checkSecondIndex++;
					if (lineMap.get(i).get(j).split(":")[1].trim() == ")") {
						j++;
						checkSecondIndex++;
						if (lineMap.get(i).get(j).split(":")[1].trim() == ";") {
							j++;
							checkSecondIndex++;
							if(tokenMap.get(i).get(j).isEmpty() || tokenMap.get(i).get(j) == null) 
								return true;
						}
					}
					impressao(i, j);
					multiplasImpressoes(i, j);
					if (lineMap.get(i).get(j).split(":")[1].trim() == ")") {
						j++;
						checkSecondIndex++;
						if (lineMap.get(i).get(j).split(":")[1].trim() == ";") {
							j++;
							checkSecondIndex++;
							if(tokenMap.get(i).get(j).isEmpty() || tokenMap.get(i).get(j) == null) 
								return true;
						}
					}

				}else{
				multiplasLeituras(i, j);
					if (lineMap.get(i).get(j).split(":")[1].trim() == ")") {
						j++;
						checkSecondIndex++;
						if (lineMap.get(i).get(j).split(":")[1].trim() == ";") {
							j++;
							checkSecondIndex++;
							if(tokenMap.get(i).get(j).isEmpty() || tokenMap.get(i).get(j) == null) 
								return true;
						}
					}
				}
			}
			
		} 
		return false;
	}
	
	//ESSE
	public boolean multiplasLeituras(int i, int j) {
		
		if (lineMap.get(i).get(j).split(":")[1].trim() == ",") {
			j++;
			checkSecondIndex++;
			if (tokenMap.get(i).get(j) == "Identificador") {
				j++;
				checkSecondIndex++;
				if(tokenMap.get(i).size() <= j){
					multiplasLeituras(i, j);
				}
			}
		}
		
		return false;
	}
	
	public boolean relacionalAritmetico(int i, int j) {
		if(addValor(i, j)){
			j++;
			checkSecondIndex++;
			if(continuar(i, j)) { //continuar e fatoracaorelacionalaritmetico sao iguais na gramatica
				j++;
				checkSecondIndex++;
				return true;
			}
		} else if(lineMap.get(i).get(j).split(":")[1].trim() == "("){
			j++;
			checkSecondIndex++;
			if(addValor(i, j)) {
				j++;
				checkSecondIndex++;
				if(grammar.getAritmetico(lineMap.get(i).get(j).split(":")[1].trim())) {
					j++;
					checkSecondIndex++;
					if(relacionalAritmetico(i, j)) {
						j++;
						checkSecondIndex++;
						if(lineMap.get(i).get(j).split(":")[1].trim() == ")"){
							j++;
							checkSecondIndex++;
							if(continuar(i, j)) return true;
						}
					}
				}
			}
		} else if(lineMap.get(i).get(j).split(":")[1].trim() == "-") {
			if(lineMap.get(i).get(j).split(":")[1].trim() == "("){
				j++;
				checkSecondIndex++;
				if(addValor(i, j)) {
					j++;
					checkSecondIndex++;
					if(grammar.getAritmetico(lineMap.get(i).get(j).split(":")[1].trim())) {
						j++;
						checkSecondIndex++;
						if(relacionalAritmetico(i, j)) {
							j++;
							checkSecondIndex++;
							if(lineMap.get(i).get(j).split(":")[1].trim() == ")"){
								j++;
								checkSecondIndex++;
								if(continuar(i, j)) return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean addValor(int i, int j) {
		if(lineMap.get(i).get(j).split(":")[1].trim() == "-") {
			j++;
			checkSecondIndex++;
			if(value(i, j)) return true;
		} else if(value(i, j)) return true;
		return false;
	}
	
	public boolean continuar(int i, int j){
		if(grammar.getAritmetico(lineMap.get(i).get(j).split(":")[1].trim())) {
			j++;
			checkSecondIndex++;
			if(relacionalAritmetico(i, j)) return true;
		} else if(tokenMap.get(i).get(j).isEmpty()) {
			checkFirstIndex++;
			return true;			
		}
		return false;
	}
	
	public boolean value(int i, int j) {
		if (tokenMap.get(i).get(j) == "Identificador") {
			j++;
			checkSecondIndex++;
			return true;
		}else if(tokenMap.get(i).get(j) == "Numero") {
			j++;
			checkSecondIndex++;
			return true;
		}else if (tokenMap.get(i).get(j) == "booleano") {
			j++;
			checkSecondIndex++;
			return true;
		}else if (tokenMap.get(i).get(j) == "Cadeia de Caracteres") {
			j++;
			checkSecondIndex++;
			return true;
		}else if (acessoVetorMatriz(i,j)) {
			j++;
			checkSecondIndex++;
			return true;
		}else if(chamadaMetodos(i,j)){
			j++;
			checkSecondIndex++;
			return true;
		}
		return false;
	}
	
	//ESSSe
	private boolean chamadaMetodos(int i, int j) {
		if (tokenMap.get(i).get(j) == "Identificador") {
			j++;
			checkSecondIndex++;
			if(fatoracaochamadaMetodos(i,j)) return true;
		}
		return false;
	}
	//ESSSe
	private boolean fatoracaochamadaMetodos(int i, int j) {
		if (lineMap.get(i).get(j).split(":")[1].trim() == "::") {
			j++;
			checkSecondIndex++;
			if (tokenMap.get(i).get(j) == "Identificador") {
				j++;
				checkSecondIndex++;
				if(fatoracaodafatoracao(i,j)) return true;
			}
		}
		return false;
	}
	
	//ESSSe
	public boolean fatoracaodafatoracao(int i, int j) {
		if (lineMap.get(i).get(j).split(":")[1].trim() == "(") {
			j++;
			checkSecondIndex++;			
			if(passagemParametros(i,j)) return true;
		}
		return false;
	}
	
	public boolean heranca(int i, int j) {
		if(lineMap.get(i).get(j).split(":")[1].trim() == "<"){
			j++;
			checkSecondIndex++;
			if(lineMap.get(i).get(j).split(":")[1].trim() == "-") {
				j++;
				checkSecondIndex++;
				if(lineMap.get(i).get(j).split(":")[1].trim() == ">") {
					j++;
					checkSecondIndex++;
					if(tokenMap.get(i).get(j) == "Identificador") return true;
				}
			}
		} else if (tokenMap.get(i).get(j).isEmpty()) {
			checkFirstIndex++;
			return true;
		}
		return false;
	}
	
	public boolean varConstObj(int i, int j) {
		if (lineMap.get(i).get(j).split(":")[1].trim() == "final") {
			j++;
			checkSecondIndex++;
			if (grammar.getTipo(lineMap.get(i).get(j).split(":")[1].trim())) {
				j++;
				checkSecondIndex++;
				if (tratamentoConstante(i, j)) {
					j++;
					checkSecondIndex++;
					if(lineMap.get(i).get(j).split(":")[1].trim() == ";") {
						j++;
						checkSecondIndex++;
						varConstObj(i, j);
					} else return false;					
				} else return false;
			} else return false;
		} else if (grammar.getTipo(lineMap.get(i).get(j).split(":")[1].trim())){
			//tratamento variavel
			j++;
			checkSecondIndex++;
			if(tratamentoVariavel(i, j)) return true;
		} else if(criarObjetosLinha(i, j)) {
			j++;
			checkSecondIndex++;
			if(varConstObj(i, j)) return true;
		} else if(tokenMap.get(i).get(j).isEmpty()){
			checkFirstIndex++;
			return true;
		}
		return false;
	}
	
	
	public boolean criarObjetosLinha(int i, int j) {
		if (tokenMap.get(i).get(j) == "Identificador") {
			j++;
			checkSecondIndex++;
			if (tokenMap.get(i).get(j) == "Identificador") {
				j++;
				checkSecondIndex++;
				if (variosObjetos(i, j)) {
					j++;
					checkSecondIndex++;
					if(lineMap.get(i).get(j).split(":")[1].trim() == ";") return true;
				}
			}
		}
		return false;
	}
	
	public boolean variosObjetos(int i, int j) {
		if(lineMap.get(i).get(j).split(":")[1].trim() == ",") {
			j++;
			checkSecondIndex++;
			if (tokenMap.get(i).get(j) == "Identificador") {
				j++;
				checkSecondIndex++;
				if(variosObjetos(i, j)) return true;
			}
		} else if (tokenMap.get(i).get(j).isEmpty()) {
			checkFirstIndex++;
			return true;
		}
		return false;
	}
	
	public boolean tratamentoVariavel(int i, int j) {
		if(variaveis(i, j)) {
			j++;
			checkSecondIndex++;
			if(lineMap.get(i).get(j).split(":")[1].trim() == ";") {
				j++;
				checkSecondIndex++;
				if(varConstObj(i, j)) return true;
			}
		}
		return false;
	}
	
	public boolean variaveis(int i, int j){
		if (tokenMap.get(i).get(j) == "Identificador") {
			j++;
			checkSecondIndex++;
			if(fatoracaoVariaveis(i, j)) return true;
		} return false;
	}
	
	public boolean fatoracaoVariaveis(int i, int j) {
		if(acrescentar(i, j)) return true;
		else if(lineMap.get(i).get(j).split(":")[1].trim() == "[") {
			j++;
			checkSecondIndex++;
			if(tokenMap.get(i).get(j) == "Numero") {
				j++;
				checkSecondIndex++;
				if (lineMap.get(i).get(j).split(":")[1].trim() == "]") {
					j++;
					checkSecondIndex++;
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
			checkSecondIndex++;
			if(tokenMap.get(i).get(j) == "Numero") {
				j++;
				checkSecondIndex++;
				if (lineMap.get(i).get(j).split(":")[1].trim() == "]") {
					j++;
					checkSecondIndex++;
					if (acrescentar(i, j)) return true;
				}
			}
		}
		return false;
	}
	
	public boolean acrescentar(int i, int j) {
		if (lineMap.get(i).get(j).split(":")[1].trim() == ",") {
			j++;
			checkSecondIndex++;
			if(variaveis(i, j)) return true;
		} else if(tokenMap.get(i).get(j).isEmpty()) {
			checkFirstIndex++;
			return true;
		}
		return false;
	}
	
	public boolean tratamentoConstante(int i, int j) {
		if (tokenMap.get(i).get(j) == "Identificador") {
			//tratamento constante
			j++;
			checkSecondIndex++;
			if (lineMap.get(i).get(j).split(":")[1].trim() == "=") {
				//Numero <gerador constante>
				j++;
				checkSecondIndex++;
				if (tokenMap.get(i).get(j) == "Numero") {
					//<gerador constante>
					j++;
					checkSecondIndex++;
					geradorConstante(i, j);
					return true;
				} else return false;
			} else return false;			
		} else return false;
	}
	
	public boolean geradorConstante(int i, int j) {
		if (lineMap.get(i).get(j).split(":")[1].trim() == ",") {
			j++;
			checkSecondIndex++;
			tratamentoConstante(i, j);
		} else if(tokenMap.get(i).get(j).isEmpty()) {
			checkFirstIndex++;
			return true;
		}
		return false;
	}
	
}
