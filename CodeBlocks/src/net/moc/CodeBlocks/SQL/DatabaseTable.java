package net.moc.CodeBlocks.SQL;

import java.util.ArrayList;

public class DatabaseTable {
	
	public ArrayList<DatabaseAttribute> attributes = new ArrayList<DatabaseAttribute>();
	protected String keys = null;
	protected String tablename = null;
	
	public DatabaseTable(String _tablename, String _keys, DatabaseAttribute... atts)
	{
		this.tablename = _tablename;
		this.keys = _keys;
		for(DatabaseAttribute att : atts)
			this.attributes.add(att);
		
	}
	
}
