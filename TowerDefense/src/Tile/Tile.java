package Tile;

import Map.PlayMap;

abstract public class Tile {
    // Các loại ô có trong map
    public enum TileType {
        ROAD,
        TOWER,
        NULL
    }

    // Tọa độ của ô theo mảng hai chiều
    private int x;
    private int y;

    // Loại ô là ô đường đi của địch hay ô có thể đặt tháp
    TileType type;

    public Tile(int x, int y, TileType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }


    // Lấy tọa độ của ô theo mảng 2 chiều:
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Lấy tọa độ của ô theo Pixel:
    public int getXPixel() {
        return x * PlayMap.tileSize;
    }

    public int getYPixel() {
        return y * PlayMap.tileSize;
    }

    public TileType getType() {
        return type;
    }
}
