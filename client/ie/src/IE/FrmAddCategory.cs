using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace TeamFound.IE
{
	public partial class FrmAddCategory : Form
	{
		public FrmAddCategory()
		{
			InitializeComponent();
		}

		public string CategoryName
		{
			get
			{
				return txtCategory.Text;
			}
		}

		public string CategoryDescription
		{
			get
			{
				return txtDescription.Text;
			}
		}
	}
}