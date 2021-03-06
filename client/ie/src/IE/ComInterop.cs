using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

namespace TeamFound.IE
{
	[ComImport(), Guid("B722BCCB-4E68-101B-A2BC-00AA00404770"),
	InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
	public interface IOleCommandTarget
	{
		[PreserveSig()]
		int QueryStatus([In, MarshalAs(UnmanagedType.Struct)] ref Guid
		pguidCmdGroup, [MarshalAs(UnmanagedType.U4)] int cCmds,
	   [In, Out] IntPtr prgCmds, [In, Out] IntPtr pCmdText);

		[PreserveSig()]
		int Exec(ref Guid pguidCmdGroup, uint nCmdID, uint nCmdExecOpt,
		object[] pvaIn, [In, Out, MarshalAs(UnmanagedType.LPArray)] 
object[] pvaOut);
	} 

}
