
package index.teamfound;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import java.io.Reader;

/**
 * Martin Klink 28.1.06
 * Analyser fuer Teamfound der fuer das Kathegorien Feld den WhitespaceTokenizer
 * anstatt des StandartTokenizers verwendet
 */

public class TeamFoundAnalyzer extends Analyzer
{
	private static final Analyzer STANDARD = new StandardAnalyzer();
	private static final Analyzer WS = new WhitespaceAnalyzer();

	public TokenStream tokenStream(String field, final Reader reader) 
	{
		// wenn wir im Kathegorienfeld sind brauchen wir nur nach whitespaces zu trennen
		if ("cats".equals(field)) 
		{
			return  WS.tokenStream(field, reader);
		} 
		else 
		{
			// fuer alles andere nehmen wir den Standard
			return STANDARD.tokenStream(field, reader);
		}
								
	}
								
}
