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
Lucene
