using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace TeamFound.IE
{
    public partial class FrmAddPage : Form
    {
        public FrmAddPage()
        {
            InitializeComponent();
        }

        private void FrmAddPage_Shown(object sender, EventArgs e)
        {
			trvCats.Load(Controller.Instance.Categories);
        }

        public Category Category
        {
            get
            {
				TreeNode node = trvCats.SelectedNode;
				if (node == null)
					return (Category)trvCats.Nodes[0].Tag;
                
				return (Category)trvCats.SelectedNode.Tag;
            }
        }

        public string URL
        {
            get
            {
                return txtUrl.Text;
            }
            set
            {
                txtUrl.Text = value;
            }
        }

		private void kategorieHinzufügenToolStripMenuItem_Click(object sender, EventArgs e)
		{
			Controller.Instance.AddCategory( (Category)trvCats.SelectedNode.Tag );

			trvCats.Load(Controller.Instance.Categories);
			trvCats.ExpandAll();
		}
    }
}