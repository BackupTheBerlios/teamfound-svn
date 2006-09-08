/*
 * Created on Dec 6, 2005
 */
package sync.tests;

import java.util.Random;

import sync.ReadWriteSync;

public class RWSyncTestThread extends Thread {
	
	protected Random r;
	
	public void run(){
		r = new Random();
		ReadWriteSync sync = ReadWriteSyncTest.sync;
		int c = 0;
		try {
			while(c < 10) {
				c++;
				if(r.nextBoolean()) {
					System.out.println("T"+getName()+": versuche zu lesen....");
					sync.doRead();
					System.out.println("T"+getName()+": lese....");
//					 warten....
					sleep(Math.abs(r.nextLong() % 500));
					if(sync.getWriter() > 0) {
						System.out.println("PENG: writer: "+sync.getWriter());
					}
					System.out.println("T"+getName()+": versuche operation (lesen) zu beenden...");
					sync.endRead();
					System.out.println("T"+getName()+": fertig....");
				} else {
					System.out.println("T"+getName()+": versuche zu schreiben....");
					sync.doWrite();
					System.out.println("T"+getName()+": schreibe....");
//					 warten....
					sleep(Math.abs(r.nextLong() % 500));
					if(sync.getReader() > 0 || sync.getWriter() > 1) {
						System.out.println("PENG: writer: "+sync.getWriter()+" reader: "+sync.getReader());
					}
					System.out.println("T"+getName()+": versuche operation (schreiben) zu beenden...");
					sync.endWrite();
					System.out.println("T"+getName()+": fertig....");
				}
				
				// warten....
				sleep(Math.abs(r.nextLong() % 1000));
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
