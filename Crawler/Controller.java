package crawler;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.uci.ics.crawler4j.crawler.*;
import edu.uci.ics.crawler4j.fetcher.*;
import edu.uci.ics.crawler4j.robotstxt.*;

public class Controller {
	
	static int numberOfCrawlers = 10;
	static String newsSiteName = "nytimes";
	static CrawlData crawlData;
	
	
	public static void main(String[] args) throws Exception {
		crawlData = new CrawlData();
		List<Object> allCrawlData = performCrawling();
		
		for(Object iter : allCrawlData)
		{
			CrawlData data = (CrawlData) iter;
			crawlData.fetchedUrls.addAll(data.fetchedUrls);
			crawlData.visitedUrls.addAll(data.visitedUrls);
			crawlData.discoveredUrls.addAll(data.discoveredUrls);
		}
		
		dumpDataIntoCSV();
		collectStatistics();
	}

	
	private static void dumpDataIntoCSV() throws Exception {
		
		File newFile = new File("fetch_" + newsSiteName + ".csv");
		newFile.delete();
		newFile.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(newFile, true));
		bw.append("Fetched URL,Status Code\n");
		
		for(FetchUrl fetchUrl : crawlData.fetchedUrls)
		{
			bw.append(fetchUrl.url + "," + fetchUrl.statusCode + "\n");
		}
		bw.close();
		
		newFile = new File("visit_" + newsSiteName + ".csv");
		newFile.delete();
		newFile.createNewFile();
		bw = new BufferedWriter(new FileWriter(newFile, true));
		bw.write("Downloaded URL,Size in Bytes,No of outlinks,ContentType\n");
		
		for(VisitUrl visitUrl : crawlData.visitedUrls)
		{
			bw.append(visitUrl.url + "," + visitUrl.size + "," + visitUrl.noOfOutlinks + "," + visitUrl.contentType + "\n");
		}
		bw.close();
		
		newFile = new File("urls_" + newsSiteName + ".csv");
		newFile.delete();
		newFile.createNewFile();
		bw = new BufferedWriter(new FileWriter(newFile, true));
		bw.write("URL,Residence Indicator\n");
		
		for(DiscoverUrl discoverUrl : crawlData.discoveredUrls)
		{
			bw.append(discoverUrl.url + "," + discoverUrl.residenceIndicator + "\n");
		}
		bw.close();
	}

	
	private static void collectStatistics() throws Exception {
		
		int noOfFetchesAttempted = crawlData.fetchedUrls.size();
		
		HashMap<Integer, Integer> statusCodes = new HashMap<Integer, Integer>();
		
		for(FetchUrl fetchUrl : crawlData.fetchedUrls)
		{
			if (statusCodes.containsKey(fetchUrl.statusCode))
			{
				statusCodes.put(fetchUrl.statusCode, statusCodes.get(fetchUrl.statusCode) + 1);
			}
			else
			{
				statusCodes.put(fetchUrl.statusCode, 1);
			}
		}
		
		int noOfFetchesSucceeded = statusCodes.get(200);
		int noOfFetchesAbortedOrFailed = noOfFetchesAttempted - noOfFetchesSucceeded;
		
		int noOfDiscoveredUrls = crawlData.discoveredUrls.size();
		int noOfUniqueUrlsWithinResidence = 0;
		HashSet<String> uniqueDiscoveredUrls = new HashSet<String>();
		
		for(DiscoverUrl discoverUrl : crawlData.discoveredUrls)
		{
			if (!uniqueDiscoveredUrls.contains(discoverUrl.url))
			{
				if (discoverUrl.residenceIndicator == "OK")
				{
					noOfUniqueUrlsWithinResidence ++;
				}
				uniqueDiscoveredUrls.add(discoverUrl.url);
			}
		}
		
		int noOfUniqueUrls = uniqueDiscoveredUrls.size();
		int noOfUniqueUrlsOutsideResidence = noOfUniqueUrls - noOfUniqueUrlsWithinResidence;
		
		int oneK = 0, tenK = 0, hundredK = 0, oneM = 0, other = 0;
		HashMap<String, Integer> contentTypes = new HashMap<String, Integer>();
		
		for (VisitUrl visitUrl : crawlData.visitedUrls)
		{
			if (visitUrl.size < 1024)
			{
				oneK ++;
			}
			else if (visitUrl.size < 10240)
			{
				tenK ++;
			}
			else if (visitUrl.size < 102400)
			{
				hundredK ++;
			}
			else if (visitUrl.size < 1024 * 1024)
			{
				oneM ++;
			}
			else
			{
				other ++;
			}
			
			if (contentTypes.containsKey(visitUrl.contentType))
			{
				contentTypes.put(visitUrl.contentType, contentTypes.get(visitUrl.contentType) + 1);
			}
			else
			{
				contentTypes.put(visitUrl.contentType, 1);
			}
		}
		
		
		File newFile = new File("CrawlReport_" + newsSiteName + ".txt");
		newFile.delete();
		newFile.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(newFile, true));
		bw.write("Name: Keerthivasan Sivasankar\nUSC ID: 9269558511\n");
		bw.write("News site crawled: " + newsSiteName +".com\nNumber of threads: " + numberOfCrawlers + "\n\n");
		
		bw.write("Fetch Statistics\n================\n");
		bw.write("# fetches attempted: " + noOfFetchesAttempted + "\n# fetches succeeded: " + noOfFetchesSucceeded +
					"\n# fetches failed or aborted: " + noOfFetchesAbortedOrFailed + "\n\n");
		
		bw.write("Outgoing URLs:\n==============\n");
		bw.write("Total URLs extracted: " + noOfDiscoveredUrls + "\n# unique URLs extracted: " + noOfUniqueUrls + "\n");
		bw.write("# unique URLs within News Site: " + noOfUniqueUrlsWithinResidence +
					"\n# unique URLs outside News Site: " + noOfUniqueUrlsOutsideResidence + "\n\n");
		
		bw.write("Status Codes:\n=============\n");
		bw.write("200 OK: " + statusCodes.get(200) + "\n");
		bw.write("301 Moved Permanently: " + statusCodes.get(301) + "\n");
		bw.write("302 Found: " + statusCodes.get(302) + "\n");
		bw.write("400 Bad Request Response: " + statusCodes.get(400) + "\n");
		bw.write("401 Unauthorized: " + statusCodes.get(401) + "\n");
		bw.write("403 Forbidden: " + statusCodes.get(403) + "\n");
		bw.write("404 Not Found: " + statusCodes.get(404) + "\n");
		bw.write("410 Gone: " + statusCodes.get(410) + "\n\n");
		
		bw.write("File Sizes:\n===========\n");
		bw.write("< 1KB: "+ oneK + "\n");
		bw.write("1KB ~ <10KB: "+ tenK + "\n");
		bw.write("10KB ~ <100KB: "+ hundredK + "\n");
		bw.write("100KB ~ <1MB: "+ oneM + "\n");
		bw.write(">= 1MB: "+ other + "\n\n");
		
		bw.write("Content Types:\n==============\n");
		
		for(String type : contentTypes.keySet())
		{
			bw.write(type + ": " + contentTypes.get(type) + "\n");
		}
		bw.close();
		
		for(int key: statusCodes.keySet())
		{
			System.out.println(key + " " + statusCodes.get(key));
		}
	}


	private static List<Object> performCrawling() throws Exception 
	{
		int maxPagesToFetch = 20000;
		int maxDepthOfCrawling = 16;
		int politenessDelay = 100;
		String crawlStorageFolder = "/data/crawl";
		String newsSiteUrl = "https://www.nytimes.com/";
		
		CrawlConfig config = new CrawlConfig();
		
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setMaxPagesToFetch(maxPagesToFetch);
		config.setMaxDepthOfCrawling(maxDepthOfCrawling);
		config.setPolitenessDelay(politenessDelay);
		config.setIncludeBinaryContentInCrawling(true);
		
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		
		controller.addSeed(newsSiteUrl);
		controller.start(MyCrawler.class, numberOfCrawlers);
		return controller.getCrawlersLocalData();
	}

}
