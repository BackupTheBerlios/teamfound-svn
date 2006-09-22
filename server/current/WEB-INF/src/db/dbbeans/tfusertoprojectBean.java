/**
 * einfache Bean zur Tabelle
 * @author Martin Klink, Jan
 */

package db.dbbeans;

import java.io.Serializable;

public class tfusertoprojectBean implements Serializable
{
	public static final long serialVersionUID = 128L;

	private Integer id;
	private Integer userid;
	private Integer rootid;
	private Boolean isadmin;

	public tfusertoprojectBean(Integer _id, Integer _userid, Integer _rootid, Boolean _isadmin)
	{
		id=_id;
		userid=_userid;
		rootid=_rootid;
		isadmin = _isadmin;
	}

	public tfusertoprojectBean()
	{};

	public Integer getUserID()
	{
		return(userid);
	}
	public void setUserID(Integer _userid)
	{
		userid=_userid;
	}
	public Integer getRootID()
	{
		return(rootid);
	}
	public void setRootID(Integer _rootid)
	{
		rootid=_rootid;
	}
	public Integer getID()
	{
		return(id);
	}
	public void setID(Integer i)
	{
		id=i;
	}
	public Boolean getAdmin()
	{
		return(isadmin);
	}
	public void setAdmin(Boolean b)
	{
		isadmin=b;
	}
	public void printAll()
	{
		System.out.println("id "+id);
		System.out.println("userid "+userid);
		System.out.println("rootid "+rootid);
		System.out.println("Isadmin "+isadmin);
	}
}
