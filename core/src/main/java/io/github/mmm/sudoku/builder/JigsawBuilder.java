package io.github.mmm.sudoku.builder;

import io.github.mmm.sudoku.Sudoku;
import io.github.mmm.sudoku.dimension.AbstractDimension;
import io.github.mmm.sudoku.dimension.IrregularDimension;
import io.github.mmm.sudoku.partition.Partition;
import io.github.mmm.sudoku.partitioning.Jigsaw;
import io.github.mmm.sudoku.partitioning.Region;

/**
 * Builder of a {@link Sudoku} using {@link Jigsaw} as {@link Region}.
 */
public class JigsawBuilder extends RegionBuilder {

  /**
   * The constructor.
   *
   * @param seed the seed to specify deterministic {@link Jigsaw} {@link Partition}s.
   */
  public JigsawBuilder(int seed) {

    // TODO
    super(null);
  }

  /**
   * @param dimension the {@link AbstractDimension} of the {@link Sudoku}.
   * @return the {@link LayerBuilder} for fluent builder API calls.
   */
  public LayerBuilder with(AbstractDimension dimension) {

    this.dim = dimension;
    return next();
  }

  /**
   * Use {@link IrregularDimension#D5 5x5}.
   *
   * @return the {@link LayerBuilder} for fluent builder API calls.
   */
  public LayerBuilder with5x5() {

    this.dim = IrregularDimension.D5;
    return next();
  }

  /**
   * Use {@link IrregularDimension#D6 6x6}.
   *
   * @return the {@link LayerBuilder} for fluent builder API calls.
   */
  public LayerBuilder with6x6() {

    this.dim = IrregularDimension.D6;
    return next();
  }

  /**
   * Use {@link IrregularDimension#D7 7x7}.
   *
   * @return the {@link LayerBuilder} for fluent builder API calls.
   */
  public LayerBuilder with7x7() {

    this.dim = IrregularDimension.D7;
    return next();
  }

  /**
   * Use {@link IrregularDimension#D8 8x8}.
   *
   * @return the {@link LayerBuilder} for fluent builder API calls.
   */
  public LayerBuilder with8x8() {

    this.dim = IrregularDimension.D8;
    return next();
  }

  /**
   * Use {@link IrregularDimension#D10 10x10}.
   *
   * @return the {@link LayerBuilder} for fluent builder API calls.
   */
  public LayerBuilder with10x10() {

    this.dim = IrregularDimension.D10;
    return next();
  }

  /**
   * Use {@link IrregularDimension#D11 11x11}.
   *
   * @return the {@link LayerBuilder} for fluent builder API calls.
   */
  public LayerBuilder with11x11() {

    this.dim = IrregularDimension.D11;
    return next();
  }

}
