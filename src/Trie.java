
package hw1;

/**
 *
 * @author Negin
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Trie {
    TrieNode root;
    int current_index;
    int TotalWords;
    OutputStreamWriter writer;

    public Trie(int Docs, OutputStreamWriter writer) throws FileNotFoundException {
        //System.out.println(Docs);
        this.root = new TrieNode(' ',-1);
        this.current_index = -1;
        this.TotalWords = 0;
        this.writer = writer;
    }

    public void add(TrieNode parent, String s, int id) {
        if(s.length()==0)
            return ;
        else if(s.length()==1) {
            current_index = IndexLetters(s.charAt(0));
            if(current_index < 0)
                return ;
            if(parent.child[current_index] == null)
                parent.child[current_index] = new TrieNode(s.charAt(0), id);
            parent.child[current_index].counter++;
            parent.child[current_index].isEnd = true;
            parent.child[current_index].id = id;
        }
        else {
            if((int)s.charAt(0) < 1741 ) {
                current_index = IndexLetters(s.charAt(0));
                if(current_index < 0)
                    return ;
                if(parent.child[current_index] == null)
                    parent.child[current_index] = new TrieNode(s.charAt(0), id);
                add(parent.child[current_index], s.substring(1), id);
            }
            else
                add(root, s.substring(1), id);
        }
    }

    public TrieNode search(TrieNode parent,String s) {
        if(s.length() == 0)
            return null ;
        current_index = IndexLetters(s.charAt(0));
        if(current_index < 0)
            return null;
        if(s.length() == 1) {
            if(parent.child[current_index] == null)
                return null;
            else if(parent.child[current_index].isEnd)
                return parent.child[current_index];
            else
                return null;
        }
        else {
            if(current_index < 0)
                return null;
            if(parent.child[current_index] == null)
                return null;
            else
                return search(parent.child[current_index], s.substring(1));
        }
    }

    public void Search_Total(TrieNode parent, String current) throws IOException {
        for(int i=0; i < 33; i++) {
            if(parent.child[i] != null) {
                if(parent.child[i].isEnd) {
                    TotalWords += parent.child[i].counter;                
                    writer.write(parent.child[i].id + "\t" + parent.child[i].counter + "\n");
                }
                Search_Total(parent.child[i] , current+parent.child[i].value);
            }
        }
    }

    public int IndexLetters(char letter) {
        int index;
        switch(letter) {
            case 'آ':
                index=0;
                break;

            case 'ب':
                index=1;
                break;

            case 'پ':
                index=2;
                break;

            case 'ت':
                index=3;
                break;

            case 'ث':
                index=4;
                break;

            case 'ج':
                index=5;
                break;

            case 'چ':
                index=6;
                break;

            case 'ح':
                index=7;
                break;

            case 'خ':
                index=8;
                break;

            case 'د':
                index=9;
                break;

            case 'ذ':
                index=10;
                break;

            case 'ر':
                index=11;
                break;

            case 'ز':
                index=12;
                break;

            case 'ژ':
                index=13;
                break;

            case 'س':
                index=14;
                break;

            case 'ش':
                index=15;
                break;

            case 'ص':
                index=16;
                break;

            case 'ض':
                index=17;
                break;

            case 'ط':
                index=18;
                break;

            case 'ظ':
                index=19;
                break;

            case 'ع':
                index=20;
                break;

            case 'غ':
                index=21;
                break;

            case 'ف':
                index=22;
                break;

            case 'ق':
                index=23;
                break;

            case 'ک':
            case 'ك':
                index=24;
                break;

            case 'گ':
                index=25;
                break;

            case 'ل':
                index=26;
                break;

            case 'م':
                index=27;
                break;

            case 'ن':
                index=28;
                break;

            case 'و':
            case 'ؤ':
                index=29;
                break;

            case 'ه':
            case 'ة':
            case 'ۀ':
                index=30;
                break;

            case 'ی':
            case 'ي':
                index=31;
                break;
            case 'ا':
            case 'إ':
            case 'أ':

                index=32;
                break;

            default:
                index=-1;
        }
        return index;
    }
}

