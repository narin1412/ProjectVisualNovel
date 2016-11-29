package com.wizered67.game.GUI.Conversations.Commands;

import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.wizered67.game.GUI.Conversations.CompleteEvent;
import com.wizered67.game.GUI.Conversations.Conversation;
import com.wizered67.game.GUI.Conversations.MessageWindow;

import java.io.IOException;

/**
 * A ConversationCommand that adds a new CharacterSprite to the scene.
 * @author Adam Victor
 */
public class CharacterAddCommand implements ConversationCommand {
    /** The name of the character to add. */
    String character;
    /** The name of the set of animations to use. */
    String animations;
    /** The name of the speaking sound to use. */
    String speakingSound;

    /** Creates a new CharacterAddCommand that adds a character with name NAME
     * and the default animations and speaking sound.
     */
    public CharacterAddCommand(String name) {
        this(name, name, null);
    }

    /** Creates a new CharacterAddCommand that adds a character with name NAME,
     * animations ANIMATIONS NAME, and speaking sound SOUND when executed.
     */
    public CharacterAddCommand(String name, String animationsName, String sound) {
        character = name;
        animations = animationsName;
        speakingSound = sound;
    }
    /** Executes the command on the MESSAGE WINDOW. */
    @Override
    public void execute(MessageWindow messageWindow) {
        messageWindow.sceneManager().addCharacter(character, animations, speakingSound);
    }
    /** Whether to wait before proceeding to the next command in the branch. */
    @Override
    public boolean waitToProceed() {
        return false;
    }
    /** Checks whether the CompleteEvent C completes this command,
     * and if so acts accordingly. */
    @Override
    public void complete(CompleteEvent c) {

    }
    /** Outputs XML to the XML WRITER for this command. */
    @Override
    public void writeXml(XmlWriter xmlWriter) {
        try {
            xmlWriter.element("addcharacter")
                    .attribute("name", character)
                    .attribute("animations", animations)
                    .attribute("sound", speakingSound)
                    .pop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /** Static method to create a new command from XML Element ELEMENT that is part of CONVERSATION. */
    public static CharacterAddCommand makeCommand(Conversation conversation, XmlReader.Element element) {
        String name = element.getAttribute("name");
        String animations = element.getAttribute("animations", name);
        String sound = element.getAttribute("sound", null);
        return new CharacterAddCommand(name, animations, sound);
    }

}
