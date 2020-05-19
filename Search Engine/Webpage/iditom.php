<?php
include 'SpellCorrector.php';
header('Content-Type:text/html; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Origin, Content-Type, X-Auth-Token');
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
ini_set ('memory_limit', -1);
error_reporting(E_ALL);

if(isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on')   
	$base_url = "https://";   
else  
	$base_url = "http://";
$base_url.= ($_SERVER['HTTP_HOST']); 

$limit = 10;
$query = isset($_REQUEST['query']) ? $_REQUEST['query'] : false;
$results = false;

if($query)
{
	session_start();
	
	require_once('Apache/Solr/Service.php');
	
	$spell_check_enabled = False;
	
	$query = rtrim($query);
	$old_query = $query;
	$new_query ="";
	
	foreach(explode(" ", $query) as $word):
		$new_query .= SpellCorrector::correct($word) . " ";
	endforeach;
	
	$new_query = rtrim($new_query);
	
	if (!isset($_GET['SpellCheckOff']))
	{
		if ($old_query != $new_query)
		{
			$spell_check_enabled = True;
			$query = $new_query;
		}
	}
	
	$solr = new Apache_Solr_Service('localhost', 8983, '/solr/csci572/');
	
	if(isset($_GET['submit'])) 
	{
		$_SESSION['ranking'] = $_REQUEST['ranking'];
	}
	
	if ($_REQUEST['ranking'] == "lucene")
	{
		$sort = "";
	}
	else
	{
		$sort = "pageRankFile asc";
	}
	
	$additionalParameters = array(
		'fl' => 'title, id, og_url, og_description',
		'sort' => $sort 
	);
	
	if (get_magic_quotes_gpc() == 1)
	{
		$query = stripslashes($query);
	}
	
	try		
	{
		$results = $solr->search($query, 0, $limit, $additionalParameters);
	}
	catch(Exception $ex)
	{
		die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$ex->__toString()}</pre></body></html>");
	}
}

?>

<html>
	<head> 
		<title> CSCI572 Assignment </title>
		<link href = "https://code.jquery.com/ui/1.10.4/themes/ui-lightness/jquery-ui.css" rel = "stylesheet">
        <script src = "https://code.jquery.com/jquery-1.10.2.js"></script>
        <script src = "https://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
		<script>
		var suggestions = [];
		var query = "";

         $(function() {
            $("#query").autocomplete({
               minLength:2,   
               delay:500,   
               source: function(request, response) {
			       response(suggestions);
			   }
            });
         });
		
		function retriveSuggestions() {
			suggestions = [];
			query = document.getElementById("query").value;
			xmlhttpPost();		
		}
		
		function xmlhttpPost() {
			var strURL = "http://localhost:8983/solr/csci572/suggest";
			var xmlHttpReq = false;
			var self = this;
			if (window.XMLHttpRequest) { // Mozilla/Safari
				self.xmlHttpReq = new XMLHttpRequest();
			}
			else if (window.ActiveXObject) { // IE
				self.xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
			}
			self.xmlHttpReq.open('POST',strURL, true);
			self.xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			self.xmlHttpReq.setRequestHeader('Accept', 'application/json');
			
			self.xmlHttpReq.onreadystatechange = function() {
				if (self.xmlHttpReq.readyState == 4) {
					evaluateResponse(self.xmlHttpReq.responseText);
				}
			}

			var params = getstandardargs().concat(getquerystring());
			var strData = params.join('&');
			self.xmlHttpReq.send(strData);
		}
		
		function getstandardargs() {
			var params = ['wt=json', 'rows=5'];
			return params;
		}
		
		function getquerystring() {
		  qstr = 'q=' + escape(query);
		  return qstr;
		}
		
		function evaluateResponse(rsp)
		{
			rsp = JSON.parse(rsp);
			var suggestion_object = rsp["suggest"]["suggest"][query]["suggestions"];
			suggestion_object.forEach(function (item, index) {
			  suggestions.push(item.term);
			});
		}

		</script>
		<style>
			body {
				font-size: 20px;
			}
			
			.link {
				text-decoration: None;
				color:#1e71d7;
			}
			
			table {
			    border-collapse: separate;
			    border-spacing: 20px 10px;
			}
			
			.ui-text {
				font-size:22px;
				margin-bottom:10px;
			}
			
			#ui-results-title {
				text-decoration: None;
				color:#1A0dab;
				font-size: 22px;
			}
			#ui-results-title a{
				text-decoration: None;
				color:#1A0dab;
			}
			#ui-results-title a:hover{
				text-decoration: underline;
			}
			
			#ui-results-url {
				text-decoration: None;
				color:#1A0dab;
				font-size: 20px;
			}
			#ui-results-url a{
				text-decoration: None;
				color:#1A0dab;
			}
			#ui-results-url a:hover{
				text-decoration: underline;
			}
			
			
			#ui-results-description {
				color:#525252;
				font-size: 18px;
			}
			
			#ui-results-id {
				color:#525252;
				font-size: 18px;
			}
			
			body .ui-menu .ui-menu-item a{
				background-color: white;
				color: black;
				background: white;
				border: None;
			}
			
			body .ui-widget {
				font-size: 20px;
			}
			
			body .ui-menu .ui-menu-item a:hover{
				background: #d4f0fc;
				color: black;
				border: None
			}
			#query:focus{
				box-shadow: 0px 0px 10px #91aeba;
				border-color: white;
				outline: white;
			}
			
			body .ui-autocomplete.source:hover {
				background: #d4f0fc;
			}
			
			.ui-autocomplete {
				background: white;
				border-radius: 0px;
			}
			
			#query:hover {
				box-shadow: 0px 0px 10px #91aeba;
			}
		</style>
	</head>
	

	<body style="text-align:center;margin-top:5%;background-color:#white">

		<h1><a href="<?php echo $base_url.$_SERVER['PHP_SELF']; ?>"> <img style="height:180px; width:450px" src="logo.png"> </a></h1>
	
		<form accept-charset="utf-8" method="get">
			<div class="ui-widget">
				<input title="Enter your Query here" id="query" name="query" style="margin: auto;" onkeyup="retriveSuggestions()" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/> <br> <br>
			</div>

			<input type ="radio" id="lucene" name="ranking" value="lucene" <?php if (!isset($_GET['ranking']) || $_GET['ranking'] == "lucene") echo "checked"; ?> >
			<label for="lucene"> Lucene </label>
			<input style="margin-left:2%" type ="radio" id="pageRank" name="ranking" value="pageRank" <?php if (isset($_GET['ranking']) && $_GET['ranking'] == "pageRank") echo "checked"; ?> >
			<label for="pageRank"> PageRank </label> <br>
			<input type="submit" style="margin-top:1%;height:35px;width:100px; border-radius:10px;font-size:20px;" value="Search"/>

		</form>
	
<?php
	if($results)
	{
		$total = (int) $results->response->numFound;
		$start = min(1, $total);
		$end = min($limit, $total);
?>  
	<hr style="margin-top:3%";>
	<div style="margin-top:2%;margin-left:10%;margin-right:10%">
<?php
	if ($spell_check_enabled)
	{    
		$spell_check_on_url = $base_url . $_SERVER['PHP_SELF'] . "?query=".$query."&ranking=".$_GET['ranking'];
		$spell_check_off_url = $base_url . $_SERVER['REQUEST_URI']. "&SpellCheckOff=True";
		echo ("<div class='ui-text'> 
				Showing Results for <b> <a class='link' href='".$spell_check_on_url."'>". $query ."</a> </b> <br>
				<div style='margin-top:5px;'>
					Search Instead of <b><a class='link' href='".$spell_check_off_url."'>". $old_query ."</a> </b> 
				</div>
			   </div>");
	}
	else if($total == 0 and $new_query)
	{
		$spell_check_on_url = $base_url . $_SERVER['PHP_SELF'] . "?query=".$new_query."&ranking=".$_GET['ranking'];
		echo ("<div class='ui-text'> 
				No Results found. Did you mean: <b> <a class='link' href='".$spell_check_on_url."'>". $new_query ."</a> </b> </div>");
	}
?>
	<div style="color:#525252"> Results <?php echo $start; ?> - <?php echo $end; ?> of <?php echo $total; ?> </div>
	<ol style="list-style:none;">
	<hr>
<?php
		foreach($results->response->docs as $doc)
		{
?>		
		<li>
			<table style="text-align:left;font-size:20px">
<?php
			$title = $doc->title;
			$link = $doc->og_url;
			$docId = $doc->id;
			$desc = $doc->og_description;
			
			if (is_null($desc))
			{
				$desc = "N/A";
			}
			
			if (is_null($link))
			{
				$file = fopen("URLtoHTML_nytimes_news.csv", "r") or die("Unable to open file!");

				while(!feof($file))
				{
					list($fileName, $url) = explode(',', fgets($file));
					if (strpos($docId,$fileName))
					{
						$link = $url;
						break;
					}
				}
				fclose($file);
			}
			
			if (is_array($desc))
			{
				$desc = implode(" ", $desc);
			}
?>
				<tr>
					<td id='ui-results-title'> <?php echo ("<a href ='" . $link . "'>" . $title . "</a>");?> </td>
				</tr>
				<tr>
					<td id='ui-results-url'><?php echo ("<a href ='" . $link . "'>" . $link . "</a>");?></td>
				</tr>
				<tr>
					<td id='ui-results-description'><?php echo htmlspecialchars($desc, ENT_NOQUOTES, 'utf-8');?></td>
				</tr>
				<tr>
					<td id='ui-results-id'><?php echo htmlspecialchars($docId, ENT_NOQUOTES, 'utf-8');?></td>
				</tr>
			</table>
		<hr>
		</li>
<?php
		}
?>
	</ol>
	</div>
<?php
	}
?>	
	</body>	
</html>