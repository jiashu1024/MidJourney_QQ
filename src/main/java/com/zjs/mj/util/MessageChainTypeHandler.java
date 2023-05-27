package com.zjs.mj.util;

import net.mamoe.mirai.message.data.MessageChain;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用于序列化messageChain到数据库
 */
public class MessageChainTypeHandler extends BaseTypeHandler<MessageChain> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MessageChain parameter, JdbcType jdbcType) throws SQLException {
        String json = MessageChain.serializeToJsonString(parameter);
        ps.setString(i, json);
    }

    @Override
    public MessageChain getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);

        return MessageChain.deserializeFromJsonString(json);
    }

    @Override
    public MessageChain getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return MessageChain.deserializeFromJsonString(json);
    }

    @Override
    public MessageChain getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return MessageChain.deserializeFromJsonString(cs.getString(columnIndex));
    }
}
