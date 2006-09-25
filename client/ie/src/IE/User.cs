using System;
using System.Collections.Generic;
using System.Text;

namespace TeamFound.IE
{
	public class User
	{
		private string name;

		public string Name
		{
			get { return name; }
			set { name = value; }
		}

		private int id;

		public int ID
		{
			get { return id; }
			set { id = value; }
		}

		private string role;

		public string Role
		{
			get { return role; }
			set { role = value; }
		}

		public override string ToString()
		{
			return name + " (" + role + ")";
		}
	
	}
}
