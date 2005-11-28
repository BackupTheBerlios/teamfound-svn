using System;
using System.Collections;

namespace TeamFound.IE.Impl
{
	/// <summary>
	/// Zusammenfassung für Category.
	/// </summary>
	public class Category : ICategory
	{
		private string _name = null;
		private ArrayList categories = new ArrayList();

		public Category( string name )
		{
			_name = name;
		}
	
		public string Name
		{
			get
			{
				return _name;
			}
		}

		public ICategory[] Categories
		{
			get
			{
				return (ICategory[]) categories.ToArray( typeof ( ICategory ) );
			}
		}

		public void AddCategory( ICategory category )
		{
			categories.Add( category );
		}
	}
}
