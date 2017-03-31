import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|mp3|mp3|zip|gz))$");
	public static FileWriter fetch, visit, urls;
	static int successfulFetches, faliedFetches;
	
	//writer1 = new FileWriter("a.csv");
	public MyCrawler() throws Exception {
		fetch  = new FileWriter("fetch_latimes.csv");
		visit  = new FileWriter("visit_latimes.csv");
		urls  = new FileWriter("urls_latimes.csv");
	}
	
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
	    String href = url.getURL().toLowerCase();
	    StringBuilder data = new StringBuilder();
	    String status = (href.startsWith("http://www.latimes.com/")) ? "OK" : "N_OK";
		data.append(href);
		data.append(",");
		data.append(status);
		data.append("\n");
		
		try {
			synchronized (urls) {
				urls.append(data.toString());
				urls.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return !FILTERS.matcher(href).matches()
	           && href.startsWith("http://www.latimes.com/");
	}
	
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		super.handlePageStatusCode(webUrl, statusCode, statusDescription);
			try {
				StringBuilder data = new StringBuilder();
				data.append(webUrl.toString().replaceAll(",amp", ""));
				data.append(",");
				data.append(statusCode);
				data.append("\n");
				synchronized (fetch) {
					fetch.append(data.toString());
					fetch.flush();
				}
				if(statusCode == 200){
					successfulFetches++;
				}else{
					faliedFetches++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	
	@Override
	public void visit(Page page) {
	    String url = page.getWebURL().getURL();
	    System.out.println("URL: " + url);
	    if (page.getParseData() instanceof HtmlParseData) {
	        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
	        String text = htmlParseData.getText();
	        String html = htmlParseData.getHtml();
	        Set<WebURL> links = htmlParseData.getOutgoingUrls();
	        
			StringBuilder downloadedData = new StringBuilder();
			downloadedData.append(url.replaceAll(",amp", ""));
			downloadedData.append(",");
			downloadedData.append(html.length());
			downloadedData.append(",");
			downloadedData.append(links.size());
			downloadedData.append(",");
			downloadedData.append(page.getContentType());
			downloadedData.append("\n");
			
			try {
				synchronized (visit) {
					visit.append(downloadedData.toString());
					visit.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	} }
}
