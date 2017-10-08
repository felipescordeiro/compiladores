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
	private final Pattern numero = Pattern.compile("[-?][\t|\n|\\x20|\r]*\\d*[\\.\\d+]");
	private final Pattern letra = Pattern.compile("[a-zA-Z]");
	private final Pattern opAritmeticos = Pattern.compile("(\\+|-|\\*|/|%)");
	private final Pattern opRelacionais = Pattern.compile("(!=|=|<|<=|>|>=)");
	private final Pattern opLogicos = Pattern.compile("!|\\|{2}|&&");
	private final Pattern delimitador = Pattern.compile("(;|,|\\(|\\)|\\[|\\]|\\{|\\})");
	private final Pattern identificadorError = Pattern.compile("\\W");

	private Matcher matcher;
	
	public Regex () {}
	
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
		return this.matcher.find();
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
	 * Identify if sequence has 'Letra'
	 * @param sequence string used for lexical analysis
	 * @return true or false
	 */
	public boolean isLetra(String sequence){
		this.matcher = this.letra.matcher(sequence);
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
