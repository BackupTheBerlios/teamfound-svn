using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using TeamFound.IE.Event;

namespace TeamFound.IE
{
	public partial class CategoryTreeView : TreeView
	{
		public event EventHandler<CategorySelectEventArgs> CategorySelected;
		public CategoryTreeView()
		{
			InitializeComponent();
		}


		public void Load(Category[] categories)
		{
			BeginUpdate();
			Nodes.Clear();
			foreach (Category cat in categories)
			{
				TreeNode node = Nodes.Add(cat.Name);
				node.Tag = cat;
				node.Checked = cat.Selected;
				AddCategoriesToNode(node, cat);
			}
			EndUpdate();
		}
		
		private void AddCategoriesToNode(TreeNode node, Category cat)
		{
			foreach (Category temp in cat.Categories)
			{
				TreeNode tnode = node.Nodes.Add(temp.Name);
				tnode.Tag = temp;
				tnode.Checked = temp.Selected;
				AddCategoriesToNode(tnode, temp);
			}
		}

		protected override void OnAfterCheck(TreeViewEventArgs e)
		{
			base.OnAfterCheck(e);

			if (e.Action != TreeViewAction.ByMouse)
				return;

			Category temp = (Category)e.Node.Tag;
			if (temp != null)
				temp.Selected = e.Node.Checked;

			if (CategorySelected != null)
				CategorySelected(this, new CategorySelectEventArgs(temp, temp.Selected));
		}

	}
}

