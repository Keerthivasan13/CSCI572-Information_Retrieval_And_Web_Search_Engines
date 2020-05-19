import networkx as nx


class PageRank:
    def __init__(self):
        path_to_edge_list = "D:\Study\CS572\HW4\LinkExtractor\edgeList.txt";
        self.G = nx.read_edgelist(path_to_edge_list, create_using=nx.DiGraph())

    def compute_page_rank(self):
        self.page_ranks = nx.pagerank(self.G, alpha=0.85, personalization=None, max_iter=30, tol=1e-06, nstart=None,
                         weight='weight', dangling=None)

    def store_to_file(self):
        rootDir = "/home/keerthivasan/shared/Data/NYTIMES/nytimes/nytimes/";
        with open("external_pageRankFile.txt", "w") as file:
            for docID, rank in self.page_ranks.items():
                file.write(rootDir + docID + "=" + str(rank) + "\n")


obj = PageRank()
obj.compute_page_rank()
obj.store_to_file()
