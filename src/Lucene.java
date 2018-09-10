package hw1;

/**
 *
 * @author Negin
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class Lucene {

    long startTime;
    int document_counter;
    int word_counter;
    String regx;    
    ArrayList<Integer> DocSize;        
    Set<String> uniqueTerms;    
    Analyzer analyzer;
    Analyzer analyzerQ;
    Directory index;
    String fileName;
    
    public Lucene() {
        startTime = System.currentTimeMillis();
        document_counter = 0;
        word_counter = 0;
        regx = "^[\u0600-\u06FF\u0698\u067E\u0686\u06AF]+$";
        DocSize = new ArrayList<>();        
        uniqueTerms = new HashSet<>();
        analyzer = new SimpleAnalyzer();
        index = new RAMDirectory();  
        fileName = ".\Hamshahri";
    }
    
    public String Tokenization(String raw) throws FileNotFoundException, IOException {        
        analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String string) {
                Tokenizer source = new StandardTokenizer();   
                return new TokenStreamComponents(source);
            }                                        
        };
        TokenStream stream = null;        
        stream = analyzer.tokenStream(null, raw);
        stream.reset();
        String result = "";
        while (stream.incrementToken()) {   
            String TN = stream.getAttribute(CharTermAttribute.class).toString();                
            if (TN.matches(regx)) {                
                word_counter++;
                uniqueTerms.add(TN);                
                result += TN + " ";
            }
        }
        return result;
    }

    public void Indexing() {                
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter index_writer = null;
        try {
            index_writer = new IndexWriter(index, config);
        } 
        catch (IOException ex) {
            Logger.getLogger(HW1.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {            
            File Hamshahri = new File(fileName);
            addDoc(index_writer, Hamshahri);
            index_writer.close();
        } 
        catch (IOException ex) {
            Logger.getLogger(HW1.class.getName()).log(Level.SEVERE, null, ex);
        }                       
        
        int min = Collections.min(DocSize);
        int minIndex = DocSize.indexOf(min);
        int max = Collections.max(DocSize);
        int maxIndex = DocSize.indexOf(max);
        int sum = 0;
        double average;
        if(!DocSize.isEmpty()) {
            for (Integer temp : DocSize)
            {
                if(temp < 0)
                    System.out.println("WTF," + temp);
                sum += temp;
            }
        }
        average = sum / DocSize.size();

        System.out.println("Number of Documents: " + document_counter);
        System.out.println("The Average of Used Word: " + average);
        System.out.println("Vocabulary Size: "+ uniqueTerms.size());
        System.out.println("The Shortest Text is: " + minIndex + " With " + min + " Length.");
        System.out.println("The Longest Text is: " + maxIndex + " With " + max + " Length.");
    }    
    
    private void addDoc(IndexWriter indexWriter,File file) throws IOException {
        if (file.isDirectory()) {
            File [] List = file.listFiles();
            for(File tempFile: List) {
                addDoc(indexWriter,tempFile);
            }
        }
        else {            
            Document document = new Document();
            String line = null;
            String Text = "";            
            boolean flag_is_text = false;
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                try {
                    while((line = bufferedReader.readLine()) != null) {
                        if(line.equals("<DOC>"))
                            document_counter ++;                        
                        else if(line.equals("<TEXT>")) {
                            word_counter = 0;
                            document = new Document();
                            flag_is_text = true;
                        }
                        else if(line.equals("</TEXT>")) {
                            flag_is_text = false;
                            DocSize.add(word_counter); 
                            document.add(new TextField("Text", Text, Field.Store.YES));
                            indexWriter.addDocument(document);
                            Text = "";
                        }
                        else if(flag_is_text) {
                            line = Tokenization(line);
                            Text += line;    
                        }
                    }
                }
                catch (IOException ex) {
                    Logger.getLogger(HW1.class.getName()).log(Level.SEVERE, null, ex);
                }
                bufferedReader.close();
            }
            catch(FileNotFoundException ex) {
                System.out.println("Unable to open file '" + file + "'");
            } 
            System.out.println("Count: " + document_counter);
        }
    }       

    public void Similarity(String qStr) throws IOException {
        analyzerQ = new Analyzer() {
                @Override
                protected TokenStreamComponents createComponents(String string) {
                    Tokenizer source = new StandardTokenizer();   
                    return new TokenStreamComponents(source);
                }                                        
            };
        Query query = null;
        try {
            query = new QueryParser("Text", analyzerQ).parse(qStr);
        } 
        catch (ParseException ex) {
            Logger.getLogger(HW1.class.getName()).log(Level.SEVERE, null, ex);
        }

        int hitPP = 10000;
        IndexReader index_reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(index_reader);
        TopDocs docs = searcher.search(query, hitPP);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < 20; i++)
            System.out.println("Document ID: " + hits[i].doc + "    Score: " + hits[i].score);         
    }
}

