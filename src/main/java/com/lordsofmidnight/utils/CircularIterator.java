package com.lordsofmidnight.utils;

public class CircularIterator<E> {

  E[] data;
  int index;

  public CircularIterator(E[] data) {
    this.data = data;
    this.index = 0;
  }

  public E next() {
    if (data.length <= index) {
      this.index = 0;
      return data[data.length - 1];
    }
    index += 1;
    return data[index - 1];
  }
}
