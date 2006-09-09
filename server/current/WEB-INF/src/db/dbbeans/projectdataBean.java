/**
 * einfache Bean zur Projectdata-Tabelle
 * @author Martin Klink/Jan kechel
 */

package db.dbbeans;

import java.io.Serializable;

public class projectdataBean implements Serializable
{
	public static final long serialVersionUID = 128L;

	private Integer id;
	private Integer root_id;
	private Integer version;
	private Boolean useruseradd;
	private Boolean userurledit;
	private Boolean usercatedit;
	private Boolean useraddurl;
	private Boolean useraddcat;
	private Boolean guestread;
	private Boolean guesturledit;
	private Boolean guestcatedit;
	private Boolean guestaddurl;
	private Boolean guestaddcat;
		
	public projectdataBean(Integer i, Integer r_id, Integer _version, Boolean _useruseradd, 
		Boolean _userurledit,
		Boolean _usercatedit,
		Boolean _useraddurl,
		Boolean _useraddcat,
		Boolean _guestread,
		Boolean _guesturledit,
		Boolean _guestcatedit,
		Boolean _guestaddurl,
		Boolean _guestaddcat )
	{
		id=i;
		root_id=r_id;
	
		version= _version;
		useruseradd= _useruseradd;
		userurledit= _userurledit;
		usercatedit= _usercatedit;
		useraddurl= _useraddurl;
		useraddcat= _useraddcat;
		guestread= _guestread;
		guesturledit= _guestread;
		guestcatedit= _guestcatedit;
		guestaddurl= _guestaddurl;
		guestaddcat= _guestaddcat;

	}

	public projectdataBean()
	{};

	
	public Integer getID()
	{
		return(id);
	}
	public void setID(Integer _b)
	{
		id=_b;
	}
	public Integer getRootID()
	{
		return(root_id);
	}
	public void setRootID(Integer _b)
	{
		root_id=_b;
	}	
	public Integer getVersion()
	{
		return(version);
	}
	public void setVersion(Integer _b)
	{
		version=_b;
	}	
	public Boolean getUserUseradd()
	{
		return(useruseradd);
	}
	public void setUserUseradd(Boolean _b)
	{
		useruseradd=_b;
	}	
	public Boolean getUserUrledit()
	{
		return(userurledit);
	}
	public void setUserUrledit(Boolean _b)
	{
		userurledit=_b;
	}	
	public Boolean getUserCatedit()
	{
		return(usercatedit);
	}
	public void setUserCatedit(Boolean _b)
	{
		usercatedit=_b;
	}	
	public Boolean getUserAddurl()
	{
		return(useraddurl);
	}
	public void setUserAddurl(Boolean _b)
	{
		useraddurl=_b;
	}	
	public Boolean getUserAddcat()
	{
		return(useraddcat);
	}
	public void setUserAddcat(Boolean _b)
	{
		useraddcat=_b;
	}	
	public Boolean getGuestRead()
	{
		return(guestread);
	}
	public void setGuestRead(Boolean _b)
	{
		guestread=_b;
	}	
	public Boolean getGuestUrledit()
	{
		return(guesturledit);
	}
	public void setGuestUrlEdit(Boolean _b)
	{
		guesturledit=_b;
	}	
	public Boolean getGuestCatedit()
	{
		return(guestcatedit);
	}
	public void setGuestCatedit(Boolean _b)
	{
		guestcatedit=_b;
	}	
	public Boolean getGuestAddurl()
	{
		return(guestaddurl);
	}
	public void setGuestAddurl(Boolean _b)
	{
		guestaddurl=_b;
	}	
	public Boolean getGuestAddcat()
	{
		return(guestaddcat);
	}
	public void setGuestAddcat(Boolean _b)
	{
		guestaddcat=_b;
	}	

	public void printAll()
	{
		System.out.println("id "+id);
		System.out.println("root "+root_id);
		System.out.println("version "+version);
		System.out.println("useraddcat "+useraddcat);
		System.out.println("useraddurl "+useraddurl);
		System.out.println("usercatedit "+usercatedit);
		System.out.println("userurledit "+userurledit);
		System.out.println("useruseradd "+useruseradd);
		System.out.println("guestaddcat "+guestaddcat);
		System.out.println("guestaddurl"+guestaddurl);
		System.out.println("guestcatedit "+guestcatedit);
		System.out.println("guesturledit "+guesturledit);
		System.out.println("guestread "+guestread);
	}
}
