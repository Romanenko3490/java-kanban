package models;

public class Node {
    private Node prev;
    private AbstractTask task;
    private Node next;

    public Node(AbstractTask task) {
        this.prev = null;
        this.task = task;
        this.next = null;
    }

    public Node(Node prev, AbstractTask task, Node next) {
        this.prev = prev;
        this.task = task;
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public Node getLast() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public AbstractTask getTask() {
        return task;
    }

    public void setTask(AbstractTask task) {
        this.task = task;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
