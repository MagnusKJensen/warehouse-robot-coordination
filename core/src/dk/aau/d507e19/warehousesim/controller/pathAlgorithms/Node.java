package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
    private T data;
    private Node<T> parent;
    private List<Node<T>> children = new ArrayList<>();

    public Node(T data, Node<T> parent) {
        this.data = data;
        this.parent = parent;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public void addChild(Node<T> child){
        children.add(child);
        if(child.getParent()!=null){
            child.getParent().removeChild(child);
        }
        child.setParent(this);
    }

    public Node<T> getParent() {
        if(parent==null){
            return null;
        }
        return parent;
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

    public boolean containsData(T node){
        if(this.data.equals(node)){
            //If this node is the node we're looking for then no need to traverse tree

            return true;
        }
        //if there are no children then return false
        if(!children.isEmpty()){
            for(Node<T> n : children){
                if(n.data.equals(node)){
                    return true;
                }else{
                    n.containsData(n.data);
                }
            }
        }
        return false;
    }
}
