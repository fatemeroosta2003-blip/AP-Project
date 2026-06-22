//public class Main {
//
//    public static void main(String[] args) {
//        Map map = setUpDefaultMaps();
//        printMap(map);
//    }
//    private static Map setUpDefaultMaps() {
//        //Designing map template number 1:
//        boolean[][] mark1 = new boolean[100][100];
//        int pickLand;
//        Map map1 = new Map(100,100);
//        for(int i = 0; i < 50; i++) {
//            for (int j = 0; j < 35; j++)
//            {
//                map1.getTile(i, j).setTexture(TileTexture.SEA);
//                mark1[i][j] = true;
//            }
//            for(int j = 65; j < 100; j++)
//            {
//                map1.getTile(i, j).setTexture(TileTexture.SEA);
//                mark1[i][j] = true;
//            }
//        }
//        for(int i = 65; i < 100; i++) {
//            for (int j = 0; j < 35; j++)
//            {
//                map1.getTile(i, j).setTexture(TileTexture.SMALL_POND);
//                mark1[i][j] = true;
//            }
//            for(int j = 65; j < 100; j++)
//            {
//                map1.getTile(i, j).setTexture(TileTexture.FORD);
//                mark1[i][j] = true;
//            }
//        }
//        for(int i = 15; i < 35; i++) {
//            for(int j = 40; j < 50; j++)
//            {
//                map1.getTile(i , j).setTexture(TileTexture.IRON);
//                mark1[i][j] = true;
//            }
//        }
//        for(int i = 85; i > 65; i--) {
//            for(int j = 56; j < 66; j++)
//            {
//                map1.getTile(i , j).setTexture(TileTexture.IRON);
//                mark1[i][j] = true;
//            }
//            if(i < 69)
//                for(int j = 45; j < 56; j++)
//                {
//                    map1.getTile(i , j).setTexture(TileTexture.IRON);
//                    mark1[i][j] = true;
//                }
//        }
//        for(int i = 0; i < 100; i++)
//            for(int j = 0; j < 100; j++)
//                if(!mark1[i][j])
//                {
//                    pickLand = (int) (3 * Math.random());
//                    if(pickLand == 2) map1.getTile(i,j).setTexture(TileTexture.SCRUB);
//                    if(pickLand == 1) map1.getTile(i,j).setTexture(TileTexture.THICK_SCRUB);
//                    if(pickLand == 0) map1.getTile(i,j).setTexture(TileTexture.EARTH);
//                }
//        return map1;
//    }
//
//
//    public static void printMap(Map map) {
//        char tileOccupation;
//        int xcounterForBreak = 0;
//        int ycounterForBreak = 0;
//        for(int i = 0; i <= 99; i++) {
//            if(ycounterForBreak % 3 == 0) {for(int k = 0; k < (99+1)*7/6+2; k++) System.out.print("-"); System.out.println();}
//            ycounterForBreak++;
//            for (int j = 0; j <= 99; j++) {
//                tileOccupation = map.getTile(i, j).getTileOccupation();
//                if (xcounterForBreak % 6 == 0) System.out.print("\033[49m|");
//                xcounterForBreak++;
//                switch (map.getTile(i, j).getTexture()) {
//                    case OIL:
//                        System.out.print("\033[100m" + tileOccupation);
//                        break;
//                    case SEA:
//                        System.out.print("\033[44m" + tileOccupation);
//                        break;
//                    case EARTH:
//                        System.out.print("\033[40m" + tileOccupation);
//                        break;
//                    case FORD:
//                        System.out.print("\033[45m" + tileOccupation);
//                        break;
//                    case IRON:
//                        System.out.print("\033[41m" + tileOccupation);
//                        break;
//                    case SCRUB:
//                        System.out.print("\033[43m" + tileOccupation);
//                        break;
//                    case THICK_SCRUB:
//                        System.out.print("\033[42m" + tileOccupation);
//                        break;
//                    case SMALL_POND:
//                        System.out.print("\033[104m" + tileOccupation);
//                        break;
//                }
//                if (j == 99)
//                {
//                    System.out.println("\033[49m|");
//                    xcounterForBreak = 0;
//                }
//            }
//        }
//        //Adding a guidance table:
//        System.out.println("-------------Table Info-------------");
//        System.out.println("OIL         ----------------    \033[100m    \033[49m");
//        System.out.println("SEA         ----------------    \033[44m    \033[49m");
//        System.out.println("EARTH       ----------------    \033[40m    \033[49m");
//        System.out.println("FORD        ----------------    \033[45m    \033[49m");
//        System.out.println("IRON        ----------------    \033[41m    \033[49m");
//        System.out.println("SCRUB       ----------------    \033[43m    \033[49m");
//        System.out.println("THICK_SCRUB ----------------    \033[42m    \033[49m");
//        System.out.println("SMALL_POND  ----------------    \033[104m    \033[49m");
//    }
//}
