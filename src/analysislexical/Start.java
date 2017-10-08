package analysislexical;

public class Start {

	public static void main(String[] args) {
		AnalysisLexical startCompiler = new AnalysisLexical();		
		startCompiler.parser();
		startCompiler.printLines();
		startCompiler.writeLexical();

	}

}
