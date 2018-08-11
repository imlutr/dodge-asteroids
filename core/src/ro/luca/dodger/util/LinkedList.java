package ro.luca.dodger.util;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class LinkedList
{
    private Node head, tail;
    private int size;

    public void add(Actor actor)
    {
        if (find(actor) == null)
        {
            Node node = new Node(actor);
            if (size == 0)
                head = tail = node;
            else
            {
                tail.next = node;
                node.previous = tail;
                tail = node;
            }
            size++;
        }
    }

    private Node find(Actor actor)
    {
        Node temp = head;
        while (temp != null && temp.value != actor)
            temp = temp.next;
        return temp;
    }

    public Actor get(int position)
    {
        Node temp = head;
        while (position > 0)
        {
            temp = temp.next;
            position--;
        }
        return temp.value;
    }

    public void removeAll()
    {
        while (head != null)
            remove(head.value);
    }

    public void remove(Actor actor)
    {
        Node node = find(actor);
        if (node.previous == null)
        {
            head = head.next;
            if (head != null)
                head.previous = null;
        }
        else if (node.next == null)
        {
            tail = node.previous;
            tail.next = null;
        }
        else
        {
            node.previous.next = node.next;
            node.next.previous = node.previous;
        }
        size--;
    }

    public int getSize()
    {
        return size;
    }

    private class Node
    {
        Actor value;
        Node next, previous;

        Node(Actor value)
        {
            this.value = value;
            next = previous = null;
        }
    }
}
