package com.lordsofmidnight.utils;

/**
 * @param <E> the data type to store
 * @author Tim Iterates through objects in a circular manner. e.g. [1,2,3] would output
 * 1,2,3,1,2,3,1,2,3...
 */
public class CircularIterator<E> {

  E[] data;
  int index;

  /**
   * @param data data to be iterated over
   */
  public CircularIterator(E[] data) {
    this.data = data;
    this.index = 0;
  }

  /**
   * @return next item in the iterator
   */
  public E next() {
    if (data.length <= index) {
      this.index = 0;
      return data[data.length - 1];
    }
    index += 1;
    return data[index - 1];
  }
}
