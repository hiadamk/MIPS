package com.lordsofmidnight.utils;

import java.util.Stack;

/**
 * @param <E> Type of data to store
 * @author Tim iterates up and down a some data. e.g. [1,2,3] iterates to 1,2,3,3,2,1,1,2,3...
 */
public class UpDownIterator<E> {

  private Stack<E> stackA = new Stack();
  private Stack<E> stackB = new Stack();
  private AorB whichStack = AorB.A;

  /**
   * Iterator
   */
  public UpDownIterator(E[] data) {
    for (E e : data) {
      stackA.push(e);
    }
  }

  public E next() {
    E nextItem = null;
    switch (whichStack) {
      case A: {
        nextItem = stackA.pop();
        stackB.push(nextItem);
        break;
      }
      case B: {
        nextItem = stackB.pop();
        stackA.push(nextItem);
        break;
      }
    }
    if (stackA.empty()) {
      whichStack = AorB.B;
    }
    if (stackB.empty()) {
      whichStack = AorB.A;
    }
    return nextItem;
  }

  private enum AorB {
    A,
    B
  }
}
