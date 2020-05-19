package crawler;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.*;
import edu.uci.ics.crawler4j.parser.*;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {
	CrawlData crawlData;
	String newsSiteName = "nytimes";
	String newsSiteDomain = "nytimes.com";
	static String regex = ".*(\\.(css|js|mp3|zip|gz|vcf|xml|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v))$";
	private final static Pattern FILTERS = Pattern.compile(regex);
	
	public MyCrawler()
	{
		crawlData = new CrawlData();
	}
	
	public static String normalizeUrl(String url)
	{
		if (url.endsWith("/"))
		{
			url = url.substring(0, url.length() - 1);
		}
			
		return url.toLowerCase().replace(",","_").replaceFirst("^(https?://)?(www.)?", "");
	}
	
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url)
	{
		String href = normalizeUrl(url.getURL());
				
		if (href.startsWith(newsSiteDomain))
		{
			crawlData.addDiscoveredUrls(url.getURL(), "OK");
		}
		else
		{
			crawlData.addDiscoveredUrls(url.getURL(), "N_OK");
		}
		
		return !FILTERS.matcher(href).matches() && href.startsWith(newsSiteDomain);
	}
	
	@Override
	public void handlePageStatusCode(WebURL url, int statusCode, String statusDescription)
	{
		crawlData.addFetchedUrls(url.getURL(), statusCode);
	}
	
	@Override
	public void visit(Page page)
	{
		String url = page.getWebURL().getURL();
		String contentType = page.getContentType().toLowerCase().split(";")[0];
		
		if (contentType.equals("text/html"))
		{
			if (page.getParseData() instanceof HtmlParseData)
			{
				HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
				Set<WebURL> links = htmlParseData.getOutgoingUrls();				
				crawlData.addVisitedUrls(url, page.getContentData().length, links.size(), contentType);
			}
		}
		else if (contentType.equals("application/pdf") || contentType.equals("application/document") ||
				contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
				contentType.equals("application/msword") || contentType.startsWith("image"))
		{
			crawlData.addVisitedUrls(url, page.getContentData().length, 0, contentType);
		}
	}
	
	@Override
	public Object getMyLocalData()
	{
		return crawlData;
	}
}
