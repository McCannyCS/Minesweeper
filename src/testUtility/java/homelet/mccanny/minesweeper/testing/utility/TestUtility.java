package homelet.mccanny.minesweeper.testing.utility;

import homelet.mccanny.minesweeper.model.game.MinesweeperGame;
import homelet.mccanny.minesweeper.repository.GameSerDer;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.platform.commons.util.ClassLoaderUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class TestUtility {

    private static final Stack<Random> TEMP_RANDOMS = new Stack<>();
    private static final GameSerDer serDer = new GameSerDer();

    // TestObject should have an empty constructor
    public static <OBJ> OBJ loadTestObj(Class<OBJ> objClass, String file) {
        InputStream inputStream = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(file + ".json");
        assumeTrue(inputStream != null, "Failed to load test object from file \"" + file + "\", contact instructor");
        try (InputStream stream = new BufferedInputStream(inputStream)) {
            JSONObject object = new JSONObject(new JSONTokener(stream));
            OBJ obj = objClass.getConstructor().newInstance();
            for (Field field : objClass.getDeclaredFields()) {
                if (field.getType().equals(MinesweeperGame.class)) {
                    field.set(obj, serDer.readGameFromJson(object.getJSONObject(field.getName())));
                } else {
                    field.set(obj, object.get(field.getName()));
                }
            }
            return obj;
        } catch (IOException | IllegalAccessException | InvocationTargetException | InstantiationException |
                 NoSuchMethodException e) {
            e.printStackTrace();
            assumeTrue(false, "Failed to create test object, contact instructor");
            return fail();
        }
    }

    public static <OBJ> void saveTestObj(OBJ obj, String file) throws IOException, IllegalAccessException {
        try (FileWriter writer = new FileWriter(file + ".json")) {
            JSONObject object = new JSONObject();
            for (Field field : obj.getClass().getDeclaredFields()) {
                if (field.getType().equals(MinesweeperGame.class)) {
                    object.put(field.getName(), serDer.saveGameToJson((MinesweeperGame) field.get(obj)));
                } else {
                    object.put(field.getName(), field.get(obj));
                }
            }
            writer.write(object.toString(4));
        }
    }

    public static void resetMinesweeperRandom() {
        try {
            Field field = MinesweeperGame.class.getDeclaredField("randomNumberGenerator");
            field.setAccessible(true);
            field.set(null, TEMP_RANDOMS.pop());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            assumeTrue(false, "Failed to reset seed, contact instructor");
        }
    }

    public static void setMinesweeperRandomSeed(long seed) {
        try {
            Field field = MinesweeperGame.class.getDeclaredField("randomNumberGenerator");
            field.setAccessible(true);
            TEMP_RANDOMS.push((Random) field.get(null));
            field.set(null, new Random(seed));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            assumeTrue(false, "Failed to set seed, contact instructor");
        }
    }

    public static void compareBoard(MinesweeperGame expected, MinesweeperGame actual, String message) {
        assertNotNull(actual, "Null instance checked");
        assertEquals(actual.getBoard().length, expected.getBoard().length, "Wrong dimension");
        for (int i = 0; i < expected.getBoard().length; i++) {
            assertEquals(actual.getBoard()[i].length, expected.getBoard()[i].length, "Wrong dimension");
            for (int j = 0; j < expected.getBoard()[i].length; j++) {
                assertEquals(expected.getBoard()[i][j], actual.getBoard()[i][j],
                        "Location (" + i + ", " + j + ") is different, " + message);
            }
        }
    }
}
