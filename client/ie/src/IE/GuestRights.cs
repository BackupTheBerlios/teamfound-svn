using System;
using System.Collections.Generic;
using System.Text;

namespace TeamFound.IE
{
	[Flags]
	public enum GuestRights : int
	{
		Read = 1,
		AddUrl = 2,
		EditUrl = 4,
		AddCategory = 8,
		EditCategory = 16
	}
}
