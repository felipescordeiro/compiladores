package analysislexical;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;


public class AnalysisLexical {

	ArrayList<String> lineArchive;
	private Properties configFile;
	String pathStorage;
	String nameArchive;
	
	public AnalysisLexical() {
		lineArchive = new ArrayList<String>();
		configFile = new Properties();
		try {//Leitura das configurações
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
			line = file.readLine();
			String strLine;
			while (line != null) {
			  	String[] object =line.split(";");
			  	if("".equals(object[0].trim())){
			  		strLine = object[0];
				}else{
					if(!object[0].contains("{") && (!object[0].contains("}"))){
						strLine = object[0]+";";
					}else{
						strLine = object[0];
					}
				}
			  	lineArchive.add(numberLine, strLine);
			  	line = file.readLine(); // lê da segunda até a última linha
			  	numberLine++;
		    }
			fileRead.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Exibição das linhas lidas
	 */
	public void printLines(){
		for(int i = 0; i < lineArchive.size(); i++){
			System.out.println(i + " = " +lineArchive.get(i));
		}
	}
}
