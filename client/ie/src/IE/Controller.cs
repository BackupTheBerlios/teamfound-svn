using System;
using System.Collections;
using System.Net;
using System.Xml;
using System.Windows.Forms;
using System.Threading;
using System.Text;
using System.Collections.Generic;
using TeamFound.IE.Event;

namespace TeamFound.IE
{
	/// <summary>
	/// Zusammenfassung für ToolBar.
	/// </summary>
	public class Controller
	{
		// Singleton pattern - the one and only instance
		private static Controller _instance = new Controller();

		// Event variable for raising search complete events
		public event EventHandler<SearchEventArgs> SearchComplete;

		// the list of all currently active categories
		private Category[] categories;

		// the current active categorie counter
		private int categoryCounter = 0;

		// the current active configuration
		private Configuration config;

		//private constructor - singleton pattern
		private Controller()
		{
			config = Configuration.Read();
		}

		/// <summary>
		/// Returns the one and only instance of the Controller
		/// </summary>
		public static Controller Instance
		{
			get
			{
				return _instance;
			}
		}

		/// <summary>
		/// Returns the current active list of categories
		/// </summary>
		public Category[] Categories
		{
			get
			{
				if (categories == null)
					categories = LoadCategories();
				return categories;
			}
		}

		/// <summary>
		/// Returns the active configuration
		/// </summary>
		public Configuration Config
		{
			get
			{
				return config;
			}
		}

		/// <summary>
		/// "Business Method" - searchs for the given keywords in the given categories
		/// </summary>
		/// <param name="keywords">String containing the query</param>
		/// <param name="categories">Array of categories. if null or length == 0 all
		/// categories will be searched</param>
		public void Search(string keywords, Category[] categories)
		{
			StringBuilder builder = new StringBuilder();

			Category[] searchin = (categories.Length > 0 ? categories : Categories);

			foreach (Category cat in searchin)
			{
				builder.Append("&category=");
				builder.Append(cat.ID);
			}

			string categoryString = builder.ToString();

			string url = config.ServerUrl + "?command=search&want=xml&version=2&keyword=" + keywords + categoryString;

			//Response document
			XmlDocument doc = new XmlDocument();

			HttpWebResponse response = null;
			try
			{
				HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
				response = (HttpWebResponse)request.GetResponse();
				if (!CheckHTTPResponse(response))
					return;

				doc.Load(response.GetResponseStream());
			}
			catch (Exception e)
			{
				MessageBox.Show(e.Message, "Fehler beim Suchen");
				return;
			}
			finally
			{
				if ( response != null )
					response.Close();
			}

			if (!CheckResult(doc))
				return;

			SetCategoryCounter(doc);

			if (SearchComplete != null)
				SearchComplete(this, new SearchEventArgs(url, doc));
		}

		/// <summary>
		/// "Business Method" - Adds another page to the server index
		/// </summary>
		/// <param name="url">The URL of the page</param>
		public void AddPage(string url)
		{
			Category cat = null;
			using (FrmAddPage frm = new FrmAddPage())
			{
				frm.URL = url;
				DialogResult res = frm.ShowDialog();

				if (res != DialogResult.OK)
					return;

				cat = frm.Category;
			}

			string catCmd = (cat == null ? "" : "&category=" + cat.ID);

			string commandUrl = config.ServerUrl + "?version=2&want=xml&command=addpage" + catCmd + "&url=" + url;

			XmlDocument doc = new XmlDocument();

			HttpWebResponse response = null;
			try
			{
				HttpWebRequest request = (HttpWebRequest)WebRequest.Create(commandUrl);
				response = (HttpWebResponse)request.GetResponse();
				if (!CheckHTTPResponse(response))
					return;

				doc.Load(response.GetResponseStream());
			}
			catch (Exception e)
			{
				MessageBox.Show(e.Message, "Fehler");
			}
			finally
			{
				if (response != null)
					response.Close();
			}

			if (!CheckResult(doc))
				return;

			SetCategoryCounter(doc);
		}

		public void AddCategory(Category category)
		{
			string newCatName = null;
			string newCatdescription = null;

			using (FrmAddCategory frmCategory = new FrmAddCategory())
			{
				DialogResult res = frmCategory.ShowDialog();
				if (res != DialogResult.OK)
					return;

				newCatName = frmCategory.CategoryName;
				newCatdescription = frmCategory.CategoryDescription;
			}
			
			DialogResult question 
				= MessageBox.Show(string.Format("Möchten Sie die Kategorie: [{0}] als Unterkategorie von: [{1}] dem aktuellen Projekt hinzufügen!", newCatName, category.Name), "Frage", MessageBoxButtons.YesNo);

			if (question != DialogResult.Yes)
				return;

			string url = config.ServerUrl + "?want=xml&version=2&command=addcategory&name=" + newCatName + "&description=" + newCatdescription + "&subcategoryof=" + category.ID;

			XmlDocument doc = new XmlDocument();

			
			HttpWebResponse response = null;
			try
			{
				HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
				response = (HttpWebResponse)request.GetResponse();
				if (!CheckHTTPResponse(response))
					return;

				
				doc.Load(response.GetResponseStream());
			}
			catch (Exception e)
			{
				MessageBox.Show(e.Message, "Fehler beim hinzufügen der Kategorie!");
				return;
			}
			finally
			{
				if (response != null)
					response.Close();
			}
			if (!CheckResult(doc))
				return;

			MessageBox.Show("Kategorie erfolgreich angelegt!");

			categories = LoadCategories();
		}

		/// <summary>
		/// Configures the Toolbar
		/// </summary>
		public void EditSettings()
		{
			using (FrmSettings settings = new FrmSettings())
			{
				settings.Configuration = config;
				if (settings.ShowDialog() == DialogResult.OK)
					config.Write();
			}
		}

		/// <summary>
		/// Loads all categories from server
		/// </summary>
		/// <returns></returns>
		private Category[] LoadCategories()
		{
			string url = Config.ServerUrl + "?version=2&want=xml&command=getcategories";

			
			HttpWebResponse response = null;

			XmlDocument doc = new XmlDocument();
			try
			{
				HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
				response = (HttpWebResponse)request.GetResponse();
				if (!CheckHTTPResponse(response))
					return new Category[0];
				doc.Load(response.GetResponseStream());
			}
			catch (Exception e)
			{
				MessageBox.Show(e.Message, "Fehler beim Laden der Kategorien");
			}
			finally
			{
				if (response != null)
					response.Close();
			}

			if (!CheckResult(doc))
				return new Category[0];

			List<Category> categories = new List<Category>();

			XmlNode firstCat = doc.SelectSingleNode("//category");

			if (firstCat == null)
				return new Category[0];

			while (firstCat != null)
			{
				Category cat = new Category(firstCat.SelectSingleNode("name/text()").Value);
				cat.ID = int.Parse(firstCat.SelectSingleNode("id/text()").Value);
				categories.Add(cat);
				LoadFurtherCategories(firstCat, cat);

				firstCat = firstCat.NextSibling;
			}

			SetCategoryCounter(doc);

			return categories.ToArray();
		}

		private bool LoadFurtherCategories(XmlNode root, Category cat)
		{
			foreach (XmlNode node in root.SelectNodes("subcategories/category"))
			{
				Category temp = new Category(node.SelectSingleNode("name/text()").Value);
				temp.ID = int.Parse(node.SelectSingleNode("id/text()").Value);
				cat.AddCategory(temp);
				LoadFurtherCategories(node, temp);
			}

			return true;
		}

		private void SetCategoryCounter(XmlDocument doc)
		{
			XmlNode node = doc.SelectSingleNode("//category-counter/text()");
			if (node == null)
				return;

			int count = Int32.Parse(node.Value);
			if (count > categoryCounter)
				LoadCategories();
		}

		internal string GetCategorieName(int p)
		{
			foreach (Category cat in categories)
			{
				if (cat.ID == p)
					return cat.Name;

				string name = GetCategorieName(cat, p);
				if (name != null)
					return name;
			}

			return null;
		}

		internal string GetCategorieName(Category cat, int p)
		{
			foreach (Category tcat in cat.Categories)
			{
				if (tcat.ID == p)
					return tcat.Name;

				string name = GetCategorieName(tcat, p);
				if (name != null)
					return name;
			}
			return null;
		}


		private bool CheckResult(XmlDocument doc)
		{
			try
			{
				XmlNode node = doc.SelectSingleNode("//return-value/text()");
				if (0 == Int32.Parse(node.Value))
					return true;

				MessageBox.Show(doc.SelectSingleNode("//return-description/text()").ToString(), "Fehler");

				return false;
			}
			catch
			{
				return false;
			}

		}

		private bool CheckHTTPResponse(HttpWebResponse response)
		{
			if (response.StatusCode != HttpStatusCode.OK)
			{
				MessageBox.Show(Enum.GetName( typeof( HttpStatusCode), response.StatusCode ), "Fehler");

				return false;
			}

			return true;
		}


	}
}
