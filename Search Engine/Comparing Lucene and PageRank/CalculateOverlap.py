import collections


filePath = "D:\Study\CS572\HW4\QueryUrlList.csv"

with open(filePath, "r") as file:
    prev_query = None
    lucene_url_counter = collections.defaultdict(int)
    page_rank_url_counter = collections.defaultdict(int)
    queries = set()
    for line in file:
        line = line.rstrip('\n')
        query, lucene, page_rank = line.split(",")
        if query != prev_query:
            if prev_query:
                no_overlaps = 0
                for q in queries:
                    no_overlaps += min(lucene_url_counter[q], page_rank_url_counter[q])
                if no_overlaps:
                    print(prev_query +"\n" + str(no_overlaps))
            prev_query = query
            lucene_url_counter = collections.defaultdict(int)
            page_rank_url_counter = collections.defaultdict(int)
            queries = set()

        queries.add(lucene)
        queries.add(page_rank)
        lucene_url_counter[lucene] += 1
        page_rank_url_counter[page_rank] += 1

    no_overlaps = 0
    for q in queries:
        no_overlaps += min(lucene_url_counter[q], page_rank_url_counter[q])
    if no_overlaps:
        print(prev_query + "\n" + str(no_overlaps))