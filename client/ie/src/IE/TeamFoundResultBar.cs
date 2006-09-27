using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using BandObjectLib;
using System.Runtime.InteropServices;
using System.Xml;
using TeamFound.IE.Event;

namespace TeamFound.IE
{
	[Guid("E07FA388-5420-4072-9EE6-BF6785CB345A")]
	[BandObjectAttribute("TeamFound Results", BandObjectStyle.Vertical)]
	public partial class TeamFoundResultBar : BandObject
	{
		public TeamFoundResultBar()
		{
			InitializeComponent();
			//Controller.Instance.SearchComplete += new EventHandler<SearchEventArgs>(Instance_SearchComplete);
		}

		void Instance_SearchComplete(object sender, SearchEventArgs e)
		{
			
			treeView1.Nodes.Clear();
			XmlDocument doc = e.Result;

			treeView1.BeginUpdate();
			XmlNodeList list = doc.SelectNodes("//found");
			foreach (XmlNode node in list)
			{
				string text = node.SelectSingleNode("title/text()").Value;
				string url = node.SelectSingleNode("url/text()").Value;

				SearchResultNode result = new SearchResultNode(text, url);
				
				treeView1.Nodes.Add(result);

				XmlNodeList categories = node.SelectNodes("incategory/text()");
				foreach ( XmlNode category in categories )
				{
					string catName = Controller.Instance.GetCategorieName( Int32.Parse(category.Value ) );
					CategoryNode tnode = new CategoryNode(catName);
					result.Nodes.Add(tnode);
				}
			}
			treeView1.EndUpdate();
		}

		private void treeView1_NodeMouseDoubleClick(object sender, TreeNodeMouseClickEventArgs e)
		{

			SearchResultNode cnode = e.Node as SearchResultNode;
			if (cnode != null)
			{
				if (Explorer.LocationURL == cnode.Url)
					return;
				object uri = cnode.Url;
				object flags = null;
				object target = null;
				object postdata = null;
				object headers = null;
				Explorer.Navigate2(ref uri, ref flags, ref target, ref postdata, ref headers);
			}
		}
	}

	internal class SearchResultNode : TreeNode
	{
		private string url;

		public SearchResultNode(string title, string url)
			: base(title)
		{
			this.url = url;
		}

		public string Url
		{
			get
			{
				return url;
			}
		}
	}

	internal class CategoryNode : TreeNode
	{
		public CategoryNode(string name)
			: base(name)
		{

		}
	}
}
