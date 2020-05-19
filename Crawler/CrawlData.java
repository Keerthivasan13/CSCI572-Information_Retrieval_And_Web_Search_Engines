package crawler;

import java.util.ArrayList;

class FetchUrl{
	String url;
	int statusCode;
	
	public FetchUrl(String url, int statusCode)
	{
		this.url = url;
		this.statusCode = statusCode;
	}
}

class VisitUrl{
	String url;
	int size;
	int noOfOutlinks;
	String contentType;
	
	public VisitUrl(String url, int size, int noOfOutlinks, String contentType)
	{
		this.url = url;
		this.size = size;
		this.noOfOutlinks = noOfOutlinks;
		this.contentType = contentType;
	}
}

class DiscoverUrl{
	String url;
	String residenceIndicator;
	
	public DiscoverUrl(String url, String residenceIndicator)
	{
		this.url = url;
		this.residenceIndicator = residenceIndicator;
	}
}

public class CrawlData {
	ArrayList<FetchUrl> fetchedUrls;
	ArrayList<VisitUrl> visitedUrls;
	ArrayList<DiscoverUrl> discoveredUrls;
	
	public CrawlData()
	{
		fetchedUrls = new ArrayList<FetchUrl>();
		visitedUrls = new ArrayList<VisitUrl>();
		discoveredUrls = new ArrayList<DiscoverUrl>();
	}
	
	public void addFetchedUrls(String url, int statusCode)
	{
		this.fetchedUrls.add(new FetchUrl(url, statusCode));
	}
	
	public void addVisitedUrls(String url, int size, int noOfOutlinks, String contentType)
	{
		this.visitedUrls.add(new VisitUrl(url, size, noOfOutlinks, contentType));
	}
	
	public void addDiscoveredUrls(String url, String residenceIndicator)
	{
		this.discoveredUrls.add(new DiscoverUrl(url, residenceIndicator));
	}
}
