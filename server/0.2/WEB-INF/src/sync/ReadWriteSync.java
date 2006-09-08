/*
 * Created on Dec 6, 2005
 */
package sync;

/**
 * Implementiert einen einfachen gegenseitigen AUsschluss f�r Lese- und Schreiboperationen
 * 
 * Dieses Objekt l�sst entweder beliebig viele lesende Zugriffe oder einen schreibenden Zugriff zu.
 * Sobald sich ein schreibender Zugriff meldet (und bis zum Ende aller lesenden Operationen geblockt wird)
 * werden weitere lesende Operationen bis zum Ende des schreibenden Zugriffs geblockt.
 * 
 * Diese Klasse hat derzeit *keinen* Timeout, jede angemeldete Operation muss ihr Ende als bekannt machen! 
 * Ansonsten verklemmt sich hier alles und nichts geht mehr!
 *  
 * Kleines Problem, was diese Klasse noch hat, ist das lesende Zugriffe theoretisch von schreibenden 
 * ausgehungert werden k�nnen. Dies ist f�r den derzeitigen Stand der Applikation aber egal
 *  
 * Die Reihenfolge in der Operationen zugelassen werden ist nichtdeterministisch, h�ngt also nicht unbedingt von der Eingangsreihenfolge ab! 
 * 
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 */
public class ReadWriteSync {
	
	protected int reads = 0;
	protected int writes = 0;
	protected int waitingWriters = 0;
	
	/**
	 * Zeigt ob gerade gelesen (=1) oder geschrieben (=2) wird
	 */
	protected int mode = 1;
	
	
	public ReadWriteSync() {
		
	}
	
	/**
	 * Meldet einen Lesewunsch an.
	 * 
	 * Der aufrufende Thread wird in dieser Methode solange blockiert, bis die Leseoperation m�glich ist.
	 * 
	 * Jeder Thread muss das Ende seiner Leseoperation durch den Aufruf von endRead() anmelden!
	 * 
	 * @throws InterruptedException Sollte der Thread beim warten unerwartet unterbrochen werden
	 */
	public synchronized void doRead() throws InterruptedException {
		
		// solange wir nicht im lesen-modus sind, warten wir
		while(mode != 1) {
			// warte in reader
			wait();
		}
		// wenn wir aus dem while herauskommen, k�nnen wir lesen, also z�hler 
		// erh�hen und thread fortsetzen lassen
		reads++;
	}
	
	/**
	 * Meldet das Ende einer Leseoperation an. 
	 *
	 * Muss von *jedem* Thread welcher doRead() durchlaufen hat aufgerufen werden.
	 */
	public synchronized void endRead() {
		// z�hler verringen, sofern der z�hler = 0 ist, ist evtl. ein mode-wechsel m�glich
		reads--;
		if(reads < 0) {
			// @todo uh, irgendwas l�uft asynchron, wat nu?
			// erstmal tun als w�re nichts passiert....
			reads = 0;
		}
		
		if(reads == 0 && mode != 1) {
			// es soll einen mode-wechsel beim ende aller lesenden operationen geben, da alle leser fertig sind, tun wir den jetzt
			// alle writer aufwecken
			notifyAll();
		}
	}
	
	/**
	 * Meldet einen Schreibwunsch an.
	 * 
	 * Blockiert sofort alle sp�ter ankommenden Leseanfragen.
	 * 
	 * Der aufrufende Thread wird in dieser Methode blockiert bis die Schreiboperation
	 * durchgef�hrt werden kann. Jeder Thread welcher diese Methode durchl�uft *muss*
	 * das Ende seiner Schreriboperation durch den Aufruf von endWrite() deutlich machen!
	 *  
	 * @throws InterruptedException Sollte der Thread beim warten unerwarteterweise unterbrichen werden
	 */
	public synchronized void doWrite() throws InterruptedException {
		waitingWriters++;
		// erstmal solange blocken bis keine raeder mehr da sind
		while(reads > 0) {
			wait();
		}
		// dann mode wechseln (falls das nicht schon passiert ist)
		mode = 2;
		
		// warten bis geschrieben werden darf *und* kein writer unterwegs ist
		while(mode != 2 || writes > 0) {
			wait();
		}
		// wir d�rfen scheinbar schreiben, erh�hen wir den z�hler um nachfolgende schreiber zu blockieren
		writes++;
		waitingWriters--;
	}

	/**
	 * Macht das Ende einer Schreiboperation deutlich
	 */
	public synchronized void endWrite() {
		// haben wir �berhaupt eine schreiboperation laufen?
		if(writes < 1) {
			// nein, abbrechen ohne etwas zu tun
			return;
		}
		writes--;
		// write fertig, wir m�ssen nun unterscheiden zwischen (a) es gibt weitere writer und (b) es gibt keine writer mehr
		if(waitingWriters > 0) {
			// es gibt noch writer, lassen wir die erstmal ran
			notifyAll();
		} else {
			// keine writer mehr, mode-wechsel auf lesen, aufwecken der reader
			mode = 1;
			notifyAll();
		}
	}
	
	/**
	 * Gibt zur�ck ob der Ausschluss derzeit im Lesen-Modus ist, also ohne zu warten gelesen werden kann
	 * @return
	 */
	public synchronized boolean canRead() {
		return (mode == 1);
	}

	/**
	 * Gibt zur�ck ob der Ausschluss derzeit im Schreiben-Modus ist
	 * @return
	 */
	public synchronized boolean canWrite() {
		return (mode == 2); 
	}

	/**
	 * Liefert die Anzahl der aktiven lesenden Zugriffe (nicht der wartenden!)
	 * @return
	 */
	public int getWriter() {
		return writes;
	}

	/**
	 * Liefert die Anzahl der schreibenden Zurgiffe (nicht der wartenden!)
	 * @return
	 */
	public int getReader() {
		return reads;
	}
}
