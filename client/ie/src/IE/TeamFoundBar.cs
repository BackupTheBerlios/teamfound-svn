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
using TeamFound.IE;

namespace IE
{
	
	/// <summary>
	/// Zusammenfassung für UserControl1.
	/// </summary>
	[Guid("FBDD6587-BB08-40c3-AC51-EDC1B59BB271")]
	[BandObjectAttribute("TeamFound Search Bar", BandObjectStyle.ExplorerToolbar | BandObjectStyle.Horizontal)]
	public class TeamFoundBar : BandObject
	{
		private Controller controller = new Controller();
		private ArrayList categories = new ArrayList();
		private System.Windows.Forms.ContextMenu ctxCategory;
		/// <summary>
		/// Erforderliche Designervariable.
		/// </summary>
		private System.ComponentModel.Container components = null;
		private Joaqs.UI.XpButton btnAdd;
		private Joaqs.UI.XpButton btnSearch;
		private Joaqs.UI.XpButton btnSettings;
		private Joaqs.UI.XpComboBox cbbSearch;
		private Joaqs.UI.XpButton btnCategory;

		private Configuration configuration = new Configuration();

		public TeamFoundBar()
		{
			// Dieser Aufruf ist für den Windows Form-Designer erforderlich.
			InitializeComponent();
			
			//Wird ausgelöst wenn wir am Explorer angedockt sind.
			this.ExplorerAttached+=new EventHandler(TeamFoundBar_ExplorerAttached);
		}

		/// <summary>
		/// Die verwendeten Ressourcen bereinigen.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
			if( disposing )
			{
				if( components != null )
					components.Dispose();
			}
			base.Dispose( disposing );
		}
		#region Vom Komponenten-Designer generierter Code
		/// <summary>
		/// Erforderliche Methode für die Designerunterstützung. 
		/// Der Inhalt der Methode darf nicht mit dem Code-Editor geändert werden.
		/// </summary>
		private void InitializeComponent()
		{
			System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(TeamFoundBar));
			this.ctxCategory = new System.Windows.Forms.ContextMenu();
			this.btnAdd = new Joaqs.UI.XpButton();
			this.btnSearch = new Joaqs.UI.XpButton();
			this.btnSettings = new Joaqs.UI.XpButton();
			this.cbbSearch = new Joaqs.UI.XpComboBox();
			this.btnCategory = new Joaqs.UI.XpButton();
			this.SuspendLayout();
			// 
			// btnAdd
			// 
			this.btnAdd.BackColor = System.Drawing.Color.Transparent;
			this.btnAdd.Location = new System.Drawing.Point(72, 0);
			this.btnAdd.Name = "btnAdd";
			this.btnAdd.Size = new System.Drawing.Size(72, 23);
			this.btnAdd.TabIndex = 9;
			this.btnAdd.Text = "Hinzufügen";
			this.btnAdd.Click += new System.EventHandler(this.btnAddPage_Click);
			// 
			// btnSearch
			// 
			this.btnSearch.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
			this.btnSearch.Location = new System.Drawing.Point(528, 0);
			this.btnSearch.Name = "btnSearch";
			this.btnSearch.TabIndex = 10;
			this.btnSearch.Text = "Suche";
			this.btnSearch.Click += new System.EventHandler(this.btnSearch_Click);
			// 
			// btnSettings
			// 
			this.btnSettings.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
			this.btnSettings.Location = new System.Drawing.Point(608, 0);
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
			this.cbbSearch.Location = new System.Drawing.Point(152, 1);
			this.cbbSearch.Name = "cbbSearch";
			this.cbbSearch.Size = new System.Drawing.Size(368, 21);
			this.cbbSearch.TabIndex = 12;
			this.cbbSearch.KeyUp += new System.Windows.Forms.KeyEventHandler(this.cbbSearch_KeyUp);
			// 
			// btnCategory
			// 
			this.btnCategory.BackColor = System.Drawing.Color.Transparent;
			this.btnCategory.Image = ((System.Drawing.Image)(resources.GetObject("btnCategory.Image")));
			this.btnCategory.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
			this.btnCategory.Location = new System.Drawing.Point(0, 0);
			this.btnCategory.Name = "btnCategory";
			this.btnCategory.RightToLeft = System.Windows.Forms.RightToLeft.No;
			this.btnCategory.Size = new System.Drawing.Size(64, 23);
			this.btnCategory.TabIndex = 13;
			this.btnCategory.Text = "Kategorie";
			this.btnCategory.Click += new System.EventHandler(this.btnDropDownCategory_Click);
			// 
			// TeamFoundBar
			// 
			this.BackColor = System.Drawing.Color.Gainsboro;
			this.Controls.Add(this.btnCategory);
			this.Controls.Add(this.cbbSearch);
			this.Controls.Add(this.btnSettings);
			this.Controls.Add(this.btnSearch);
			this.Controls.Add(this.btnAdd);
			this.MaxSize = new System.Drawing.Size(-1, 23);
			this.MinSize = new System.Drawing.Size(536, 23);
			this.Name = "TeamFoundBar";
			this.Size = new System.Drawing.Size(696, 23);
			this.Title = "TeamFound";
			this.Resize += new System.EventHandler(this.TeamFoundBar_Resize);
			this.ResumeLayout(false);

		}
		#endregion

		private void btnDropDownCategory_Click(object sender, System.EventArgs e)
		{
			ctxCategory.Show( btnCategory, new Point( btnCategory.Left, btnCategory.Top+btnCategory.Height ) );
		}

		private void CategoryMenuClick( object sender, EventArgs e )
		{
			CategoryMenuItem item = sender as CategoryMenuItem;
			if ( item == null )
				return;

			item.Checked = ! item.Checked;

			if ( item.Checked )
			{
				categories.Add( item.Category );
			}
			else
			{
				categories.Remove( item.Category );
			}
		}

		private void btnSearch_Click(object sender, System.EventArgs e)
		{
			if ( Explorer.Busy )
				return;
		
			string searchString = cbbSearch.Text;
			if (searchString.Trim(  ) == "" )
				return;

			object flags = null;
			object targetFrame = null;
			object headers = null;
			object postData = null;
			Explorer.Navigate( configuration.ServerUrl + "/search.pl?keyword=" + searchString, 
				ref flags, ref targetFrame, ref postData, ref headers );
		}

		private void btnAddPage_Click(object sender, System.EventArgs e)
		{
			if ( Explorer.Busy )
				return;
		
			string url = Explorer.LocationURL;
			if (url.Trim(  ) == "" )
				return;

			object flags = null;
			object targetFrame = null;
			object headers = null;
			object postData = null;
			Explorer.Navigate( configuration.ServerUrl + "/addpage.pl?url=" + url, 
				ref flags, ref targetFrame, ref postData, ref headers );
		}

		private void Explorer_NavigateComplete(string URL)
		{
			
		}

		private void TeamFoundBar_ExplorerAttached(object sender, EventArgs e)
		{
			try
			{
				configuration.Read( );

				Explorer.NavigateComplete+=new SHDocVw.DWebBrowserEvents_NavigateCompleteEventHandler(Explorer_NavigateComplete);	
			
				ICategory[] categories = controller.LoadCategories( );

				AddCategoriesToMenu( categories, ctxCategory );
			}
			catch ( Exception ex)
			{
				MessageBox.Show( ex.Message );
			}
		}

		private void AddCategoriesToMenu( ICategory[] categories, Menu menu )
		{
			foreach ( ICategory category in categories )
			{
				CategoryMenuItem item = new CategoryMenuItem( category.Name, category );
				
				item.Click+=new EventHandler( CategoryMenuClick );
				menu.MenuItems.Add( item );
				AddCategoriesToMenu( category.Categories, item );
			}
		}

		protected override void OnPaint(PaintEventArgs e)
		{
			base.OnPaint (e);

			try
			{
				using ( LinearGradientBrush brush = new LinearGradientBrush( e.ClipRectangle,Color.WhiteSmoke,Color.Gainsboro,LinearGradientMode.Horizontal) )
				{
					e.Graphics.FillRectangle( brush, e.ClipRectangle );
				}
			}
			catch {}
		}

		private void btnSettings_Click(object sender, System.EventArgs e)
		{
			using ( FrmSettings settings = new FrmSettings() )
			{
				settings.Configuration = configuration;
				if ( settings.ShowDialog( this ) == DialogResult.OK )
					configuration.Write( );				
			}
		}

		private void TeamFoundBar_Resize(object sender, System.EventArgs e)
		{
			Invalidate();
		}

		private void cbbSearch_KeyUp(object sender, System.Windows.Forms.KeyEventArgs e)
		{
			if ( e.KeyCode == Keys.Return )
				btnSearch_Click( btnSearch, e);
		}
	}

	internal class SearchCondition
	{
		private ArrayList keywords = new ArrayList();

		private string _condition;

		public SearchCondition(string condition)
		{
			_condition = condition;
			ParseCondition( condition );
		}

		public string[] Keywords
		{
			get
			{
				return keywords.ToArray( typeof ( string ) ) as string[];
			}
		}

		public string Condition
		{
			get
			{
				return _condition;
			}
		}

		private void ParseCondition( string condition )
		{
			string[] elements = condition.Split( ' ' );
			StringBuilder builder = new StringBuilder();

			foreach ( String element in elements )
			{
				if ( element.ToLower(  ).Equals( "and" ) )
					AddKeyWord( builder );
				else if ( element.ToLower().Equals( "or" ) ) 
					AddKeyWord( builder );
				else
				{	
					if ( builder.Length > 0)
						builder.Append( " " );
					builder.Append( element );
				}
			}

			AddKeyWord( builder );
		}

		private void AddKeyWord( StringBuilder builder )
		{
			keywords.Add( builder.ToString(  ) );
			builder = new StringBuilder();
		}


	}


	internal class CategoryMenuItem : MenuItem
	{
		private ICategory _category;

		public CategoryMenuItem( string name, ICategory category ): base( name )
		{
			_category = category;
		}

		public ICategory Category
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
