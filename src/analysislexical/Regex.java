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
	private final Pattern identificador = Pattern.compile("\\b\\[a-zA-Z]\\w*");
	private final Pattern palavrasReservadas = Pattern.compile("\\b(class|final|if|else|for|scan"
							+ "|print|int|float|bool|true|false|string)\\b");
	private final Pattern digito = Pattern.compile("\\d");
	private final Pattern letra = Pattern.compile("[a-zA-Z]");
	private final Pattern opAritmeticos = Pattern.compile("(+|-|*|/|%)");
	private final Pattern opRelacionais = Pattern.compile("(!=|=|<|<=|>|>=)");
	private final Pattern opLogicos = Pattern.compile("!|\\|{2}|&&");
	private Matcher matcher;
	
	public Regex () {}
	
	/**
	 * Identify if sequence has 'Identificador'
	 * @param sequence string used for lexical analysis
	 * @return true or false
	 */
	public boolean isIdentificador(String sequence) {
		this.matcher = this.identificador.matcher(sequence);
		return this.matcher.matches();
	}
	
	/**
	 * Identify if sequence has 'Palavra Reservada'
	 * @param sequence string used for lexical analysis
	 * @return true or false
	 */
	public boolean isPalavrasReservadas(String sequence) {
		this.matcher = this.palavrasReservadas.matcher(sequence);
		return this.matcher.matches();
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
	 * Identify if sequence has 'Letra'
	 * @param sequence string used for lexical analysis
	 * @return true or false
	 */
	public boolean isLetra(String sequence){
		this.matcher = this.letra.matcher(sequence);
		return this.matcher.matches();
	}
	
	/**
	 * Identify if sequence has 'Operador Aritmetico'
	 * @param sequence string used for lexical analysis
	 * @return true or false
	 */
	public boolean isOpAritmeticos(String sequence){
		this.matcher = this.opAritmeticos.matcher(sequence);
		return this.matcher.matches();
	}
	
	/**
	 * Identify if sequence has 'Operador Relacional'
	 * @param sequence string used for lexical analysis
	 * @return true or false
	 */
	public boolean isOpRelacionais(String sequence){
		this.matcher = this.opRelacionais.matcher(sequence);
		return this.matcher.matches();
	}
	
	/**
	 * Identify if sequence has 'Operador Logico'
	 * @param sequence string used for lexical analysis
	 * @return true or false
	 */
	public boolean isOpLogicos(String sequence){
		this.matcher = this.opLogicos.matcher(sequence);
		return this.matcher.matches();
	}
}
