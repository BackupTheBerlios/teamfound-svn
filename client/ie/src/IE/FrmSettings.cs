using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;

namespace TeamFound.IE
{
	/// <summary>
	/// Zusammenfassung für FrmSettings.
	/// </summary>
	public class FrmSettings : System.Windows.Forms.Form
	{
		private System.Windows.Forms.Label lblServer;
		private System.Windows.Forms.Button btnOK;
		private System.Windows.Forms.Button btnCancel;
		/// <summary>
		/// Erforderliche Designervariable.
		/// </summary>
		private System.ComponentModel.Container components = null;
		private System.Windows.Forms.TextBox txtServer;

		private Configuration _config;

		public FrmSettings()
		{
			//
			// Erforderlich für die Windows Form-Designerunterstützung
			//
			InitializeComponent();

			//
			// TODO: Fügen Sie den Konstruktorcode nach dem Aufruf von InitializeComponent hinzu
			//
		}

		/// <summary>
		/// Die verwendeten Ressourcen bereinigen.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
			if( disposing )
			{
				if(components != null)
				{
					components.Dispose();
				}
			}
			base.Dispose( disposing );
		}

		#region Vom Windows Form-Designer generierter Code
		/// <summary>
		/// Erforderliche Methode für die Designerunterstützung. 
		/// Der Inhalt der Methode darf nicht mit dem Code-Editor geändert werden.
		/// </summary>
		private void InitializeComponent()
		{
			this.txtServer = new System.Windows.Forms.TextBox();
			this.lblServer = new System.Windows.Forms.Label();
			this.btnOK = new System.Windows.Forms.Button();
			this.btnCancel = new System.Windows.Forms.Button();
			this.SuspendLayout();
			// 
			// txtServer
			// 
			this.txtServer.Location = new System.Drawing.Point(64, 16);
			this.txtServer.Name = "txtServer";
			this.txtServer.Size = new System.Drawing.Size(216, 20);
			this.txtServer.TabIndex = 0;
			this.txtServer.Text = "";
			this.txtServer.TextChanged += new System.EventHandler(this.txtServer_TextChanged);
			// 
			// lblServer
			// 
			this.lblServer.AutoSize = true;
			this.lblServer.Location = new System.Drawing.Point(16, 16);
			this.lblServer.Name = "lblServer";
			this.lblServer.Size = new System.Drawing.Size(41, 16);
			this.lblServer.TabIndex = 2;
			this.lblServer.Text = "Server:";
			// 
			// btnOK
			// 
			this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
			this.btnOK.Location = new System.Drawing.Point(64, 48);
			this.btnOK.Name = "btnOK";
			this.btnOK.TabIndex = 3;
			this.btnOK.Text = "OK";
			// 
			// btnCancel
			// 
			this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
			this.btnCancel.Location = new System.Drawing.Point(152, 48);
			this.btnCancel.Name = "btnCancel";
			this.btnCancel.TabIndex = 4;
			this.btnCancel.Text = "Abbrechen";
			// 
			// FrmSettings
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.ClientSize = new System.Drawing.Size(292, 77);
			this.Controls.Add(this.btnCancel);
			this.Controls.Add(this.btnOK);
			this.Controls.Add(this.lblServer);
			this.Controls.Add(this.txtServer);
			this.Name = "FrmSettings";
			this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
			this.Text = "Einstellungen";
			this.ResumeLayout(false);

		}
		#endregion


		public Configuration Configuration
		{
			get
			{
				return _config;
			}
			set
			{
				SetConfiguration( value );
			}
		}

		private void SetConfiguration( Configuration value )
		{
			_config = value;
			this.txtServer.Text = value.ServerUrl;

		}

		private void txtServer_TextChanged(object sender, System.EventArgs e)
		{
			_config.ServerUrl = ((TextBox) sender).Text;
		}
	}
}
