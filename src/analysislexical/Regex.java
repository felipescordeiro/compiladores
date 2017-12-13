/**
 * 
 */
package analysislexical;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author Lucas Morais
 * 
 */
public class Regex {
	private final Pattern identificador = Pattern.compile("[a-zA-Z]\\w*");
	private final Pattern palavrasReservadas = Pattern.compile("(class|final|if|else|for|scan"
							+ "|print|int|float|bool|true|false|string)");
	private final Pattern digito = Pattern.compile("\\d"); 
	private final Pattern numero = Pattern.compile("-?[\\x09|\\x0A|\\x0D|\\x20]*?\\b[0-9]+(\\x2E[0-9]+)?\\b");
	private final Pattern letra = Pattern.compile("[a-zA-Z]");
	private final Pattern opAritmeticos = Pattern.compile("(\\+|-|\\*|/|%)");
	private final Pattern opRelacionais = Pattern.compile("(!=|=|<|<=|>|>=)");
	private final Pattern opLogicos = Pattern.compile("!|\\|{2}|&&");
	private final Pattern delimitador = Pattern.compile("(;|,|\\(|\\)|\\[|\\]|\\{|\\})");
	private final Pattern identificadorError = Pattern.compile("\\W");
	private final Pattern quotes = Pattern.compile("\"");
	private final Pattern cadeia = Pattern.compile("\".*\"");
	private final Pattern notSimbolo = Pattern.compile("[\\x00-\\x1F\\x7F-\\xFF]");
	private final Pattern tipo = Pattern.compile("float|int|string|bool");
	private final Pattern booleano = Pattern.compile("true|false");
	
	private Matcher matcher;
	
	public Regex () {}
	
	public boolean getBooleano(String sequence) {
		try{
			this.matcher = this.booleano.matcher(sequence);
			return this.matcher.matches();
		}catch (Exception e){
			return false;
		}
	}
	
	public boolean getLogico(String sequence) {
		try{
			this.matcher = this.opLogicos.matcher(sequence);
			return this.matcher.matches();
		}catch (Exception e){
			return false;
		}
	}
	
	public boolean getAritmetico(String sequence) {
		try{
			this.matcher = this.opAritmeticos.matcher(sequence);
			return this.matcher.matches();
		}catch (Exception e){
			return false;
		}
	}
	
	public boolean getTipo(String sequence) {
		try{
			this.matcher = this.tipo.matcher(sequence);
			return this.matcher.matches();
		}catch (Exception e){
			return false;
		}
	}
	
	public boolean isNotSimbolo(String sequence) {
		try{
			this.matcher = this.notSimbolo.matcher(sequence);
			return this.matcher.find();
		}catch (Exception e) {
			return false;
		}
	}
	
	public String groupIdentificador(String sequence) {
		try{
			this.matcher = this.identificador.matcher(sequence);
			return this.matcher.group();
		}catch (Exception e){
			return sequence;
		}		
	}
	
	public String groupReservada(String sequence){
		this.matcher = this.palavrasReservadas.matcher(sequence);
		return this.matcher.group();
	}
	
	public String groupNum(String sequence) {
		try{
			this.matcher = this.numero.matcher(sequence);
			return this.matcher.group();
		}catch (Exception e){
			return sequence;
		}
	}
	public boolean hasQuotes(String sequence) {
		this.matcher = this.quotes.matcher(sequence);
		return this.matcher.find();
	}
	public int countQuotes(String sequence) {
		this.matcher = this.cadeia.matcher(sequence);
		return this.matcher.groupCount();
	}
	public boolean hasErrorId(String sequence) {
		this.matcher = this.identificadorError.matcher(sequence);
		return this.matcher.find();
	}
	
	public boolean isDelimitador(String sequence) {
		this.matcher = this.delimitador.matcher(sequence);
		return this.matcher.find();
	}
	/**
	 * Identify if sequence has 'Identificador'
	 * @param sequence string used for lexical analysis
	 * @return true or false
	 */
	public boolean isIdentificador(String sequence) {
		this.matcher = this.identificador.matcher(sequence);
		return this.matcher.find();
	}
	
	/**
	 * Identify if sequence has 'Palavra Reservada'
	 * @param sequence string used for lexical analysis
	 * @return true or false
	 */
	public boolean isPalavrasReservadas(String sequence) {
		this.matcher = this.palavrasReservadas.matcher(sequence);
		return this.matcher.find();
	}
	
	
	/**
	 * Identify if sequence has 'Digito'
	 * @param sequence string used for lexical analysis
	 * @return true or false
	 */
	public boolean isDigito(String sequence) {
		this.matcher = this.digito.matcher(sequence);
		return this.matcher.matches();
	}
	
	/**
	 * Identify if sequence has 'Numero'
	 * @param sequence string used for lexical analysis
	 * @return true or false
	 */
	public boolean isNumero(String sequence) {
		this.matcher = this.numero.matcher(sequence);
		return this.matcher.find();
	}
	
	/**
	 * Identify if char is 'Letra'
	 * @param char used for lexical analysis
	 * @return true or false
	 */
	public boolean isLetra(CharSequence ch){
		this.matcher = this.letra.matcher(ch);
		return this.matcher.find();
	}
	
	/**
	 * Identify if sequence has 'Operador Aritmetico'
	 * @param sequence string used for lexical analysis
	 * @return true or false
	 */
	public boolean isOpAritmeticos(String sequence){
		this.matcher = this.opAritmeticos.matcher(sequence);
		return this.matcher.find();
	}
	
	/**
	 * Identify if sequence has 'Operador Relacional'
	 * @param sequence string used for lexical analysis
	 * @return true or false
	 */
	public boolean isOpRelacionais(String sequence){
		this.matcher = this.opRelacionais.matcher(sequence);
		return this.matcher.find();
	}
	
	/**
	 * Identify if sequence has 'Operador Logico'
	 * @param sequence string used for lexical analysis
	 * @return true or false
	 */
	public boolean isOpLogicos(String sequence){
		this.matcher = this.opLogicos.matcher(sequence);
		return this.matcher.find();
	}


}
