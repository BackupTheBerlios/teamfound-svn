using System;
using System.Collections.Generic;
using System.Text;

namespace TeamFound.IE
{
	public class Project
	{
		private string name;
		private string description;
		private int version;
		private int id;
		private GuestRights guestRights;
		private UserRights userRights;

		public string Name
		{
			get { return name; }
			set { name = value; }
		}
		
		public string Description
		{
			get { return description; }
			set { description = value; }
		}

		public int Version
		{
			get { return version; }
			set { version = value; }
		}

		public int ID
		{
			get { return id; }
			set { id = value; }
		}

		public GuestRights GuestRights
		{
			get { return guestRights; }
			set { guestRights = value; }
		}

		public UserRights UserRights
		{
			get { return userRights; }
			set { userRights = value; }
		}
	
		public override string ToString()
		{
			return name + (description == null ? "" : " : " + description);
		}
	}
}
