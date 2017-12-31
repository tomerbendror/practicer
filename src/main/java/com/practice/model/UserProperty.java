package com.practice.model;

import org.apache.log4j.Logger;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by Tomer
 */
@Entity
@Table(name="user_property")
public class UserProperty<T> extends BaseEntity {

    private static final Logger logger = Logger.getLogger(UserProperty.class);

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private User user;

    @Column(name = "PROPERTY_NAME")
    private String propertyName;

    @Column(name = "PROPERTY_CLASS")
    private String propertyClass;

    @Column(name = "VALUE_STR")
    private String valueStr;

    @Column(name = "LAST_UPDATED", updatable = false, insertable = false)
    private Date lastUpdated;

    @Transient
    private T value;

    public UserProperty() {
    }

    public UserProperty(User user, PropertyKey<T> key, T value) {
        this.user = user;
        this.valueStr = (value == null) ? null : value.toString();
        this.propertyName = key.name;
        this.propertyClass = key.clazz.getName();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public User getUser() {
        return user;
    }

    @SuppressWarnings("unchecked")
    public Object getValue() {
        if (value == null && valueStr != null) {
            try {
                Class<?> clazz = Class.forName(propertyClass);
                Method method = clazz.getMethod("valueOf", String.class);
                value = (T) method.invoke(null, valueStr);
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                String message = "error when invoking 'valueOf' for class: " + propertyClass + ", with param: " + valueStr;
                logger.error(message, e);
                throw new RuntimeException(message);
            }
        }
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        valueStr = (value == null) ? null : value.toString();
    }

    public static class PropertyKey<T> {
        public final String name;
        public final T defaultVal;
        public final Class<T> clazz;

        public PropertyKey(String name, Class<T> clazz, T defaultVal) {
            this.name = name;
            this.clazz = clazz;
            this.defaultVal = defaultVal;
        }
    }
}
