package viewer;

/**
 * One snapshot of a map's grid of display characters, captured from the
 * console output.
 *
 * @param grid   the characters, indexed [row][column]
 * @param width  number of columns
 * @param height number of rows
 */
public record MapFrame(char[][] grid, int width, int height) {
}
