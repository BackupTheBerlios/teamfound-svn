using System;

namespace TeamFound.IE
{
	/// <summary>
	/// Zusammenfassung für ICategory.
	/// </summary>
	public interface ICategory
	{
		string Name { get; }

		ICategory[] Categories { get; }
	}
}
