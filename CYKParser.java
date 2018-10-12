import java.util.*;
import java.io.*;

enum NonTerm {
	Noun, Prep, Verb, VerbAndObject, PP, PPList, NP, VPWithPPList, S;
}

public class CYKParser {

	public static void main(String[] args) {
		String sentence = "";
		ArrayList<String> cnfGrammar = new ArrayList<String>();
		ArrayList<String> wordGrammar = new ArrayList<String>();
		try {
			File file = new File("input.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			// read in the Chomsky Normal Form grammar first
			while (!(line = bufferedReader.readLine()).equals("")) {
				cnfGrammar.add(line);
			}
			// then read in the word probabilities for the grammar
			while (!(line = bufferedReader.readLine()).equals("")) {
				wordGrammar.add(line);
			}
			// then read in the sentence
			sentence = bufferedReader.readLine().toLowerCase();
			fileReader.close();
		} catch (Exception e) {
			System.out.println("File not found");
		}
		// turn sentence into list
		ArrayList<String> sentenceList = new ArrayList<String>();
		String[] a = sentence.split(" ");
		for (String s : a) {
			sentenceList.add(s);
		}

		// compute parse
		Tree[][][] answer = CYKParse(sentenceList, cnfGrammar, wordGrammar);

		// print
		printTree(answer, sentenceList.size());
	}


	public static Tree[][][] CYKParse(ArrayList<String> sentence, ArrayList<String> cnfGrammar, ArrayList<String> wordGrammar) {
		int n = sentence.size();
		Tree[][][] p = new Tree[9][n][n];

		// initialize tree with 0 probabilities in order to avoid nulls
		for (int i = 0; i < 9; i ++) {
			for (int j = 0; j < n; j ++) {
				for (int k = 0; k < n; k ++) {
					p[i][j][k] = new Tree();
				}
			}
		}

		// insert word -> POS probabilities at the diagonals of each level
		for (int i = 0; i < sentence.size(); i ++) {
			String word = sentence.get(i);
			for (int j = 0; j < wordGrammar.size(); j ++) {
				String[] rule = wordGrammar.get(j).split(" ");
				if (rule[2].equals(word)) {
					double prob = new Double(rule[3].substring(1, rule[3].length() - 1)).doubleValue();
					p[NonTerm.valueOf(rule[0]).ordinal()][i][i] = new Tree(NonTerm.valueOf(rule[0]), i, i, word, null, null, prob);
				}
			}
		}

		double newProb = 0.0;
		double prob = 0.0;
		NonTerm z = null;
		NonTerm y = null;
		for (int length = 2; length < n + 1; length ++) {
			// indexed at 0
			for (int i = 0; i < n + 1 - length; i ++) {
				int j = i + length - 1;
				// iterate through nonterminals
				for (NonTerm m : NonTerm.values()) {
					p[m.ordinal()][i][j] = new Tree(m, i, j, null, null, null, 0.0);
					for (int k = i; k < j; k ++) {
						for (int l = 0; l < cnfGrammar.size(); l ++) {
							String[] rule = cnfGrammar.get(l).split(" ");
							if (rule[0].equals(m.name())) {
								y = NonTerm.valueOf(rule[2]);
								z = NonTerm.valueOf(rule[3]);

								// calculate probabilities and compare - make changes to most likely if needed
								prob = new Double(rule[4].substring(1, rule[4].length() - 1)).doubleValue();
								newProb = p[y.ordinal()][i][k].getProb() * p[z.ordinal()][k + 1][j].getProb() * prob;

								if (newProb > p[m.ordinal()][i][j].getProb()) {
	                  				p[m.ordinal()][i][j].setLeft(p[y.ordinal()][i][k]);
	                				p[m.ordinal()][i][j].setRight(p[z.ordinal()][k + 1][j]);
	                  				p[m.ordinal()][i][j].setProb(newProb);
								}
							}
						}
					}
				}
			}
		}
		return p;
	}

	public static void printTree(Tree[][][] tree, int length) {
		double probability = tree[NonTerm.S.ordinal()][0][length - 1].getProb();
		// if probability is still 0, that means there is no parse tree and it cannot be parsed
		if (probability <= 0) {
			System.out.println("This sentence cannot be parsed");
			return;
		}
		printTree1(tree[NonTerm.S.ordinal()][0][length - 1], 0);

		System.out.println("Probability: " + probability);
	}

	public static void printTree1(Tree tree, int indent) {
		if (tree != null) {
      		for (int i = 0; i < indent; i ++) {
      			System.out.print(" ");
      		}
      		System.out.print(tree.getPhrase());
      		if (tree.getWord() != null) {
      			System.out.print("   " + tree.getWord());
      		}
      		System.out.println();
      		printTree1(tree.getLeft(), indent + 3);
      		printTree1(tree.getRight(), indent + 3);
   		}
	}
	
}