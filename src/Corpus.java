
package hw1;

/**
 *
 * @author Negin
 */
import java.io.*;
import java.util.*;

public class Corpus {
    long startTime;
    int Xmls;
    int TotalDocs;       
    int index;
    int countIdf;
    int PastLocation;
    int CurrLocation;
    int NextStep;
    double Weight;
    double math;
    int Ni;
    double Tf;
    static int indexRes = 0;
    String current_word;
    String regx;
    String query;
    String fileName;
    Trie trie;
    ArrayList<Integer> DocSize;
    ArrayList<Integer> temp;
    ArrayList<String> Array1;
    ArrayList<String>Array2;
    ArrayList<String> Array3;
    ArrayList<String>Array4;
    HashMap<String, Integer> hashMap;
    HashMap<String, List<Integer>> hashMapIdf;
    HashMap<String, Integer> HashMapTfIdf;
    RandomAccessFile invert;
    OutputStreamWriter writer;
    FileInputStream reader;

    public Corpus() throws FileNotFoundException {
        startTime = System.currentTimeMillis();
        Xmls = 0;
        TotalDocs = 0;
        index = 0;
        countIdf = 0;
        PastLocation = 0;
        CurrLocation = 1000;
        NextStep = 1000;      
        regx = "^[\u0600-\u06FF\u0698\u067E\u0686\u06AF]+$";       
        fileName = ".\Hamshahri";
        trie = new Trie(-1, writer);
        DocSize = new ArrayList<>();
        Array1 = new ArrayList<>();
        Array2 = new ArrayList<>();
        Array3 = new ArrayList<>();
        Array4 = new ArrayList<>();
        hashMap = new HashMap<>();
        hashMapIdf = new HashMap<>();        
        HashMapTfIdf = new HashMap<>();     
        invert = new RandomAccessFile(".\Inverted.txt", "rw");
        writer = new OutputStreamWriter(new FileOutputStream( ".\ByIndex\\" + 0 + ".txt"));        
    }
    
    public String[] Tokenization(String raw) throws UnsupportedEncodingException {
        String[] words = raw.split(" ");
        String result = "";
        for (String tmp: words) {
            if(tmp.matches(regx)){
                result += tmp + " ";
            }
        }
        return result.split(" ");
    }

    private void addDoc(File file) throws FileNotFoundException, IOException{
        if (file.isDirectory()) {
            File [] List = file.listFiles();
            for(File tempFile: List) {
                addDoc(tempFile);
            }
        }
        else {
            boolean is_first;
            Xmls++;                
            is_first = true;               
            //System.out.println(file);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null; 
            boolean flag_is_text = false;
            while((line = bufferedReader.readLine()) != null) {                
                if(line.equals("<TEXT>")) {                                                                                  
                    flag_is_text = true;     
                    trie.Search_Total(trie.root, "");
                    if (is_first) {
                        writer.close();
                        writer = new OutputStreamWriter(new FileOutputStream( ".\ByIndex\\" + Xmls + ".txt"));
                        is_first = false;
                    }
                    if (TotalDocs != 0)
                        DocSize.add(trie.TotalWords);                                                               
                    TotalDocs++;
                    trie = null;
                    trie = new Trie(TotalDocs, writer);
                }
                else if(line.equals("</TEXT>")) {                                                        
                    flag_is_text = false;
                    writer.write("* " + TotalDocs + "\n");  
                }
                else if(flag_is_text) {   
                    String[] tokens = Tokenization(line);
                    for (String tmp: tokens){
                        current_word = tmp;
                        if(current_word.matches(regx)){
                            if (trie.search(trie.root, current_word) != null)
                                trie.search(trie.root, current_word).counter++;
                            else {
                                trie.add(trie.root, current_word, Indexing());
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void TF() throws FileNotFoundException, IOException {                                                                   
        File Hamshahri = new File(fileName);
        addDoc(Hamshahri);    
        
        trie.Search_Total(trie.root,"");
        DocSize.add(trie.TotalWords);
        int min = Collections.min(DocSize);
        int minIndex = DocSize.indexOf(min);
        int max = Collections.max(DocSize);
        int maxIndex = DocSize.indexOf(max);
        int sum = 0;
        double average;
        if(!DocSize.isEmpty()) {
            for (Integer temp : DocSize)
                sum += temp;
        }
        average = sum / DocSize.size();

        System.out.println("Number of Documents: " + TotalDocs);
        System.out.println("The Average of Used Word: " + average);
        System.out.println("Vocabulary Size: "+ hashMap.size());
        System.out.println("The Shortest Text is: " + minIndex + " With " + min + " Length.");
        System.out.println("The Longest Text is: " + maxIndex + " With " + max + " Length."); 
        trie.writer.close();
    }        
    
    public void IDF() throws FileNotFoundException, IOException {
        int Docs;
        while(CurrLocation < 500000) {
            Docs = 0;
            hashMapIdf = null;
            hashMapIdf = new HashMap<>();
            String words[];
            for(int i=0; i < Xmls; i++) {                
                FileReader fileReader = new FileReader(".\ByIndex\\" + i + ".txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line = null; 
                while((line = bufferedReader.readLine()) != null) {                    
                    words = line.split("\t");
                    if(words[0].equals(""))
                        break;                        
                    if (words[0].startsWith("*"))
                        Docs++;                    
                    else if(Integer.valueOf(words[0]) > PastLocation && Integer.valueOf(words[0]) <= CurrLocation) {
                        try {
                            hashMapIdf.get(words[0]).add(Docs);
                        }
                        catch (NullPointerException nullPointer) {
                            temp = null;
                            temp = new ArrayList<>();
                            temp.add(Docs);
                            hashMapIdf.put(words[0], temp);
                        }
                    }
                }
            }         
            System.out.println("here " + CurrLocation + " Size: " + hashMapIdf.size());
            for(int i = PastLocation+1; i <= CurrLocation; i++) {
                invert.writeChars("#"+i);                
                try {
                    for(int tag:hashMapIdf.get(String.valueOf(i)))
                        invert.writeChars("\t" + String.valueOf(tag));                    
                    invert.writeChars("\n");
                }
                catch(NullPointerException n) {
                    invert.writeChars("\n");
                }
            }
           
            if(CurrLocation == 4000)
                NextStep += 1000;
            else if(CurrLocation == 16000)
                NextStep += 2000;
            else if(CurrLocation == 24000)
                NextStep += 4000;
            else if(CurrLocation == 40000)
                NextStep += 30000;
            else if(CurrLocation == 78000)
                NextStep += 62000;
            else if(CurrLocation == 178000)
                NextStep += 100000;
            else if(CurrLocation == 378000)
                NextStep += 110000;

            PastLocation = CurrLocation;
            CurrLocation += NextStep;
        }        
    }

    public void TFIDF() throws FileNotFoundException, IOException {
        int Docs = 0;
        FileReader fileReader = new FileReader(".\Inverted.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;         
        String []words = null;
        while((line = bufferedReader.readLine()) != null) {            
            words = line.split("\t");            
            if(words[0].length() > 2)
                words[0] = words[0].replaceAll("" + words[0].charAt(2),"");                               
            if(words[0].length() > 0 && words[0].charAt(0) == '#') {                
                try {                    
                    int a = HashMapTfIdf.get(words[0].substring(1));                    
                }
                catch(NullPointerException n) {                    
                    HashMapTfIdf.put(words[0].substring(1), words.length-1);                    
                }
            }
        }
        System.out.println(HashMapTfIdf.size());
        System.out.println("IDF Finished!!!");
        
        boolean is_first = true;
        for(int i=0; i <= Xmls; i++) { 
            System.out.println(i);            
            FileReader fileReader_tf = new FileReader(".\ByIndex\\" + i + ".txt");
            BufferedReader bufferedReader_tf = new BufferedReader(fileReader_tf);
            line = null;                         
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(".\Weights\W" + i + ".txt"));
            while((line = bufferedReader_tf.readLine()) != null) {
                words = line.split("\t");
                    if(words[0].equals(""))
                        break;       
                    if (words[0].startsWith("*")) {
                        Docs++;
                        if(!is_first) {
                            for(int j=0; j < Array1.size(); j++) {
                                try {
                                    Ni = HashMapTfIdf.get(Array1.get(j));
                                }
                                catch(NullPointerException n) {                                                                       
                                }                                
                                math = (double)TotalDocs/Ni;
                                Tf = (double)(1+ Math.log(Integer.parseInt(Array2.get(j))));                
                                Weight = (double)(Tf * (Math.log10(math)));
                                writer.write(Array1.get(j) + "\t" + String.valueOf(Weight) + "\n");
                            }
                        }
                        else
                            is_first = false;
                        writer.write("* " + Docs + "\n"); 
                        Array1.clear();
                        Array2.clear();
                    }
                    else {
                        Array1.add(words[0]);
                        Array2.add(words[1]);
                    }
            }                    
            writer.close();
        }
    }

    public void CosineSimilarity(String query) throws IOException {
        int qIndex = -1;
        HashMap<Integer, Integer> hashMapQuery = new HashMap<>();
        this.query = query;
        String []words;
        int QueryWords = 0;
        double[] Scores = new double[TotalDocs];
        HashMap<Double, Integer> hashMapScore = new HashMap<>();
        String line;
        int Docs = 0;

        String[] out_1 = query.split("\n");
        for(String tmp:out_1) {
            words = tmp.split(" ");
            QueryWords += words.length;
            for (int i = 0; i < words.length; i++) {                
                current_word = words[i];
                if(current_word.matches(regx)) {
                    try {
                        qIndex = (int) hashMap.get(current_word);
                    }
                    catch (NullPointerException nullPointer) {
                        qIndex = -10;
                    }
                    try {
                        hashMapQuery.put(qIndex, (hashMapQuery.get(qIndex) + 1));
                    }
                    catch (NullPointerException nullPointer) {
                        if (qIndex != -10)
                            hashMapQuery.put(qIndex, 1);
                    }
                }
                else 
                    QueryWords--;
            }
        }
        
        for(int i = 0; i <= Xmls; i++) { 
            Scores[0] = 0;
            System.out.println(i);            
            FileReader fileReader = new FileReader(".\Weights\W" + i + ".txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            line = null;                                     
            while((line = bufferedReader.readLine()) != null) {
                words = line.split("\t");
                if(words[0].equals(""))
                    break;       
                if (words[0].startsWith("*")) {                        
                        Scores[Docs] = 0;
                        for(int j=0; j < Array3.size(); j++) {    
                            try {     
                                Ni = HashMapTfIdf.get(Array3.get(j));
                                math = (double)TotalDocs/Ni;
                                Tf = (double)(1+ Math.log(hashMapQuery.get(Integer.parseInt(Array3.get(j)))));                                
                                Scores[Docs] += (double) (Tf * (Math.log10(math))) * Double.parseDouble(Array4.get(j));                                                   
                            }
                            catch(NullPointerException n) {                                                                 
                            }
                        }
                    Scores[Docs] = Scores[Docs] / (Math.sqrt(QueryWords) * Math.sqrt(DocSize.get(Docs)));
                    hashMapScore.put(Scores[Docs], Docs);
                    Docs++;
                    Array3.clear();
                    Array4.clear();
                }
                else {
                    Array3.add(words[0]);
                    Array4.add(words[1]);
                }
            }                         
        }                         
        
        int walker = 0;
        Map<Double, Integer> treeMap = new TreeMap<>(Collections.reverseOrder());
        treeMap.putAll(hashMapScore);
        Set entrySet = treeMap.entrySet();
        Iterator it = entrySet.iterator();
        boolean flag = true;
        while(it.hasNext() && walker < 20 && flag) {
            walker++;
            Map.Entry me = (Map.Entry) it.next();            
            if(me.getKey() == 0.0)            
                flag = false;  
            else
                System.out.println("Document ID: " + me.getKey() +"    Score: " + me.getValue());
        }
    }

    public int Indexing() throws IOException {        
        try {
            indexRes = (int) hashMap.get(current_word);
        }
        catch (NullPointerException nullPointer) {
            index ++;
            hashMap.put(current_word, index);
            indexRes = index;
        }
        return indexRes;
    }
}