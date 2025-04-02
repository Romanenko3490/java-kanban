package controllers;

import models.AbstractTask;
import models.Node;

import javax.print.attribute.HashAttributeSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> historyMap = new HashMap<>();

    private Node head;
    private Node tail;
    private int size = 0;

    @Override
    public ArrayList<AbstractTask> getHistory() {
        return getTasks();
    }

    @Override
    public void addToHistory(AbstractTask task) {
        if (task == null)
            return;
        if (historyMap.containsKey(task.getId())) {
            Node nodeToChange = historyMap.get(task.getId());
            removeNode(nodeToChange);
            linkLast(nodeToChange);
        } else {
            Node newNode = new Node(task);
            linkLast(newNode);
            historyMap.put(task.getId(), newNode);
            size++;
        }
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = historyMap.get(id);
        if (nodeToRemove != null) {
            removeNode(nodeToRemove);
            historyMap.remove(id);
            size--;
        }
    }

    public void clearHistory() {
        historyMap.clear();
        head = null;
        tail = null;
        size = 0;
    }

    //Реализация двусвязного списка
    public void linkLast(Node newNode) {
        if (newNode == null) {
            return;
        }
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            newNode.setNext(null);
            tail = newNode;
        }
    }

    private void removeNode(Node nodeForRemove) {
        if (nodeForRemove == null)
            return;
        if (nodeForRemove == head && nodeForRemove == tail) {
            head = tail = null;
        } else if (nodeForRemove == head) {
            head = head.getNext();
            head.setPrev(null);
        } else if (nodeForRemove == tail) {
            tail = tail.getLast();
            tail.setNext(null);
        } else {
            Node prev = nodeForRemove.getPrev();
            Node next = nodeForRemove.getNext();
            prev.setNext(next);
            next.setPrev(prev);
        }
        nodeForRemove.setPrev(null);
        nodeForRemove.setNext(null);
    }

    //Сбор задач из двусвязного списка в список
    private ArrayList<AbstractTask> getTasks() {
        ArrayList<AbstractTask> historyTaskList = new ArrayList<>();
        Node current = head;
        while (current != null) {
            historyTaskList.add(current.getTask());
            current = current.getNext();
        }
        return historyTaskList;
    }

    public int getSize() {
        return size;
    }

    public Map<Integer, Node> getHistoryMap() {
        Map<Integer, Node> copyOfHistorymap = new HashMap<>(historyMap);
        return copyOfHistorymap;
    }
}
s