package com.lordsofmidnight.utils;

import java.util.Stack;

public class UpDownIterator<E> {

  private Stack<E> stackA = new Stack();
  private Stack<E> stackB = new Stack();

  private enum AorB {
    A, B
  }

  private AorB whichStack = AorB.A;


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
}
