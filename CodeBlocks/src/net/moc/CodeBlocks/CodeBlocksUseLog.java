package net.moc.CodeBlocks;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import net.moc.CodeBlocks.workspace.Function;

public class CodeBlocksUseLog {
	private CodeBlocks plugin;
	private String logUrl = "http://isue-server.eecs.ucf.edu/codeblocks2/tracker.php";
	private String commentUrl = "http://isue-server.eecs.ucf.edu/codeblocks2/comment.php";
	private String charset = "UTF-8";
	private long logDelta = 86400000;

	public CodeBlocksUseLog(CodeBlocks plugin) { this.plugin = plugin; }
	
	public void submitLog() {
		if (!plugin.getConfiguration().doLogStatistics()) return;
		if (System.currentTimeMillis() - plugin.getConfiguration().getLastLogSubmit() < logDelta) return; 
		
		String serverName = "NAME-" + plugin.getServer().getServerName();
		String serverData = "BUKKIT-" + plugin.getServer().getBukkitVersion() + " SERVER-" + plugin.getServer().getVersion();
		ArrayList<Function> functions = plugin.getSQL().getFunctions();
		if (functions.size() == 0) return;
		
		plugin.getConfiguration().setLastLogSubmit(System.currentTimeMillis());
		
		String serverFunctions = "FUNCTIONCOUNT-" + functions.size();
		
		for (Function function : functions)
			serverFunctions += "\nFUNCTION START " + function.getName() + "\n" + function.toText() + "\nFUNCTION END " + function.getName();
		
		try {
			String query = "serverName=" + URLEncoder.encode(serverName, charset) +
						   "&serverData=" + URLEncoder.encode(serverData, charset) +
						   "&serverFunctions=" + URLEncoder.encode(serverFunctions, charset);
			
			URLConnection connection = new URL(logUrl + "?" + query).openConnection();
			connection.setRequestProperty("Accept-Charset", charset);
			InputStream response = connection.getInputStream();
			response.close();
			
		} catch (IOException e) {}
		
	}
	
	public void submitFeedback(String type, String playerName, String message) {
		if (!plugin.getConfiguration().doFeedback()) return;
		
		String serverName = "NAME-" + plugin.getServer().getServerName();
		String serverData = "BUKKIT-" + plugin.getServer().getBukkitVersion() + " SERVER-" + plugin.getServer().getVersion();
		
		try {
			String query = "&serverName=" + URLEncoder.encode(serverName, charset) +
						   "&serverData=" + URLEncoder.encode(serverData, charset) +
						   "&playerName=" + URLEncoder.encode(playerName, charset) +
						   "&commentType=" + URLEncoder.encode(type, charset) +
						   "&comment=" + URLEncoder.encode(message, charset);
			
			URLConnection connection = new URL(commentUrl + "?" + query).openConnection();
			connection.setRequestProperty("Accept-Charset", charset);
			InputStream response = connection.getInputStream();
			response.close();
			
		} catch (IOException e) {}
		
	}
	
}
