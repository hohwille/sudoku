/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.sudoku.partitioning;

import java.util.Iterator;

import io.github.mmm.base.collection.ArrayIterator;
import io.github.mmm.sudoku.Sudoku;
import io.github.mmm.sudoku.child.SudokuChildObject;
import io.github.mmm.sudoku.common.AttributeComplete;
import io.github.mmm.sudoku.common.AttributeRegular;
import io.github.mmm.sudoku.dimension.Dimension;
import io.github.mmm.sudoku.field.Field;
import io.github.mmm.sudoku.partition.Partition;
import io.github.mmm.sudoku.style.BorderType;
import io.github.mmm.sudoku.style.ColorType;

/**
 * Each {@link Partitioning} splits the {@link Sudoku} board into a {@link #getPartitionCount() specific number} of
 * {@link Partition}s. Each {@link Partition} contains {@link Field}s with distinct {@link Field#getValue() values}.
 * Typically the {@link #getPartitionCount() number of partitions} is also the {@link Sudoku#getSize() sudoku size} but
 * there are excuses where this is not the case (e.g. X, {@link Hyper} or {@link Percent}).<br>
 * Every {@link Sudoku} has {@link Column}s as the first partitioning and {@link Row}s as the second partitioning. For
 * ease of use, the method {@link Sudoku#getField(int, int)} allows direct access to a {@link Field} using its x- and
 * y-coordinates. Via {@link Partitioning} the same is also available via {@link Column} using the following code:
 *
 * <pre>
 * {@link Sudoku sudoku}.{@link Sudoku#getPartitioning(int) getPartitioning}(1).{@link #getPartition(int) getPartition}(y){@link Partition#getField(int) getField}(x);
 * </pre>
 *
 * Or via {@link Row}:
 *
 * <pre>
 * {@link Sudoku sudoku}.{@link Sudoku#getPartitioning(int) getPartitioning}(2).{@link #getPartition(int) getPartition}(x){@link Partition#getField(int) getField}(y);
 * </pre>
 *
 * Every {@link Sudoku} has one or more additional {@link Partitioning}s. A normal {@link Sudoku} has {@link Box} as
 * additional {@link Partitioning}. Special {@link Sudoku#getType() types} like {@link Hyper} add a fourth {@link Layer}
 * typically visualised by {@link Partitioning#getColorType() color}. However, there are also {@link Sudoku#getType()
 * types} like {@link io.github.mmm.sudoku.JigsawSudoku} that use irregular shapes instead of {@link Box boxes} and this
 * also allows to have a {@link Dimension} that is <b>not</b> {@link Sudoku#isRegular() regular} such as e.g. a 5x5
 * {@link Sudoku} board that can not be split into {@link Box}es since 5 is not a square number.<br>
 * As you can see {@link Partitioning} is a very generic but powerful concept that allows to implement many
 * {@link Sudoku#getType() types} of {@link Sudoku}s easily without the need to write much extra code.<br>
 * Also a {@link Sum}Doku is possible where no {@link Field#isGiven() clues are given} but only the
 * {@link Partition#getSum() sum} of {@link Field}s for specific {@link Partition}s.
 *
 * @see Sudoku#getPartitioning(int)
 * @see Sudoku#getPartitioningCount()
 * @see Partition
 */
public abstract class Partitioning extends SudokuChildObject
    implements Iterable<Partition>, AttributeRegular, AttributeComplete {

  private final int index;

  private final Partition[] partitions;

  /**
   * The constructor.
   *
   * @param sudoku the {@link #getSudoku() sudoku}.
   * @param index the {@link #getIndex() index}.
   * @param function the {@link PartitionFunction}.
   */
  public Partitioning(Sudoku sudoku, int index, PartitionFunction function) {

    this(sudoku, index, function, null);
  }

  /**
   * The constructor.
   *
   * @param sudoku the {@link #getSudoku() sudoku}.
   * @param index the {@link #getIndex() index}.
   * @param partitions the pre-defined {@link #getPartition(int) partitions}.
   */
  protected Partitioning(Sudoku sudoku, int index, Partition[] partitions) {

    this(sudoku, index, null, partitions);
  }

  private Partitioning(Sudoku sudoku, int index, PartitionFunction function, Partition[] partitions) {

    super(sudoku);
    if (isRegular() && !sudoku.isRegular()) {
      throw new IllegalStateException(
          "Partitioning " + getName() + " is regular and does not support size " + sudoku.getSize());
    }
    this.index = index;
    if (partitions == null) {
      this.partitions = new Partition[function.getPartitionCount(sudoku)];
      for (int partitionIndex = 1; partitionIndex <= this.partitions.length; partitionIndex++) {
        this.partitions[partitionIndex - 1] = function.createPartition(this, partitionIndex);
      }
    } else {
      assert (function == null);
      this.partitions = partitions;
    }
  }

  /**
   * @return the {@link Sudoku#getPartitioning(int) index} of this {@link Partitioning} in the range from {@code 1} to
   *         <code>{@link #getPartitionCount()}</code>.
   */
  public final int getIndex() {

    return this.index;
  }

  /**
   * @return the name of this {@link Partitioning}.
   */
  public String getName() {

    return getClass().getSimpleName();
  }

  /**
   * @return the number of actual partitions available for this {@link Partitioning}. Typically the
   *         {@link Sudoku#getSize() size} of the {@link Sudoku} but may differ for special {@link Partitioning}s.
   */
  public final int getPartitionCount() {

    return this.partitions.length;
  }

  @Override
  public boolean isRegular() {

    return true;
  }

  /**
   * @return {@code true} if this {@link Partitioning} guarantees that always all its {@link Partition}s are
   *         {@link Partition#isComplete() complete}, {@code false} otherwise (e.g. for {@link Sum}).
   */
  public boolean hasAlwaysCompletePartitions() {

    return true;
  }

  /**
   * @param i the index of the requested {@link Partition} in the range from {@code 1} to
   *        <code>{@link #getPartitionCount()}</code>.
   * @return the requested {@link Partition}.
   */
  public Partition getPartition(int i) {

    if ((i < 1) || (i > this.partitions.length)) {
      throw new IndexOutOfBoundsException(i);
    }
    return this.partitions[i - 1];
  }

  @Override
  public Iterator<Partition> iterator() {

    return new ArrayIterator<>(this.partitions);
  }

  /**
   * @return the {@link ColorType} of the {@link #getPartitionCount() partitions}.
   */
  public ColorType getColorType() {

    return ColorType.NONE;
  }

  /**
   * @return the {@link BorderType} of the {@link #getPartitionCount() partitions}.
   */
  public BorderType getBorderType() {

    return BorderType.NONE;
  }

  @Override
  public String toString() {

    return '#' + getIndex() + ":" + getName() + "[" + getPartitionCount() + "]";
  }

  /**
   * A {@link PartitionFunction} allows to implement a {@link Partitioning} easier by implementing
   * {@link #getField(Sudoku, int, int)} as a lambda.
   */
  @FunctionalInterface
  public interface PartitionFunction {

    /**
     * @param dimension the {@link Dimension}.
     * @return the number of actual partitions available for this {@link Partitioning}. Typically the
     *         {@link Sudoku#getSize() size} of the {@link Sudoku} but may differ for special {@link Partitioning}s.
     */
    default int getPartitionCount(Dimension dimension) {

      return dimension.getSize();
    }

    /**
     * @param partitioning the owning {@link Partitioning}.
     * @param partitionIndex the {@link Partition#getIndex() partition index}.
     * @return the {@link Partition#getFieldCount() field count} for the specified {@link Partition}.
     */
    default int getFieldCount(Partitioning partitioning, int partitionIndex) {

      // by default each Partition has size fields with the distinct values of the sudoku
      return partitioning.getSudoku().getSize();
    }

    /**
     * @param partitioning the owning {@link Partitioning}.
     * @param partitionIndex the {@link Partition#getIndex() partition index}.
     * @return the created {@link Partition}.
     */
    default Partition createPartition(Partitioning partitioning, int partitionIndex) {

      Field[] fields = new Field[getFieldCount(partitioning, partitionIndex)];
      for (int fieldIndex = 1; fieldIndex <= fields.length; fieldIndex++) {
        fields[fieldIndex - 1] = getField(partitioning.getSudoku(), partitionIndex, fieldIndex);
      }
      return new Partition(partitioning, partitionIndex, Field.UNDEFINED, fields);
    }

    /**
     * @param sudoku the owning {@link Sudoku}.
     * @param partitionIndex the {@link Partition#getIndex() partition index}.
     * @param fieldIndex the {@link Partition#getField(int) field index}.
     * @return the requested {@link Field}.
     */
    Field getField(Sudoku sudoku, int partitionIndex, int fieldIndex);
  }

  /**
   * {@link PartitionFunction} for a single {@link Partition}.
   */
  @FunctionalInterface
  public interface SinglePartitionFunction extends PartitionFunction {

    @Override
    default int getPartitionCount(Dimension dimension) {

      return 1;
    }

    @Override
    default Field getField(Sudoku sudoku, int partitionIndex, int fieldIndex) {

      assert (partitionIndex == 1);
      return getField(sudoku, fieldIndex);
    }

    /**
     * @param sudoku the owning {@link Sudoku}.
     * @param fieldIndex the {@link Partition#getField(int) field index}.
     * @return the requested {@link Field}.
     */
    Field getField(Sudoku sudoku, int fieldIndex);

  }

}