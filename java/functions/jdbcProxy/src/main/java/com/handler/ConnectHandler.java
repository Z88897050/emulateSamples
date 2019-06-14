package com.handler;

import com.audit.AuditEvent;
import com.audit.AuditManager;
import com.jdbc.bean.WrapConnect;
import com.util.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import static com.handler.IOHandler.*;

import java.sql.SQLException;
import java.util.Arrays;

public class ConnectHandler {

    public static void handler(WrapConnect connect, ByteBuf src, ChannelHandlerContext out) throws SQLException {
        String mName = readByteLen(src);
        if ("setAutoCommit".equals(mName)) {
            String bool = readByteLen(src);
            AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(), connect.getUser(), mName, bool));
            connect.setAutoCommit("true".equals(bool));
            out.write(writeByte(OK));
        } else if ("commit".equals(mName)) {
            AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(), connect.getUser(), mName));
            connect.commit();
            out.write(writeByte(OK));
        } else if ("rollback".equals(mName)) {
            AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(), connect.getUser(), mName));
            connect.rollback();
            out.write(writeByte(OK));
        } else if ("createStatement".equals(mName)) {
            String user = null, pwd = null;
            short hasUser = src.readByte();
            if (1 == hasUser) {
                user = readShortLen(src);
                pwd = readShortLen(src);
            }
            if (Constants.verifyOperation) {
                if (user == null) throw new SQLException("proxy need verify operation");
                AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(), connect.getUser(),
                        "login", user));
                UserHandler.login(user, pwd);
            }
            short pc = src.readByte();
            String stmtId;
            if (0 == pc) {
                AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(),
                        user == null ? connect.getUser() : user, mName));
                stmtId = connect.createStatement();
            } else if (2 == pc) {
                int resultSetType = src.readInt();
                int resultSetConcurrency = src.readInt();
                AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(),
                        user == null ? connect.getUser() : user, mName, resultSetType,
                        resultSetConcurrency));
                stmtId = connect.createStatement(resultSetType, resultSetConcurrency);
            } else if (3 == pc) {
                int resultSetType = src.readInt();
                int resultSetConcurrency = src.readInt();
                int resultSetHoldability = src.readInt();
                AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(),
                        user == null ? connect.getUser() : user, mName, resultSetType,
                        resultSetConcurrency, resultSetHoldability));
                stmtId = connect.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
            } else throw new SQLException("createStatement param num[" + pc + "] is not exist");
            if (user != null) connect.getStatement(stmtId).setUser(user, pwd);
            out.write(writeShortStr(OK, stmtId));
        } else if ("prepareStatement".equals(mName)) {
            String user = null, pwd = null;
            short hasUser = src.readByte();
            if (1 == hasUser) {
                user = readShortLen(src);
                pwd = readShortLen(src);
            }
            if (Constants.verifyOperation) {
                if (user == null) throw new SQLException("proxy need verify operation");
                AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(), connect.getUser(),
                        "login", user));
                UserHandler.login(user, pwd);
            }
            short pc = src.readByte();
            String stmtId;
            String sql = readIntLen(src);
            UserHandler.authSql(connect, user == null ? connect.getUser() : user, sql);
            if (1 == pc) {
                AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(),
                        user == null ? connect.getUser() : user, mName, sql));
                stmtId = connect.prepareStatement(sql);
            } else if (2 == pc) {
                int arrSize = src.readShort();
                short type = src.readByte();
                if (0 == arrSize) {
                    if (0 != type) throw new SQLException("createPreparedStatement[autoGeneratedKeys] type[" +
                            type + "] error");
                    int autoGeneratedKeys = src.readInt();
                    AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(),
                            user == null ? connect.getUser() : user, mName, sql, autoGeneratedKeys));
                    stmtId = connect.prepareStatement(sql, autoGeneratedKeys);
                } else {
                    if (0 == type) {
                        int[] columnIndexes = readInt(arrSize, src);
                        AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(),
                                user == null ? connect.getUser() : user, mName, sql,
                                Arrays.toString(columnIndexes)));
                        stmtId = connect.prepareStatement(sql, columnIndexes);
                    } else if (1 == type) {
                        String[] columnNames = readShortLen(arrSize, src);
                        AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(),
                                user == null ? connect.getUser() : user, mName, sql,
                                Arrays.toString(columnNames)));
                        stmtId = connect.prepareStatement(sql, columnNames);
                    } else throw new SQLException("createPreparedStatement[array] type[" + type + "] error");
                }
            } else if (3 == pc) {
                int resultSetType = src.readInt();
                int resultSetConcurrency = src.readInt();
                AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(),
                        user == null ? connect.getUser() : user, mName, sql, resultSetType,
                        resultSetConcurrency));
                stmtId = connect.prepareStatement(sql, resultSetType, resultSetConcurrency);
            } else if (4 == pc) {
                int resultSetType = src.readInt();
                int resultSetConcurrency = src.readInt();
                int resultSetHoldability = src.readInt();
                AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(),
                        user == null ? connect.getUser() : user, mName, sql, resultSetType,
                        resultSetConcurrency, resultSetHoldability));
                stmtId = connect.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
            } else throw new SQLException("createPreparedStatement methodLength[" + pc + "] is not exit");
            if (user != null) connect.getStatement(stmtId).setUser(user, pwd);
            out.write(writeShortStr(OK, stmtId));
        } else if ("setCatalog".equals(mName)) {
            String catalog = readByteLen(src);
            AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(),
                    connect.getUser(), mName, catalog));
            connect.setCatalog(catalog);
            out.write(writeByte(OK));
        } else if ("setSchema".equals(mName)) {
            String schema = readByteLen(src);
            AuditManager.getInstance().audit(new AuditEvent(connect.getRemoteAddr(),
                    connect.getUser(), mName, schema));
            connect.setSchema(schema);
            out.write(writeByte(OK));
        } else throw new SQLException("connectMethod[" + mName + "] is not support");
    }
}
