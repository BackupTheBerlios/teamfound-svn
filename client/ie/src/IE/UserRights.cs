using System;
using System.Collections.Generic;
using System.Text;

namespace TeamFound.IE
{
	public enum UserRights : int
	{
		AddUrl = 1,
		
		EditUrl = 2,
		
		AddCategory = 4,
		
		EditCategory = 8,
		
		AddUser = 16,

		RemoveUser = 32
	}
}
