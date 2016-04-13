package home.checklist;

import android.provider.ContactsContract;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gorbatiuk Illia on 05.04.2016.
 */

class constants {
    public enum opcodes {
        OP_NULL(0x00),
        OP_LIST_DELETE(0x08),
        OP_LOGIN(0x10),
        OP_LIST_ADD(0x20),
        OP_LIST_GET(0x40),
        OP_LIST_SET(0x80);

        private byte _val;

        public byte getCode() {
            return this._val;
        }

        opcodes(int val) {
            this._val = (byte) val;
        }

        opcodes(byte val) {
            this._val = val;
        }

        public static opcodes fromByte(byte b)
        {
            opcodes[] opcodes = constants.opcodes.values();
            for(int i = 0; i < opcodes.length; i++) {
                if (opcodes[i].getCode() == b) {
                    return opcodes[i];
                }
            }

            return constants.opcodes.OP_NULL;
        }
    }

    public static byte[] packet_sign = new byte[] {0x05, 0x05, 0x03};
}

class packetBuilder {
    constants.opcodes _opcode = constants.opcodes.OP_NULL;
    byte _subcode = 0;
    byte[] _data = null;

    public void setOpcode(constants.opcodes opcode) {
        this._opcode = opcode;
    }

    public void setSubcode(byte subcode) {
        this._subcode = subcode;
    }

    public void setData(byte[] data) {
        this._data = data;
    }

    public void setStringAsData(String str) throws UnsupportedEncodingException {
        byte[] bytes = str.getBytes("UTF-16BE");
        this.setData(bytes);
    }

    public byte[] build() {
        int buffer_length = constants.packet_sign.length + 2;
        if (_data != null) {
            buffer_length += _data.length + 1;
        }

        byte[] buffer = new byte[buffer_length];
        System.arraycopy(constants.packet_sign, 0, buffer, 0, constants.packet_sign.length);
        int pos = constants.packet_sign.length;
        buffer[pos++] = _opcode.getCode();
        buffer[pos++] = _subcode;
        if (_data != null)
        {
            buffer[2] = (byte)0xFF;
            buffer[pos++] = (byte)_data.length;
            System.arraycopy(_data, 0, buffer, pos, _data.length);
        }
        return buffer;
    }
}

class packetReceiver {
    private constants.opcodes _opcode = constants.opcodes.OP_NULL;
    private byte _subcode = 0;
    private byte[] _data = null;


    public constants.opcodes getOpcode() {
        return this._opcode;
    }

    public byte getSubcode() {
        return this._subcode;
    }

    public byte[] getData() {
        return this._data;
    }

    public String getDataAsString() throws UnsupportedEncodingException {
        String str = new String(_data, "UTF-16BE");
        return str;
    }

    private boolean isValidPacket(Byte[] bytes) {
        boolean valid = true;
        for(int i = 0; i < constants.packet_sign.length; i++) {
            if (bytes[i] != constants.packet_sign[i]) {
                valid = false;
                break;
            }
        }
        return  valid;
    }

    public boolean receivePacket(serverConnector connector) {
        boolean hasData = false;
        ArrayList<Byte> bytes = new ArrayList<Byte>();
        try {
            while(bytes.size() != constants.packet_sign.length + 2)
            {
                bytes.add(connector.receiveByte());
            }
            if (bytes.get(2) == (byte)0xFF) {
                hasData = true;
                bytes.set(2, (byte)0x03);
            }
            Byte[] packet = new Byte[bytes.size()];
            packet = bytes.toArray(packet);
            if (!isValidPacket(packet))
            {
                return false;
            }

            int pos = constants.packet_sign.length;
            _opcode = constants.opcodes.fromByte(bytes.get(pos++));
            _subcode = bytes.get(pos);

            if (hasData)
            {
                int count = connector.receiveByte();
                _data = new byte[count];
                for(int i = 0; i < count; i++)
                {
                    _data[i] = connector.receiveByte();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

public class serverConnector {
    private Socket _serverSocket = null;

    public void connect() throws IOException {
        _serverSocket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 51789);
        _serverSocket.connect(socketAddress);
    }

    public void disconnect() throws IOException {
        if (_serverSocket != null) {
            _serverSocket.close();
        }
    }

    public void sendPacket(byte[] buffer) throws IOException {
        _serverSocket.getOutputStream().write(buffer);
    }

    public byte receiveByte() throws IOException {
        return (byte)_serverSocket.getInputStream().read();
    }
}
