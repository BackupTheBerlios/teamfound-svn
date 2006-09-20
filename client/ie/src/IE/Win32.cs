using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

namespace TeamFound.IE
{
	public static class Win32
	{
		[DllImport("user32.dll", SetLastError=true)]
		public static extern int SetParent(int child, int parent);

		[DllImport("user32.dll", SetLastError = true)]
		public static extern bool SetForegroundWindow(int hWnd);

	}
}
