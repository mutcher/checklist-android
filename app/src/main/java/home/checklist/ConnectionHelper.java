package home.checklist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Gorbatiuk Illia on 05.04.2016.
 */
public class ConnectionHelper {
    private serverConnector _connector = null;

    public ConnectionHelper(serverConnector connector) {
        this._connector = connector;
    }

    public boolean signIn(String login, String password) throws IOException {
        _connector.disconnect();
        _connector.connect();
        packetBuilder builder = new packetBuilder();
        builder.setOpcode(constants.opcodes.OP_LOGIN);
        builder.setSubcode((byte) 0x00);
        StringBuilder sb = new StringBuilder();
        sb.append(login);
        sb.append(":");
        sb.append(password);
        builder.setStringAsData(sb.toString());
        byte[] packet = builder.build();
        _connector.sendPacket(packet);

        packetReceiver receiver = new packetReceiver();
        if (receiver.receivePacket(_connector)) {
            if (receiver.getOpcode() == constants.opcodes.OP_LOGIN &&
                    receiver.getSubcode() != 0x00) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new IOException();
        }
    }

    public ArrayList<String> loadList(byte listId) throws IOException {
        packetBuilder builder = new packetBuilder();
        builder.setOpcode(constants.opcodes.OP_LIST_GET);
        builder.setSubcode(listId);
        byte[] packet = builder.build();
        _connector.sendPacket(packet);

        ArrayList<String> items = new ArrayList<String>();
        packetReceiver receiver = new packetReceiver();
        int count = 0;
        do {
            if (receiver.receivePacket(_connector))
            {
                count = receiver.getSubcode();
                if (count == 0) break;
                String item = receiver.getDataAsString();
                items.add(item);
            }
            else
            {
                count = 0;
            }
        } while(count > 1);

        Collections.reverse(items);
        return items;
    }

    public boolean addListItem(byte listID, String listItem) throws IOException {
        packetBuilder builder = new packetBuilder();
        builder.setOpcode(constants.opcodes.OP_LIST_ADD);
        builder.setSubcode(listID);
        builder.setStringAsData(listItem);
        byte[] packet = builder.build();
        _connector.sendPacket(packet);

        packetReceiver receiver = new packetReceiver();
        if (receiver.receivePacket(_connector)) {
            if (receiver.getSubcode() == (byte)0x00) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean removeList(byte listID, String listName) throws IOException {
        packetBuilder builder = new packetBuilder();
        builder.setOpcode(constants.opcodes.OP_LIST_DELETE);
        builder.setSubcode(listID);
        builder.setStringAsData(listName);
        byte[] packet = builder.build();

        _connector.sendPacket(packet);

        packetReceiver receiver = new packetReceiver();
        if (receiver.receivePacket(_connector)) {
            if (receiver.getSubcode() == (byte)0x00) {
                return false;
            } else {
                return true;
            }
        }

        return false;
    }
}
