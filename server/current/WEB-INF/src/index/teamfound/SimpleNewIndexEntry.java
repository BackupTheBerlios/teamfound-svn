/*
 * Created on Dec 6, 2005
 */
package index.teamfound;

import java.io.File;
import java.util.Map;
import java.io.IOException;
import java.lang.InterruptedException;

import index.NewIndexEntry;

import org.apache.lucene.document.*;
import index.Parser.Html.HTMLParser;

/**
 * Eine erste simple Implementation für einen neuen Index-Eintrag
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 *
 * Martin(28.1.06)
 * Fuege Methode zum erstellen eines Lucene-document hinzu, 
 * welches wir dann in den Index einfuegen koennen.
 *
 */
public class SimpleNewIndexEntry implements NewIndexEntry {
	
	protected String url;
	protected File f;
	protected Map headers;
	protected int[] categoryID;

	/**
	 * @param url
	 * @param f
	 * @param headers
	 */
	public SimpleNewIndexEntry(String url, File f, Map headers, int[] id) {
		this.url = url;
		this.f = f;
		this.headers = headers;
		this.categoryID = id;
	}

	/**
	 * @return Returns the File
	 */
	public File getFile() {
		return f;
	}

	/**
	 * @return Returns the headers.
	 */
	public Map getHeaders() {
		return headers;
	}

	/**
	 * @return Returns the url.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return Returns the Categorys as IDs
	 */
	public int[] getCategoryID() {
		return categoryID;
	}
	/**
	 * Erzeugt ein Lucene Dokument,dass wir dann dem Index inzufuegen koennen.
	 * @return Returns an org.apache.lucene.document.Document 
	 */
	public Document getdocument()  throws IOException, InterruptedException		
	{
		Document doc = new Document();

		//URL als KeyWord-Feld ins Document adden
		//Bedeutet es wird nicht in Tokens zerlegt aber indexiert und gespeichert
		//es ist suchbar
		doc.add(Field.Keyword("url",url));

		//ein Datum ( z.b. letzte modifikation)
		//bin noch nicht sicher ob wir das brauchen, da wir ja ein Datum in der Daten
		//bank haben .. allerdings gibt uns das die Moeglichkeit einfach aus dem Index alle
		//eintraege rauszufischen die z.b. ein bestimmtes alter ueberschritten haben
		//auch hier nicht in Tokens zerlegt aber suchbar gespeichert
		/* doc.add(Field.Keyword("datum",datum));*/

		//TODO anderen HTML Parser suchen -> dann koennen wir ohne das apbspeichern der URL als File auskommen
		//TODO Test ob File existiert
		
		index.Parser.Html.HTMLParser parser = new HTMLParser(f);
		
		//Ok, der Parser liefert uns einen Reader der uns den Text ohne HTML Tags etc.
		//liefert und dieser kann dann indexiert und suchbar gespeichert werden
		doc.add(Field.Text("contents",parser.getReader()));
	
		//wir speichern die Zusammenfassung der Seite aber indexieren nicht
		//kann nur zum anzeigen bei Suchergebnissen genutzt werden nicht durchsuchbar
		doc.add(Field.UnIndexed("summary",parser.getSummary()));

		//Title wird wieder indexiert damit er zusaetzlich durchsucht werden kann
		doc.add(Field.Text("title", parser.getTitle()));

		//Kategorien wir bauen erstmal einen String der die ids beinhaltet und 
		//z.b. so aussieht "id:3 id:45 id:6 ..."
		//eine Aufzaehlung aller Kathegorien mit leerzeichen getrennt
		//Dann nehmen wir unseren eigen Analyser der bei dem Feld Cats automatisch
		//den org.apache.lucene.analysis.WhitespaceTokenizer benutzt somit haben
		//wir die ids schoen durchsuchbar gespeichert
		String cats = new String();
		for(int i=0; i<categoryID.length; i++ )
		{
			cats = (cats + "id:" + categoryID[i] + " ");
		}
		//Kategorien speicher indexieren und zwar als tokens
		doc.add(new Field("cats",cats,true,true,true));

		return doc; 
		
	}
	
}
