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

    public void makeRoot(){
        this.updateTree(this);
    }

    private void updateTree(Node<T> node){
        if(this.getParent()!=null){
            this.getParent().updateTree(this);
            if(node.equals(this)){
                this.removeParent();
                return;
            }
            //make sure we remove the parents child node from list of children before we set the child as the parent of the parent.
            this.removeChild(node);
            this.setParent(node);
            return;
        }
        this.setParent(node);
    }

    public void setParent(Node<T> parent) {
        if(this.getParent()!=null){
            this.getParent().removeChild(this);
        }
        this.parent = parent;
        this.parent.children.add(this);
    }
    public void removeParent(){
        this.parent = null;
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
            child.removeParent();
        }
    }
    public List<Node<T>> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void printTree(Node<T> node){
        System.out.println(node.getData().toString());
        for(Node<T> n : node.getChildren()){
            printTree(n);
        }
    }
}
