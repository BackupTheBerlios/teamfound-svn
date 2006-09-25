using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace TeamFound.IE
{
	public partial class FrmAdmin : Form
	{
	
		private UserRights userRights;

		private GuestRights guestRights;

		public FrmAdmin()
		{
			InitializeComponent();

			addUserRoleComboBox.Items.AddRange(Controller.Instance.Roles);
			serversListBox.Items.AddRange(Controller.Instance.Servers);
			Controller.Instance.ServersChanged += new EventHandler(ServersChanged);
			Controller.Instance.LoggedIn += new EventHandler(LoggedIn);
			Controller.Instance.LoggedOut += new EventHandler(LoggedOut);
			mainTabControl.TabPages.Remove(categoriesTabPage);
			mainTabControl.TabPages.Remove(rightsTabPage);
			mainTabControl.TabPages.Remove(usersTabPage);
			mainTabControl.TabPages.Remove(projectsTabPage);
		}

		
		private void LoggedIn(object sender, EventArgs e)
		{
			loginButton.Text = "Logout";
			mainTabControl.TabPages.Add(projectsTabPage);
			mainTabControl.TabPages.Add(usersTabPage);
			mainTabControl.TabPages.Add(rightsTabPage);
			mainTabControl.TabPages.Add(categoriesTabPage);
			mainTabControl.SelectedTab = projectsTabPage;
		}

		private void LoggedOut(object sender, EventArgs e)
		{
			loginButton.Text = "Login";
			mainTabControl.TabPages.Remove(categoriesTabPage);
			mainTabControl.TabPages.Remove(rightsTabPage);
			mainTabControl.TabPages.Remove(usersTabPage);
			mainTabControl.TabPages.Remove(projectsTabPage);
		}

		private void ServersChanged(object sender, EventArgs e)
		{
			serversListBox.Items.Clear();
			serversListBox.Items.AddRange(Controller.Instance.Servers);
		}

		private void removeProjectComboBox_DropDown(object sender, EventArgs e)
		{
			ComboBox box = sender as ComboBox;

			box.Items.Clear();
			box.Items.AddRange(Controller.Instance.Projects);
		}

		private void addProjectButton_Click(object sender, EventArgs e)
		{
			Controller.Instance.AddProject(addProjectTextBox.Text, descriptionTextBox.Text);
		}

		private void removeProjectButton_Click(object sender, EventArgs e)
		{
			Controller.Instance.RemoveProject((Project)removeProjectComboBox.SelectedItem);
		}

		private void loginButton_Click(object sender, EventArgs e)
		{
			if (Controller.Instance.IsLoggedIn)
				Controller.Instance.Logout();
			else
				Controller.Instance.Login((Server)serversComboBox.SelectedItem);
		}

		private void removeUserComboBox_DropDown(object sender, EventArgs e)
		{
			ComboBox box = sender as ComboBox;
			if (box == null)
				return;

			box.Items.Clear();
			box.Items.AddRange(Controller.Instance.Users);
		}

		private void projectComboBox_SelectedIndexChanged(object sender, EventArgs e)
		{
			Controller.Instance.Project = (Project)projectComboBox.SelectedItem;
		}

		private void addUserButton_Click(object sender, EventArgs e)
		{
			Controller.Instance.AddUser(addUserTextBox.Text, (string)addUserRoleComboBox.SelectedItem);
		}

		private void removeUserButton_Click(object sender, EventArgs e)
		{
			Controller.Instance.RemoveUser((User)removeUserComboBox.SelectedItem);
		}

		private void UserRightCheckedChanged(object sender, EventArgs e)
		{
			CheckBox box = sender as CheckBox;
			if (box == null)
				return;

			int tag = Int32.Parse(box.Tag.ToString());

			if (box.Checked)
				userRights = userRights | (UserRights)tag;
			else
				userRights = userRights ^ (UserRights)tag;
		}

		private void guestReadCheckBox_CheckedChanged(object sender, EventArgs e)
		{
			CheckBox box = sender as CheckBox;
			if (box == null)
				return;

			int tag = Int32.Parse(box.Tag.ToString());

			if (box.Checked)
				guestRights = guestRights | (GuestRights)tag;
			else
				guestRights = guestRights ^ (GuestRights)tag;
		}

		private void button1_Click(object sender, EventArgs e)
		{
			Controller.Instance.EditPermissions((Project)projectComboBox2.SelectedItem,
				userRights, guestRights); 
		}

		private void projectComboBox2_SelectedIndexChanged(object sender, EventArgs e)
		{
			Project project = (Project)projectComboBox2.SelectedItem;
			if (project == null)
				return;

			SetUserRights(project.UserRights);
			
			SetGuestRights(project.GuestRights);
		}

		private void SetUserRights(UserRights userRights)
		{
			this.userRights = userRights;

			userAddUrlCheckBox.Checked = (userRights & UserRights.AddUrl) == UserRights.AddUrl;
			userEditUrlCheckBox.Checked = (userRights & UserRights.EditUrl) == UserRights.EditUrl;
			userAddCategoryCheckBox.Checked = (userRights & UserRights.AddCategory) == UserRights.AddCategory;
			userEditCategoryCheckBox.Checked = (userRights & UserRights.EditCategory) == UserRights.EditCategory;
			userAddUserCheckBox.Checked = (userRights & UserRights.AddUser) == UserRights.AddUser;
			userRemoveUserCheckBox.Checked = (userRights & UserRights.RemoveUser) == UserRights.RemoveUser;

		}

		private void SetGuestRights(GuestRights guestRights)
		{
			this.guestRights = guestRights;

			guestReadCheckBox.Checked = (guestRights & GuestRights.Read) == GuestRights.Read;
			guestAddUrlCheckBox.Checked = (guestRights & GuestRights.AddUrl) == GuestRights.AddUrl;
			guestEditUrlCheckBox.Checked = (guestRights & GuestRights.EditUrl) == GuestRights.EditUrl;
			guestAddCategoryCheckBox.Checked = (guestRights & GuestRights.AddCategory) == GuestRights.AddCategory;
			guestEditCategoryCheckBox.Checked = (guestRights & GuestRights.EditCategory) == GuestRights.EditCategory;
		}

		private void serversComboBox_DropDown(object sender, EventArgs e)
		{
			serversComboBox.Items.Clear();
			serversComboBox.Items.AddRange(Controller.Instance.Servers);
		}

		private void addServerButton_Click(object sender, EventArgs e)
		{
			Controller.Instance.AddServer(urlTextBox.Text, userTextBox.Text, passwordTextBox.Text);
		}
	}
}