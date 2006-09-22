/*
 * Created on Sept 21, 2006
 */
package controller.response;


import org.jdom.Document;
import org.jdom.Element;



public class EditPermissionsResponse extends Response {
	
	protected Integer ID;
	
	public EditPermissionsResponse(Integer newID) {
		super();
		this.ID = newID;
	}
	
	public Document getXML() {
		Document d = super.getBaseDocument();
		//Element xmlroot = d.getRootElement();
		
		// unterbaum aufbauen
		Element add = new Element("editpermissions");
		teamfound.addContent(add);
		Element id = new Element("id");
		add.addContent(id);
		
		// inhalte hinzufügen
		id.addContent(ID.toString());
		
		// fertig
		return d;
	}

	@Override
	public String getHTML() {
		// TODO Auto-generated method stub
		return "not yet implemented";
	}

}
