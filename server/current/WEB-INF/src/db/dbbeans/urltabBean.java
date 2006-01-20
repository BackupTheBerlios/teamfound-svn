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
	private Date datum;

	public urltabBean()
	{};

	public urltabBean(String neurl)
	{
		url = neurl;
	}
	public urltabBean(String neurl,Date dat,Integer id)
	{
		url = neurl;
		datum = dat;
		id = id;
		
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
	
	public Integer getID()
	{
		return(id);
	}
	public void setID(Integer neid)
	{
		id=neid;
	}
	public void printAll()
	{
		System.out.println("ID:"+id+" URL:"+url+" Datum:"+datum.getTime());
	}
}
