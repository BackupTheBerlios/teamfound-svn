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

		public void Read()
		{
			RegistryKey key = Registry.CurrentUser.OpenSubKey( @"Software\Teamfound", true );
			if ( key == null )
				return;

			_serverUrl = (string)key.GetValue( "serverUrl", "http://localhost" );
			key.SetValue( "serverUrl", _serverUrl );
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
