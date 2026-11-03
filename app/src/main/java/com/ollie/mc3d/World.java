package com.ollie.mc3d;

import java.util.Random;

public class World {
    public static final int SIZE = 64; // Мир 64x64 блоков
    public static final int HEIGHT = 64;
    public int[][][] blocks = new int[SIZE][HEIGHT][SIZE];

    public World() {
        generate();
    }

    private void generate() {
        Random rand = new Random();
        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {
                // Простой шум для холмов
                double noise = Math.sin(x * 0.1) * 5 + Math.cos(z * 0.1) * 5 + Math.sin((x+z) * 0.05) * 8;
                int groundY = (int) (HEIGHT / 2 + noise);

                for (int y = 0; y < HEIGHT; y++) {
                    if (y < groundY - 4) blocks[x][y][z] = 3; // Камень
                    else if (y < groundY) blocks[x][y][z] = 2; // Земля
                    else if (y == groundY) blocks[x][y][z] = 1; // Трава
                    else blocks[x][y][z] = 0; // Воздух
                }

                // Генерация деревьев
                if (blocks[x][groundY][z] == 1 && rand.nextInt(100) < 5) {
                    generateTree(x, groundY + 1, z);
                }
            }
        }
    }

    private void generateTree(int x, int y, int z) {
        if (x < 2 || x > SIZE - 3 || z < 2 || z > SIZE - 3) return;
        // Ствол
        for (int i = 0; i < 4; i++) {
            if (y + i < HEIGHT) blocks[x][y + i][z] = 4; // Дерево
        }
        // Листва
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 2; dy <= 4; dy++) {
                    if (Math.abs(dx) == 2 && Math.abs(dz) == 2) continue;
                    int ly = y + dy;
                    if (ly < HEIGHT && blocks[x+dx][ly][z+dz] == 0) {
                        blocks[x+dx][ly][z+dz] = 5; // Листва
                    }
                }
            }
        }
    }
}
