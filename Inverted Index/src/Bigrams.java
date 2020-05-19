import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Bigrams {

  public static class IndexMapper extends Mapper<Object, Text, Text, LongWritable> 
  {
	  private Text word = new Text();
	  private LongWritable docId = new LongWritable();
	  public void map(Object key, Text value, Context context) throws IOException, InterruptedException 
	  {
		  String arr[] = value.toString().split("\t", 2);
		  docId.set(Long.parseLong(arr[0]));
		  String txt = arr[1].toLowerCase().replaceAll("[^a-z]+", " ");
		  String[] words = txt.split(" ");
		  
		  for(int i = 0; i < words.length - 1; i++)
		  {
			  word.set(words[i] + " " + words[i+1]);
			  context.write(word, docId);
		  }
	  }
  }

  public static class IndexReducer extends Reducer<Text,LongWritable,Text,Text> 
  {
	  private Text result = new Text();
	  public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException 
	  {
		  HashMap<Long, Integer> counter = new HashMap<Long, Integer>();
		  
		  for (LongWritable val : values) 
		  {
			  Long docId = val.get();
			  if (counter.containsKey(docId))
			  {
				  counter.put(docId, counter.get(docId) + 1);
			  }
			  else
			  {
				  counter.put(docId, 1);
			  }
		  }
		  
		  String count = "";
		  for (Map.Entry<Long, Integer> elem : counter.entrySet())
		  {
			  count += elem.getKey() + ":" + elem.getValue() + " ";
		  }
		  
		  result.set(count);
		  context.write(key, result);
	  }
  }

  public static void main(String[] args) throws Exception 
  {
	  if (args.length != 2) {
		  System.err.println("Usage: Word PostingsList <Input Path> <OutputPath>");
		  System.exit(-1);
	  }
	  Job job = new Job();
	  job.setJarByClass(Bigrams.class);
	  job.setJobName("Word PostingsList");
	  
	  job.setMapperClass(IndexMapper.class);
	  job.setReducerClass(IndexReducer.class);
	  job.setMapOutputKeyClass(Text.class);
	  job.setMapOutputValueClass(LongWritable.class);
	  job.setOutputKeyClass(Text.class);
	  job.setOutputValueClass(Text.class);
	  FileInputFormat.addInputPath(job, new Path(args[0]));
	  FileOutputFormat.setOutputPath(job, new Path(args[1]));
	  System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}