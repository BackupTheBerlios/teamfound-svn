/*
 * Created on Dec 6, 2005
 */
package sync.tests;

import sync.ReadWriteSync;

public class ReadWriteSyncTest {
	public static ReadWriteSync sync;
	public static void main(String[] args) throws InterruptedException {
		 sync = new ReadWriteSync();
		 
		 RWSyncTestThread t1, t2, t3;
		 t1 = new RWSyncTestThread();
		 t2 = new RWSyncTestThread();
		 t3 = new RWSyncTestThread();
		 t1.start();
		 t2.start();
		 t3.start();
		 
		 Thread.sleep(12000);
		 
	}
	
}
