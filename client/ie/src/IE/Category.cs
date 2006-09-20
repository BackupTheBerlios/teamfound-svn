using System;
using System.Collections.Generic;

namespace TeamFound.IE
{
	/// <summary>
	/// Zusammenfassung für Category.
	/// </summary>
	public class Category
	{
		private string _name = null;
		private List<Category> categories = new List<Category>();
		private int _id;
		private bool selected = false;

		public Category(string name)
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

		public int ID
		{
			get
			{
				return _id;
			}
			set
			{
				_id = value;
			}
		}

		public bool Selected
		{
			get
			{
				return selected;
			}
			set
			{
				selected = value;
			}
		}

		public Category[] Categories
		{
			get
			{
				return categories.ToArray();
			}
		}

		public void AddCategory(Category category)
		{
			categories.Add(category);
		}


	}
}
