package green.liam.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import green.liam.base.GameObject;
import processing.core.PImage;
import processing.core.PVector;

public class Grid {
    int cellSize;
    int cols;
    int rows;
    int[][] grid;
    BiFunction<Integer, Integer, GameObject> mapper;

    public Grid(int cellSize, int cols, int rows, BiFunction<Integer, Integer, GameObject> mapper) {
        this.cellSize = cellSize;
        this.cols = cols;
        this.rows = rows;
        this.grid = new int[cols][rows];
        this.mapper = mapper;
    }

    public void clear() {
        this.grid = new int[this.cols][this.rows];
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

    public void randomFillMap(int smoothingIterations) {
        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                double perlin = (PerlinNoise.noise(i, j) + 2f) / 4f;
                this.grid[i][j] = perlin > 0.5 ? 1 : 0;
            }
        }
        for (int i = 0; i < smoothingIterations; i++) {
            this.smoothMap();
        }
    }

    private int getSurroundingWallCount(int gridX, int gridY) {
        int wallCount = 0;
        for (int neighbourX = gridX - 1; neighbourX <= gridX + 1; neighbourX++) {
            for (int neighbourY = gridY - 1; neighbourY <= gridY + 1; neighbourY++) {
                if (neighbourX >= 0 && neighbourX < this.cols && neighbourY >= 0 && neighbourY < this.rows) {
                    if (neighbourX != gridX || neighbourY != gridY) {
                        wallCount += this.grid[neighbourX][neighbourY];
                    }
                } else {
                    wallCount++;
                }
            }
        }
        return wallCount;
    }

    private void smoothMap() {
        for (int x = 0; x < this.cols; x++) {
            for (int y = 0; y < this.rows; y++) {
                int neighbourWallTiles = this.getSurroundingWallCount(x, y);
                if (neighbourWallTiles >= 7)
                    this.grid[x][y] = 1;
                if (neighbourWallTiles <= 2)
                    this.grid[x][y] = 0;
            }
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

    public PImage toImage(int scale) {
        PImage img = new PImage(this.cols * scale, this.rows * scale);
        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                for (int x = 0; x < scale; x++) {
                    for (int y = 0; y < scale; y++) {
                        img.set(i * scale + x, j * scale + y, this.grid[i][j] == 1 ? 0 : 255);
                    }
                }
            }
        }
        return img;
    }
}
