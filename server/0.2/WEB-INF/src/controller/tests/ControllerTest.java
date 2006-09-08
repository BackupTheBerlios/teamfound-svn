/*
 * Created on Mar 25, 2006
 */
package controller.tests;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import controller.response.*;
import controller.teamfound.*;
import controller.*;

public class ControllerTest {
	public static void main(String[] args) 
	{
	
		System.out.println("Dieser Test sollte in einem leeren TeamfoundDir probiert werden");
		
		try
		{
			java.io.File f = new java.io.File("/home/moddin/build/teamfound.properties");

			java.io.FileInputStream pin = new java.io.FileInputStream(f);

			java.util.Properties props = new java.util.Properties();

			props.load(pin);
		
			config.Config tfc = new config.teamfound.TeamFoundConfig(props);
		
			Controller ctrl = new TeamFoundController(tfc);
		
			
			System.out.println("Server initializieren!");
			if(ctrl.initServer())
				System.out.println("Server ist bereit\n\n");
			else
				System.out.println("Server ist nicht bereit\n\n");

			System.out.println("\n\n");
			System.out.println("------GetProjects Test---------");
			GetProjectsResponse r1 = ctrl.getProjects();
			printdoc(r1);

			System.out.println("\n\n");
			System.out.println("-------Add Category Test--------");
			AddCategoriesResponse r2;
			r2 = ctrl.addCategory("Cat1",0,"Unterkat vom Projekt Tf");
			printdoc(r2);
			
			r2 = ctrl.addCategory("Cat2",0,"Unterkat vom Projekt Tf");
			printdoc(r2);

			r2 = ctrl.addCategory("SubCat1",1,"Unterkat von Cat1");
			printdoc(r2);


			
			System.out.println("\n\n");
			System.out.println("-------GetCategories Test--------");
			GetCategoriesResponse r3 = ctrl.getCategories(0);
			printdoc(r3);
			
			System.out.println("\n\n");
			System.out.println("-------AddPage Test--------");
			int[] cats = new int[1];
			cats[0]=3;//ist Subcat1
			String url = new String("http://www.black-sparx.de/");
			try
			{
				AddPageResponse r4 = ctrl.addToIndex(url,cats);
				printdoc(r4);
				r4 = ctrl.addToIndex(url,cats);
				printdoc(r4);
				cats[0] = 2;//ist Cat2
				r4 = ctrl.addToIndex(url,cats);
				printdoc(r4);
				url = ("http://de.wikipedia.org/wiki/Reiseveranstalter");
				r4 = ctrl.addToIndex(url,cats);
				printdoc(r4);


			}
			catch (Exception e)
			{
				System.out.println(e);
			}
			System.out.println("\n\n");
			System.out.println("-------Search Test--------");
			cats[0] = 2;
			try
			{
				SearchResponse r5 = ctrl.search("Black",0,cats);
				printdoc(r5);
				r5 = ctrl.search("Produkte",0,cats);
				printdoc(r5);
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		//	System.out.println("\n\n");
		//	System.out.println("-------GetCategories Test--------");
		//	r3 = ctrl.getCategories(0);
		//	printdoc(r3);
			

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
	
	static void printdoc(Response result)
	{
		Document d = result.getXML();
		
		XMLOutputter xmlout = new XMLOutputter(Format.getPrettyFormat());
		System.out.println(xmlout.outputString(d));
		
	}
}
