package io.github.mmm.sudoku.solution;

import java.util.List;

import io.github.mmm.sudoku.Sudoku;

/**
 * A {@link Hint} is a logical next step on the way to the solution of a {@link Sudoku}. It is {@link #getSteps()
 * composed} of multiple {@link HintStep} that allow to explain this {@link Hint} to the end-user. Also, it
 * {@link #getDifficulty() has} a {@link Difficulty} scoring how easy or hard it is to come to the hint.
 */
public class Hint {

  private final Difficulty difficulty;

  private final List<HintStep> steps;

  /**
   * The constructor.
   *
   * @param difficulty the {@link #getDifficulty}
   * @param steps the {@link #getSteps() steps}.
   */
  public Hint(Difficulty difficulty, List<HintStep> steps) {

    super();
    this.difficulty = difficulty;
    this.steps = steps;
  }

  /**
   * @return the {@link Difficulty} of this {@link Hint}.
   */
  public Difficulty getDifficulty() {

    return this.difficulty;
  }

  /**
   * @return the {@link List} of {@link HintStep}s of this {@link Hint}.
   */
  public List<HintStep> getSteps() {

    return this.steps;
  }

}
