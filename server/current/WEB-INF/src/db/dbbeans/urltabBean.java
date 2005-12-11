/**
 * einfache Bean zur indexed Urls-Tabelle
 * @author Martin Klink
 */
package db.dbbeans;

import java.util.Date;
import java.io.Serializable;
	
public class urltabBean implements Serializable
{
	public static final long serialVersionUID = 128L;

	private Integer id;
	private String url;
	private Integer cat_id;
	private Date datum;

	public urltabBean()
	{};

	public urltabBean(String neurl,Date dat)
	{
		url = neurl;
		datum = dat;
		
	}
	public urltabBean(String neurl,Date dat,Integer cat)
	{
		url = neurl;
		datum = dat;
		cat_id = cat;
		
	}
	
	public String getUrl()
	{
		return(url);
	}
	public void setUrl(String neurl)
	{
		url=neurl;
	}

	public Date getDate()
	{
		return(datum);
	}
	public void setDate(Date dat)
	{
		datum=dat;
	}
	
	public Integer getCategory()
	{
		return(cat_id);
	}
	public void setCat(Integer cat)
	{
		cat_id=cat;
	}
}
