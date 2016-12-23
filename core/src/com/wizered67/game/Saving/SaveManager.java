package com.wizered67.game.Saving;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import com.wizered67.game.GUI.Conversations.CharacterSprite;
import com.wizered67.game.GUI.Conversations.ConversationController;
import com.wizered67.game.GUI.Conversations.SceneManager;
import com.wizered67.game.GUI.GUIManager;
import com.wizered67.game.GameManager;
import com.wizered67.game.MusicManager;
import com.wizered67.game.Saving.Serializers.CharacterSpriteSerializer;
import com.wizered67.game.Saving.Serializers.ConversationSerializer;
import com.wizered67.game.Saving.Serializers.LuaSerializers.LuaBooleanSerializer;
import com.wizered67.game.Saving.Serializers.LuaSerializers.LuaDoubleSerializer;
import com.wizered67.game.Saving.Serializers.LuaSerializers.LuaIntegerSerializer;
import com.wizered67.game.Saving.Serializers.LuaSerializers.LuaStringSerializer;
import com.wizered67.game.Saving.Serializers.MusicManagerSerializer;
import com.wizered67.game.Saving.Serializers.SceneManagerSerializer;
import com.wizered67.game.Scripting.LuaScript;
import com.wizered67.game.Scripting.LuaScriptManager;
import com.wizered67.game.Scripting.ScriptManager;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;

import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;


/**
 * Created by Adam on 12/20/2016.
 */
public class SaveManager {
    private static Kryo kryo = new Kryo();

    public static void init() {
        kryo.addDefaultSerializer(MusicManager.class, MusicManagerSerializer.class);
        kryo.addDefaultSerializer(CharacterSprite.class, CharacterSpriteSerializer.class);
        kryo.addDefaultSerializer(ConversationController.class, ConversationSerializer.class);
        kryo.addDefaultSerializer(SceneManager.class, SceneManagerSerializer.class);
        kryo.addDefaultSerializer(LuaBoolean.class, LuaBooleanSerializer.class);
        kryo.addDefaultSerializer(LuaDouble.class, LuaDoubleSerializer.class);
        kryo.addDefaultSerializer(LuaInteger.class, LuaIntegerSerializer.class);
        kryo.addDefaultSerializer(LuaString.class, LuaStringSerializer.class);
        kryo.setReferences(true);
        Log.set(Log.LEVEL_TRACE);
        kryo.register(Color.class, new Serializer<Color>() {
            public Color read (Kryo kryo, Input input, Class<Color> type) {
                Color color = new Color();
                Color.rgba8888ToColor(color, input.readInt());
                return color;
            }

            public void write (Kryo kryo, Output output, Color color) {
                output.writeInt(Color.rgba8888(color));
            }
        });
    }

    public static void save(FileHandle fileHandle) {
        SaveData data = new SaveData();
        data.musicManager = GameManager.musicManager();
        ConversationController conversationController = GUIManager.conversationController();
        data.conversationController = conversationController;
        data.sceneManager = conversationController.sceneManager();
        //LuaScriptManager sm = (LuaScriptManager) ConversationController.scriptManager("Lua");
        Map<String, ScriptManager> managers = ConversationController.allScriptManagers();
        for (String name : managers.keySet()) {
            data.scriptingVariables.put(name, managers.get(name).saveMap());
        }
        saveData(fileHandle, data);
    }

    public static void saveData(FileHandle fileHandle, SaveData saveData) {
        Output output = new Output(new DeflaterOutputStream(fileHandle.write(false)));
        kryo.writeObject(output, saveData);
        output.close();
    }

    public static void load(FileHandle fileHandle) {
        SaveData saveData = loadData(fileHandle);
        for (String name : saveData.scriptingVariables.keySet()) {
            Map<String, Object> variables = saveData.scriptingVariables.get(name);
            ConversationController.scriptManager(name).reload(variables);
        }
        //ConversationController conversationController = GUIManager.conversationController();
        //conversationController.reload(saveData);
        //conversationController.sceneManager().reload(saveData);
    }

    public static SaveData loadData(FileHandle fileHandle) {
        Input input = new Input(new InflaterInputStream(fileHandle.read()));
        SaveData data = kryo.readObject(input, SaveData.class);
        input.close();
        return data;
    }
}