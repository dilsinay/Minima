package org.minima.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.minima.system.network.rpc.RPCClient;
import org.minima.utils.json.JSONObject;
import org.minima.utils.json.parser.JSONParser;

public class NodeCounter {

	public static void main(String[] zArgs) {
		
		//Check some data given..
		if(zArgs.length < 1) {
			System.out.println("Must specify hosts..");
			return;
		}
		
		//List of all the hosts..
		ArrayList<String> allhosts = new ArrayList<>();
		
		//First get a complete list of the nodes we are going to ask..
		StringTokenizer strtok = new StringTokenizer(zArgs[0],"#");
		while(strtok.hasMoreTokens()) {
			allhosts.add(strtok.nextToken());
		}
		
		//Hosts found..
		System.out.println(allhosts.size()+" hosts found..");
		System.out.println(allhosts);
		
		//Connect manager
		RPCClient client = new RPCClient();
		
		//loop
		while(true) {
		
			long total = 0;
			for(String host : allhosts) {
				try {
					
					//Get the current status
					String hoststatus = client.sendGET("http://"+host+"/status");
				
					//Convert to JSON
					JSONObject status = (JSONObject) new JSONParser().parse(hoststatus);
					
					//Get the details..
					JSONObject details = (JSONObject) status.get("response");
				
					//Connections
					long connections 	= (long) details.get("connections");
				
					//Add to the total
					total += connections;
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
			//Create URL call to store in DB
			String urlcall = "http://mifi.minima.global/nodestatus/nodecounter.php?"
											+ "connections="+total;
			
			//And now make this GET request..
			System.out.println(urlcall);
			try {
				String finalresp = client.sendGET(urlcall);
				System.out.println(finalresp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			//Wait an hour..
			try {Thread.sleep(1000 * 60 * 60);} catch (InterruptedException e) {}
		}
		
	}
	
}
