using System;
using Microsoft.Win32;
using System.Collections.Generic;

namespace TeamFound.IE
{
	/// <summary>
	/// Zusammenfassung für Configuration.
	/// </summary>
	public class Configuration
	{
		private string _serverUrl;
		private List<Server> servers = new List<Server>();

		private Configuration()
		{
			
		}

		public string ServerUrl
		{
			get
			{
				return _serverUrl;
			}
			set
			{
				_serverUrl = value;
			}
		}

		

		public Server[] Servers
		{
			get { return servers.ToArray(); }
		}

		public void AddServer(Server server)
		{
			servers.Add(server);
		}

		public static Configuration Read()
		{
			Configuration cfg = new Configuration();
			RegistryKey key = Registry.CurrentUser.OpenSubKey( @"Software\Teamfound", true );
			if ( key == null )
				return cfg;

			cfg.servers.Clear();

			string[] keyNames = key.GetSubKeyNames();
			foreach (string name in keyNames)
			{
				if (!name.StartsWith("server"))
					continue;

				RegistryKey temp = key.OpenSubKey(name);

				Server server = new Server();
				server.Url = temp.GetValue("url").ToString();
				server.User = temp.GetValue("user").ToString();
				server.Password = temp.GetValue("password").ToString();
				
				cfg.servers.Add(server);
			}
			key.Close();
			return cfg;
		}

		public void Write()
		{
			RegistryKey key = Registry.CurrentUser.OpenSubKey( @"Software\Teamfound", true );
			if ( key == null )
				key = Registry.CurrentUser.CreateSubKey( @"Software\Teamfound" );

			string[] subKeyNames = key.GetSubKeyNames();

			foreach (string name in subKeyNames)
			{
				if (!name.StartsWith("server"))
					continue;

				key.DeleteSubKeyTree(name);
			}

			for (int i = 0; i < servers.Count; i++)
			{
				RegistryKey key2 = key.CreateSubKey("server" + i);
				key2.SetValue("url", servers[i].Url);
				key2.SetValue("user", servers[i].User);
				key2.SetValue("password", servers[i].Password);
				key2.Close();
			}

			key.Close();
		}
	}
}
