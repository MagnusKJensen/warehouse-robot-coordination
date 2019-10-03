package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import java.util.List;

public class Node<T> {
    private T data;
    private Node<T> parent;
    private List<Node<T>> children;

    public Node(T data, Node<T> parent) {
        this.data = data;
        this.parent = parent;
    }
    public void addChild(Node<T> child){
        children.add(child);
    }
    public void removeChild(Node<T> child){
        if(children.contains(child)){
            children.remove(child);
        }
    }
    public List<Node<T>> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public boolean containsData(Node<T> node){
        if(this.data.equals(node.data)){
            //If this node is the node we're looking for then no need to traverse tree
            System.out.println("The node we're looking for is the one we have(goal == start");
            return true;
        }
        //if there are no children then return false
        if(!children.isEmpty()){
            for(Node<T> n : children){
                if(n.data.equals(node.data)){
                    return true;
                }else{
                    n.containsData(n);
                }
            }
        }
        return false;
    }
}
