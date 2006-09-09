/**
 * einfache Bean zur projectadmin-Tabelle
 * @author Martin Klink, Jan
 */

package db.dbbeans;

import java.io.Serializable;
import java.sql.Timestamp;

public class projectadminBean implements Serializable
{
	public static final long serialVersionUID = 128L;

	private Integer id;
	private Integer userid;
	private Integer rootid;

	public projectadminBean(Integer _id, Integer _userid, Integer _rootid)
	{
		id=_id;
		userid=_userid;
		rootid=_rootid;
	}

	public projectadminBean()
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

	public void printAll()
	{
		System.out.println("id "+id);
		System.out.println("userid "+userid);
		System.out.println("rootid "+rootid);
	}
}
