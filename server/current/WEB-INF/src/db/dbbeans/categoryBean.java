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

	//weiss noch nicht ob wir das wirklich brauchen
	private Integer left;
	private Integer right;

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
}
