using System;
using System.Collections;
using TeamFound.IE.Impl;

namespace TeamFound.IE
{
	delegate void OnCategoryAdded( object sender, ICategory category );

	/// <summary>
	/// Zusammenfassung für ToolBar.
	/// </summary>
	public class Controller
	{

		public Controller()
		{
			//
			// TODO: Fügen Sie hier die Konstruktorlogik hinzu
			//
		}

		public void Search( ICategory[] categories, string[] searchoptions )
		{
			
		}

		public ICategory[] LoadCategories()
		{
			ArrayList categories = new ArrayList();
			Category cat = new Category("Auto");
			cat.AddCategory( new Category("VW") );
			categories.Add( cat );
			cat = new Category( "Computer" );
			cat.AddCategory( new Category( "Hardware" ) );
			cat.AddCategory( new Category( "Software" ) );
			categories.Add( cat );
			return categories.ToArray( typeof ( ICategory ) ) as ICategory[];
		}
	}
}
