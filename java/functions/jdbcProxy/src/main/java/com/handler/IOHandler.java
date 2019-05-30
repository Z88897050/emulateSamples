package com.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class IOHandler {

    public final static byte OK = (byte) 0x0;
    public final static byte ERROR = (byte) 0x1;

    public static String readByteLen(ByteBuf buf) {
        short length = buf.readUnsignedByte();
        String str = new String(ByteBufUtil.getBytes(buf, buf.readerIndex(), length), StandardCharsets.UTF_8);
        buf.readerIndex(buf.readerIndex() + length);
        return str;
    }

    public static String readShortLen(ByteBuf buf) {
        int length = buf.readUnsignedShort();
        String str = new String(ByteBufUtil.getBytes(buf, buf.readerIndex(), length), StandardCharsets.UTF_8);
        buf.readerIndex(buf.readerIndex() + length);
        return str;
    }


    public static String readIntLen(ByteBuf buf) {
        int length = buf.readInt();
        String str = new String(ByteBufUtil.getBytes(buf, buf.readerIndex(), length), StandardCharsets.UTF_8);
        buf.readerIndex(buf.readerIndex() + length);
        return str;
    }


    public static int[] readInt(int size, ByteBuf buf) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = buf.readInt();
        }
        return arr;
    }

    public static String[] readIntLen(int size, ByteBuf buf) {
        String[] arr = new String[size];
        for (int i = 0; i < size; i++) {
            arr[i] = readIntLen(buf);
        }
        return arr;
    }

    public static String[] readShortLen(int size, ByteBuf buf) {
        String[] arr = new String[size];
        for (int i = 0; i < size; i++) {
            arr[i] = readShortLen(buf);
        }
        return arr;
    }

    public static ByteBuf writeCmd(byte cmd) {
        ByteBuf buf = Unpooled.buffer(1);
        buf.writeByte(cmd);
        return buf;
    }

    public static ByteBuf writeCmdShortStr(byte cmd, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = Unpooled.buffer(3 + bytes.length);
        buf.writeByte(cmd);
        buf.writeShort(bytes.length);
        buf.writeBytes(bytes);
        return buf;
    }

    public static ByteBuf writeCmdInt(byte cmd, int code) {
        ByteBuf buf = Unpooled.buffer(5);
        buf.writeByte(cmd);
        buf.writeInt(code);
        return buf;
    }

    public static ByteBuf writeCmdInt(byte cmd, int[] code) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(cmd);
        for (int value : code) {
            buf.writeInt(value);
        }
        return buf;
    }
    /**
     * length -1 represent str is null
     */
    public static void writeShortString(String str, ByteBuf buf) {
        if (str == null) buf.writeShort(~0);
        else {
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            buf.writeShort(bytes.length);
            buf.writeBytes(bytes);
        }
    }


}
