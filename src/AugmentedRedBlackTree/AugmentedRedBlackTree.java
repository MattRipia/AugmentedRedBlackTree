package AugmentedRedBlackTree;

import java.awt.Color;

public class AugmentedRedBlackTree extends BinarySearchTree
{
    protected static final Color RED = Color.red;
    protected static final Color BLACK = Color.black;
    
    public static class BlackHeightException extends RuntimeException
    {
    }

    protected class Node extends BinarySearchTree.Node
    {
        protected Color color;
        protected int min;
        protected int max;
        protected int minDifference;
        
        public Node(Integer data)
        {
            super(data);
            this.color = RED;
            this.min = Integer.MAX_VALUE;
            this.max = Integer.MIN_VALUE;
            this.minDifference = Integer.MAX_VALUE;
        }
        
        @Override
        public String toString()
        {
            return super.toString() + ", " + (color == RED ? "red " : "black ")
                    + " dif: " + minDifference + " min: " + min + " max: " + max
                    + " p: " + parent.data + " lc: " + left.data + " rc: " + right.data;
        }
    }
   
    protected void setNil(Node node)
    {
        node.color = BLACK;
        super.setNil(node);
    }

    public AugmentedRedBlackTree()
    {
        setNil(new Node(null));
        root = nil;
    }

    protected void leftRotate(Node x)
    {
        Node y = (Node) x.right;

        // Swap the in-between subtree from y to x.
        x.right = y.left;
        if (y.left != nil)
            y.left.parent = x;

        // Make y the root of the subtree for which x was the root.
        y.parent = x.parent;

        // If x is the root of the entire tree, make y the root.
        // Otherwise, make y the correct child of the subtree's
        // parent.
        if (x.parent == nil)
            root = y;
        else 
            if (x == x.parent.left)
                x.parent.left = y;
            else
                x.parent.right = y;

        // Relink x and y.
        y.left = x;
        x.parent = y;
    }

    /**
     * Performs a right rotation on a node, making the node's left
     * child its parent.
     *
     * @param x The node.
     */
    protected void rightRotate(Node x)
    {
        Node y = (Node) x.left;

        x.left = y.right;
        if (x.left != null)
            y.right.parent = x;

        y.parent = x.parent;

        y.right = x;
        x.parent = y;

        if (root == x)
            root = y;
        else
            if (y.parent.left == x)
                y.parent.left = y;
            else
                y.parent.right = y;
    }

    @Override
    public Object insert(Integer data)
    {
        Node z = new Node(data);
        treeInsert(z);

        return z;
    }

    protected void treeInsert(Node z)
    {
        super.treeInsert(z);
        insertFixup(z);
    }

    protected void insertFixup(Node z)
    {
        Node y = null;
        Node originalInsertNode = z;

        while (((Node) z.parent).color == RED) 
        {
            if (z.parent == z.parent.parent.left) 
            {
                y = (Node) z.parent.parent.right;
                
                // if both the parent and the uncle are are red, then recolour
                if (y.color == RED) 
                {
                    ((Node) z.parent).color = BLACK;
                    y.color = BLACK;
                    ((Node) z.parent.parent).color = RED;
                    z = (Node) z.parent.parent;
                }
                
                // we need to rotate right
                else 
                {
                    System.out.println("we need to rotate right");
                    // if we are the right child
                    if (z ==  z.parent.right) 
                    {
                        // left rotate
                        z = (Node) z.parent;
                        leftRotate(z);
                        fixAugmentedData(z);
                        System.out.println("... we need to rotate left first:" + z.data);
                    }

                    // right rotate
                    ((Node) z.parent).color = BLACK;
                    ((Node) z.parent.parent).color = RED;
                    System.out.println("right rotating... " + z.parent.parent.data);
                    rightRotate((Node) z.parent.parent);
                    
                    // fix the new min/max/dif for the node we just rotated
                    fixAugmentedData((Node)z.parent.right);
                }
            }
            
            // we need to rotate left
            else 
            {
                y = (Node) z.parent.parent.left;
                
                // if the uncle and parent are both red, no rotation just recolour
                if (y.color == RED) 
                {
                    ((Node) z.parent).color = BLACK;
                    y.color = BLACK;
                    ((Node) z.parent.parent).color = RED;
                    z = (Node) z.parent.parent;
                }
                else 
                {
                    System.out.println("we need to rotate left");
                    if (z ==  z.parent.left) 
                    {
                        z = (Node) z.parent;
                        rightRotate(z);  
                        fixAugmentedData(z);
                        System.out.println("... we need to rotate right first: " + z.data);
                    }

                    ((Node) z.parent).color = BLACK;
                    ((Node) z.parent.parent).color = RED;
                    System.out.println("left rotating... " + z.parent.parent.data);
                    leftRotate((Node) z.parent.parent);
                    
                    // fix the new min/max/dif for the node we just rotated
                    fixAugmentedData((Node) z.parent.left);
                }
            }
        }
        
        fixAugmentedData(originalInsertNode);
        ((Node) root).color = BLACK;
    }
    
    protected void fixAugmentedData(Node z)
    {
        System.out.println("inserting: " + z.data);
        // reset the current min and max values incase a rotation occured
        if(z.left != nil)
        {
            Node leftNode = (Node)z.left;
            z.min = leftNode.min;
        }
        else
        {
            z.min = z.data;
        }

        if(z.right != nil)
        {
            Node rightNode = (Node)z.right;
            z.max = rightNode.max;
        }
        else
        {
            z.max = z.data;
        }
        
        // traverse up the tree, assigning the augmented values - O(log n)
        while(z != nil)
        {
            System.out.println("fixing: " + z.data);
            // child node
            if(z.left == nil && z.right == nil)
            {
                z.min = (int)z.data;
                z.max = (int)z.data;
                z.minDifference = Integer.MAX_VALUE;
            }
            
            // parent node
            else
            {
                Node left = (Node)z.left;
                Node right = (Node)z.right;
                
                Integer leftDif = null;
                Integer rightDif = null;
                
                // left child
                if(left != nil)
                {
                    leftDif = Math.abs((int)z.data - (int)left.data);
                    int leftDifTwo = Math.abs((int)z.data - (int)left.max);
                    leftDif = Math.min(leftDif, leftDifTwo);
                    
                    if(left.min < z.min)
                    {
                        z.min = left.min;
                    }
                    if(left.max > z.max)
                    {
                        z.max = left.max;
                    }
                }
                
                // right child
                if(right != nil)
                {
                    rightDif = Math.abs((int)z.data - (int)right.data);
                    int rightDiffTwo = Math.abs((int)z.data - (int)right.min);
                    rightDif = Math.min(rightDif, rightDiffTwo);
                    //System.out.println("parent: " + z.data + " child: " + right.data + " diff: " + rightDif);
                    
                    if(right.min < z.min)
                    {
                        z.min = right.min;
                    }
                    if(right.max > z.max)
                    {
                        z.max = right.max;
                    }
                }
                
                // both children
                int currMin = 0;
                int leftMin = 0;
                int rightMin = 0;
                
                if(leftDif != null && rightDif != null)
                {
                    leftMin = Math.min(leftDif, left.minDifference);
                    rightMin = Math.min(rightDif, right.minDifference);
                    currMin = Math.min(leftMin, rightMin);
                    currMin = Math.min(z.minDifference, currMin);
                }
                else if(leftDif != null)
                {
                    leftMin = Math.min(leftDif, left.minDifference);
                    currMin = Math.min(leftMin, z.minDifference);
                }
                else if(rightDif != null)
                {
                    rightMin = Math.min(rightDif, right.minDifference);
                    currMin = Math.min(rightMin, z.minDifference);
                }
                
                z.minDifference = currMin;
            }
                       
            z = (Node)z.parent;
        }
    }

    @Override
    public void delete(Object handle)
    {
        Node z = (Node) handle;
        Node y = z;
        Node x = (Node) nil;

        // Do not allow the sentinel to be deleted.
        if (z == nil)
            throw new DeleteSentinelException();

        if (z.left != nil && z.right != nil)
            // find the successor of z if z has two children
            y = (Node) successor(z);

        if (z.left != nil)
            // replace the current node with the left child
            x = (Node) y.left;
        else
            // replace the current node with the right child
            x = (Node) y.right;

        // sets the new parent of the successor node
        x.parent = y.parent;

        // if y is the root, set it as such
        if (y.parent == nil)
            root = x;
        else
            // if y is not the root set the parents new left/right child
            if (y == y.parent.left)
                y.parent.left = x;
            else
                y.parent.right = x;

        if (y != z) 
        {
            y.left = z.left;
            y.left.parent = y;
            y.right = z.right;
            y.right.parent = y;
            y.parent = z.parent;
            
            if (z == root)
                root = y;
            else if (z == z.parent.left)
                z.parent.left = y;
            else
                z.parent.right = y;
        }

        if (y.color == BLACK)
            deleteFixup(x);
    }

    protected void deleteFixup(Node x)
    {
        while (x != root && x.color == BLACK) 
        {
            if (x.parent.left == x) 
            {
                Node w = (Node) x.parent.right;

                if (w.color == RED) 
                {
                    w.color = BLACK;
                    ((Node) x.parent).color = RED;
                    leftRotate((Node) x.parent);
                    w = (Node) x.parent.right;
                }

                if (((Node) w.left).color == BLACK && ((Node) w.right).color == BLACK) 
                {
                    w.color = RED;
                    x = (Node) x.parent;
                }
                else 
                {
                    if (((Node) w.right).color == BLACK) 
                    {
                        ((Node) w.left).color = BLACK;
                        w.color = RED;
                        rightRotate(w);
                        w = (Node) x.parent.right;
                    }

                    w.color = ((Node) x.parent).color;
                    ((Node) x.parent).color = BLACK;
                    ((Node) w.right).color = BLACK;
                    leftRotate((Node) x.parent);
                    x = (Node) root;
                }
            }
            else 
            {
                Node w = (Node) x.parent.left;

                if (w.color == RED) 
                {
                    w.color = BLACK;
                    ((Node) x.parent).color = RED;
                    rightRotate((Node) x.parent);
                    w = (Node) x.parent.left;
                }

                if (((Node) w.right).color == BLACK && ((Node) w.left).color == BLACK) 
                {
                    w.color = RED;
                    x = (Node) x.parent;
                }
                else 
                {
                    if (((Node) w.left).color == BLACK) 
                    {
                        ((Node) w.right).color = BLACK;
                        w.color = RED;
                        leftRotate(w);
                        w = (Node) x.parent.left;
                    }

                    w.color = ((Node) x.parent).color;
                    ((Node) x.parent).color = BLACK;
                    ((Node) w.left).color = BLACK;
                    rightRotate((Node) x.parent);
                    x = (Node) root;
                }       
            }
        }
        
        x.color = BLACK;
    }

    public int blackHeight(Node z)
    {
        if (z == nil)
            return 0;

        int left = blackHeight((Node) z.left);
        int right = blackHeight((Node) z.right);
        if (left == right)
            if (z.color == BLACK)
                return left + 1;
            else
                return left;
        else
            throw new BlackHeightException();
    }

    public int blackHeight()
    {
        return blackHeight((Node) root);
    }
}