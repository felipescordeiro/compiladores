package analysislexical;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class Start {

	public static void main(String[] args) {
		AnalysisLexical startCompiler = new AnalysisLexical();		
		startCompiler.parser();
		startCompiler.printLines();
		//startCompiler.writeLexical();
		/*Pattern p = Pattern.compile("-?[\\x09|\\x0A|\\x0D|\\x20]*?\\b[0-9]+(\\x2E[0-9]+)?\\b");
		Matcher m = p.matcher("_classasf_- abc x t &t");
		int i = 0;
		while(m.find()) {
			System.out.println(i + " -> Count: " + m.groupCount() + " Group: " + m.group());
			i++;
		}
		System.out.print("SAIU");*/
	}

}
/*else if (regex.hasQuotes(quotes)){
hasQuotes = true;
if(quotes.charAt(0) == '\"') {
	words.add("<ERRO, > Cadeia de Caracteres malformado Linha: " + linha);
	
}
nextIndex = quotes.indexOf(ch)
while(hasQuotes) {
	if (quotes.charAt(quotes.indexOf("\"")-1) == '\\') quotes = quotes.substring()
}
}*/