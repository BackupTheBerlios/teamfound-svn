using System;
using Microsoft.Win32;

namespace TeamFound.IE
{
	/// <summary>
	/// Zusammenfassung für Configuration.
	/// </summary>
	public class Configuration
	{
		private string _serverUrl;

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

		public static Configuration Read()
		{
			Configuration cfg = new Configuration();
			RegistryKey key = Registry.CurrentUser.OpenSubKey( @"Software\Teamfound", true );
			if ( key == null )
				return cfg;


			cfg._serverUrl = (string)key.GetValue( "serverUrl", "http://localhost" );

			return cfg;
		}

		public void Write()
		{
			RegistryKey key = Registry.CurrentUser.OpenSubKey( @"Software\Teamfound", true );
			if ( key == null )
				key = Registry.CurrentUser.CreateSubKey( @"Software\Teamfound" );
			
			key.SetValue( "serverUrl", _serverUrl );
		}
	}

	
}
