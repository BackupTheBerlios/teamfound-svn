/**
 * @author Martin Klink
 */

package db.dbbeans;

import java.util.HashMap;
import java.util.Vector;
import java.io.Serializable;
import db.dbbeans.projectdataBean;

public class userRightBean implements Serializable
{
	public static final long serialVersionUID = 128L;

	private HashMap<Integer,projectdataBean> userprojects;//<porjectid,projectdata>
	private HashMap<Integer,projectdataBean> adminprojects;

	public userRightBean()
	{};

	public userRightBean(HashMap<Integer,projectdataBean> m, HashMap<Integer,projectdataBean> ad)
	{
		this.userprojects = m;
		this.adminprojects = ad;
	}

	public HashMap<Integer,projectdataBean> getUserProjects()
	{
		return userprojects;
	}
	public HashMap<Integer,projectdataBean> getAdminProjects()
	{
		return adminprojects;
	}
	public boolean isAdmin(Integer projectid)
	{
		return(adminprojects.containsKey(projectid));
	}

	/*
	* ist so gebaut, dass wenn er admin ist hier false zurueck kommt
	 **/
	public boolean isUser(Integer projectid)
	{
		return(userprojects.containsKey(projectid));
	}
	public boolean useradd(Integer projectid)
	{
		if(isAdmin(projectid))
			return(true);
		if(!isUser(projectid))
			return(false);
		projectdataBean pd = userprojects.get(projectid);
		return(pd.getUserUseradd().booleanValue());
	}
	public boolean userUrledit(Integer projectid)
	{
		if(isAdmin(projectid))
			return(true);
		if(!isUser(projectid))
			return(false);
		projectdataBean pd = userprojects.get(projectid);
		return(pd.getUserUrledit().booleanValue());
	}
	public boolean userCatedit(Integer projectid)
	{
		if(isAdmin(projectid))
			return(true);
		if(!isUser(projectid))
			return(false);
		projectdataBean pd = userprojects.get(projectid);
		return(pd.getUserCatedit().booleanValue());
	}
	public boolean addUrl(Integer projectid)
	{
		if(isAdmin(projectid))
			return(true);
		if(!isUser(projectid))
			return(false);
		projectdataBean pd = userprojects.get(projectid);
		return(pd.getUserAddurl().booleanValue());
	}
	
	public boolean addCat(Integer projectid)
	{
		if(isAdmin(projectid))
			return(true);
		if(!isUser(projectid))
			return(false);
		projectdataBean pd = userprojects.get(projectid);
		return(pd.getUserAddcat().booleanValue());
	}
	
}
