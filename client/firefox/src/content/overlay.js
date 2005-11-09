
var TeamFound = 
{
	onLoad: function() 
	{
		// initialization code
		this.initialized = true;
	},

	onMenuItemCommand: function() 
	{
		if(this.initialized)
			alert("TeamFound ;-)");
	}
};

window.addEventListener(
	"load", 
	function(e) 
	{ 
		TeamFound.onLoad(e); 
	}, 
	false); 

