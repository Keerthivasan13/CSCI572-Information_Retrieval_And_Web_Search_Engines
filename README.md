# CSCI572 Information Retrieval And Web Search Engines
Contains assignments done for the CSCI572 course at University of Southern California.

## 1. Comparing Google and Bing Search Results

**Languages :** Python 3.7  
**I/P :** Set of Queries and their corresponding top 10 Google Results  
**O/P :** Spearman Coefficient for each query comparing Google and Bing Results

## 2. Crawler

**Languages :** Java 11  
**Libraries :** crawler4j  
**I/P :** Website domain name to Crawl  
**O/P :** List of URLs fetched along with their statistics

## 3. Inverted Index  

**Languages :** Java 11  
**Libraries :** Apache Hadoop  
**Sytems :** Google Cloud, Microsoft Azure 
**I/P :** List of URLs to be indexed  
**O/P :** Index files for Unigrams and Bigrams for the URLs

## 4. Search Engine  

**Languages :** Java 11, Python 3.7, HTML, CSS, PHP, JavaScript, JQuery  
**Libraries :** Apache Lucene, Apache Solr, Apache Tika, NetworkX, jsoup, PeterNorvig's SpellCorrector  
**Functionalities :**  

**1. Page rank Calculation :**   
Used jsoup in Java to parse all the links from the Crawled webpages to create EdgeList file.  
Utilized NetworkX in Python to create a Directed Graph from the EdgeList and calculate Page Rank values for all the URLs.  

**2. Lucene and Solr setup :**  
Created a Solr core to Crawl html files and produce Inverted index efficiently.  
Select Command in Solr retrieves webpages for the query given. It internally uses Lucene's Vector space and Boolean Model representation and sorts the results with TF-IDF.  
Provided additional functionality for sorting the webpage results with the Page rank values computed previously.

**3. Iditom Search Engine Webpage :**  
Developed a PHP code to act as the Client for getting the Query from the users. 
User can choose to use Lucene's default Ranking algorithm or Page Rank algorithm for Sorting the fetched results.
Using Client API to Solr, webpage results are retrived for the Query.  
Google like web interface is provided.  

**4. Spell Check and Autocomplete  :**  
Created a vocabulary text file after parsing and preprocessing words from the Crawled webpages using Apache Tika.  
Utilizied Peter-Norvig's SpellCorrector algorithm and fed this vocabulary to compute the probabilities for Edit Distance 1 and 2.  
Added the Spell Check functionality to the Iditom Search Engine, imitating Google (Showing Results for __ , Search Instead for __ ).  
Used Solr's default Autocomplete results which uses Fuzzy Factory lookup for the word suggestions.  


