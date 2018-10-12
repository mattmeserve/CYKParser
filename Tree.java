public class Tree {  
    NonTerm phrase; // The Non-terminal
    int startPhrase, endPhrase; // indices of starting and ending word
    String word; // If a leaf, then the word 
    Tree left;     
    Tree right;    
    double prob = 0.0;

    public Tree() {

    }

    public Tree(NonTerm phrase, int startPhrase, int endPhrase, String word, Tree left, Tree right, double prob) {
    	this.phrase = phrase;
    	this.startPhrase = startPhrase;
    	this.endPhrase = endPhrase;
    	this.word = word;
    	this.left = left;
    	this.right = right;
    	this.prob = prob;
    }

    public double getProb() {
    	return prob;
    }

    public void setProb(double prob) {
    	this.prob = prob;
    }

    public void setRight(Tree right) {
    	this.right = right;
    }

    public void setLeft(Tree left) {
    	this.left = left;
    }

    public Tree getRight() {
    	return right;
    }

    public Tree getLeft() {
    	return left;
    }

    public NonTerm getPhrase() {
    	return phrase;
    }

    public String getWord() {
    	return word;
    }
}