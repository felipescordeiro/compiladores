package analysislexical;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;

public class grammar {
	private final Pattern className = Pattern.compile("class");
	private final Pattern numero = Pattern.compile("-?[\\x09|\\x0A|\\x0D|\\x20]*?\\b[0-9]+(\\x2E[0-9]+)?\\b");
	private final Pattern cadeia = Pattern.compile("\"(.|\\\")*\"");
	private final Pattern booleano = Pattern.compile("true|false");
	private final Pattern identificador = Pattern.compile("[a-zA-Z]\\w*");
	private final Pattern tipo = Pattern.compile("float|int|string|bool");
	private Matcher matcher;
	
	public grammar() {}
	
	public boolean getInicio(String sequence) {
		//transformar num array com duas posicoes
		if (this.varConstObj(sequence) && this.getClass(sequence)) return true;
		else return false;
	}
	
	public boolean varConstObj(String sequence){
//		if (sequence == "final") {
//			
//		}
		return false;
	}
	
	public boolean getClass(String sequence) {
		
		return false;
	}
	
	public boolean getClassName(String sequence) {
		try{
			this.matcher = this.className.matcher(sequence);
			return this.matcher.matches();
		}catch (Exception e){
			return false;
		}
	}
	public boolean getNumero(String sequence) {
		try{
			this.matcher = this.numero.matcher(sequence);
			return this.matcher.matches();
		}catch (Exception e){
			return false;
		}
	}
	public boolean getCadeia(String sequence) {
		try{
			this.matcher = this.cadeia.matcher(sequence);
			return this.matcher.matches();
		}catch (Exception e){
			return false;
		}
	}
	public boolean getBooleano(String sequence) {
		try{
			this.matcher = this.booleano.matcher(sequence);
			return this.matcher.matches();
		}catch (Exception e){
			return false;
		}
	}
	public boolean getIdentificador(String sequence) {
		try{
			this.matcher = this.identificador.matcher(sequence);
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

	
	
	
	
	
}
