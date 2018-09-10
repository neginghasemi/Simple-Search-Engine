
package hw1;

/**
 *
 * @author Negin
 */
public class TrieNode {
    TrieNode[] child;
    char value;
    int counter;
    int id;
    boolean isEnd;

    public TrieNode(char c, int id) {
        child = new TrieNode[33];
        this.id = id;
        this.value = c;
        for(int i=0; i < 33; i++)
            this.child[i] = null;
        this.counter = 0;
        this.isEnd = false;
    }
}
