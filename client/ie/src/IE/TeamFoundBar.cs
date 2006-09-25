using System;
using System.Collections;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Drawing.Drawing2D;
using System.Runtime.InteropServices;
using System.Text;
using System.Text.RegularExpressions;
using System.Windows.Forms;
using BandObjectLib;
using mshtml;
using System.Xml;
using SHDocVw;
using System.Collections.Generic;
using TeamFound.IE.Event;
namespace TeamFound.IE
{

	/// <summary>
	/// Zusammenfassung für UserControl1.
	/// </summary>
	[Guid("FBDD6587-BB08-40c3-AC51-EDC1B59BB271")]
	[BandObjectAttribute("TeamFound Search Bar", BandObjectStyle.ExplorerToolbar | BandObjectStyle.Horizontal)]
	public class TeamFoundBar : BandObject
	{
		private List<Category> categories = new List<Category>();
		private System.Windows.Forms.ContextMenu ctxServer;
		/// <summary>
		/// Erforderliche Designervariable.
		/// </summary>
		private System.ComponentModel.Container components = null;
		private Joaqs.UI.XpButton btnAdd;
		private Joaqs.UI.XpButton btnSearch;
		private Joaqs.UI.XpButton btnSettings;
		private Joaqs.UI.XpComboBox cbbSearch;
		private Joaqs.UI.XpButton btnCategory;
		private Joaqs.UI.XpButton xpButton1;
		private Joaqs.UI.XpComboBox projectsComboBox;

		private CategoryTreeView tree = new CategoryTreeView();

		public TeamFoundBar()
		{
			// Dieser Aufruf ist für den Windows Form-Designer erforderlich.
			InitializeComponent();

			//Wird ausgelöst wenn wir am Explorer angedockt sind.
			this.ExplorerAttached += new EventHandler(TeamFoundBar_ExplorerAttached);


			tree.Location = new Point(0, 0);
			tree.Size = new Size(320, 200);
			tree.Visible = false;
			tree.LostFocus += new EventHandler(tree_LostFocus);
			tree.BorderStyle = BorderStyle.FixedSingle;
			tree.CategorySelected += new EventHandler<CategorySelectEventArgs>(tree_CategorySelected);
			Controls.Add(tree);
		}





		/// <summary>
		/// Die verwendeten Ressourcen bereinigen.
		/// </summary>
		protected override void Dispose(bool disposing)
		{
			if (disposing)
			{
				if (components != null)
					components.Dispose();
			}
			base.Dispose(disposing);
		}

		#region Vom Komponenten-Designer generierter Code
		/// <summary>
		/// Erforderliche Methode für die Designerunterstützung. 
		/// Der Inhalt der Methode darf nicht mit dem Code-Editor geändert werden.
		/// </summary>
		private void InitializeComponent()
		{
			System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(TeamFoundBar));
			this.ctxServer = new System.Windows.Forms.ContextMenu();
			this.btnAdd = new Joaqs.UI.XpButton();
			this.btnSearch = new Joaqs.UI.XpButton();
			this.btnSettings = new Joaqs.UI.XpButton();
			this.cbbSearch = new Joaqs.UI.XpComboBox();
			this.btnCategory = new Joaqs.UI.XpButton();
			this.xpButton1 = new Joaqs.UI.XpButton();
			this.projectsComboBox = new Joaqs.UI.XpComboBox();
			this.SuspendLayout();
			// 
			// ctxServer
			// 
			this.ctxServer.Popup += new System.EventHandler(this.ctxServer_Popup);
			// 
			// btnAdd
			// 
			this.btnAdd.BackColor = System.Drawing.Color.Transparent;
			this.btnAdd.Location = new System.Drawing.Point(265, 0);
			this.btnAdd.Name = "btnAdd";
			this.btnAdd.Size = new System.Drawing.Size(72, 23);
			this.btnAdd.TabIndex = 9;
			this.btnAdd.Text = "Hinzufügen";
			this.btnAdd.UseVisualStyleBackColor = false;
			this.btnAdd.Click += new System.EventHandler(this.btnAddPage_Click);
			// 
			// btnSearch
			// 
			this.btnSearch.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
			this.btnSearch.Location = new System.Drawing.Point(609, 0);
			this.btnSearch.Name = "btnSearch";
			this.btnSearch.Size = new System.Drawing.Size(75, 23);
			this.btnSearch.TabIndex = 10;
			this.btnSearch.Text = "Suche";
			this.btnSearch.Click += new System.EventHandler(this.btnSearch_Click);
			// 
			// btnSettings
			// 
			this.btnSettings.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
			this.btnSettings.Location = new System.Drawing.Point(689, 0);
			this.btnSettings.Name = "btnSettings";
			this.btnSettings.Size = new System.Drawing.Size(80, 23);
			this.btnSettings.TabIndex = 11;
			this.btnSettings.Text = "Einstellungen";
			this.btnSettings.Click += new System.EventHandler(this.btnSettings_Click);
			// 
			// cbbSearch
			// 
			this.cbbSearch.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
						| System.Windows.Forms.AnchorStyles.Right)));
			this.cbbSearch.Location = new System.Drawing.Point(343, 1);
			this.cbbSearch.Name = "cbbSearch";
			this.cbbSearch.Size = new System.Drawing.Size(258, 21);
			this.cbbSearch.TabIndex = 12;
			this.cbbSearch.KeyUp += new System.Windows.Forms.KeyEventHandler(this.cbbSearch_KeyUp);
			// 
			// btnCategory
			// 
			this.btnCategory.BackColor = System.Drawing.Color.Transparent;
			this.btnCategory.Image = ((System.Drawing.Image)(resources.GetObject("btnCategory.Image")));
			this.btnCategory.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
			this.btnCategory.Location = new System.Drawing.Point(195, 0);
			this.btnCategory.Name = "btnCategory";
			this.btnCategory.RightToLeft = System.Windows.Forms.RightToLeft.No;
			this.btnCategory.Size = new System.Drawing.Size(64, 23);
			this.btnCategory.TabIndex = 13;
			this.btnCategory.Text = "Kategorie";
			this.btnCategory.UseVisualStyleBackColor = false;
			this.btnCategory.Click += new System.EventHandler(this.btnDropDownCategory_Click);
			// 
			// xpButton1
			// 
			this.xpButton1.BackColor = System.Drawing.Color.Transparent;
			this.xpButton1.Image = ((System.Drawing.Image)(resources.GetObject("xpButton1.Image")));
			this.xpButton1.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
			this.xpButton1.Location = new System.Drawing.Point(3, 0);
			this.xpButton1.Name = "xpButton1";
			this.xpButton1.RightToLeft = System.Windows.Forms.RightToLeft.No;
			this.xpButton1.Size = new System.Drawing.Size(64, 23);
			this.xpButton1.TabIndex = 14;
			this.xpButton1.Text = "Server";
			this.xpButton1.UseVisualStyleBackColor = false;
			this.xpButton1.Click += new System.EventHandler(this.xpButton1_Click);
			// 
			// projectsComboBox
			// 
			this.projectsComboBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
						| System.Windows.Forms.AnchorStyles.Right)));
			this.projectsComboBox.Location = new System.Drawing.Point(73, 1);
			this.projectsComboBox.Name = "projectsComboBox";
			this.projectsComboBox.Size = new System.Drawing.Size(116, 21);
			this.projectsComboBox.TabIndex = 15;
			this.projectsComboBox.SelectedIndexChanged += new System.EventHandler(this.projectsComboBox_SelectedIndexChanged);
			this.projectsComboBox.DropDown += new System.EventHandler(this.projectsComboBox_DropDown);
			// 
			// TeamFoundBar
			// 
			this.BackColor = System.Drawing.Color.Gainsboro;
			this.Controls.Add(this.projectsComboBox);
			this.Controls.Add(this.xpButton1);
			this.Controls.Add(this.btnCategory);
			this.Controls.Add(this.cbbSearch);
			this.Controls.Add(this.btnSettings);
			this.Controls.Add(this.btnSearch);
			this.Controls.Add(this.btnAdd);
			this.IntegralSize = new System.Drawing.Size(-1, 23);
			this.Margin = new System.Windows.Forms.Padding(3, 0, 3, 0);
			this.MaximumSize = new System.Drawing.Size(10000, 23);
			this.MaxSize = new System.Drawing.Size(10000, 23);
			this.MinSize = new System.Drawing.Size(700, 23);
			this.Name = "TeamFoundBar";
			this.Size = new System.Drawing.Size(777, 23);
			this.Title = "TeamFound";
			this.Resize += new System.EventHandler(this.TeamFoundBar_Resize);
			this.ResumeLayout(false);

		}
		#endregion

		private void btnDropDownCategory_Click(object sender, System.EventArgs e)
		{
			tree.Location = new Point(this.Left, this.Top + btnCategory.Height);
			tree.Load(Controller.Instance.Categories);
			tree.ExpandAll();
			tree.CheckBoxes = true;
			tree.Visible = true;
			Win32.SetForegroundWindow(tree.Handle.ToInt32());
			tree.Focus();
		}

		private void btnSearch_Click(object sender, System.EventArgs e)
		{
			string searchString = cbbSearch.Text;

			if (string.IsNullOrEmpty(searchString))
				return;


			Controller.Instance.Search(this.cbbSearch.Text, categories.ToArray());
		}

		private void btnAddPage_Click(object sender, System.EventArgs e)
		{
			string url = Explorer.LocationURL;
			Controller.Instance.AddPage(url);
		}

		private void TeamFoundBar_ExplorerAttached(object sender, EventArgs e)
		{
			try
			{
				Category[] categories = Controller.Instance.Categories;
				tree.Parent = null;
				tree.Visible = false;
				Win32.SetParent(tree.Handle.ToInt32(), Explorer.HWND);
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.Message + "\r\n" + ex.StackTrace);
			}
		}

		protected override void OnPaint(PaintEventArgs e)
		{
			base.OnPaint(e);

			try
			{
				using (LinearGradientBrush brush = new LinearGradientBrush(e.ClipRectangle, Color.WhiteSmoke, Color.Gainsboro, LinearGradientMode.Horizontal))
				{
					e.Graphics.FillRectangle(brush, e.ClipRectangle);
				}
			}
			catch { }
		}

		private void btnSettings_Click(object sender, System.EventArgs e)
		{
			Controller.Instance.EditSettings();
		}

		private void TeamFoundBar_Resize(object sender, System.EventArgs e)
		{
			Invalidate();
		}

		private void cbbSearch_KeyUp(object sender, System.Windows.Forms.KeyEventArgs e)
		{
			if (e.KeyCode == Keys.Return)
				btnSearch_Click(btnSearch, e);
		}

		private void tree_CategorySelected(object sender, CategorySelectEventArgs e)
		{
			if (e.Selected)
				this.categories.Add(e.Category);
			else
				this.categories.Remove(e.Category);
		}

		private void tree_LostFocus(object sender, EventArgs e)
		{
			tree.Visible = false;
		}

		private void ctxServer_Popup(object sender, EventArgs e)
		{
			ctxServer.MenuItems.Clear();
			foreach(Server server in Controller.Instance.Servers)
			{
				MenuItem item = new MenuItem();
				item.Text = server.ToString();
				item.Tag = server;
				item.Click +=new EventHandler(item_Click);
				ctxServer.MenuItems.Add(item);
			}
		}

		private void item_Click(object sender, EventArgs e)
		{
			MenuItem item = (MenuItem)sender;
			Server server = (Server)item.Tag;

			Controller.Instance.Login(server);
		}

		private void xpButton1_Click(object sender, EventArgs e)
		{
			ctxServer.Show(xpButton1, new Point(0, xpButton1.Height));
		}

		private void projectsComboBox_DropDown(object sender, EventArgs e)
		{
			projectsComboBox.Items.Clear();
			projectsComboBox.Items.AddRange(Controller.Instance.Projects);
		}

		private void projectsComboBox_SelectedIndexChanged(object sender, EventArgs e)
		{
			Controller.Instance.Project = (Project)projectsComboBox.SelectedItem;
		}
	}

	internal class SearchCondition
	{
		private ArrayList keywords = new ArrayList();

		private string _condition;

		public SearchCondition(string condition)
		{
			_condition = condition;
			ParseCondition(condition);
		}

		public string[] Keywords
		{
			get
			{
				return keywords.ToArray(typeof(string)) as string[];
			}
		}

		public string Condition
		{
			get
			{
				return _condition;
			}
		}

		private void ParseCondition(string condition)
		{
			string[] elements = condition.Split(' ');
			StringBuilder builder = new StringBuilder();

			foreach (String element in elements)
			{
				if (element.ToLower().Equals("and"))
					AddKeyWord(builder);
				else if (element.ToLower().Equals("or"))
					AddKeyWord(builder);
				else
				{
					if (builder.Length > 0)
						builder.Append(" ");
					builder.Append(element);
				}
			}

			AddKeyWord(builder);
		}

		private void AddKeyWord(StringBuilder builder)
		{
			keywords.Add(builder.ToString());
			builder = new StringBuilder();
		}


	}


	internal class CategoryMenuItem : MenuItem
	{
		private Category _category;

		public CategoryMenuItem(string name, Category category)
			: base(name)
		{
			_category = category;
		}

		public Category Category
		{
			get
			{
				return _category;
			}
			set
			{
				_category = value;
			}
		}
	}

}
