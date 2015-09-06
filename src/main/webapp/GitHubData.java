package main.webapp;
 
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*Library providing github api's */
import org.kohsuke.github.*;

/* Request mapping for main class*/
@Path("/users")
public class GitHubData {
 
	/* Accept post request and accepts Specific type of media and generates a 
	 * html page
	 * data is mapped to variable in the function*/
	@POST
	@Path("/repoinfo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html")
	public Response getGitData(String data) {
		/* contains the output html */
		String output=null ;
		
		/* Connection can throw IOException*/
		try{
		String input=data.substring(data.indexOf("=")+1,data.length()) ;
		if(input.contains("+"))
			input=input.substring(input.indexOf("+")+1, input.length());
		input=input.replace("%2F", "/");
		
		/*Connect to github without any credentials*/
		GitHub github = GitHub.connectAnonymously();
		
		/*gets the input repository*/
	     GHRepository repo=github.getRepository(input.trim()) ;
	    
	     /*Get list of all open issues*/
	     List<GHIssue> list=repo.getIssues(GHIssueState.OPEN) ;
	     
	     /*Count of issues opened in last 24 hrs*/
		int countlast24=0;
		
		/*Count of issues opened between last day and past 7 days*/
		int countlast7days=0;
		
		/*Count of issue opened before last 7 days*/
		int countbefore7days=0 ;
		
		for(GHIssue issue:list)
		{
			if(issue.getCreatedAt().after(new Date(System.currentTimeMillis()-TimeUnit.HOURS.toMillis(24))))
					{
				countlast24++ ;
					}
			else if(issue.getCreatedAt().before(new Date(System.currentTimeMillis()-TimeUnit.HOURS.toMillis(24)))&&issue.getCreatedAt().after(new Date(System.currentTimeMillis()-TimeUnit.DAYS.toMillis(7))))
			{
				countlast7days++ ;
			}
			else 
			{
				countbefore7days++ ;
			}
		}
		/*Table output of result*/
		output = "<table><tr><td>Total number of open issues </td> <td>: "+repo.getOpenIssueCount()+"</td></tr>"+
						"<tr><td>Number of open issues that were opened in the last 24 hours </td><td>: "+countlast24+"</td></tr>"+
						"<tr><td>Number of open issues that were opened more than 24 hours ago but less than 7 days ago </td><td>: "+countlast7days+"</td></tr>"+
						"<tr><td>Number of open issues that were opened more than 7 days ago </td><td>: "+countbefore7days+"</td></tr></table>";                          
		} catch (Exception e)
		{
			System.out.println(e);
			output="<p><b>Either the Input Format is incorrect(Pls read Readme file) or Repository doesnt exist/is private.</b></p>" ;
		}
		return Response.status(200).entity(output).build();
		 		
	} 
	
}