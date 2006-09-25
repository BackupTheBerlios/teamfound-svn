namespace TeamFound.IE
{
	partial class FrmAdmin
	{
		/// <summary>
		/// Erforderliche Designervariable.
		/// </summary>
		private System.ComponentModel.IContainer components = null;

		/// <summary>
		/// Verwendete Ressourcen bereinigen.
		/// </summary>
		/// <param name="disposing">True, wenn verwaltete Ressourcen gelöscht werden sollen; andernfalls False.</param>
		protected override void Dispose(bool disposing)
		{
			if (disposing && (components != null))
			{
				components.Dispose();
			}
			base.Dispose(disposing);
		}

		#region Vom Windows Form-Designer generierter Code

		/// <summary>
		/// Erforderliche Methode für die Designerunterstützung.
		/// Der Inhalt der Methode darf nicht mit dem Code-Editor geändert werden.
		/// </summary>
		private void InitializeComponent()
		{
			this.mainTabControl = new System.Windows.Forms.TabControl();
			this.serversTabPage = new System.Windows.Forms.TabPage();
			this.passwordLabel = new System.Windows.Forms.Label();
			this.urlLabel = new System.Windows.Forms.Label();
			this.passwordTextBox = new System.Windows.Forms.TextBox();
			this.userTextBox = new System.Windows.Forms.TextBox();
			this.urlTextBox = new System.Windows.Forms.TextBox();
			this.userLabel = new System.Windows.Forms.Label();
			this.serversListBox = new System.Windows.Forms.ListBox();
			this.addServerButton = new System.Windows.Forms.Button();
			this.projectsTabPage = new System.Windows.Forms.TabPage();
			this.projectNameLabel = new System.Windows.Forms.Label();
			this.descriptionLabel = new System.Windows.Forms.Label();
			this.descriptionTextBox = new System.Windows.Forms.TextBox();
			this.removeProjectLabel = new System.Windows.Forms.Label();
			this.addProjectLabel = new System.Windows.Forms.Label();
			this.removeProjectComboBox = new System.Windows.Forms.ComboBox();
			this.addProjectTextBox = new System.Windows.Forms.TextBox();
			this.removeProjectButton = new System.Windows.Forms.Button();
			this.addProjectButton = new System.Windows.Forms.Button();
			this.usersTabPage = new System.Windows.Forms.TabPage();
			this.addUserRoleLabel = new System.Windows.Forms.Label();
			this.addUserNameLabel = new System.Windows.Forms.Label();
			this.addUserRoleComboBox = new System.Windows.Forms.ComboBox();
			this.removeUserLabel = new System.Windows.Forms.Label();
			this.removeUserComboBox = new System.Windows.Forms.ComboBox();
			this.removeUserButton = new System.Windows.Forms.Button();
			this.addUserTextBox = new System.Windows.Forms.TextBox();
			this.addUserLabel = new System.Windows.Forms.Label();
			this.addUserButton = new System.Windows.Forms.Button();
			this.projectLabel = new System.Windows.Forms.Label();
			this.projectComboBox = new System.Windows.Forms.ComboBox();
			this.rightsTabPage = new System.Windows.Forms.TabPage();
			this.setUserRightsButton = new System.Windows.Forms.Button();
			this.userRemoveUserCheckBox = new System.Windows.Forms.CheckBox();
			this.guestReadCheckBox = new System.Windows.Forms.CheckBox();
			this.userAddUserCheckBox = new System.Windows.Forms.CheckBox();
			this.userEditCategoryCheckBox = new System.Windows.Forms.CheckBox();
			this.userAddCategoryCheckBox = new System.Windows.Forms.CheckBox();
			this.userEditUrlCheckBox = new System.Windows.Forms.CheckBox();
			this.userAddUrlCheckBox = new System.Windows.Forms.CheckBox();
			this.guestEditCategoryCheckBox = new System.Windows.Forms.CheckBox();
			this.guestAddCategoryCheckBox = new System.Windows.Forms.CheckBox();
			this.guestEditUrlCheckBox = new System.Windows.Forms.CheckBox();
			this.guestAddUrlCheckBox = new System.Windows.Forms.CheckBox();
			this.label2 = new System.Windows.Forms.Label();
			this.label1 = new System.Windows.Forms.Label();
			this.projectComboBox2 = new System.Windows.Forms.ComboBox();
			this.projectLabel2 = new System.Windows.Forms.Label();
			this.categoriesTabPage = new System.Windows.Forms.TabPage();
			this.label5 = new System.Windows.Forms.Label();
			this.textBox1 = new System.Windows.Forms.TextBox();
			this.label4 = new System.Windows.Forms.Label();
			this.button1 = new System.Windows.Forms.Button();
			this.treeView1 = new System.Windows.Forms.TreeView();
			this.label3 = new System.Windows.Forms.Label();
			this.comboBox1 = new System.Windows.Forms.ComboBox();
			this.okButton = new System.Windows.Forms.Button();
			this.serverLabel = new System.Windows.Forms.Label();
			this.loginButton = new System.Windows.Forms.Button();
			this.serversComboBox = new System.Windows.Forms.ComboBox();
			this.mainTabControl.SuspendLayout();
			this.serversTabPage.SuspendLayout();
			this.projectsTabPage.SuspendLayout();
			this.usersTabPage.SuspendLayout();
			this.rightsTabPage.SuspendLayout();
			this.categoriesTabPage.SuspendLayout();
			this.SuspendLayout();
			// 
			// mainTabControl
			// 
			this.mainTabControl.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
						| System.Windows.Forms.AnchorStyles.Left)
						| System.Windows.Forms.AnchorStyles.Right)));
			this.mainTabControl.Controls.Add(this.serversTabPage);
			this.mainTabControl.Controls.Add(this.projectsTabPage);
			this.mainTabControl.Controls.Add(this.usersTabPage);
			this.mainTabControl.Controls.Add(this.rightsTabPage);
			this.mainTabControl.Controls.Add(this.categoriesTabPage);
			this.mainTabControl.Location = new System.Drawing.Point(12, 40);
			this.mainTabControl.Name = "mainTabControl";
			this.mainTabControl.SelectedIndex = 0;
			this.mainTabControl.Size = new System.Drawing.Size(410, 285);
			this.mainTabControl.TabIndex = 0;
			// 
			// serversTabPage
			// 
			this.serversTabPage.Controls.Add(this.passwordLabel);
			this.serversTabPage.Controls.Add(this.urlLabel);
			this.serversTabPage.Controls.Add(this.passwordTextBox);
			this.serversTabPage.Controls.Add(this.userTextBox);
			this.serversTabPage.Controls.Add(this.urlTextBox);
			this.serversTabPage.Controls.Add(this.userLabel);
			this.serversTabPage.Controls.Add(this.serversListBox);
			this.serversTabPage.Controls.Add(this.addServerButton);
			this.serversTabPage.Location = new System.Drawing.Point(4, 22);
			this.serversTabPage.Name = "serversTabPage";
			this.serversTabPage.Padding = new System.Windows.Forms.Padding(3);
			this.serversTabPage.Size = new System.Drawing.Size(402, 259);
			this.serversTabPage.TabIndex = 4;
			this.serversTabPage.Text = "Servers";
			this.serversTabPage.UseVisualStyleBackColor = true;
			// 
			// passwordLabel
			// 
			this.passwordLabel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
			this.passwordLabel.AutoSize = true;
			this.passwordLabel.Location = new System.Drawing.Point(147, 235);
			this.passwordLabel.Name = "passwordLabel";
			this.passwordLabel.Size = new System.Drawing.Size(53, 13);
			this.passwordLabel.TabIndex = 7;
			this.passwordLabel.Text = "Password";
			// 
			// urlLabel
			// 
			this.urlLabel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
			this.urlLabel.AutoSize = true;
			this.urlLabel.Location = new System.Drawing.Point(6, 209);
			this.urlLabel.Name = "urlLabel";
			this.urlLabel.Size = new System.Drawing.Size(20, 13);
			this.urlLabel.TabIndex = 6;
			this.urlLabel.Text = "Url";
			// 
			// passwordTextBox
			// 
			this.passwordTextBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)
						| System.Windows.Forms.AnchorStyles.Right)));
			this.passwordTextBox.Location = new System.Drawing.Point(206, 232);
			this.passwordTextBox.Name = "passwordTextBox";
			this.passwordTextBox.Size = new System.Drawing.Size(100, 20);
			this.passwordTextBox.TabIndex = 5;
			// 
			// userTextBox
			// 
			this.userTextBox.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
			this.userTextBox.Location = new System.Drawing.Point(41, 232);
			this.userTextBox.Name = "userTextBox";
			this.userTextBox.Size = new System.Drawing.Size(100, 20);
			this.userTextBox.TabIndex = 4;
			// 
			// urlTextBox
			// 
			this.urlTextBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)
						| System.Windows.Forms.AnchorStyles.Right)));
			this.urlTextBox.Location = new System.Drawing.Point(41, 206);
			this.urlTextBox.Name = "urlTextBox";
			this.urlTextBox.Size = new System.Drawing.Size(265, 20);
			this.urlTextBox.TabIndex = 3;
			// 
			// userLabel
			// 
			this.userLabel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
			this.userLabel.AutoSize = true;
			this.userLabel.Location = new System.Drawing.Point(6, 235);
			this.userLabel.Name = "userLabel";
			this.userLabel.Size = new System.Drawing.Size(29, 13);
			this.userLabel.TabIndex = 2;
			this.userLabel.Text = "User";
			// 
			// serversListBox
			// 
			this.serversListBox.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
						| System.Windows.Forms.AnchorStyles.Left)
						| System.Windows.Forms.AnchorStyles.Right)));
			this.serversListBox.FormattingEnabled = true;
			this.serversListBox.Location = new System.Drawing.Point(6, 6);
			this.serversListBox.Name = "serversListBox";
			this.serversListBox.Size = new System.Drawing.Size(390, 186);
			this.serversListBox.TabIndex = 1;
			// 
			// addServerButton
			// 
			this.addServerButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.addServerButton.Location = new System.Drawing.Point(321, 230);
			this.addServerButton.Name = "addServerButton";
			this.addServerButton.Size = new System.Drawing.Size(75, 23);
			this.addServerButton.TabIndex = 0;
			this.addServerButton.Text = "Add";
			this.addServerButton.UseVisualStyleBackColor = true;
			this.addServerButton.Click += new System.EventHandler(this.addServerButton_Click);
			// 
			// projectsTabPage
			// 
			this.projectsTabPage.Controls.Add(this.projectNameLabel);
			this.projectsTabPage.Controls.Add(this.descriptionLabel);
			this.projectsTabPage.Controls.Add(this.descriptionTextBox);
			this.projectsTabPage.Controls.Add(this.removeProjectLabel);
			this.projectsTabPage.Controls.Add(this.addProjectLabel);
			this.projectsTabPage.Controls.Add(this.removeProjectComboBox);
			this.projectsTabPage.Controls.Add(this.addProjectTextBox);
			this.projectsTabPage.Controls.Add(this.removeProjectButton);
			this.projectsTabPage.Controls.Add(this.addProjectButton);
			this.projectsTabPage.Location = new System.Drawing.Point(4, 22);
			this.projectsTabPage.Name = "projectsTabPage";
			this.projectsTabPage.Padding = new System.Windows.Forms.Padding(3);
			this.projectsTabPage.Size = new System.Drawing.Size(402, 259);
			this.projectsTabPage.TabIndex = 0;
			this.projectsTabPage.Text = "Projects";
			this.projectsTabPage.UseVisualStyleBackColor = true;
			// 
			// projectNameLabel
			// 
			this.projectNameLabel.AutoSize = true;
			this.projectNameLabel.Location = new System.Drawing.Point(29, 39);
			this.projectNameLabel.Name = "projectNameLabel";
			this.projectNameLabel.Size = new System.Drawing.Size(35, 13);
			this.projectNameLabel.TabIndex = 8;
			this.projectNameLabel.Text = "Name";
			// 
			// descriptionLabel
			// 
			this.descriptionLabel.AutoSize = true;
			this.descriptionLabel.Location = new System.Drawing.Point(29, 65);
			this.descriptionLabel.Name = "descriptionLabel";
			this.descriptionLabel.Size = new System.Drawing.Size(60, 13);
			this.descriptionLabel.TabIndex = 7;
			this.descriptionLabel.Text = "Description";
			// 
			// descriptionTextBox
			// 
			this.descriptionTextBox.Location = new System.Drawing.Point(95, 62);
			this.descriptionTextBox.Name = "descriptionTextBox";
			this.descriptionTextBox.Size = new System.Drawing.Size(220, 20);
			this.descriptionTextBox.TabIndex = 6;
			// 
			// removeProjectLabel
			// 
			this.removeProjectLabel.AutoSize = true;
			this.removeProjectLabel.Location = new System.Drawing.Point(6, 108);
			this.removeProjectLabel.Name = "removeProjectLabel";
			this.removeProjectLabel.Size = new System.Drawing.Size(83, 13);
			this.removeProjectLabel.TabIndex = 5;
			this.removeProjectLabel.Text = "Remove Project";
			// 
			// addProjectLabel
			// 
			this.addProjectLabel.AutoSize = true;
			this.addProjectLabel.Location = new System.Drawing.Point(6, 13);
			this.addProjectLabel.Name = "addProjectLabel";
			this.addProjectLabel.Size = new System.Drawing.Size(62, 13);
			this.addProjectLabel.TabIndex = 4;
			this.addProjectLabel.Text = "Add Project";
			// 
			// removeProjectComboBox
			// 
			this.removeProjectComboBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
						| System.Windows.Forms.AnchorStyles.Right)));
			this.removeProjectComboBox.FormattingEnabled = true;
			this.removeProjectComboBox.Location = new System.Drawing.Point(95, 103);
			this.removeProjectComboBox.Name = "removeProjectComboBox";
			this.removeProjectComboBox.Size = new System.Drawing.Size(220, 21);
			this.removeProjectComboBox.TabIndex = 3;
			this.removeProjectComboBox.DropDown += new System.EventHandler(this.removeProjectComboBox_DropDown);
			// 
			// addProjectTextBox
			// 
			this.addProjectTextBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
						| System.Windows.Forms.AnchorStyles.Right)));
			this.addProjectTextBox.Location = new System.Drawing.Point(95, 36);
			this.addProjectTextBox.Name = "addProjectTextBox";
			this.addProjectTextBox.Size = new System.Drawing.Size(220, 20);
			this.addProjectTextBox.TabIndex = 2;
			// 
			// removeProjectButton
			// 
			this.removeProjectButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
			this.removeProjectButton.Location = new System.Drawing.Point(321, 103);
			this.removeProjectButton.Name = "removeProjectButton";
			this.removeProjectButton.Size = new System.Drawing.Size(75, 23);
			this.removeProjectButton.TabIndex = 1;
			this.removeProjectButton.Text = "Remove";
			this.removeProjectButton.UseVisualStyleBackColor = true;
			this.removeProjectButton.Click += new System.EventHandler(this.removeProjectButton_Click);
			// 
			// addProjectButton
			// 
			this.addProjectButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
			this.addProjectButton.Location = new System.Drawing.Point(321, 62);
			this.addProjectButton.Name = "addProjectButton";
			this.addProjectButton.Size = new System.Drawing.Size(75, 23);
			this.addProjectButton.TabIndex = 0;
			this.addProjectButton.Text = "Add";
			this.addProjectButton.UseVisualStyleBackColor = true;
			this.addProjectButton.Click += new System.EventHandler(this.addProjectButton_Click);
			// 
			// usersTabPage
			// 
			this.usersTabPage.Controls.Add(this.addUserRoleLabel);
			this.usersTabPage.Controls.Add(this.addUserNameLabel);
			this.usersTabPage.Controls.Add(this.addUserRoleComboBox);
			this.usersTabPage.Controls.Add(this.removeUserLabel);
			this.usersTabPage.Controls.Add(this.removeUserComboBox);
			this.usersTabPage.Controls.Add(this.removeUserButton);
			this.usersTabPage.Controls.Add(this.addUserTextBox);
			this.usersTabPage.Controls.Add(this.addUserLabel);
			this.usersTabPage.Controls.Add(this.addUserButton);
			this.usersTabPage.Controls.Add(this.projectLabel);
			this.usersTabPage.Controls.Add(this.projectComboBox);
			this.usersTabPage.Location = new System.Drawing.Point(4, 22);
			this.usersTabPage.Name = "usersTabPage";
			this.usersTabPage.Padding = new System.Windows.Forms.Padding(3);
			this.usersTabPage.Size = new System.Drawing.Size(402, 259);
			this.usersTabPage.TabIndex = 1;
			this.usersTabPage.Text = "User";
			this.usersTabPage.UseVisualStyleBackColor = true;
			// 
			// addUserRoleLabel
			// 
			this.addUserRoleLabel.AutoSize = true;
			this.addUserRoleLabel.Location = new System.Drawing.Point(25, 91);
			this.addUserRoleLabel.Name = "addUserRoleLabel";
			this.addUserRoleLabel.Size = new System.Drawing.Size(29, 13);
			this.addUserRoleLabel.TabIndex = 10;
			this.addUserRoleLabel.Text = "Role";
			// 
			// addUserNameLabel
			// 
			this.addUserNameLabel.AutoSize = true;
			this.addUserNameLabel.Location = new System.Drawing.Point(25, 65);
			this.addUserNameLabel.Name = "addUserNameLabel";
			this.addUserNameLabel.Size = new System.Drawing.Size(35, 13);
			this.addUserNameLabel.TabIndex = 9;
			this.addUserNameLabel.Text = "Name";
			// 
			// addUserRoleComboBox
			// 
			this.addUserRoleComboBox.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
			this.addUserRoleComboBox.FormattingEnabled = true;
			this.addUserRoleComboBox.Location = new System.Drawing.Point(87, 88);
			this.addUserRoleComboBox.Name = "addUserRoleComboBox";
			this.addUserRoleComboBox.Size = new System.Drawing.Size(228, 21);
			this.addUserRoleComboBox.TabIndex = 8;
			// 
			// removeUserLabel
			// 
			this.removeUserLabel.AutoSize = true;
			this.removeUserLabel.Location = new System.Drawing.Point(6, 141);
			this.removeUserLabel.Name = "removeUserLabel";
			this.removeUserLabel.Size = new System.Drawing.Size(75, 13);
			this.removeUserLabel.TabIndex = 7;
			this.removeUserLabel.Text = "Remove User:";
			// 
			// removeUserComboBox
			// 
			this.removeUserComboBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
						| System.Windows.Forms.AnchorStyles.Right)));
			this.removeUserComboBox.FormattingEnabled = true;
			this.removeUserComboBox.Location = new System.Drawing.Point(87, 138);
			this.removeUserComboBox.Name = "removeUserComboBox";
			this.removeUserComboBox.Size = new System.Drawing.Size(228, 21);
			this.removeUserComboBox.TabIndex = 6;
			this.removeUserComboBox.DropDown += new System.EventHandler(this.removeUserComboBox_DropDown);
			// 
			// removeUserButton
			// 
			this.removeUserButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
			this.removeUserButton.Location = new System.Drawing.Point(321, 136);
			this.removeUserButton.Name = "removeUserButton";
			this.removeUserButton.Size = new System.Drawing.Size(75, 23);
			this.removeUserButton.TabIndex = 5;
			this.removeUserButton.Text = "Remove";
			this.removeUserButton.UseVisualStyleBackColor = true;
			this.removeUserButton.Click += new System.EventHandler(this.removeUserButton_Click);
			// 
			// addUserTextBox
			// 
			this.addUserTextBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
						| System.Windows.Forms.AnchorStyles.Right)));
			this.addUserTextBox.Location = new System.Drawing.Point(87, 62);
			this.addUserTextBox.Name = "addUserTextBox";
			this.addUserTextBox.Size = new System.Drawing.Size(228, 20);
			this.addUserTextBox.TabIndex = 4;
			// 
			// addUserLabel
			// 
			this.addUserLabel.AutoSize = true;
			this.addUserLabel.Location = new System.Drawing.Point(6, 40);
			this.addUserLabel.Name = "addUserLabel";
			this.addUserLabel.Size = new System.Drawing.Size(54, 13);
			this.addUserLabel.TabIndex = 3;
			this.addUserLabel.Text = "Add User:";
			// 
			// addUserButton
			// 
			this.addUserButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
			this.addUserButton.Location = new System.Drawing.Point(321, 86);
			this.addUserButton.Name = "addUserButton";
			this.addUserButton.Size = new System.Drawing.Size(75, 23);
			this.addUserButton.TabIndex = 2;
			this.addUserButton.Text = "Add";
			this.addUserButton.UseVisualStyleBackColor = true;
			this.addUserButton.Click += new System.EventHandler(this.addUserButton_Click);
			// 
			// projectLabel
			// 
			this.projectLabel.AutoSize = true;
			this.projectLabel.Location = new System.Drawing.Point(6, 9);
			this.projectLabel.Name = "projectLabel";
			this.projectLabel.Size = new System.Drawing.Size(40, 13);
			this.projectLabel.TabIndex = 1;
			this.projectLabel.Text = "Project";
			// 
			// projectComboBox
			// 
			this.projectComboBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
						| System.Windows.Forms.AnchorStyles.Right)));
			this.projectComboBox.FormattingEnabled = true;
			this.projectComboBox.Location = new System.Drawing.Point(52, 6);
			this.projectComboBox.Name = "projectComboBox";
			this.projectComboBox.Size = new System.Drawing.Size(344, 21);
			this.projectComboBox.TabIndex = 0;
			this.projectComboBox.SelectedIndexChanged += new System.EventHandler(this.projectComboBox_SelectedIndexChanged);
			this.projectComboBox.DropDown += new System.EventHandler(this.removeProjectComboBox_DropDown);
			// 
			// rightsTabPage
			// 
			this.rightsTabPage.Controls.Add(this.setUserRightsButton);
			this.rightsTabPage.Controls.Add(this.userRemoveUserCheckBox);
			this.rightsTabPage.Controls.Add(this.guestReadCheckBox);
			this.rightsTabPage.Controls.Add(this.userAddUserCheckBox);
			this.rightsTabPage.Controls.Add(this.userEditCategoryCheckBox);
			this.rightsTabPage.Controls.Add(this.userAddCategoryCheckBox);
			this.rightsTabPage.Controls.Add(this.userEditUrlCheckBox);
			this.rightsTabPage.Controls.Add(this.userAddUrlCheckBox);
			this.rightsTabPage.Controls.Add(this.guestEditCategoryCheckBox);
			this.rightsTabPage.Controls.Add(this.guestAddCategoryCheckBox);
			this.rightsTabPage.Controls.Add(this.guestEditUrlCheckBox);
			this.rightsTabPage.Controls.Add(this.guestAddUrlCheckBox);
			this.rightsTabPage.Controls.Add(this.label2);
			this.rightsTabPage.Controls.Add(this.label1);
			this.rightsTabPage.Controls.Add(this.projectComboBox2);
			this.rightsTabPage.Controls.Add(this.projectLabel2);
			this.rightsTabPage.Location = new System.Drawing.Point(4, 22);
			this.rightsTabPage.Name = "rightsTabPage";
			this.rightsTabPage.Padding = new System.Windows.Forms.Padding(3);
			this.rightsTabPage.Size = new System.Drawing.Size(402, 259);
			this.rightsTabPage.TabIndex = 2;
			this.rightsTabPage.Text = "Rights";
			this.rightsTabPage.UseVisualStyleBackColor = true;
			// 
			// setUserRightsButton
			// 
			this.setUserRightsButton.Location = new System.Drawing.Point(321, 216);
			this.setUserRightsButton.Name = "setUserRightsButton";
			this.setUserRightsButton.Size = new System.Drawing.Size(75, 23);
			this.setUserRightsButton.TabIndex = 15;
			this.setUserRightsButton.Text = "Set";
			this.setUserRightsButton.UseVisualStyleBackColor = true;
			this.setUserRightsButton.Click += new System.EventHandler(this.button1_Click);
			// 
			// userRemoveUserCheckBox
			// 
			this.userRemoveUserCheckBox.AutoSize = true;
			this.userRemoveUserCheckBox.Location = new System.Drawing.Point(230, 184);
			this.userRemoveUserCheckBox.Name = "userRemoveUserCheckBox";
			this.userRemoveUserCheckBox.Size = new System.Drawing.Size(88, 17);
			this.userRemoveUserCheckBox.TabIndex = 14;
			this.userRemoveUserCheckBox.Tag = "32";
			this.userRemoveUserCheckBox.Text = "RemoveUser";
			this.userRemoveUserCheckBox.UseVisualStyleBackColor = true;
			this.userRemoveUserCheckBox.CheckedChanged += new System.EventHandler(this.UserRightCheckedChanged);
			// 
			// guestReadCheckBox
			// 
			this.guestReadCheckBox.AutoSize = true;
			this.guestReadCheckBox.Location = new System.Drawing.Point(26, 68);
			this.guestReadCheckBox.Name = "guestReadCheckBox";
			this.guestReadCheckBox.Size = new System.Drawing.Size(52, 17);
			this.guestReadCheckBox.TabIndex = 13;
			this.guestReadCheckBox.Tag = "1";
			this.guestReadCheckBox.Text = "Read";
			this.guestReadCheckBox.UseVisualStyleBackColor = true;
			this.guestReadCheckBox.CheckedChanged += new System.EventHandler(this.guestReadCheckBox_CheckedChanged);
			// 
			// userAddUserCheckBox
			// 
			this.userAddUserCheckBox.AutoSize = true;
			this.userAddUserCheckBox.Location = new System.Drawing.Point(229, 160);
			this.userAddUserCheckBox.Name = "userAddUserCheckBox";
			this.userAddUserCheckBox.Size = new System.Drawing.Size(70, 17);
			this.userAddUserCheckBox.TabIndex = 12;
			this.userAddUserCheckBox.Tag = "16";
			this.userAddUserCheckBox.Text = "Add User";
			this.userAddUserCheckBox.UseVisualStyleBackColor = true;
			this.userAddUserCheckBox.CheckedChanged += new System.EventHandler(this.UserRightCheckedChanged);
			// 
			// userEditCategoryCheckBox
			// 
			this.userEditCategoryCheckBox.AutoSize = true;
			this.userEditCategoryCheckBox.Location = new System.Drawing.Point(229, 137);
			this.userEditCategoryCheckBox.Name = "userEditCategoryCheckBox";
			this.userEditCategoryCheckBox.Size = new System.Drawing.Size(89, 17);
			this.userEditCategoryCheckBox.TabIndex = 11;
			this.userEditCategoryCheckBox.Tag = "8";
			this.userEditCategoryCheckBox.Text = "Edit Category";
			this.userEditCategoryCheckBox.UseVisualStyleBackColor = true;
			this.userEditCategoryCheckBox.CheckedChanged += new System.EventHandler(this.UserRightCheckedChanged);
			// 
			// userAddCategoryCheckBox
			// 
			this.userAddCategoryCheckBox.AutoSize = true;
			this.userAddCategoryCheckBox.Location = new System.Drawing.Point(229, 114);
			this.userAddCategoryCheckBox.Name = "userAddCategoryCheckBox";
			this.userAddCategoryCheckBox.Size = new System.Drawing.Size(90, 17);
			this.userAddCategoryCheckBox.TabIndex = 10;
			this.userAddCategoryCheckBox.Tag = "4";
			this.userAddCategoryCheckBox.Text = "Add Category";
			this.userAddCategoryCheckBox.UseVisualStyleBackColor = true;
			this.userAddCategoryCheckBox.CheckedChanged += new System.EventHandler(this.UserRightCheckedChanged);
			// 
			// userEditUrlCheckBox
			// 
			this.userEditUrlCheckBox.AutoSize = true;
			this.userEditUrlCheckBox.Location = new System.Drawing.Point(229, 91);
			this.userEditUrlCheckBox.Name = "userEditUrlCheckBox";
			this.userEditUrlCheckBox.Size = new System.Drawing.Size(60, 17);
			this.userEditUrlCheckBox.TabIndex = 9;
			this.userEditUrlCheckBox.Tag = "2";
			this.userEditUrlCheckBox.Text = "Edit Url";
			this.userEditUrlCheckBox.UseVisualStyleBackColor = true;
			this.userEditUrlCheckBox.CheckedChanged += new System.EventHandler(this.UserRightCheckedChanged);
			// 
			// userAddUrlCheckBox
			// 
			this.userAddUrlCheckBox.AutoSize = true;
			this.userAddUrlCheckBox.Location = new System.Drawing.Point(229, 68);
			this.userAddUrlCheckBox.Name = "userAddUrlCheckBox";
			this.userAddUrlCheckBox.Size = new System.Drawing.Size(61, 17);
			this.userAddUrlCheckBox.TabIndex = 8;
			this.userAddUrlCheckBox.Tag = "1";
			this.userAddUrlCheckBox.Text = "Add Url";
			this.userAddUrlCheckBox.UseVisualStyleBackColor = true;
			this.userAddUrlCheckBox.CheckedChanged += new System.EventHandler(this.UserRightCheckedChanged);
			// 
			// guestEditCategoryCheckBox
			// 
			this.guestEditCategoryCheckBox.AutoSize = true;
			this.guestEditCategoryCheckBox.Location = new System.Drawing.Point(26, 160);
			this.guestEditCategoryCheckBox.Name = "guestEditCategoryCheckBox";
			this.guestEditCategoryCheckBox.Size = new System.Drawing.Size(89, 17);
			this.guestEditCategoryCheckBox.TabIndex = 7;
			this.guestEditCategoryCheckBox.Tag = "16";
			this.guestEditCategoryCheckBox.Text = "Edit Category";
			this.guestEditCategoryCheckBox.UseVisualStyleBackColor = true;
			this.guestEditCategoryCheckBox.CheckedChanged += new System.EventHandler(this.guestReadCheckBox_CheckedChanged);
			// 
			// guestAddCategoryCheckBox
			// 
			this.guestAddCategoryCheckBox.AutoSize = true;
			this.guestAddCategoryCheckBox.Location = new System.Drawing.Point(26, 137);
			this.guestAddCategoryCheckBox.Name = "guestAddCategoryCheckBox";
			this.guestAddCategoryCheckBox.Size = new System.Drawing.Size(90, 17);
			this.guestAddCategoryCheckBox.TabIndex = 8;
			this.guestAddCategoryCheckBox.Tag = "8";
			this.guestAddCategoryCheckBox.Text = "Add Category";
			this.guestAddCategoryCheckBox.UseVisualStyleBackColor = true;
			this.guestAddCategoryCheckBox.CheckedChanged += new System.EventHandler(this.guestReadCheckBox_CheckedChanged);
			// 
			// guestEditUrlCheckBox
			// 
			this.guestEditUrlCheckBox.AutoSize = true;
			this.guestEditUrlCheckBox.Location = new System.Drawing.Point(26, 114);
			this.guestEditUrlCheckBox.Name = "guestEditUrlCheckBox";
			this.guestEditUrlCheckBox.Size = new System.Drawing.Size(60, 17);
			this.guestEditUrlCheckBox.TabIndex = 5;
			this.guestEditUrlCheckBox.Tag = "4";
			this.guestEditUrlCheckBox.Text = "Edit Url";
			this.guestEditUrlCheckBox.UseVisualStyleBackColor = true;
			this.guestEditUrlCheckBox.CheckedChanged += new System.EventHandler(this.guestReadCheckBox_CheckedChanged);
			// 
			// guestAddUrlCheckBox
			// 
			this.guestAddUrlCheckBox.AutoSize = true;
			this.guestAddUrlCheckBox.Location = new System.Drawing.Point(26, 91);
			this.guestAddUrlCheckBox.Name = "guestAddUrlCheckBox";
			this.guestAddUrlCheckBox.Size = new System.Drawing.Size(61, 17);
			this.guestAddUrlCheckBox.TabIndex = 4;
			this.guestAddUrlCheckBox.Tag = "2";
			this.guestAddUrlCheckBox.Text = "Add Url";
			this.guestAddUrlCheckBox.UseVisualStyleBackColor = true;
			this.guestAddUrlCheckBox.CheckedChanged += new System.EventHandler(this.guestReadCheckBox_CheckedChanged);
			// 
			// label2
			// 
			this.label2.AutoSize = true;
			this.label2.Location = new System.Drawing.Point(203, 41);
			this.label2.Name = "label2";
			this.label2.Size = new System.Drawing.Size(29, 13);
			this.label2.TabIndex = 3;
			this.label2.Text = "User";
			// 
			// label1
			// 
			this.label1.AutoSize = true;
			this.label1.Location = new System.Drawing.Point(11, 41);
			this.label1.Name = "label1";
			this.label1.Size = new System.Drawing.Size(40, 13);
			this.label1.TabIndex = 2;
			this.label1.Text = "Guests";
			// 
			// projectComboBox2
			// 
			this.projectComboBox2.FormattingEnabled = true;
			this.projectComboBox2.Location = new System.Drawing.Point(52, 6);
			this.projectComboBox2.Name = "projectComboBox2";
			this.projectComboBox2.Size = new System.Drawing.Size(344, 21);
			this.projectComboBox2.TabIndex = 1;
			this.projectComboBox2.SelectedIndexChanged += new System.EventHandler(this.projectComboBox2_SelectedIndexChanged);
			this.projectComboBox2.DropDown += new System.EventHandler(this.removeProjectComboBox_DropDown);
			// 
			// projectLabel2
			// 
			this.projectLabel2.AutoSize = true;
			this.projectLabel2.Location = new System.Drawing.Point(6, 9);
			this.projectLabel2.Name = "projectLabel2";
			this.projectLabel2.Size = new System.Drawing.Size(40, 13);
			this.projectLabel2.TabIndex = 0;
			this.projectLabel2.Text = "Project";
			// 
			// categoriesTabPage
			// 
			this.categoriesTabPage.Controls.Add(this.label5);
			this.categoriesTabPage.Controls.Add(this.textBox1);
			this.categoriesTabPage.Controls.Add(this.label4);
			this.categoriesTabPage.Controls.Add(this.button1);
			this.categoriesTabPage.Controls.Add(this.treeView1);
			this.categoriesTabPage.Controls.Add(this.label3);
			this.categoriesTabPage.Controls.Add(this.comboBox1);
			this.categoriesTabPage.Location = new System.Drawing.Point(4, 22);
			this.categoriesTabPage.Name = "categoriesTabPage";
			this.categoriesTabPage.Padding = new System.Windows.Forms.Padding(3);
			this.categoriesTabPage.Size = new System.Drawing.Size(402, 259);
			this.categoriesTabPage.TabIndex = 3;
			this.categoriesTabPage.Text = "Categories";
			this.categoriesTabPage.UseVisualStyleBackColor = true;
			// 
			// label5
			// 
			this.label5.AutoSize = true;
			this.label5.Location = new System.Drawing.Point(6, 41);
			this.label5.Name = "label5";
			this.label5.Size = new System.Drawing.Size(57, 13);
			this.label5.TabIndex = 6;
			this.label5.Text = "Categories";
			// 
			// textBox1
			// 
			this.textBox1.Location = new System.Drawing.Point(61, 221);
			this.textBox1.Name = "textBox1";
			this.textBox1.Size = new System.Drawing.Size(254, 20);
			this.textBox1.TabIndex = 5;
			// 
			// label4
			// 
			this.label4.AutoSize = true;
			this.label4.Location = new System.Drawing.Point(6, 223);
			this.label4.Name = "label4";
			this.label4.Size = new System.Drawing.Size(49, 13);
			this.label4.TabIndex = 4;
			this.label4.Text = "Category";
			// 
			// button1
			// 
			this.button1.Location = new System.Drawing.Point(321, 219);
			this.button1.Name = "button1";
			this.button1.Size = new System.Drawing.Size(75, 23);
			this.button1.TabIndex = 3;
			this.button1.Text = "Add";
			this.button1.UseVisualStyleBackColor = true;
			// 
			// treeView1
			// 
			this.treeView1.Location = new System.Drawing.Point(9, 57);
			this.treeView1.Name = "treeView1";
			this.treeView1.Size = new System.Drawing.Size(387, 155);
			this.treeView1.TabIndex = 2;
			// 
			// label3
			// 
			this.label3.AutoSize = true;
			this.label3.Location = new System.Drawing.Point(6, 9);
			this.label3.Name = "label3";
			this.label3.Size = new System.Drawing.Size(40, 13);
			this.label3.TabIndex = 1;
			this.label3.Text = "Project";
			// 
			// comboBox1
			// 
			this.comboBox1.FormattingEnabled = true;
			this.comboBox1.Location = new System.Drawing.Point(52, 6);
			this.comboBox1.Name = "comboBox1";
			this.comboBox1.Size = new System.Drawing.Size(344, 21);
			this.comboBox1.TabIndex = 0;
			// 
			// okButton
			// 
			this.okButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.okButton.Location = new System.Drawing.Point(347, 331);
			this.okButton.Name = "okButton";
			this.okButton.Size = new System.Drawing.Size(75, 23);
			this.okButton.TabIndex = 1;
			this.okButton.Text = "OK";
			this.okButton.UseVisualStyleBackColor = true;
			// 
			// serverLabel
			// 
			this.serverLabel.AutoSize = true;
			this.serverLabel.Location = new System.Drawing.Point(13, 16);
			this.serverLabel.Name = "serverLabel";
			this.serverLabel.Size = new System.Drawing.Size(38, 13);
			this.serverLabel.TabIndex = 3;
			this.serverLabel.Text = "Server";
			// 
			// loginButton
			// 
			this.loginButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
			this.loginButton.Location = new System.Drawing.Point(347, 11);
			this.loginButton.Name = "loginButton";
			this.loginButton.Size = new System.Drawing.Size(75, 23);
			this.loginButton.TabIndex = 4;
			this.loginButton.Text = "Login";
			this.loginButton.UseVisualStyleBackColor = true;
			this.loginButton.Click += new System.EventHandler(this.loginButton_Click);
			// 
			// serversComboBox
			// 
			this.serversComboBox.FormattingEnabled = true;
			this.serversComboBox.Location = new System.Drawing.Point(57, 13);
			this.serversComboBox.Name = "serversComboBox";
			this.serversComboBox.Size = new System.Drawing.Size(284, 21);
			this.serversComboBox.TabIndex = 5;
			this.serversComboBox.DropDown += new System.EventHandler(this.serversComboBox_DropDown);
			// 
			// FrmAdmin
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.ClientSize = new System.Drawing.Size(434, 366);
			this.Controls.Add(this.serversComboBox);
			this.Controls.Add(this.loginButton);
			this.Controls.Add(this.serverLabel);
			this.Controls.Add(this.okButton);
			this.Controls.Add(this.mainTabControl);
			this.Name = "FrmAdmin";
			this.Text = "FrmAdmin";
			this.mainTabControl.ResumeLayout(false);
			this.serversTabPage.ResumeLayout(false);
			this.serversTabPage.PerformLayout();
			this.projectsTabPage.ResumeLayout(false);
			this.projectsTabPage.PerformLayout();
			this.usersTabPage.ResumeLayout(false);
			this.usersTabPage.PerformLayout();
			this.rightsTabPage.ResumeLayout(false);
			this.rightsTabPage.PerformLayout();
			this.categoriesTabPage.ResumeLayout(false);
			this.categoriesTabPage.PerformLayout();
			this.ResumeLayout(false);
			this.PerformLayout();

		}

		#endregion

		private System.Windows.Forms.TabControl mainTabControl;
		private System.Windows.Forms.TabPage projectsTabPage;
		private System.Windows.Forms.TabPage usersTabPage;
		private System.Windows.Forms.Button okButton;
		private System.Windows.Forms.Label serverLabel;
		private System.Windows.Forms.TextBox addUserTextBox;
		private System.Windows.Forms.Label addUserLabel;
		private System.Windows.Forms.Button addUserButton;
		private System.Windows.Forms.Label projectLabel;
		private System.Windows.Forms.ComboBox projectComboBox;
		private System.Windows.Forms.Label removeUserLabel;
		private System.Windows.Forms.ComboBox removeUserComboBox;
		private System.Windows.Forms.Button removeUserButton;
		private System.Windows.Forms.Button removeProjectButton;
		private System.Windows.Forms.Button addProjectButton;
		private System.Windows.Forms.Label removeProjectLabel;
		private System.Windows.Forms.Label addProjectLabel;
		private System.Windows.Forms.ComboBox removeProjectComboBox;
		private System.Windows.Forms.TextBox addProjectTextBox;
		private System.Windows.Forms.Button loginButton;
		private System.Windows.Forms.Label projectNameLabel;
		private System.Windows.Forms.Label descriptionLabel;
		private System.Windows.Forms.TextBox descriptionTextBox;
		private System.Windows.Forms.Label addUserNameLabel;
		private System.Windows.Forms.ComboBox addUserRoleComboBox;
		private System.Windows.Forms.Label addUserRoleLabel;
		private System.Windows.Forms.TabPage rightsTabPage;
		private System.Windows.Forms.CheckBox guestEditCategoryCheckBox;
		private System.Windows.Forms.CheckBox guestAddCategoryCheckBox;
		private System.Windows.Forms.CheckBox guestEditUrlCheckBox;
		private System.Windows.Forms.CheckBox guestAddUrlCheckBox;
		private System.Windows.Forms.Label label2;
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.ComboBox projectComboBox2;
		private System.Windows.Forms.Label projectLabel2;
		private System.Windows.Forms.CheckBox userEditCategoryCheckBox;
		private System.Windows.Forms.CheckBox userAddCategoryCheckBox;
		private System.Windows.Forms.CheckBox userEditUrlCheckBox;
		private System.Windows.Forms.CheckBox userAddUrlCheckBox;
		private System.Windows.Forms.Button setUserRightsButton;
		private System.Windows.Forms.CheckBox userRemoveUserCheckBox;
		private System.Windows.Forms.CheckBox guestReadCheckBox;
		private System.Windows.Forms.CheckBox userAddUserCheckBox;
		private System.Windows.Forms.TabPage categoriesTabPage;
		private System.Windows.Forms.Label label5;
		private System.Windows.Forms.TextBox textBox1;
		private System.Windows.Forms.Label label4;
		private System.Windows.Forms.Button button1;
		private System.Windows.Forms.TreeView treeView1;
		private System.Windows.Forms.Label label3;
		private System.Windows.Forms.ComboBox comboBox1;
		private System.Windows.Forms.TabPage serversTabPage;
		private System.Windows.Forms.Label urlLabel;
		private System.Windows.Forms.TextBox passwordTextBox;
		private System.Windows.Forms.TextBox userTextBox;
		private System.Windows.Forms.TextBox urlTextBox;
		private System.Windows.Forms.Label userLabel;
		private System.Windows.Forms.ListBox serversListBox;
		private System.Windows.Forms.Button addServerButton;
		private System.Windows.Forms.Label passwordLabel;
		private System.Windows.Forms.ComboBox serversComboBox;
	}
}