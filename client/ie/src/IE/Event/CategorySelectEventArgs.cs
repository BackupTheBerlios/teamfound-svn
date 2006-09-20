using System;
using System.Collections.Generic;
using System.Text;

namespace TeamFound.IE.Event
{
	public class CategorySelectEventArgs : EventArgs
	{
		private Category category;
		private bool selected;

		public CategorySelectEventArgs(Category category, bool selected)
		{
			this.category = category;
			this.selected = selected;
		}

		public Category Category
		{
			get
			{
				return category;
			}
		}

		public bool Selected
		{
			get
			{
				return selected;
			}
		}
	}
}
