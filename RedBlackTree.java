package tree3;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
public class RedBlackTree 
{
	 public enum Color {                   //RED=0 , BLACK=1
	        RED, 
	        BLACK
	    }

	    public static class Node {         //Node structure
	        int data;
	        Color color;
	        Node left;
	        Node right;
	        Node parent;
	        boolean isNullLeaf;               //for checking if the node is leaf node or not
	    }

	    private static Node createBlackNode(int data) {     //creating black node in case of root node
	        Node node = new Node();
	        node.data = data;
	        node.color = Color.BLACK;
	        node.left = createNullLeafNode(node);
	        node.right = createNullLeafNode(node);
	        return node;
	    }

	    private static Node createNullLeafNode(Node parent) {     //creating null node
	        Node leaf = new Node();
	        leaf.color = Color.BLACK;
	        leaf.isNullLeaf = true;
	        leaf.parent = parent;
	        return leaf;
	    }

	    private static Node createRedNode(Node parent, int data) {       //creating red colored node
	        Node node = new Node();
	        node.data = data;
	        node.color = Color.RED;
	        node.parent = parent;
	        node.left = createNullLeafNode(node);
	        node.right = createNullLeafNode(node);
	        return node;
	    }

	    public Node insert(Node parent, Node root, int data) {         //inserting element
	        if(root  == null || root.isNullLeaf) {              //checking if root is not present
	            if(parent != null) {                            //checking if parent is present
	                return createRedNode(parent, data);        //root present
	            } else { 
	                return createBlackNode(data);              //no node present
	            }
	        }

	        if(root.data == data) {                 //duplicate data not allowed
	            throw new IllegalArgumentException("Duplicate date " + data);
	        }
	        boolean isLeft;                //to check if root is in left side or not
	        if(root.data > data) {         //go to left
	            Node left = insert(root, root.left, data);      //create a node
	            
	            if(left == root.parent) {       //to check if rotation is done already or not
	                return left;
	            }
	          
	            root.left = left;             //insert newly create node
	           
	            isLeft = true;                //because inserted in left side
	        } else {                                                             //go to right
	            Node right = insert(root, root.right, data);
	      
	            if(right == root.parent) {
	                return right;
	            }
	            
	            root.right = right;
	        
	            isLeft = false;
	        }

	        if(isLeft) {                                 //if node is inserted in left
	           
	            if(root.color == Color.RED && root.left.color == Color.RED) {
	           
	                Optional<Node> sibling = findSiblingNode(root);       //to check if sibling is present or not
	                if(!sibling.isPresent() || sibling.get().color == Color.BLACK) {   //if absent or present with black color
	                    if(isLeftChild(root)) {              //if if root is left child of its parent
	                        rightRotate(root, true);         //LL Rotation so rotate right
	                    } else {
	                        rightRotate(root.left, false);   //LR rotation so rotate left(dont color)--> rotate right(recolor)
	                        root = root.parent; 
	                        leftRotate(root, true);
	                    }

	                } else {                                  //only recoloring
	                    root.color = Color.BLACK;
	                    sibling.get().color = Color.BLACK;
	                    if(root.parent.parent != null) {
	                        root.parent.color = Color.RED;
	                    }
	                }
	            }
	        } else {                    //if node is inserted in right
	            if(root.color == Color.RED && root.right.color == Color.RED) {  
	                Optional<Node> sibling = findSiblingNode(root);
	                if(!sibling.isPresent() || sibling.get().color == Color.BLACK) {
	                    if(!isLeftChild(root)) {
	                        leftRotate(root, true);
	                    } else {
	                        leftRotate(root.right, false);
	                        root = root.parent;
	                        rightRotate(root, true);
	                    }
	                } else {
	                    root.color = Color.BLACK;
	                    sibling.get().color = Color.BLACK;
	                    if(root.parent.parent != null) {
	                        root.parent.color = Color.RED;
	                    }
	                }
	            }
	        }
	        return root;
	    }
	    private void rightRotate(Node root, boolean changeColor) {      //R rotation
	        Node parent = root.parent;
	        root.parent = parent.parent;
	        if(parent.parent != null) {
	            if(parent.parent.right == parent) {
	                parent.parent.right = root;
	            } else {
	                parent.parent.left = root;
	            }
	        }
	        Node right = root.right;
	        root.right = parent;
	        parent.parent = root;
	        parent.left = right;
	        if(right != null) {
	            right.parent = parent;
	        }
	        if(changeColor) {
	            root.color = Color.BLACK;
	            parent.color = Color.RED;
	        }
	    }

	    private void leftRotate(Node root, boolean changeColor) {    //L rotation
	        Node parent = root.parent;
	        root.parent = parent.parent;
	        if(parent.parent != null) {
	            if(parent.parent.right == parent) {
	                parent.parent.right = root;
	            } else {
	                parent.parent.left = root;
	            }
	        }
	        Node left = root.left;
	        root.left = parent;
	        parent.parent = root;
	        parent.right = left;
	        if(left != null) {
	            left.parent = parent;
	        }
	        if(changeColor) {
	            root.color = Color.BLACK;
	            parent.color = Color.RED;
	        }
	    }

	    private Optional<Node> findSiblingNode(Node root) {                 //return sibling
	        Node parent = root.parent;
	        if(isLeftChild(root)) {
	            return Optional.ofNullable(parent.right.isNullLeaf ? null : parent.right);
	        } else {
	            return Optional.ofNullable(parent.left.isNullLeaf ? null : parent.left);
	        }
	    }

	    private boolean isLeftChild(Node root) {     //tells if its left child or not
	        Node parent = root.parent;
	        if(parent.left == root) {
	            return true;
	        } else {
	            return false;
	        }
	    }
	    public void printRedBlackTree(Node root) {       //print the tree
	        if(root == null || root.isNullLeaf) {
	            return;
	        }
	        printRedBlackTree(root.right);
	        System.out.print(root.data+" ");
	        printRedBlackTree(root.left);
	    }
	    public Node delete(Node root, int data) {
	        AtomicReference<Node> rootReference = new AtomicReference<>();
	        delete(root, data, rootReference);
	        if(rootReference.get() == null) {
	            return root;
	        } else {
	            return rootReference.get();
	        }
	    }


	public boolean validateRedBlackTree(Node root) {

	    if(root == null) {
	        return true;
	    }
	    //check if root is black
	    if(root.color != Color.BLACK) {
	        System.out.print("Root is not black");
	        return false;
	    }
	    //Use of AtomicInteger solely because java does not provide any other mutable int wrapper.
	    AtomicInteger blackCount = new AtomicInteger(0);
	    //make sure black count is same on all path and there is no red red relationship
	    return checkBlackNodesCount(root, blackCount, 0) && noRedRedParentChild(root, Color.BLACK);
	}



	private void delete(Node root, int data, AtomicReference<Node> rootReference) {
	    if(root == null || root.isNullLeaf) {
	        return;
	    }
	    if(root.data == data) {
	        //if node to be deleted has 0 or 1 null children then we have
	        //deleteOneChild use case as discussed in video.
	        if(root.right.isNullLeaf || root.left.isNullLeaf) {
	            deleteOneChild(root, rootReference);
	        } else {
	            //otherwise look for the inorder successor in right subtree.
	            //replace inorder successor data at root data.
	            //then delete inorder successor which should have 0 or 1 not null child.
	            Node inorderSuccessor = findSmallest(root.right);
	            root.data = inorderSuccessor.data;
	            delete(root.right, inorderSuccessor.data, rootReference);
	        }
	    }
	    //search for the node to be deleted.
	    if(root.data < data) {
	        delete(root.right, data, rootReference);
	    } else {
	        delete(root.left, data, rootReference);
	    }
	}

	private Node findSmallest(Node root) {
	    Node prev = null;
	    while(root != null && !root.isNullLeaf) {
	        prev = root;
	        root = root.left;
	    }
	    return prev != null ? prev : root;
	}

	private void deleteOneChild(Node nodeToBeDelete, AtomicReference<Node> rootReference) {
	    Node child = nodeToBeDelete.right.isNullLeaf ? nodeToBeDelete.left : nodeToBeDelete.right;
	    //replace node with either one not null child if it exists or null child.
	    replaceNode(nodeToBeDelete, child, rootReference);
	    //if the node to be deleted is BLACK. See if it has one red child.
	    if(nodeToBeDelete.color == Color.BLACK) {
	        //if it has one red child then change color of that child to be Black.
	        if(child.color == Color.RED) {
	            child.color = Color.BLACK;
	        } else {
	            //otherwise we have double black situation.
	            deleteCase1(child, rootReference);
	        }
	    }
	}


	/**
	 * If double black node becomes root then we are done. Turning it into
	 * single black node just reduces one black in every path.
	 */
	private void deleteCase1(Node doubleBlackNode, AtomicReference<Node> rootReference) {
	    if(doubleBlackNode.parent == null) {
	        rootReference.set(doubleBlackNode);
	        return;
	    }
	    deleteCase2(doubleBlackNode, rootReference);
	}

	/**
	 * If sibling is red and parent and sibling's children are black then rotate it
	 * so that sibling becomes black. Double black node is still double black so we need
	 * further processing.
	 */
	private void deleteCase2(Node doubleBlackNode, AtomicReference<Node> rootReference) {
	    Node siblingNode = findSiblingNode(doubleBlackNode).get();
	    if(siblingNode.color == Color.RED) {
	        if(isLeftChild(siblingNode)) {
	            rightRotate(siblingNode, true);
	        } else {
	            leftRotate(siblingNode, true);
	        }
	        if(siblingNode.parent == null) {
	            rootReference.set(siblingNode);
	        }
	    }
	    deleteCase3(doubleBlackNode, rootReference);
	}

	/**
	 * If sibling, sibling's children and parent are all black then turn sibling into red.
	 * This reduces black node for both the paths from parent. Now parent is new double black
	 * node which needs further processing by going back to case1.
	 */
	private void deleteCase3(Node doubleBlackNode, AtomicReference<Node> rootReference) {

	    Node siblingNode = findSiblingNode(doubleBlackNode).get();

	    if(doubleBlackNode.parent.color == Color.BLACK && siblingNode.color == Color.BLACK && siblingNode.left.color == Color.BLACK
	            && siblingNode.right.color == Color.BLACK) {
	        siblingNode.color = Color.RED;
	        deleteCase1(doubleBlackNode.parent, rootReference);
	    } else {
	        deleteCase4(doubleBlackNode, rootReference);
	    }
	}

	/**
	 * 	 * If sibling color is black, parent color is red and sibling's children color is black then swap color b/w sibling
	 * and parent. This increases one black node on double black node path but does not affect black node count on
	 * sibling path. We are done if we hit this situation.
	 */
	private void deleteCase4(Node doubleBlackNode, AtomicReference<Node> rootReference) {
	    Node siblingNode = findSiblingNode(doubleBlackNode).get();
	    if(doubleBlackNode.parent.color == Color.RED && siblingNode.color == Color.BLACK && siblingNode.left.color == Color.BLACK
	    && siblingNode.right.color == Color.BLACK) {
	        siblingNode.color = Color.RED;
	        doubleBlackNode.parent.color = Color.BLACK;
	        return;
	    } else {
	        deleteCase5(doubleBlackNode, rootReference);
	    }
	}

	/**
	 * If sibling is black, double black node is left child of its parent, siblings right child is black
	 * and sibling's left child is red then do a right rotation at siblings left child and swap colors.
	 * This converts it to delete case6. It will also have a mirror case.
	 */
	private void deleteCase5(Node doubleBlackNode, AtomicReference<Node> rootReference) {
	    Node siblingNode = findSiblingNode(doubleBlackNode).get();
	    if(siblingNode.color == Color.BLACK) {
	        if (isLeftChild(doubleBlackNode) && siblingNode.right.color == Color.BLACK && siblingNode.left.color == Color.RED) {
	            rightRotate(siblingNode.left, true);
	        } else if (!isLeftChild(doubleBlackNode) && siblingNode.left.color == Color.BLACK && siblingNode.right.color == Color.RED) {
	            leftRotate(siblingNode.right, true);
	        }
	    }
	    deleteCase6(doubleBlackNode, rootReference);
	}

	/**
	 * If sibling is black, double black node is left child of its parent, sibling left child is black and sibling's right child is
	 * red, sibling takes its parent color, parent color becomes black, sibling's right child becomes black and then do
	 * left rotation at sibling without any further change in color. This removes double black and we are done. This
	 * also has a mirror condition.
	 */
	private void deleteCase6(Node doubleBlackNode, AtomicReference<Node> rootReference) {
	    Node siblingNode = findSiblingNode(doubleBlackNode).get();
	    siblingNode.color = siblingNode.parent.color;
	    siblingNode.parent.color = Color.BLACK;
	    if(isLeftChild(doubleBlackNode)) {
	        siblingNode.right.color = Color.BLACK;
	        leftRotate(siblingNode, false);
	    } else {
	        siblingNode.left.color = Color.BLACK;
	        rightRotate(siblingNode, false);
	    }
	    if(siblingNode.parent == null) {
	        rootReference.set(siblingNode);
	    }
	}

	private void replaceNode(Node root, Node child, AtomicReference<Node> rootReference) {
	    child.parent = root.parent;
	    if(root.parent == null) {
	        rootReference.set(child);
	    }
	    else {
	        if(isLeftChild(root)) {
	            root.parent.left = child;
	        } else {
	            root.parent.right = child;
	        }
	    }
	}

	private boolean noRedRedParentChild(Node root, Color parentColor) {
	    if(root == null) {
	        return true;
	    }
	    if(root.color == Color.RED && parentColor == Color.RED) {
	        return false;
	    }

	    return noRedRedParentChild(root.left, root.color) && noRedRedParentChild(root.right, root.color);
	}

	private boolean checkBlackNodesCount(Node root, AtomicInteger blackCount, int currentCount) {

	    if(root.color == Color.BLACK) {
	        currentCount++;
	    }

	    if(root.left == null && root.right == null) {
	        if(blackCount.get() == 0) {
	            blackCount.set(currentCount);
	            return true;
	        } else {
	            return currentCount == blackCount.get();
	        }
	    }
	    return checkBlackNodesCount(root.left, blackCount, currentCount) && checkBlackNodesCount(root.right, blackCount, currentCount);
	}
	    public static void main(String args[]) {
	        Node root = null;
	        RedBlackTree redBlackTree = new RedBlackTree();

	        root = redBlackTree.insert(null,root, 10);
	        root = redBlackTree.insert(null,root, 18);
	        root = redBlackTree.insert(null,root, 7);
	        root = redBlackTree.insert(null,root, 15);
	        root = redBlackTree.insert(null,root, 16);
	        root = redBlackTree.insert(null,root, 30);
	        root = redBlackTree.insert(null,root, 25);
	        root = redBlackTree.insert(null,root, 40);
	        root = redBlackTree.insert(null,root, 60);
	        root = redBlackTree.insert(null,root, 2);
	        root = redBlackTree.insert(null,root, 1);
	        root = redBlackTree.insert(null,root, 70);
	        redBlackTree.printRedBlackTree(root);
	        root = redBlackTree.delete(root, 70);
	        redBlackTree.printRedBlackTree(root);
	    }
}
