import com.fasterxml.jackson.annotation.JsonTypeInfo;
import controller.MapMenuController;
import model.*;
import model.buildings.BuildingEnum;
import model.buildings.BuildingEnumType;
import model.units.UnitEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import view.MapMenu;
import view.enums.ProfisterControllerOut;
import view.enums.TreeTypes;

import java.io.IOException;
import java.lang.reflect.Executable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

import static org.hamcrest.CoreMatchers.hasItem;

public class MapTests {
    MapMenuController mapMenuController = new MapMenuController();

    @Test
    public void changeTexture() throws IOException {
        mapMenuController.setUpACustom(200);
        Assertions.assertEquals(mapMenuController.setTextureForTheWholeMap(mapMenuController.selectedMap,"-x 49 -y 49 -t lawn"),
                "Texture set successfully!");
        Assertions.assertEquals(mapMenuController.selectedMap.getTile(150,49).getTexture(), TileTexture.LAWN);
        Assertions.assertEquals(mapMenuController.setTextureForTheWholeMap(mapMenuController.selectedMap,"-x 49 -y 49 -t earth"),
                "Texture set successfully!");
        Assertions.assertEquals(mapMenuController.selectedMap.getTile(150,49).getTexture(), TileTexture.EARTH);
        Assertions.assertEquals(mapMenuController.setTextureForTheWholeMap(mapMenuController.selectedMap,"-y 49 -x 52 -t river"),
                "Texture set successfully!");
        Assertions.assertEquals(mapMenuController.selectedMap.getTile(150,52).getTexture(), TileTexture.RIVER);
        Assertions.assertEquals(mapMenuController.setTextureForTheWholeMap(mapMenuController.selectedMap,"-t iron -y 49 -x 52"),
                "Texture set successfully!");
        Assertions.assertEquals(mapMenuController.selectedMap.getTile(150,52).getTexture(), TileTexture.IRON);
        Assertions.assertEquals(mapMenuController.setTextureForTheWholeMap(mapMenuController.selectedMap,"-t iron -y 22222 -x 52"),
                "Mission failed: invalid coordinates!");
        Assertions.assertEquals(mapMenuController.setTextureForTheWholeMap(mapMenuController.selectedMap,"-t iron -y 2 -x 52 -y2 5 -x2"),
                ProfisterControllerOut.INVALID_INPUT_FORMAT.getContent());
        Assertions.assertEquals(mapMenuController.setTextureForTheWholeMap(mapMenuController.selectedMap,"-t scrub -y 49 -x 49 -y2 35 -x2 54"),
                "Texture set successfully!");
        for(int i = 150; i < 164; i++)
            for(int j = 49; j < 54; j++)
                Assertions.assertEquals(mapMenuController.selectedMap.getTile(i,j).getTexture(), TileTexture.SCRUB);
        int icounter = 0;
        int jcounter = 199;
        EnumSet<TileTexture> walker = EnumSet.allOf(TileTexture.class);
        for (TileTexture tileTexture : walker) {
            Assertions.assertEquals(mapMenuController.setTextureForTheWholeMap(mapMenuController.selectedMap,"-x    "
                            + icounter++ + "    -y " + jcounter-- + " -t   " +
                            tileTexture.toString().toLowerCase()),
                    "Texture set successfully!");
        }
    }

    @Test
    public void outputCheckers() throws IOException {
        mapMenuController.setUpACustom(200);
        Assertions.assertNotEquals(mapMenuController.showDetail(" -x 25 -y 25"),ProfisterControllerOut.INVALID_INPUT_FORMAT.getContent());
        Assertions.assertEquals(mapMenuController.showDetail(" -x 25 -y 23535"),ProfisterControllerOut.INVALID_INPUT_FORMAT.getContent());
        Assertions.assertTrue((mapMenuController.setUpATemplate().startsWith( "I give you a 200*200 map.")));
        Assertions.assertNotNull(mapMenuController.showMap("   -x 56    -y 78"));
        Assertions.assertNotNull(mapMenuController.moveMap("   up left down "));
        Assertions.assertNotNull(mapMenuController.moveMap("  right 5 up  0 left "));
        Assertions.assertNotNull(mapMenuController.moveMap("  right 50000 up  0 left "));
        Assertions.assertEquals(mapMenuController.clearTile("   -y 22 -x 52"),
                "Tile cleared successfully!");
        Assertions.assertEquals(mapMenuController.clearTile("   -y 52562 -x 52"),
                "failed: invalid coordinates");
    }

    @Test
    public void treeDrop() throws IOException {
        mapMenuController.setUpACustom(200);
        EnumSet<TreeTypes> jungle = EnumSet.allOf(TreeTypes.class);
        for (TreeTypes tt : jungle) {
            Assertions.assertEquals(mapMenuController.dropTree("-t " + tt.name().toLowerCase() + " -y 22 -x 52"),
                    "Tree added successfully!");
            boolean shot = false;
            for (Tree tree : mapMenuController.selectedMap.getTile(177, 52).getTrees()) {
                if(tree.getType().equals(tt))
                {
                    shot = true; break;
                }
            }

            Assertions.assertTrue(shot);
        }
        Assertions.assertEquals(mapMenuController.dropTree("-t olive -y 22222 -x 52"),ProfisterControllerOut.FAILED.getContent());;
        Assertions.assertEquals(mapMenuController.dropTree("-t olive -y  -y 22 -x 554542"),ProfisterControllerOut.FAILED.getContent());
        Assertions.assertEquals(mapMenuController.dropTree("-t jhhjghj -y  -y 22 -x 554542"),ProfisterControllerOut.FAILED.getContent());
    }

    @Test
    public void rockDrop() throws IOException {
        mapMenuController.setUpACustom(200);
        Assertions.assertEquals(mapMenuController.dropRock(" -y 22 -x 52 -d n   "),
                "Rock added successfully!");
        Assertions.assertEquals(mapMenuController.selectedMap.getTile(177,52).getTexture(), TileTexture.ROCK);
        Assertions.assertEquals(mapMenuController.selectedMap.getTile(177,52).getRockDirection(), "n");
        Assertions.assertEquals(mapMenuController.dropRock(" -y 22   -d   r  -x 52   "),
                "Rock added successfully!");
        Assertions.assertEquals(mapMenuController.dropRock(" -y 22 -x 554542 -d    "),
                ProfisterControllerOut.INVALID_INPUT_FORMAT.getContent());
        Assertions.assertEquals(mapMenuController.dropRock(" -y 22 -x 554542 -d n  s "),
                ProfisterControllerOut.FAILED.getContent());
    }

    @Test
    public void buildingDrop() throws IOException {
        mapMenuController.setUpACustom(200);
        User tester = new User("nick", "tiered", "nick", "n@j.l", "nik was here", 2, "ilani", 0);
        Assertions.assertEquals(mapMenuController.dropBuilding(" -y 32  -x 52 -t randomStuff  ", tester),
                ProfisterControllerOut.INVALID_INPUT_FORMAT);
        Assertions.assertEquals(mapMenuController.dropBuilding(" -y 23232  -x 52 -t hovel  ", tester),
                ProfisterControllerOut.INVALID_INPUT_FORMAT);
        Assertions.assertEquals(mapMenuController.dropBuilding(" -y 0  -x 0 -t hovel  ", tester),
                ProfisterControllerOut.NOT_A_VALID_PLACE);
        tester.getGovernance().changeGold(1000000000);
        tester.getGovernance().changeResourceAmount(ResourceEnum.WOOD, 500000000);
        tester.getGovernance().changeResourceAmount(ResourceEnum.STONE, 500000000);
        tester.getGovernance().changeResourceAmount(ResourceEnum.IRON, 500000000);
        EnumSet<BuildingEnum> buildingTypes = EnumSet.allOf(BuildingEnum.class);
        for (BuildingEnum type : buildingTypes) {
            mapMenuController.extractDataForTexture(" -y 22  -x 52 -t " + type.getName());
            if (!mapMenuController.checkLocation(mapMenuController.selectedMap, mapMenuController.getyTexture(),
                    mapMenuController.getxTexture(), type))
                Assertions.assertEquals(mapMenuController.dropBuilding(" -y 22  -x 52 -t " + type.getName(), tester),
                        ProfisterControllerOut.NOT_A_VALID_PLACE);
        }
    }
}