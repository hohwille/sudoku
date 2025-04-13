/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.sudoku.child;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.github.mmm.sudoku.Sudoku;
import io.github.mmm.sudoku.style.BorderStyle;
import io.github.mmm.sudoku.style.BorderType;
import io.github.mmm.sudoku.style.ColorType;

/**
 * Represents a single {@link Field} of the {@link Sudoku} puzzle. It can either have a {@link #getValue() value} filled
 * (if not {@code -1}) or represent a list of {@link #hasCandidate(int) candidates}.
 */
public class Field extends SudokuChildObject {

  /** {@link #getValue() Value} if undefined. */
  public static final int UNDEFINED = -1;

  private final int[] partitionIndexes;

  private boolean given;

  private List<String> styles;

  private int value;

  private int solution;

  private final BitSet excludedCandidates;

  private boolean error;

  /**
   * The constructor.
   *
   * @param sudoku the {@link #getSudoku() sudoku}.
   * @param x the {@link #getX() x-coordinate}.
   * @param y the {@link #getY() y-coordinate}.
   */
  public Field(Sudoku sudoku, int x, int y) {

    super(sudoku);
    this.partitionIndexes = new int[sudoku.getPartitionings().size()];
    this.partitionIndexes[0] = x;
    this.partitionIndexes[1] = y;
    this.value = UNDEFINED;
    this.solution = UNDEFINED;
    this.excludedCandidates = new BitSet();
  }

  /**
   * @param partitioning the {@link Partitioning}.
   * @return the {@code partitionIndex} of this {@link Field} in the given {@link Partitioning} so it can be found via
   *         {@link Partitioning#getPartitionField(int, int)}. May be {@code -1} if this {@link Field} is not included
   *         in the given {@link Partitioning} (e.g. for Hyper, Percent, or X).
   */
  public int getPartitionIndex(Partitioning partitioning) {

    int id = partitioning.getId();
    if (this.partitionIndexes[id] == 0) {
      initPartitionIndexes();
    }
    return this.partitionIndexes[id];
  }

  private void initPartitionIndexes() {

    int size = this.sudoku.getSize();
    for (Partitioning partitioning : this.sudoku.getPartitionings()) {
      int id = partitioning.getId();
      if (id > 1) {
        int partitionCount = partitioning.getPartitionCount();
        for (int partitionIndex = 1; partitionIndex <= partitionCount; partitionIndex++) {
          for (int fieldIndex = 1; fieldIndex <= size; fieldIndex++) {
            Field field = partitioning.getPartitionField(partitionIndex, fieldIndex);
            assert (field.partitionIndexes[id] == 0);
            field.partitionIndexes[id] = partitionIndex;
          }
        }
      }
    }
    for (int x = 1; x <= size; x++) {
      for (int y = 1; y <= size; y++) {
        Field field = this.sudoku.getField(x, y);
        for (int i = 2; i < field.partitionIndexes.length; i++) {
          if (field.partitionIndexes[i] == 0) {
            field.partitionIndexes[i] = UNDEFINED; // field not reachable within this partitioning
          }
        }
      }
    }
  }

  /**
   * @return the x-coordinate or column. The value is one based so {@code 1} is the first column.
   */
  public int getX() {

    return this.partitionIndexes[0];
  }

  /**
   * @return the y-coordinate or row. The value is one based so {@code 1} is the first row.
   */
  public int getY() {

    return this.partitionIndexes[1];
  }

  /**
   * @return styles
   */
  public List<String> getStyles() {

    if (this.styles == null) {
      initStyles();
    }
    return this.styles;
  }

  private void initStyles() {

    int size = this.sudoku.getSize();
    List<Partitioning> partitionings = this.sudoku.getPartitionings();
    Partitioning borders = null;
    BorderType borderType = null;
    for (Partitioning partitioning : partitionings) {
      BorderType type = partitioning.getBorderType();
      if (type != BorderType.NONE) {
        borderType = type;
        borders = partitioning;
        break;
      }
    }
    for (int x = 1; x <= size; x++) {
      for (int y = 1; y <= size; y++) {
        Field field = this.sudoku.getField(x, y);
        field.initColorStyle();
        if (borderType != null) {
          field.initBorderStyle(borders);
        }
        if (field.styles == null) {
          field.styles = Collections.emptyList();
        } else {
          field.styles = Collections.unmodifiableList(field.styles);
        }
      }
    }
  }

  private void initColorStyle() {

    int color = 0;
    List<Partitioning> partitionings = this.sudoku.getPartitionings();
    for (int i = 0; i < partitionings.size(); i++) {
      Partitioning partitioning = partitionings.get(i);
      ColorType colorType = partitioning.getColorType();
      if (colorType != ColorType.NONE) {
        int partitionIndex = getPartitionIndex(partitioning);
        if (partitionIndex < 0) {
          color = color + colorType.getOffset(partitioning);
        } else {
          color = colorType.getColor(color, partitionIndex, partitioning);
          if (color > 0) {
            addStyle("color" + color);
          }
          return;
        }
      }
    }
  }

  private void addStyle(String style) {

    if (this.styles == null) {
      this.styles = new ArrayList<>();
    }
    this.styles.add(style);
  }

  private void initBorderStyle(Partitioning borders) {

    BorderStyle borderStyle = BorderStyle.EMPTY;
    if (isLeftBorder(borders)) {
      borderStyle = borderStyle.withLeft();
    }
    if (isTopBorder(borders)) {
      borderStyle = borderStyle.withTop();
    }
    if (isRightBorder(borders)) {
      borderStyle = borderStyle.withRight();
    }
    if (isBottomBorder(borders)) {
      borderStyle = borderStyle.withBottom();
    }
    if (borderStyle != BorderStyle.EMPTY) {
      String style = borders.getBorderType().toString().toLowerCase(Locale.ROOT) + "-" + borderStyle;
      addStyle(style);
    }
  }

  private boolean isTopBorder(Partitioning borders) {

    int y = getY();
    if (y == 1) {
      return true;
    }
    return false;
  }

  private boolean isRightBorder(Partitioning borders) {

    int x = getX();
    if (x == this.sudoku.getSize()) {
      return true;
    }
    Field right = this.sudoku.getField(x + 1, getY());
    if (getPartitionIndex(borders) != right.getPartitionIndex(borders)) {
      return true;
    }
    return false;
  }

  private boolean isBottomBorder(Partitioning borders) {

    int y = getY();
    if (y == this.sudoku.getSize()) {
      return true;
    }
    Field up = this.sudoku.getField(getX(), y + 1);
    if (getPartitionIndex(borders) != up.getPartitionIndex(borders)) {
      return true;
    }
    return false;
  }

  private boolean isLeftBorder(Partitioning borders) {

    int x = getX();
    if (x == 1) {
      return true;
    }
    return false;
  }

  /**
   * @param i the index of the {@link Sudoku#getSymbol(int) symbol} in the range from {@code 0} to
   *        <code>{@link Sudoku#getSize()}-1</code>.
   * @return {@code true} if the candidate is possible, {@code false} otherwise (already excluded).
   */
  public boolean hasCandidate(int i) {

    if (this.value > 0) {
      return (this.value == i);
    }
    if (i <= 0) {
      return false;
    }
    return !this.excludedCandidates.get(i);
  }

  /**
   * @param i the {@link #getValue() value} candidate to exclude in the range from {@code 1} to
   *        <code>{@link Sudoku#getSize()}</code>.
   */
  public void excludeCandidate(int i) {

    validateValue(i);
    this.excludedCandidates.set(i);
  }

  /**
   * @param i the {@link #getValue() value} candidate to (re)include in the range from {@code 1} to
   *        <code>{@link Sudoku#getSize()}</code>.
   */
  public void includeCandidate(int i) {

    validateValue(i);
    this.excludedCandidates.clear(i);
  }

  /**
   * @return the number of candidates that have been {@link #excludeCandidate(int) excluded}.
   */
  public int getExcludedCandidateCount() {

    return this.excludedCandidates.cardinality();
  }

  /**
   * @return the number of candidates that are still included and have not been {@link #excludeCandidate(int) excluded}.
   *         If this is {@code 1} then only a {@link #getSingle() single candidate} is left that is supposed to be the
   *         correct {@link #getValue() value}.
   */
  public int getIncludedCandidateCount() {

    return this.sudoku.getSize() - getExcludedCandidateCount();
  }

  /**
   * @return the remaining single {@link #getIncludedCandidateCount() included candidate} or {@link #UNDEFINED -1} if
   *         {@link #getIncludedCandidateCount()} is not equal to {@code 1}.
   */
  public int getSingle() {

    if (getIncludedCandidateCount() == 1) {
      int size = this.sudoku.getSize();
      for (int i = 1; i <= size; i++) {
        if (!this.excludedCandidates.get(i)) {
          return i;
        }
      }
    }
    return UNDEFINED;
  }

  /**
   * @return the value or {@link #UNDEFINED -1} if not (yet) {@link #hasValue() defined}. Please note that the value is
   *         an internal representation that needs to be mapped to a {@link Sudoku#getSymbol(int) symbol} for
   *         presentation to the end-user. Typically the mapping is trivial and {@code 1} is mapped to "1" but this is
   *         configured and symbols could also be emojis or whatever.
   */
  public int getValue() {

    return this.value;
  }

  /**
   * @return {@code true} if the {@link #getValue() value} is defined, {@code false} otherwise (see {@link #UNDEFINED}).
   */
  public boolean hasValue() {

    return (this.value != UNDEFINED);
  }

  /**
   * Low-level method to set the {@link #getValue() value}.
   *
   * @param value the new {@link #getValue() value}.
   * @see Sudoku#setFieldValue(Field, int)
   */
  public void setValue(int value) {

    setValue(value, false);
  }

  /**
   * Low-level method to set the {@link #getValue() value}.
   *
   * @param value the new {@link #getValue() value}.
   * @param given the {@link #isGiven() given flag}.
   * @see Sudoku#setFieldValue(Field, int, boolean)
   */
  public void setValue(int value, boolean given) {

    validateValue(value, !given);
    this.value = value;
    if (given) {
      this.given = given;
    }
  }

  /**
   * @return the solution (correct {@link #getValue() value}) or {@code -1} if not (yet) defined.
   */
  public int getSolution() {

    return this.solution;
  }

  /**
   * @param solution the new {@link #getSolution() solution}.
   */
  public void setSolution(int solution) {

    validateValue(solution, true);
    this.solution = solution;
  }

  private void validateValue(int v) {

    validateValue(v, false);
  }

  private void validateValue(int v, boolean acceptUndefined) {

    if ((v > this.sudoku.getSize()) || (v <= 0) && (!acceptUndefined || (v != UNDEFINED))) {
      throw new IllegalArgumentException(Integer.toString(v));
    }
  }

  /**
   * @return {@code true} if the {@link #getSolution() solution} is defined, {@code false} otherwise (see
   *         {@link #UNDEFINED}).
   */
  public boolean hasSolution() {

    return (this.solution != UNDEFINED);
  }

  /**
   * @return {@code true} if this {@link Field} has a <em>given</em> {@link #getValue() value}. A given value is also
   *         called <em>clue</em>.
   */
  public boolean isGiven() {

    return this.given;
  }

  /**
   * @return {@code true} if this {@link Field} is currently marked as error (conflicts with other value), {@code false}
   *         otherwise.
   */
  public boolean isError() {

    return this.error;
  }

  /**
   * <b>ATTENTION:</b> Internal method that should not be invoked directly.
   *
   * @param error new value of {@link #isError()}.
   */
  public void setError(boolean error) {

    this.error = error;
  }

  @Override
  public String toString() {

    return "Field " + getX() + "x" + getY() + " value=" + this.value + " excluded=" + this.excludedCandidates
        + " solution=" + this.solution + " error=" + this.error;
  }
}