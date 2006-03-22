/* Martin Klink 21.3.06
 */


package index.tests;

import index.teamfound.*;

public class IndexerTest
{
	public static void main(String args[])
	{
		TeamFoundIndexer tfindexer = new TeamFoundIndexer();
		String pfad = new String("/home/moddin/Uni/OpenSourceSoftEnt/teamfound/server/teamfoundDir/index");

		try
		{
			tfindexer.createIndex(pfad);
			System.out.println("IndexCreated!");
		}
		catch(Exception e)
		{
			System.out.println(" caught a " + e.getClass() +
			"\n with message: " + e.getMessage());
			
		}
			
	
		
	}
}
