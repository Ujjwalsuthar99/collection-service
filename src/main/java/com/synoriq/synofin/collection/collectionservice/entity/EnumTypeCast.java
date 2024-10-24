package com.synoriq.synofin.collection.collectionservice.entity;

import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.EnumType;

import java.sql.*;

public class EnumTypeCast extends EnumType<EnumSQLConstants.LogNames> {

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {

        st.setObject(index, value != null ? ((Enum) value).name() : null, Types.OTHER);
    }
}
