package iti.tree;


import java.util.List;

import beast.core.BEASTObject;
import beast.core.Description;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.evolution.alignment.TaxonSet;
import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;

@Description("Set of ancestral nodes for a set of taxa")
public class NodeSet extends BEASTObject {
	final public Input<TaxonSet> taxaInput = new Input<>("taxon", "set of taxa for which ancestral nodes will be determined");
	final public Input<Tree> treeInput = new Input<>("tree", "tree for which we identify its nodes", Validate.REQUIRED);

	Tree tree;
	// node numbers that are in the MRCA clade and have two descendants in taxon set
	int [] nodeNumbers;
	
	
	@Override
	public void initAndValidate() {
		TaxonSet taxonset = taxaInput.get();
		List<String> taxa = taxonset.asStringList();
		tree = treeInput.get();
		int [] taxonNumbers = new int[taxa.size()];
		int i = 0;
		for (String taxon : taxa) {
			taxonNumbers[i++] = tree.getTaxonset().getTaxonIndex(taxon);
		}
		
		nodeNumbers = new int[taxa.size() - 1];
		traverse(tree.getRoot(), taxonNumbers, new int[1]);		
	}


	/*
	 * populates nodeNumber array with node numbers of nodes that have both left and right child
	 * ancestral to a node with nr in taxonNumbers.
	 * 
	 * returns true if clade contains a taxon with nr in taxonNumbers
	 */
	private boolean traverse(Node node, int[] taxonNumbers, int[] index) {
		if (node.isLeaf()) {
			int i = node.getNr();
			for (int j : taxonNumbers) {
				if (i == j) {
					return true;
				}
			}
			return false;
		} else {
			boolean left = traverse(node.getLeft(), taxonNumbers, index);
			boolean right = traverse(node.getRight(), taxonNumbers, index);
			if (left && right) {
				nodeNumbers[index[0]] = node.getNr();
				index[0]++;
			}
			return left || right;
		}
	}

	
	public int [] getNodeNumbers() {
		return nodeNumbers;
	}
}
