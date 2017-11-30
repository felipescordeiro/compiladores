package analysislexical;

import java.util.ArrayList;
import java.util.HashMap;

public class syntax {

	AnalysisLexical lexico;
	String pathInput, pathStorage;
	ArrayList<String> lineArchive;
	
	public syntax(String pathInput, String pathStorage){
		lineArchive = new ArrayList<String>();
		
		
		this.pathInput = pathInput;
		this.pathStorage = pathStorage;
	}
	
	
}
