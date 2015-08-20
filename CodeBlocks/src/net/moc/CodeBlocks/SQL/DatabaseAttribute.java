package net.moc.CodeBlocks.SQL;

import java.util.Formatter;

public class DatabaseAttribute {
	
	protected String attname;
	protected String atttype;
	protected String attdefault;
		
	public DatabaseAttribute(String _attname, String _atttype, String _attdefault)
	{
		this.attname = _attname;
		this.atttype = _atttype;
		this.attdefault = _attdefault;
	}
	
	// Call this to get what is passed to the database for this attribute
	public String toString()
	{
		Formatter f = new Formatter();
		f.format("%s %s DEFAULT %s",this.attname,this.atttype,this.attdefault);
		String retval = f.toString();
		
		return retval;
	}

}
