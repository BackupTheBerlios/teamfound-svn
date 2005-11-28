using System;

namespace TeamFound.IE
{
	/// <summary>
	/// Zusammenfassung f�r ICategory.
	/// </summary>
	public interface ICategory
	{
		string Name { get; }

		ICategory[] Categories { get; }
	}
}
