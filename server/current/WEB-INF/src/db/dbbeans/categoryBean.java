/**
 * einfache Bean zur Category-Tabelle
 * @author Martin Klink
 */

package db.dbbeans;

import java.io.Serializable;

public class categoryBean implements Serializable
{
	public static final long serialVersionUID = 128L;

	private Integer id;
	private Integer root_id;
	private String category;
	private String beschreibung;
	private Integer left;
	private Integer right;

	public categoryBean(Integer i, Integer r_id, String cat, String be, Integer l, Integer r)
	{
		id=i;
		root_id=r_id;
		category=cat;
		beschreibung=be;
		left=l;
		right=r;
	
	
	}

	public categoryBean()
	{};

	public categoryBean(String cat)
	{
		category = cat;
	}
	
	public String getCategory()
	{
		return(category);
	}
	public void setCategory(String cat)
	{
		category=cat;
	}
	public String getBeschreibung()
	{
		return(beschreibung);
	}
	public void setBeschreibung(String s)
	{
		beschreibung=s;
	}
	public Integer getLeft()
	{
		return(left);
	}
	public void setLeft(Integer i)
	{
		left=i;
	}
	public Integer getRight()
	{
		return(right);
	}
	public void setRight(Integer i)
	{
		right=i;
	}
	public Integer getRootID()
	{
		return(root_id);
	}
	public void setRootID(Integer i)
	{
		root_id=i;
	}
	public Integer getID()
	{
		return(id);
	}
	public void setID(Integer i)
	{
		id=i;
	}
	public void printAll()
	{
		System.out.println("id "+id);
		System.out.println("root "+root_id);
		System.out.println("cat:"+category);
		System.out.println("bes "+beschreibung);
		System.out.println("l "+left);
		System.out.println("r "+right);

	}
}
