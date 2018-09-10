package hw1;

import java.io.File;
import java.io.IOException;

public class HW1
{
    public static void main(String[] args) throws IOException {
        
        Corpus corpus = new Corpus();                
        corpus.TF();        
        corpus.IDF();
        corpus.TFIDF();
        corpus.CosineSimilarity("بازار بزرگ تهران");
        System.out.println(System.currentTimeMillis()-corpus.startTime);
        
//        Lucene lucene = new Lucene();         
//        lucene.Indexing();
//        lucene.Similarity("بازار بزرگ تهران");
//        System.out.println(System.currentTimeMillis()-lucene.startTime);
    }
}

