import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.SAXException;
import org.apache.tika.sax.BodyContentHandler;

public class SpellCheck {
	public static void main(String args[]) throws IOException, SAXException, TikaException
	{
		String dirPath = "D:\\Study\\CS572\\HW4\\Ubuntu\\solr-7.7.2\\dataset\\NYTIMES\\nytimes";
		FileWriter fileWriter = new FileWriter("big.txt");
		
		
		File folder = new File(dirPath);
		int count = 0;
		try {
			for (File file: folder.listFiles())
			{
				count += 1;
				InputStream stream = new FileInputStream(file);
				HtmlParser htmlParser = new HtmlParser();
				BodyContentHandler handler = new BodyContentHandler(-1);
				ParseContext context = new ParseContext();
				context.set(Parser.class, htmlParser);
				htmlParser.parse(stream, handler, new Metadata(), context);
				String contents[] = handler.toString().split("\\r+\\n+|\\s+");
				for (String token:contents)
				{
					if(token.matches("[a-zA-Z]+\\.?"))
					{
						//System.out.print(token);
						fileWriter.write(token + "\n");
					}
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex.toString());
		}
		System.out.println(count);
		fileWriter.close();
	}
}