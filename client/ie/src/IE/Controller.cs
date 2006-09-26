using System;
using System.Collections;
using System.Net;
using System.Xml;
using System.Windows.Forms;
using System.Threading;
using System.Text;
using System.Collections.Generic;
using TeamFound.IE.Event;
using System.IO;

namespace TeamFound.IE
{
	/// <summary>
	/// Zusammenfassung für ToolBar.
	/// </summary>
	public class Controller
	{
		public event EventHandler ServersChanged;

		public event EventHandler LoggedIn;

		public event EventHandler LoggedOut;

		private const string version = "3";

		// Singleton pattern - the one and only instance
		private static Controller _instance = new Controller();

		// Event variable for raising search complete events
		public event EventHandler<SearchEventArgs> SearchComplete;

		// the list of all currently active categories
		private Category[] categories;

		//The list of all projects
		private Project[] projects;

		private User[] users;

		private string[] roles = { "User", "Admin" };

		// the current active categorie counter
		private int categoryCounter = 0;

		// the current active configuration
		private Configuration config;

		private Project project;

		private CookieContainer cookies = new CookieContainer();

		private Server server;

		private bool isLoggedIn;

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

		public string[] Roles
		{
			get
			{
				return roles;
			}
		}

		public Project Project
		{
			get { return project; }
			set
			{
				if (project == value)
					return;

				project = value;
				users = null;
				categories = null;
			}
		}

		public Project[] Projects
		{
			get
			{
				if (projects == null)
					projects = LoadProjects();

				return projects;
			}
		}


		/// <summary>
		/// Returns the current active list of categories
		/// </summary>
		public Category[] Categories
		{
			get
			{
				if (project == null)
					return null;

				if (categories == null)
					categories = LoadCategories(project.ID);

				return categories;
			}
		}

		public User[] Users
		{
			get
			{
				if (users == null)
					users = LoadUsers(project);
				return users;
			}
		}

		public Server[] Servers
		{
			get
			{
				return config.Servers;
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

		

		public bool IsLoggedIn
		{
			get { return isLoggedIn; }
			set { isLoggedIn = value; }
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

			string url = GetBaseUrl("search");

			url += "&keyword=" + keywords + categoryString;

			//Response document
			XmlDocument doc = SendRequest(url);
			if (doc == null)
				return;

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

			string commandUrl = GetBaseUrl("addpage");
			commandUrl += catCmd + "&url=" + url;

			XmlDocument doc = SendRequest(commandUrl);

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

			string url = GetBaseUrl("addcategory");
			url += "&name=" + newCatName + "&description=" + newCatdescription + "&subcategoryof=" + category.ID;

			XmlDocument doc = SendRequest(url);

			if (!CheckResult(doc))
				return;

			MessageBox.Show("Kategorie erfolgreich angelegt!");

			categories = LoadCategories(project.ID);
		}

		/// <summary>
		/// Configures the Toolbar
		/// </summary>
		public void EditSettings()
		{
			using (FrmAdmin settings = new FrmAdmin())
			{
				if (settings.ShowDialog() == DialogResult.OK)
					config.Write();
			}
		}

		/// <summary>
		/// Loads all categories from server
		/// </summary>
		/// <returns></returns>
		public Category[] LoadCategories(int project)
		{
			string url = GetBaseUrl("getcategories");
			url += "&projectid=" + project;

			XmlDocument doc = SendRequest(url);
			
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
				LoadCategories(project.ID);
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
				XmlNode node = doc.SelectSingleNode("/response/teamfound/return-value/text()");
				if (0 == Int32.Parse(node.Value))
					return true;

				MessageBox.Show(doc.SelectSingleNode("/response/teamfound/return-description/text()").Value, "Error");

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


		public void AddServer(string url, string user, string password)
		{
			Server server = new Server();
			server.Url = url;
			server.User = user;
			server.Password = password;

			config.AddServer(server);
			config.Write();

			if (ServersChanged != null)
				ServersChanged(this, new EventArgs());
		}

		/// <summary>
		/// Loads all projects from server
		/// </summary>
		/// <returns></returns>
		public Project[] LoadProjects()
		{
			if (server == null)
				return new Project[0];

			string url =  GetBaseUrl("getprojects");

			XmlDocument doc = SendRequest(url);

			if (doc == null)
				return null;

			if (!CheckResult(doc))
				return null;

			List<Project> projects = new List<Project>();

			XmlNode projectsNode = doc.SelectSingleNode("//projects");
			foreach (XmlNode projectNode in projectsNode)
			{
				Project project = new Project();
				project.Name = projectNode.SelectSingleNode("name/text()").Value;
				project.Description = projectNode.SelectSingleNode("description/text()").Value;
				project.ID = Int32.Parse(projectNode.SelectSingleNode("id/text()").Value);
				project.Version = Int32.Parse(projectNode.SelectSingleNode ("version/text()").Value);

				GuestRights guestRights = 0;
				if (projectNode.SelectSingleNode("guestread/text()").Value == "yes")
					guestRights = guestRights | GuestRights.Read;
				if (projectNode.SelectSingleNode("guesturledit/text()").Value == "yes")
					guestRights = guestRights | GuestRights.EditUrl;
				if (projectNode.SelectSingleNode("guestaddurl/text()").Value == "yes")
					guestRights = guestRights | GuestRights.AddUrl;
				if (projectNode.SelectSingleNode("guestcatedit/text()").Value == "yes")
					guestRights = guestRights | GuestRights.EditCategory;
				if (projectNode.SelectSingleNode("guestaddcat/text()").Value == "yes")
					guestRights = guestRights | GuestRights.AddCategory;
				project.GuestRights = guestRights;

				UserRights userRights = 0;
				XmlNode node = projectNode.SelectSingleNode("useraddurl/text()");
				if (node != null && node.Value == "yes")
					userRights = userRights | UserRights.AddUrl;
				node = projectNode.SelectSingleNode("userurledit/text()");
				if (node != null && node.Value == "yes")
					userRights = userRights | UserRights.EditUrl;
				node = projectNode.SelectSingleNode("useraddcat/text()");
				if (node != null && node.Value == "yes")
					userRights = userRights | UserRights.AddCategory;
				node = projectNode.SelectSingleNode("usercatedit/text()");
				if (node != null && node.Value == "yes")
					userRights = userRights | UserRights.EditCategory;
				node = projectNode.SelectSingleNode("useruseradd/text()");
				if (node != null && node.Value == "yes")
					userRights = userRights | UserRights.AddUser;
				project.UserRights = userRights;

				projects.Add(project);
			}

			return projects.ToArray();
		}

		/// <summary>
		/// Loads all users from the given project
		/// </summary>
		/// <param name="project"></param>
		/// <returns></returns>
		private User[] LoadUsers(Project project)
		{
			string url = GetBaseUrl("getuser") + "&projectid=" + project.ID;

			XmlDocument doc = SendRequest(url);

			XmlNodeList userNodes = doc.SelectNodes("//user");

			List<User> result = new List<User>();
			foreach (XmlNode node in userNodes)
			{
				User user = new User();
				user.Name = node.SelectSingleNode("name/text()").Value;
				user.ID = Int32.Parse(node.SelectSingleNode("id/text()").Value);
				user.Role = node.SelectSingleNode("role/text()").Value;
				result.Add(user);
			}

			return result.ToArray();
		}


		/// <summary>
		/// Adds a new Projects to the server
		/// </summary>
		/// <param name="p"></param>
		/// <param name="p_2"></param>
		internal void AddProject(string name, string description)
		{
			string url = GetBaseUrl("addcategory") +
				string.Format("&name={1}&subcategoryof=-1", version, name);

			url += string.IsNullOrEmpty(description) ? "" : "&description=" + description;

			XmlDocument doc = SendRequest(url);

		}

		/// <summary>
		/// Removes the project from the server
		/// </summary>
		/// <param name="project"></param>
		internal void RemoveProject(Project project)
		{
			string url = GetBaseUrl("removecategory");
			url += "&category=" + project.ID;

			XmlDocument doc = SendRequest(url);
			
		}

		/// <summary>
		/// Adds the given user to the current project
		/// </summary>
		/// <param name="p"></param>
		/// <param name="p_2"></param>
		internal void AddUser(string name, string role)
		{
			string url = GetBaseUrl("adduser");
			url += string.Format("&projectid={0}&user={1}&role={2}",project.ID, name, role == roles[0] ? "user" : "projectadmin");

			XmlDocument doc = SendRequest(url);
		}

		/// <summary>
		/// Removes the user from the current project
		/// </summary>
		/// <param name="user"></param>
		internal void RemoveUser(User user)
		{
			string url = GetBaseUrl( "removeuser");
			url += string.Format("&projectid={0}&user={1}", project.ID, user.Name);

			XmlDocument doc = SendRequest(url);
		}

		internal void EditPermissions(Project project, UserRights userRights, GuestRights guestRights)
		{
			string url = GetBaseUrl("editpermissions");
			StringBuilder builder = new StringBuilder(url);

			builder.Append("&projectid=");
			builder.Append(project.ID);
			
			builder.Append("&useruseradd=");
			builder.Append((userRights & UserRights.AddUser) == UserRights.AddUser ? "yes" : "no");
			builder.Append("&useraddurl=");
			builder.Append((userRights & UserRights.AddUrl) == UserRights.AddUrl ? "yes" : "no");
			builder.Append("&userurledit=");
			builder.Append((userRights & UserRights.EditUrl) == UserRights.EditUrl ? "yes" : "no");
			builder.Append("&useraddcat=");
			builder.Append((userRights & UserRights.AddCategory) == UserRights.AddCategory ? "yes" : "no");
			builder.Append("&usercatedit=");
			builder.Append((userRights & UserRights.EditCategory) == UserRights.EditCategory ? "yes" : "no");

			builder.Append("&guestread=");
			builder.Append((guestRights & GuestRights.Read) == GuestRights.Read ? "yes" : "no");
			builder.Append("&guestaddurl=");
			builder.Append((guestRights & GuestRights.AddUrl) == GuestRights.AddUrl ? "yes" : "no");
			builder.Append("&guesturledit=");
			builder.Append((guestRights & GuestRights.EditUrl) == GuestRights.EditUrl ? "yes" : "no");
			builder.Append("&guestaddcat=");
			builder.Append((guestRights & GuestRights.AddCategory) == GuestRights.AddCategory ? "yes" : "no");
			builder.Append("&guestcatedit=");
			builder.Append((guestRights & GuestRights.EditCategory) == GuestRights.EditCategory ? "yes" : "no");

			url = builder.ToString();
			XmlDocument doc = SendRequest(url);
		}


		/// <summary>
		/// command = login
		/// user = hans
		/// pass = qwerty
		/// uniforgeuser = yes/no 
		/// </summary>
		/// <param name="p"></param>
		/// <param name="p_2"></param>
		internal void Login(Server server)
		{
			string url = GetBaseUrl(server,"login") +
				string.Format("&user={0}&pass={1}",server.User, server.Password);

			XmlDocument doc = SendRequest(url);

			if (!CheckResult(doc))
				return;

			if (this.server != null)
				Logout();

			this.server = server;

			isLoggedIn = true;
			project = null;
			projects = null;
			users = null;

			if (LoggedIn != null)
				LoggedIn(this, new EventArgs());
		}

		internal void Logout()
		{
			string url = GetBaseUrl("logout");

			HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
			HttpWebResponse response = null;
			try
			{
				response = (HttpWebResponse)request.GetResponse();
			}
			finally
			{
				if (response != null)
					request.GetResponse().Close();
			}

			isLoggedIn = false;
			if (LoggedOut != null)
				LoggedOut(this, new EventArgs());
		}

		/// <summary>
		/// Returns the base url for the given command
		/// </summary>
		/// <param name="command"></param>
		/// <returns></returns>
		private string GetBaseUrl(string command)
		{
			
			string url = server.Url +
				string.Format("?want=xml&version={0}&command={1}", version,command);

			return url;
		}

		private string GetBaseUrl(Server server, string command)
		{
			string url = server.Url +
				string.Format("?want=xml&version={0}&command={1}", version, command);

			return url;
		}

		private XmlDocument SendRequest(string url)
		{
			HttpWebResponse response = null;
			try
			{
				HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
				request.CookieContainer = cookies;
				response = (HttpWebResponse)request.GetResponse();
				if (!CheckHTTPResponse(response))
					return null;
				if (!(response.ContentType.StartsWith("application/xml")))
				{
					return null;
				}
				
				XmlDocument doc = new XmlDocument();
				doc.Load(new StreamReader(response.GetResponseStream(),Encoding.GetEncoding(response.CharacterSet)));
				return doc;
			}
			catch (Exception e)
			{
				MessageBox.Show(e.Message, "Error");
				return null;
			}
			finally
			{
				if (response != null)
					response.Close();
			}
		}

	}
}
