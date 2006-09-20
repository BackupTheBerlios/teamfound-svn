using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;

namespace TeamFound.IE.Event
{
	public class SearchEventArgs : EventArgs
	{
		private XmlDocument doc;
		private string url;

		public SearchEventArgs(string url, XmlDocument doc)
		{
			this.doc = doc;
			this.url = url;
		}

		public XmlDocument Result
		{
			get
			{
				return doc;
			}
		}

		public string Url
		{
			get
			{
				return url;
			}
		}
	}
}
