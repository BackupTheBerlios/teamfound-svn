using System;
using System.Collections.Generic;
using System.Text;

namespace TeamFound.IE
{
	public class Server
	{
		private string url;
		private string user;
		private string password;

		public string Url
		{
			get { return url; }
			set { url = value; }
		}

		public string User
		{
			get { return user; }
			set { user = value; }
		}

		public string Password
		{
			get { return password; }
			set { password = value; }
		}

		public override string ToString()
		{
			return url + " (" + user + ")";
		}
	}
}
