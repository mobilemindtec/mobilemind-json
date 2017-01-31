package br.com.mobilemind.api.json;

/*
 * #%L
 * Mobile Mind - JSON
 * %%
 * Copyright (C) 2012 Mobile Mind Empresa de Tecnologia
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
import br.com.mobilemind.api.json.annotations.JsonColumn;
import br.com.mobilemind.api.json.annotations.JsonEntity;
import br.com.mobilemind.api.json.annotations.JsonEnumType;
import br.com.mobilemind.api.security.key.Base64;
import br.com.mobilemind.api.utils.ClassUtil;
import br.com.mobilemind.api.utils.MobileMindUtil;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.file.FileVisitResult;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Ricardo Bocchi
 */
public class JSON<T> implements JSONAble<T> {

    private final Class<T> clazz;
    private T entity;
    private final List<Field> fields;
    private final JsonEntity jsonEnttiy;

    public JSON(Class<T> clazz) {
        this.clazz = clazz;
        this.fields = ClassUtil.getAnnotatedsFields(clazz, JsonColumn.class);
        this.jsonEnttiy = clazz.getAnnotation(JsonEntity.class);

        if (jsonEnttiy == null) {
            throw new JSONException("@JsonEntity nof foud to class " + clazz.getCanonicalName());
        }

        if (fields.isEmpty()) {
            throw new JSONException("not fields founds for " + clazz.getCanonicalName());
        }

        for (Field field : fields) {
            field.setAccessible(true);
        }

    }

    public static <T> T toEntity(Class<T> cls, String json) {
        return new JSON<T>(cls).fromJSON(json);
    }

    public static <T> String toString(Class<T> cls, T entity) {
        return new JSON<T>(cls).toJSON(entity).toString();
    }

    public static <T> JSONObject toJson(Class<T> cls, T entity) {
        return new JSON<T>(cls).toJSON(entity);
    }

    private String getFieldNameToJSON(Field field) {
        JsonColumn f = field.getAnnotation(JsonColumn.class);

        if ("".equals(f.name())) {
            return field.getName();
        }
        return f.name();
    }

    public T getEntity() {
        return entity;
    }

    public JSON setEntity(T entity) {
        this.entity = entity;
        return this;
    }

    public JSONObject toJSON(T entity) throws JSONException {
        this.entity = entity;
        return toJSON();
    }

    @Override
    public JSONObject toJSON() throws JSONException {

        if (this.entity == null) {
            throw new JSONException("entity can't be null");
        }

        JSONObject json = new JSONObject();
        Object value = null;

        for (Field f : this.fields) {
            try {

                JsonColumn jfield = f.getAnnotation(JsonColumn.class);

                if (jfield.useJavaBean()) {
                    value = ClassUtil.getGetMethod(f.getName(), this.clazz).invoke(this.entity);
                } else {
                    value = f.get(this.entity);
                }

                if (value == null) {
                    continue;
                }

                if (value.getClass().isEnum()) {

                    if (jfield.enumConverter() == JsonEnumType.ORDINAL) {
                        if (jfield.enumOrdinalDiff() != 0) {
                            value = ((Enum) value).ordinal() + jfield.enumOrdinalDiff();
                        } else {
                            value = ((Enum) value).ordinal();
                        }
                    } else {
                        value = ((Enum) value).name();
                    }
                }

                if (ClassUtil.isDate(f.getType())) {

                    String pattern = jfield.patternToDateConverter();
                    if ("".equals(pattern) && !jfield.timeToMilliseconds()) {
                        throw new JSONException("pattern of date Unknown");
                    }
                    if (jfield.timeToMilliseconds()) {
                        value = ((Date) value).getTime();
                    } else {
                        value = new SimpleDateFormat(pattern).format(value);
                    }
                } else if (ClassUtil.isByteArray(f.getType())) {
                    value = Base64.encodeBytes((byte[]) value, Base64.GZIP);
                } else {
                    if (jfield.isComplexType()) {
                        JSON jnew = new JSON(f.getType());
                        value = jnew.toJSON(value);
                    } else if (ClassUtil.isAssignableFrom(f.getType(), Collection.class)) {
                        ParameterizedType listType = (ParameterizedType) f.getGenericType();
                        Class genericType = null;

                        if (jfield.genericListType() != Object.class) {
                            genericType = jfield.genericListType();
                        } else {
                            if (listType.getActualTypeArguments() == null
                                    || listType.getActualTypeArguments().length == 0) {
                                throw new JSONException("generic type of list not found. field[" + f.getName() + "]");
                            }
                            genericType = (Class) listType.getActualTypeArguments()[0];
                        }

                        if (ClassUtil.isPrimitive(genericType)) {
                            value = new JSONArray(value);
                        } else {
                            JSON jnew = new JSON(genericType);
                            value = jnew.toJSONArray((Collection) value);
                        }
                    }
                }

            } catch (Exception e) {
                throw new JSONException(e);
            }
            json.put(this.getFieldNameToJSON(f), value);
        }

        return json;
    }

    @Override
    public JSONArray toJSONArray(Collection<T> list) throws JSONException {
        return new JSONArray(list, this);
    }

    @Override
    public T fromJSON(JSONObject json) throws JSONException {
        T source = null;
        try {
            source = this.clazz.newInstance();
        } catch (Exception ex) {
            throw new JSONException(ex);
        }

        for (Field f : this.fields) {
            setValue(json, source, f);
        }

        return source;
    }

    @Override
    public T fromJSON(String json) throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        return fromJSON(jsonObj);
    }

    @Override
    public List<T> fromJSONArray(String array) throws JSONException {

        if (!array.substring(array.length() - 2).contains("]")) {
            List<T> list = new LinkedList<T>();
            list.add(fromJSON(array));
        } else if (!array.startsWith("[")) {
            int x = array.indexOf("[");
            array = array.substring(x, array.length() - 2);
        }

        JSONArray jsonArray = new JSONArray(array);
        return fromJSONArray(jsonArray);
    }

    @Override
    public List<T> fromJSONArray(JSONArray array) throws JSONException {
        List<T> list = new LinkedList<T>();

        for (int i = 0; i < array.length(); i++) {
            list.add(fromJSON(array.getJSONObject(i)));
        }
        return list;
    }

    private void setValue(JSONObject json, T source, Field field) throws JSONException {

        Class type = field.getType();
        String name = this.getFieldNameToJSON(field);
        JsonColumn jsonField = field.getAnnotation(JsonColumn.class);
        Object value = null;
        boolean setted = false;

        try {

            if (!json.has(name)) {                
                if(jsonField.optional())
                    return;
                else
                    throw new JSONException(JSONException.KEY_NOT_FOUND);
            }

            if (ClassUtil.isInteger(type)) {
                value = json.getInt(name);
                setted = true;
            } else if (ClassUtil.isBoolean(type)) {
                value = json.getBoolean(name);
                setted = true;
            } else if (ClassUtil.isDouble(type)) {
                value = json.getDouble(name);
                setted = true;
            } else if (ClassUtil.isString(type)) {
                value = json.getString(name);
                setted = true;
            } else if (ClassUtil.isLong(type)) {
                value = json.getLong(name);
                setted = true;
            } else if (type.isEnum()) {

                if (json.get(name) == null || json.isNull(name)) {
                    value = null;
                } else {

                    if (jsonField.enumConverter() == JsonEnumType.STRING) {
                        if (jsonField.enumUpperCase()) {
                            name = json.getString(name).toUpperCase();
                            value = Enum.valueOf(type, name);
                        } else {
                            value = json.getEnumOnSimpleJSON(type, name);
                        }
                    } else {
                        int val = json.getInt(name);

                        for (Object it : type.getEnumConstants()) {
                            Enum e = (Enum) it;
                            if ((e.ordinal() + jsonField.enumOrdinalDiff()) == val) {
                                value = it;
                                break;
                            }
                        }
                    }
                }
                setted = true;
            } else if (ClassUtil.isByteArray(type)) {
                value = json.get(name);
                if (value instanceof String) {
                    try {
                        value = Base64.decode((String) value, Base64.GZIP);
                    } catch (Exception e) {
                    }
                } else {
                    value = null;
                }
                setted = true;
            } else if (ClassUtil.isDate(type)) {
                String patter = jsonField.patternToDateConverter();
                if ("".equals(patter) && !jsonField.timeToMilliseconds()) {
                    throw new JSONException("pattern of date Unknown");
                }
                if (jsonField.timeToMilliseconds()) {
                    Object time = json.optLong(name);

                    if (!jsonField.optional() && time == null) {
                        throw new JSONException(JSONException.KEY_NOT_FOUND);
                    }

                    if (time == null || (Long) time == 0) {
                        value = null;
                    } else {
                        value = new Date((Long) time);
                    }
                    setted = true;
                } else {
                    Object time = json.optDateOnSimpleJSON(patter, name);
                    if (!jsonField.optional() && time == null) {
                        throw new JSONException(JSONException.KEY_NOT_FOUND);
                    }
                    value = time;
                    setted = true;
                }
            } else {
                if (jsonField.isComplexType()) {
                    JSONObject jobj = json.optJSONObject(name);
                    if (jobj == null) {
                        if (!jsonField.optional()) {
                            throw new JSONException(JSONException.KEY_NOT_FOUND);
                        }
                    } else {
                        JSON jnew = new JSON(field.getType());
                        value = jnew.fromJSON(jobj);
                        setted = true;
                    }
                } else if (ClassUtil.isAssignableFrom(field.getType(), Collection.class)) {
                    JSONArray jobj = json.getJSONArray(name);
                    if (jobj == null) {
                        if (!jsonField.optional()) {
                            throw new JSONException(JSONException.KEY_NOT_FOUND);
                        }
                    } else {

                        if (jobj.length() == 0) {
                            return;
                        }

                        ParameterizedType listType = (ParameterizedType) field.getGenericType();
                        Class genericType = null;

                        if (jsonField.genericListType() != Object.class) {
                            genericType = jsonField.genericListType();
                        } else {
                            if (listType.getActualTypeArguments() == null
                                    || listType.getActualTypeArguments().length == 0) {
                                throw new JSONException("generic type of list not found. field[" + field.getName() + "]");
                            }
                            genericType = (Class) listType.getActualTypeArguments()[0];
                        }

                        if (ClassUtil.isPrimitive(genericType)) {
                            value = new ArrayList();
                            for (int i = 0; i < jobj.length(); i++) {
                                ((ArrayList) value).add(jobj.get(i));
                            }
                        } else {
                            JSON jnew = new JSON(genericType);
                            value = jnew.fromJSONArray(jobj);
                        }
                        setted = true;
                    }
                } else {
                    throw new JSONException("type " + type.getName() + " not found for conversion");
                }
            }

            if (setted) {
                if (!jsonField.useJavaBean()) {
                    field.set(source, value);
                } else {
                    ClassUtil.getSetMethod(field.getName(), this.clazz).invoke(source, value);
                }
            }

        } catch (Exception e) {
            if (e instanceof JSONException) {

                if (e.getMessage() != null && e.getMessage().contains("not found") && jsonEnttiy.ignoreProperttNotFound()) {
                    return;
                }

                throw (JSONException) e;
            }
            throw new JSONException(e);
        }
    }
}
