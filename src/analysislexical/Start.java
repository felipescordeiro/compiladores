package analysislexical;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Start {

	
	
	public Start(String pathInput, String pathStorage){
		
	}
	
	public static void main(String[] args) {
		
		Properties configFile = new Properties();
		try {//Leitura das configuracoes
			configFile.load(new FileInputStream("resources/LexicalConfigFile.properties"));
			String pathInput = configFile.getProperty("PATHINPUT");
			String pathStorage = configFile.getProperty("PATHSTORAGE");
			
		
			AnalysisLexical startCompiler = new AnalysisLexical(pathInput, pathStorage);		
			File archive[];
			//Caminho do diretório onde os arquivos serão lidos
			File directory = new File (pathInput);
			archive = directory.listFiles();
			for(int i = 0; i < archive.length; i++){
				startCompiler.parser(archive[i].getName());
				startCompiler.printLines();
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		
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