package Tile;

import org.newdawn.slick.geom.Rectangle;

public class TowerTile extends Tile {
    Rectangle toBuild;

    public TowerTile(int x, int y) {
        super(x, y, TileType.TOWER);
    }

}
