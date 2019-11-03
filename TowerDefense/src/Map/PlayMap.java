package Map;

import Tile.*;

public class PlayMap {
    public static Tile[][] mapTile;
    public static Tile startTile, exitTile;
    public static int tileSize = 48;

    public PlayMap(int[][] customMap) {

        mapTile = new Tile[customMap.length][customMap[0].length];

        // Create 2D Tile Map from 2D array
        for (int y = 0; y < mapTile.length; y++) {

            for (int x = 0; x < mapTile[0].length; x++) {

                if (customMap[y][x] == 0) {

                    mapTile[y][x] = new TowerTile(x, y);

                } else if (customMap[y][x] == 1) {

                    mapTile[y][x] = new RoadTile(x, y);

                } else if (customMap[y][x] == 2) {

                    mapTile[y][x] = new RoadTile(x, y);

                    startTile = new RoadTile(x, y);

                } else {

                    mapTile[y][x] = new RoadTile(x, y);

                    exitTile = new RoadTile(x, y);

                }
            }
        }
    }

    public static Tile getTile(int x, int y) {
        if (x > -1 && x < getWidthOfMap() && y > -1 && y < getHeightOfMap()) {
            return mapTile[y][x];
        } else {
            return new NullTile();
        }
    }

    public static boolean buildableTile(int x, int y) {
        if (getTile(x, y).getType() == Tile.TileType.TOWER) return true;
        else return false;
    }

    public static int getWidthOfMap() {
        return mapTile[0].length;
    }

    public static int getHeightOfMap() {
        return mapTile.length;
    }

    public static int getWidthOfMapInPixel() { return getWidthOfMap() * tileSize; }

    public static int getHeightOfMapInPixel() { return getHeightOfMap() * tileSize; }
}
