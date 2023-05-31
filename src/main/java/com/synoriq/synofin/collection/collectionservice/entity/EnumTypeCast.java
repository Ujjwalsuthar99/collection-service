package com.synoriq.synofin.collection.collectionservice.entity;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.sql.*;

public class EnumTypeCast extends org.hibernate.type.EnumType {

    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {

        st.setObject(index, value != null ? ((Enum) value).name() : null, Types.OTHER);
    }
}
