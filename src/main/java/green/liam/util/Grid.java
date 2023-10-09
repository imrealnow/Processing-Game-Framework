package green.liam.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import green.liam.base.GameObject;
import processing.core.PVector;

public class Grid {
    int cellSize;
    int cols;
    int rows;
    int[][] grid;
    GameObject[][] renderedGrid;
    BiFunction<Integer, Integer, GameObject> mapper;

    public Grid(int cellSize, int cols, int rows, BiFunction<Integer, Integer, GameObject> mapper) {
        this.cellSize = cellSize;
        this.cols = cols;
        this.rows = rows;
        this.grid = new int[cols][rows];
        this.renderedGrid = new GameObject[cols][rows];
        this.mapper = mapper;
    }

    public void set(int x, int y, int value) {
        this.grid[x][y] = value;
    }

    public int get(int x, int y) {
        return this.grid[x][y];
    }

    /**
     * Set the mapper function for the grid.
     * 
     * @param mapper
     *            function that maps grid integer values and grid cellSize to
     *            GameObjects
     * @param update
     *            whether to update the grid after setting the mapper
     */
    public void setMapper(BiFunction<Integer, Integer, GameObject> mapper, boolean update) {
        this.mapper = mapper;
        if (update) {
            this.renderGrid();
        }
    }

    public List<GameObject> renderGrid() {
        List<GameObject> rendered = new ArrayList<>();
        float gridHalfWidth = this.cols * this.cellSize / 2;
        float gridHalfHeight = this.rows * this.cellSize / 2;
        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                GameObject obj = this.mapper.apply(this.grid[i][j], this.cellSize);
                if (obj != null) {
                    obj.transform().setPosition(
                            new PVector(i * this.cellSize - gridHalfWidth, j * this.cellSize - gridHalfHeight));
                    rendered.add(obj);
                }
                this.renderedGrid[i][j] = obj;
            }
        }
        return rendered;
    }

    public void loadFromString(String str) {
        String[] lines = str.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String[] values = lines[i].split(",");
            for (int j = 0; j < values.length; j++) {
                this.grid[j][i] = Integer.parseInt(values[j]);
            }
        }
    }
}
