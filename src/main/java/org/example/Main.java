package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Main {


    public static class PathFinder {
        private record Point(int x, int y, int weight) {
        }

        private final int[][] possibleDirections = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        private Integer[][] weightMatrix;
        private int[][] obstacleMatrix;

        private int matrixWidth;
        private int matrixHeight;

        private List<Point> pointsToObserve;


        public int[][] findPath(int[][] obstacleMatrixParam, int startPointX, int startPointY, int endPointX, int endPointY) {
            obstacleMatrix = obstacleMatrixParam;

            matrixHeight = obstacleMatrix.length;
            matrixWidth = obstacleMatrix[0].length;

            weightMatrix = new Integer[matrixHeight][matrixWidth];

            weightMatrix[startPointX][startPointY] = 0;

            markConnectedPointsAsEqual3(startPointX, startPointY);
            markConnectedPointsAsEqual3(endPointX, endPointY);

            pointsToObserve = new ArrayList<>();
            pointsToObserve.add(new Point(startPointX, startPointY, 0));

            while (!pointsToObserve.isEmpty()) {
                Point pointWithMinimalWeight = pointsToObserve.stream().min(Comparator.comparingInt(p -> p.weight)).get();
                if (pointWithMinimalWeight.x == endPointX && pointWithMinimalWeight.y == endPointY) {
                    break;
                }
                upgradeMatrixWithWeights(pointWithMinimalWeight);
                pointsToObserve.remove(pointWithMinimalWeight);
            }
            if (weightMatrix[endPointX][endPointY] != null) {
                traceBackPath(endPointX, endPointY);
            }

            for (int[] row: obstacleMatrix) {
                for (int i = 0; i< row.length; i++) {
                    if (row[i] == 3) {
                        row[i] = 1;
                    }
                }
            }
            return obstacleMatrix;
        }

        private void markConnectedPointsAsEqual3(int x, int y) {
            obstacleMatrix[x][y] = 3;
            for (int[] possibleDirection : possibleDirections) {
                int newX = x + possibleDirection[0];
                int newY = y + possibleDirection[1];
                if (isPointInBounds(newX, newY) && obstacleMatrix[newX][newY] == 1) {
                    markConnectedPointsAsEqual3(newX, newY);
                }
            }
        }

        private void upgradeMatrixWithWeights(Point point) {
            for (int[] possibleDirection : possibleDirections) {
                int newX = point.x + possibleDirection[0];
                int newY = point.y + possibleDirection[1];
                if (isPointInBounds(newX, newY)
                        && isPointConnectedOrNotObstacle(newX, newY) && (weightMatrix[newX][newY] == null || weightMatrix[newX][newY] > point.weight + 1)) {
                    weightMatrix[newX][newY] = point.weight + 1;
                    pointsToObserve.add(new Point(newX, newY, point.weight + 1));
                }
            }

        }
        
        private boolean isPointConnectedOrNotObstacle(int x, int y) {
            return obstacleMatrix[x][y]==3 ||
                    (obstacleMatrix[x][y]==0 && !isPointNearToNotConnectedObstacle(x, y));
        }
        
        private boolean isPointNearToNotConnectedObstacle(int x, int y) {
            for (int[] possibleDirection : possibleDirections) {
                int newX = x + possibleDirection[0];
                int newY = y + possibleDirection[1];
                if (isPointInBounds(newX, newY) && obstacleMatrix[newX][newY] == 1) {
                    return true;
                }
            }
            return false;
        }

        private void traceBackPath(int endPointX, int endPointY) {
            int currentX = endPointX;
            int currentY = endPointY;
            while (weightMatrix[currentX][currentY] != 0) {
                obstacleMatrix[currentX][currentY] = 2;
                for (int[] possibleDirection : possibleDirections) {
                    int newX = currentX + possibleDirection[0];
                    int newY = currentY + possibleDirection[1];
                    if (isPointInBounds(newX, newY)) {
                        if (weightMatrix[newX][newY] != null && weightMatrix[newX][newY] == weightMatrix[currentX][currentY] - 1) {
                            currentX = newX;
                            currentY = newY;
                            break;
                        }
                    }
                }
            }
            obstacleMatrix[currentX][currentY] = 2;
        }


        private boolean isPointInBounds(int x, int y) {
            return x >= 0 && x < matrixWidth && y >= 0 && y < matrixHeight;
        }

    }

    public static void main(String[] args) {

        int[][] testMatrix1 = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        int[][] testMatrix2 = {
                {0, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1}
        };

        int[][] testMatrix3 = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 0, 0, 0, 0, 1, 1, 0},
                {0, 1, 1, 0, 1, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 1, 0, 1, 1, 0},
                {0, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 1, 1, 1, 1, 1, 0},
                {0, 1, 0, 0, 0, 0, 0, 1, 0, 0},
                {0, 1, 1, 1, 1, 0, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
                {0, 1, 1, 0, 1, 1, 0, 0, 1, 0}
        };

        int[][] testMatrix4 = {
                {0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 1, 0, 1, 1, 1, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 1, 0, 0},
                {0, 1, 1, 1, 1, 1, 0, 1, 1, 0},
                {0, 0, 0, 0, 0, 1, 0, 0, 1, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
                {1, 1, 1, 1, 1, 0, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 0, 1}
        };

        int[][] testMatrix5 = { // no way
                {0, 1, 1, 0, 0, 0, 0, 0, 1, 0},
                {0, 1, 0, 0, 0, 0, 1, 0, 1, 0},
                {0, 1, 0, 0, 1, 0, 1, 0, 1, 0},
                {0, 0, 0, 0, 1, 0, 1, 0, 1, 0},
                {0, 0, 0, 0, 1, 0, 1, 0, 1, 0},
                {0, 1, 0, 0, 1, 0, 1, 0, 0, 0},
                {0, 1, 0, 0, 1, 0, 1, 0, 0, 0},
                {0, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 0, 0, 0, 0}
        };

        PathFinder pathFinder = new PathFinder();
        int[][] result1 = pathFinder.findPath(testMatrix1, 0, 0, 9, 9);
        int[][] result2 = pathFinder.findPath(testMatrix2, 0, 0, 9, 9);
        int[][] result3 = pathFinder.findPath(testMatrix3, 0, 0, 9, 9);
        int[][] result4 = pathFinder.findPath(testMatrix4, 0, 0, 9, 9);
        int[][] result5 = pathFinder.findPath(testMatrix5, 0, 0, 9, 9);

        for (int[][] result : List.of(result1, result2, result3, result4, result5)) {
            for (int[] row: result) {
                System.out.println(Arrays.toString(row));
            }
            System.out.println();
        }
    }
}