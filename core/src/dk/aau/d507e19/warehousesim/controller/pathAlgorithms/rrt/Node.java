package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node<T> {
    private T data;
    private Node<T> parent;
    private List<Node<T>> children = new ArrayList<>();
    private boolean blockedStatus;

    public Node(T data, Node<T> parent, boolean blockedStatus) {
        this.data = data;
        this.blockedStatus = blockedStatus;
        if (parent != null) {
            setParent(parent);

        } else {
            this.parent = parent;
        }
    }

    public boolean getBlockedStatus() {
        return blockedStatus;
    }

    public void setBlockedStatus(boolean blockedStatus) {
        this.blockedStatus = blockedStatus;
    }

    public int stepsToRoot() {
        if (!(this.getParent() == null)) {
            return this.getParent().stepsToRoot() + 1;
        } else return 0;
    }

    public void makeRoot() {
        this.updateTree(this);
    }

    public Node<T> getRoot() {
        if (this.getParent() != null) {
            return this.getParent().getRoot();
        } else {
            return this;
        }
    }

    public Node<T> copy() {
        //WARNING - SHOULD ONLY BE CALLED ON ROOT NODE //todo make impossible to call on other than root
        Node<T> copiedNode = new Node<>(this.data, null, false);
        if (this.children != null) {
            for (Node<T> n : this.getChildren()) {
                n.copy().setParent(copiedNode);
            }
        }
        return copiedNode;

    }

    public Node<T> findNode(T data) {
        //Checks if given Node is a child, returns node if found else returns null
        if (this.getData().equals(data)) {
            return this;
        } else {
            for (Node<T> n : this.getChildren()) {
                Node<T> foundNode = n.findNode(data);
                if (foundNode == null) {
                    continue;
                }
                return foundNode;
            }
        }
        return null;
    }

    private void updateTree(Node<T> node) {
        if (this.getParent() != null) {
            this.getParent().updateTree(this);
            if (node.equals(this)) {
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
        if (this.getParent() != null) {
            this.getParent().removeChild(this);
        }
        //remove future parent from list of children if its there
        if(this.getChildren().contains(parent)){
            this.removeChild(parent);
        }
        this.parent = parent;
        this.parent.children.add(this);
    }

    public void removeParent() {
        this.parent = null;
    }

    public Node<T> getParent() {
        if (parent == null) {
            return null;
        }
        return parent;
    }

    public void removeChild(Node<T> child) {
        if (children.contains(child)) {
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

    public void printTree(Node<T> node) {
        System.out.println(node.getData().toString());
        for (Node<T> n : node.getChildren()) {
            printTree(n);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(data, node.data) &&
                Objects.equals(children, node.children);
    }

}
