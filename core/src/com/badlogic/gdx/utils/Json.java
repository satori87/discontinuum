// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.reflect.Constructor;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import java.io.IOException;
import java.util.Iterator;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import java.util.Arrays;
import java.io.Closeable;
import com.badlogic.gdx.files.FileHandle;
import java.io.Writer;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.security.AccessControlException;
import java.util.Collection;
import java.util.Collections;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import java.util.ArrayList;

public class Json
{
    private static final boolean debug = false;
    private JsonWriter writer;
    private String typeName;
    private boolean usePrototypes;
    private JsonWriter.OutputType outputType;
    private boolean quoteLongValues;
    private boolean ignoreUnknownFields;
    private boolean ignoreDeprecated;
    private boolean readDeprecated;
    private boolean enumNames;
    private Serializer defaultSerializer;
    private final ObjectMap<Class, OrderedMap<String, FieldMetadata>> typeToFields;
    private final ObjectMap<String, Class> tagToClass;
    private final ObjectMap<Class, String> classToTag;
    private final ObjectMap<Class, Serializer> classToSerializer;
    private final ObjectMap<Class, Object[]> classToDefaultValues;
    private final Object[] equals1;
    private final Object[] equals2;
    
    public Json() {
        this.typeName = "class";
        this.usePrototypes = true;
        this.enumNames = true;
        this.typeToFields = new ObjectMap<Class, OrderedMap<String, FieldMetadata>>();
        this.tagToClass = new ObjectMap<String, Class>();
        this.classToTag = new ObjectMap<Class, String>();
        this.classToSerializer = new ObjectMap<Class, Serializer>();
        this.classToDefaultValues = new ObjectMap<Class, Object[]>();
        this.equals1 = new Object[1];
        this.equals2 = new Object[1];
        this.outputType = JsonWriter.OutputType.minimal;
    }
    
    public Json(final JsonWriter.OutputType outputType) {
        this.typeName = "class";
        this.usePrototypes = true;
        this.enumNames = true;
        this.typeToFields = new ObjectMap<Class, OrderedMap<String, FieldMetadata>>();
        this.tagToClass = new ObjectMap<String, Class>();
        this.classToTag = new ObjectMap<Class, String>();
        this.classToSerializer = new ObjectMap<Class, Serializer>();
        this.classToDefaultValues = new ObjectMap<Class, Object[]>();
        this.equals1 = new Object[1];
        this.equals2 = new Object[1];
        this.outputType = outputType;
    }
    
    public void setIgnoreUnknownFields(final boolean ignoreUnknownFields) {
        this.ignoreUnknownFields = ignoreUnknownFields;
    }
    
    public boolean getIgnoreUnknownFields() {
        return this.ignoreUnknownFields;
    }
    
    public void setIgnoreDeprecated(final boolean ignoreDeprecated) {
        this.ignoreDeprecated = ignoreDeprecated;
    }
    
    public void setReadDeprecated(final boolean readDeprecated) {
        this.readDeprecated = readDeprecated;
    }
    
    public void setOutputType(final JsonWriter.OutputType outputType) {
        this.outputType = outputType;
    }
    
    public void setQuoteLongValues(final boolean quoteLongValues) {
        this.quoteLongValues = quoteLongValues;
    }
    
    public void setEnumNames(final boolean enumNames) {
        this.enumNames = enumNames;
    }
    
    public void addClassTag(final String tag, final Class type) {
        this.tagToClass.put(tag, type);
        this.classToTag.put(type, tag);
    }
    
    public Class getClass(final String tag) {
        return this.tagToClass.get(tag);
    }
    
    public String getTag(final Class type) {
        return this.classToTag.get(type);
    }
    
    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }
    
    public void setDefaultSerializer(final Serializer defaultSerializer) {
        this.defaultSerializer = defaultSerializer;
    }
    
    public <T> void setSerializer(final Class<T> type, final Serializer<T> serializer) {
        this.classToSerializer.put(type, serializer);
    }
    
    public <T> Serializer<T> getSerializer(final Class<T> type) {
        return this.classToSerializer.get(type);
    }
    
    public void setUsePrototypes(final boolean usePrototypes) {
        this.usePrototypes = usePrototypes;
    }
    
    public void setElementType(final Class type, final String fieldName, final Class elementType) {
        final ObjectMap<String, FieldMetadata> fields = this.getFields(type);
        final FieldMetadata metadata = fields.get(fieldName);
        if (metadata == null) {
            throw new SerializationException("Field not found: " + fieldName + " (" + type.getName() + ")");
        }
        metadata.elementType = elementType;
    }
    
    private OrderedMap<String, FieldMetadata> getFields(final Class type) {
        final OrderedMap<String, FieldMetadata> fields = this.typeToFields.get(type);
        if (fields != null) {
            return fields;
        }
        final Array<Class> classHierarchy = new Array<Class>();
        for (Class nextClass = type; nextClass != Object.class; nextClass = nextClass.getSuperclass()) {
            classHierarchy.add(nextClass);
        }
        final ArrayList<Field> allFields = new ArrayList<Field>();
        for (int i = classHierarchy.size - 1; i >= 0; --i) {
            Collections.addAll(allFields, ClassReflection.getDeclaredFields(classHierarchy.get(i)));
        }
        final OrderedMap<String, FieldMetadata> nameToField = new OrderedMap<String, FieldMetadata>(allFields.size());
        for (int j = 0, n = allFields.size(); j < n; ++j) {
            final Field field = allFields.get(j);
            if (!field.isTransient()) {
                if (!field.isStatic()) {
                    if (!field.isSynthetic()) {
                        if (!field.isAccessible()) {
                            try {
                                field.setAccessible(true);
                            }
                            catch (AccessControlException ex) {
                                continue;
                            }
                        }
                        if (!this.ignoreDeprecated || this.readDeprecated || !field.isAnnotationPresent(Deprecated.class)) {
                            nameToField.put(field.getName(), new FieldMetadata(field));
                        }
                    }
                }
            }
        }
        this.typeToFields.put(type, nameToField);
        return nameToField;
    }
    
    public String toJson(final Object object) {
        return this.toJson(object, (object == null) ? null : object.getClass(), (Class)null);
    }
    
    public String toJson(final Object object, final Class knownType) {
        return this.toJson(object, knownType, (Class)null);
    }
    
    public String toJson(final Object object, final Class knownType, final Class elementType) {
        final StringWriter buffer = new StringWriter();
        this.toJson(object, knownType, elementType, buffer);
        return buffer.toString();
    }
    
    public void toJson(final Object object, final FileHandle file) {
        this.toJson(object, (object == null) ? null : object.getClass(), null, file);
    }
    
    public void toJson(final Object object, final Class knownType, final FileHandle file) {
        this.toJson(object, knownType, null, file);
    }
    
    public void toJson(final Object object, final Class knownType, final Class elementType, final FileHandle file) {
        Writer writer = null;
        try {
            writer = file.writer(false, "UTF-8");
            this.toJson(object, knownType, elementType, writer);
        }
        catch (Exception ex) {
            throw new SerializationException("Error writing file: " + file, ex);
        }
        finally {
            StreamUtils.closeQuietly(writer);
        }
        StreamUtils.closeQuietly(writer);
    }
    
    public void toJson(final Object object, final Writer writer) {
        this.toJson(object, (object == null) ? null : object.getClass(), null, writer);
    }
    
    public void toJson(final Object object, final Class knownType, final Writer writer) {
        this.toJson(object, knownType, null, writer);
    }
    
    public void toJson(final Object object, final Class knownType, final Class elementType, final Writer writer) {
        this.setWriter(writer);
        try {
            this.writeValue(object, knownType, elementType);
        }
        finally {
            StreamUtils.closeQuietly(this.writer);
            this.writer = null;
        }
        StreamUtils.closeQuietly(this.writer);
        this.writer = null;
    }
    
    public void setWriter(Writer writer) {
        if (!(writer instanceof JsonWriter)) {
            writer = new JsonWriter(writer);
        }
        (this.writer = (JsonWriter)writer).setOutputType(this.outputType);
        this.writer.setQuoteLongValues(this.quoteLongValues);
    }
    
    public JsonWriter getWriter() {
        return this.writer;
    }
    
    public void writeFields(final Object object) {
        final Class type = object.getClass();
        final Object[] defaultValues = this.getDefaultValues(type);
        final OrderedMap<String, FieldMetadata> fields = this.getFields(type);
        int i = 0;
        for (final FieldMetadata metadata : new OrderedMap.OrderedMapValues<Object>((OrderedMap<?, Object>)fields)) {
            final Field field = metadata.field;
            if (this.readDeprecated && this.ignoreDeprecated && field.isAnnotationPresent(Deprecated.class)) {
                continue;
            }
            try {
                final Object value = field.get(object);
                if (defaultValues != null) {
                    final Object defaultValue = defaultValues[i++];
                    if (value == null && defaultValue == null) {
                        continue;
                    }
                    if (value != null && defaultValue != null) {
                        if (value.equals(defaultValue)) {
                            continue;
                        }
                        if (value.getClass().isArray() && defaultValue.getClass().isArray()) {
                            this.equals1[0] = value;
                            this.equals2[0] = defaultValue;
                            if (Arrays.deepEquals(this.equals1, this.equals2)) {
                                continue;
                            }
                        }
                    }
                }
                this.writer.name(field.getName());
                this.writeValue(value, field.getType(), metadata.elementType);
            }
            catch (ReflectionException ex) {
                throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex);
            }
            catch (SerializationException ex2) {
                ex2.addTrace(field + " (" + type.getName() + ")");
                throw ex2;
            }
            catch (Exception runtimeEx) {
                final SerializationException ex3 = new SerializationException(runtimeEx);
                ex3.addTrace(field + " (" + type.getName() + ")");
                throw ex3;
            }
        }
    }
    
    private Object[] getDefaultValues(final Class type) {
        if (!this.usePrototypes) {
            return null;
        }
        if (this.classToDefaultValues.containsKey(type)) {
            return this.classToDefaultValues.get(type);
        }
        Object object;
        try {
            object = this.newInstance(type);
        }
        catch (Exception ex4) {
            this.classToDefaultValues.put(type, null);
            return null;
        }
        final ObjectMap<String, FieldMetadata> fields = this.getFields(type);
        final Object[] values = new Object[fields.size];
        this.classToDefaultValues.put(type, values);
        int i = 0;
        for (final FieldMetadata metadata : fields.values()) {
            final Field field = metadata.field;
            if (this.readDeprecated && this.ignoreDeprecated && field.isAnnotationPresent(Deprecated.class)) {
                continue;
            }
            try {
                values[i++] = field.get(object);
            }
            catch (ReflectionException ex) {
                throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex);
            }
            catch (SerializationException ex2) {
                ex2.addTrace(field + " (" + type.getName() + ")");
                throw ex2;
            }
            catch (RuntimeException runtimeEx) {
                final SerializationException ex3 = new SerializationException(runtimeEx);
                ex3.addTrace(field + " (" + type.getName() + ")");
                throw ex3;
            }
        }
        return values;
    }
    
    public void writeField(final Object object, final String name) {
        this.writeField(object, name, name, null);
    }
    
    public void writeField(final Object object, final String name, final Class elementType) {
        this.writeField(object, name, name, elementType);
    }
    
    public void writeField(final Object object, final String fieldName, final String jsonName) {
        this.writeField(object, fieldName, jsonName, null);
    }
    
    public void writeField(final Object object, final String fieldName, final String jsonName, Class elementType) {
        final Class type = object.getClass();
        final ObjectMap<String, FieldMetadata> fields = this.getFields(type);
        final FieldMetadata metadata = fields.get(fieldName);
        if (metadata == null) {
            throw new SerializationException("Field not found: " + fieldName + " (" + type.getName() + ")");
        }
        final Field field = metadata.field;
        if (elementType == null) {
            elementType = metadata.elementType;
        }
        try {
            this.writer.name(jsonName);
            this.writeValue(field.get(object), field.getType(), elementType);
        }
        catch (ReflectionException ex) {
            throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex);
        }
        catch (SerializationException ex2) {
            ex2.addTrace(field + " (" + type.getName() + ")");
            throw ex2;
        }
        catch (Exception runtimeEx) {
            final SerializationException ex3 = new SerializationException(runtimeEx);
            ex3.addTrace(field + " (" + type.getName() + ")");
            throw ex3;
        }
    }
    
    public void writeValue(final String name, final Object value) {
        try {
            this.writer.name(name);
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
        if (value == null) {
            this.writeValue(value, null, null);
        }
        else {
            this.writeValue(value, value.getClass(), null);
        }
    }
    
    public void writeValue(final String name, final Object value, final Class knownType) {
        try {
            this.writer.name(name);
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
        this.writeValue(value, knownType, null);
    }
    
    public void writeValue(final String name, final Object value, final Class knownType, final Class elementType) {
        try {
            this.writer.name(name);
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
        this.writeValue(value, knownType, elementType);
    }
    
    public void writeValue(final Object value) {
        if (value == null) {
            this.writeValue(value, null, null);
        }
        else {
            this.writeValue(value, value.getClass(), null);
        }
    }
    
    public void writeValue(final Object value, final Class knownType) {
        this.writeValue(value, knownType, null);
    }
    
    public void writeValue(final Object value, Class knownType, Class elementType) {
        try {
            if (value == null) {
                this.writer.value(null);
                return;
            }
            if ((knownType != null && ((Class)knownType).isPrimitive()) || knownType == String.class || knownType == Integer.class || knownType == Boolean.class || knownType == Float.class || knownType == Long.class || knownType == Double.class || knownType == Short.class || knownType == Byte.class || knownType == Character.class) {
                this.writer.value(value);
                return;
            }
            Class actualType = value.getClass();
            if (actualType.isPrimitive() || actualType == String.class || actualType == Integer.class || actualType == Boolean.class || actualType == Float.class || actualType == Long.class || actualType == Double.class || actualType == Short.class || actualType == Byte.class || actualType == Character.class) {
                this.writeObjectStart(actualType, null);
                this.writeValue("value", value);
                this.writeObjectEnd();
                return;
            }
            if (value instanceof Serializable) {
                this.writeObjectStart(actualType, (Class)knownType);
                ((Serializable)value).write(this);
                this.writeObjectEnd();
                return;
            }
            final Serializer serializer = this.classToSerializer.get(actualType);
            if (serializer != null) {
                serializer.write(this, value, (Class)knownType);
                return;
            }
            if (value instanceof Array) {
                if (knownType != null && actualType != knownType && actualType != Array.class) {
                    throw new SerializationException("Serialization of an Array other than the known type is not supported.\nKnown type: " + knownType + "\nActual type: " + actualType);
                }
                this.writeArrayStart();
                final Array array = (Array)value;
                for (int i = 0, n = array.size; i < n; ++i) {
                    this.writeValue(array.get(i), elementType, null);
                }
                this.writeArrayEnd();
            }
            else if (value instanceof Queue) {
                if (knownType != null && actualType != knownType && actualType != Queue.class) {
                    throw new SerializationException("Serialization of a Queue other than the known type is not supported.\nKnown type: " + knownType + "\nActual type: " + actualType);
                }
                this.writeArrayStart();
                final Queue queue = (Queue)value;
                for (int i = 0, n = queue.size; i < n; ++i) {
                    this.writeValue(queue.get(i), elementType, null);
                }
                this.writeArrayEnd();
            }
            else {
                if (value instanceof Collection) {
                    if (this.typeName != null && actualType != ArrayList.class && (knownType == null || knownType != actualType)) {
                        this.writeObjectStart(actualType, (Class)knownType);
                        this.writeArrayStart("items");
                        for (final Object item : (Collection)value) {
                            this.writeValue(item, elementType, null);
                        }
                        this.writeArrayEnd();
                        this.writeObjectEnd();
                    }
                    else {
                        this.writeArrayStart();
                        for (final Object item : (Collection)value) {
                            this.writeValue(item, elementType, null);
                        }
                        this.writeArrayEnd();
                    }
                    return;
                }
                if (actualType.isArray()) {
                    if (elementType == null) {
                        elementType = actualType.getComponentType();
                    }
                    final int length = ArrayReflection.getLength(value);
                    this.writeArrayStart();
                    for (int i = 0; i < length; ++i) {
                        this.writeValue(ArrayReflection.get(value, i), elementType, null);
                    }
                    this.writeArrayEnd();
                    return;
                }
                if (value instanceof ObjectMap) {
                    if (knownType == null) {
                        knownType = ObjectMap.class;
                    }
                    this.writeObjectStart(actualType, (Class)knownType);
                    for (final ObjectMap.Entry entry : ((ObjectMap)value).entries()) {
                        this.writer.name(this.convertToString(entry.key));
                        this.writeValue(entry.value, elementType, null);
                    }
                    this.writeObjectEnd();
                    return;
                }
                if (value instanceof ArrayMap) {
                    if (knownType == null) {
                        knownType = ArrayMap.class;
                    }
                    this.writeObjectStart(actualType, (Class)knownType);
                    final ArrayMap map = (ArrayMap)value;
                    for (int i = 0, n = map.size; i < n; ++i) {
                        this.writer.name(this.convertToString(map.keys[i]));
                        this.writeValue(map.values[i], elementType, null);
                    }
                    this.writeObjectEnd();
                    return;
                }
                if (value instanceof Map) {
                    if (knownType == null) {
                        knownType = HashMap.class;
                    }
                    this.writeObjectStart(actualType, (Class)knownType);
                    for (final Map.Entry entry2 : ((Map)value).entrySet()) {
                        this.writer.name(this.convertToString(entry2.getKey()));
                        this.writeValue(entry2.getValue(), elementType, null);
                    }
                    this.writeObjectEnd();
                    return;
                }
                if (ClassReflection.isAssignableFrom(Enum.class, actualType)) {
                    if (this.typeName != null && (knownType == null || knownType != actualType)) {
                        if (actualType.getEnumConstants() == null) {
                            actualType = actualType.getSuperclass();
                        }
                        this.writeObjectStart(actualType, null);
                        this.writer.name("value");
                        this.writer.value(this.convertToString((Enum)value));
                        this.writeObjectEnd();
                    }
                    else {
                        this.writer.value(this.convertToString((Enum)value));
                    }
                    return;
                }
                this.writeObjectStart(actualType, (Class)knownType);
                this.writeFields(value);
                this.writeObjectEnd();
            }
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }
    
    public void writeObjectStart(final String name) {
        try {
            this.writer.name(name);
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
        this.writeObjectStart();
    }
    
    public void writeObjectStart(final String name, final Class actualType, final Class knownType) {
        try {
            this.writer.name(name);
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
        this.writeObjectStart(actualType, knownType);
    }
    
    public void writeObjectStart() {
        try {
            this.writer.object();
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }
    
    public void writeObjectStart(final Class actualType, final Class knownType) {
        try {
            this.writer.object();
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
        if (knownType == null || knownType != actualType) {
            this.writeType(actualType);
        }
    }
    
    public void writeObjectEnd() {
        try {
            this.writer.pop();
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }
    
    public void writeArrayStart(final String name) {
        try {
            this.writer.name(name);
            this.writer.array();
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }
    
    public void writeArrayStart() {
        try {
            this.writer.array();
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }
    
    public void writeArrayEnd() {
        try {
            this.writer.pop();
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }
    
    public void writeType(final Class type) {
        if (this.typeName == null) {
            return;
        }
        String className = this.getTag(type);
        if (className == null) {
            className = type.getName();
        }
        try {
            this.writer.set(this.typeName, className);
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }
    
    public <T> T fromJson(final Class<T> type, final Reader reader) {
        return this.readValue(type, null, new JsonReader().parse(reader));
    }
    
    public <T> T fromJson(final Class<T> type, final Class elementType, final Reader reader) {
        return this.readValue(type, elementType, new JsonReader().parse(reader));
    }
    
    public <T> T fromJson(final Class<T> type, final InputStream input) {
        return this.readValue(type, null, new JsonReader().parse(input));
    }
    
    public <T> T fromJson(final Class<T> type, final Class elementType, final InputStream input) {
        return this.readValue(type, elementType, new JsonReader().parse(input));
    }
    
    public <T> T fromJson(final Class<T> type, final FileHandle file) {
        try {
            return this.readValue(type, null, new JsonReader().parse(file));
        }
        catch (Exception ex) {
            throw new SerializationException("Error reading file: " + file, ex);
        }
    }
    
    public <T> T fromJson(final Class<T> type, final Class elementType, final FileHandle file) {
        try {
            return this.readValue(type, elementType, new JsonReader().parse(file));
        }
        catch (Exception ex) {
            throw new SerializationException("Error reading file: " + file, ex);
        }
    }
    
    public <T> T fromJson(final Class<T> type, final char[] data, final int offset, final int length) {
        return this.readValue(type, null, new JsonReader().parse(data, offset, length));
    }
    
    public <T> T fromJson(final Class<T> type, final Class elementType, final char[] data, final int offset, final int length) {
        return this.readValue(type, elementType, new JsonReader().parse(data, offset, length));
    }
    
    public <T> T fromJson(final Class<T> type, final String json) {
        return this.readValue(type, null, new JsonReader().parse(json));
    }
    
    public <T> T fromJson(final Class<T> type, final Class elementType, final String json) {
        return this.readValue(type, elementType, new JsonReader().parse(json));
    }
    
    public void readField(final Object object, final String name, final JsonValue jsonData) {
        this.readField(object, name, name, null, jsonData);
    }
    
    public void readField(final Object object, final String name, final Class elementType, final JsonValue jsonData) {
        this.readField(object, name, name, elementType, jsonData);
    }
    
    public void readField(final Object object, final String fieldName, final String jsonName, final JsonValue jsonData) {
        this.readField(object, fieldName, jsonName, null, jsonData);
    }
    
    public void readField(final Object object, final String fieldName, final String jsonName, Class elementType, final JsonValue jsonMap) {
        final Class type = object.getClass();
        final ObjectMap<String, FieldMetadata> fields = this.getFields(type);
        final FieldMetadata metadata = fields.get(fieldName);
        if (metadata == null) {
            throw new SerializationException("Field not found: " + fieldName + " (" + type.getName() + ")");
        }
        final Field field = metadata.field;
        if (elementType == null) {
            elementType = metadata.elementType;
        }
        this.readField(object, field, jsonName, elementType, jsonMap);
    }
    
    public void readField(final Object object, final Field field, final String jsonName, final Class elementType, final JsonValue jsonMap) {
        final JsonValue jsonValue = jsonMap.get(jsonName);
        if (jsonValue == null) {
            return;
        }
        try {
            field.set(object, this.readValue((Class<Object>)field.getType(), elementType, jsonValue));
        }
        catch (ReflectionException ex) {
            throw new SerializationException("Error accessing field: " + field.getName() + " (" + field.getDeclaringClass().getName() + ")", ex);
        }
        catch (SerializationException ex2) {
            ex2.addTrace(String.valueOf(field.getName()) + " (" + field.getDeclaringClass().getName() + ")");
            throw ex2;
        }
        catch (RuntimeException runtimeEx) {
            final SerializationException ex3 = new SerializationException(runtimeEx);
            ex3.addTrace(jsonValue.trace());
            ex3.addTrace(String.valueOf(field.getName()) + " (" + field.getDeclaringClass().getName() + ")");
            throw ex3;
        }
    }
    
    public void readFields(final Object object, final JsonValue jsonMap) {
        final Class type = object.getClass();
        final ObjectMap<String, FieldMetadata> fields = this.getFields(type);
        for (JsonValue child = jsonMap.child; child != null; child = child.next) {
            final FieldMetadata metadata = fields.get(child.name().replace(" ", "_"));
            if (metadata == null) {
                if (!child.name.equals(this.typeName)) {
                    if (!this.ignoreUnknownFields) {
                        if (!this.ignoreUnknownField(type, child.name)) {
                            final SerializationException ex = new SerializationException("Field not found: " + child.name + " (" + type.getName() + ")");
                            ex.addTrace(child.trace());
                            throw ex;
                        }
                    }
                }
            }
            else {
                final Field field = metadata.field;
                try {
                    field.set(object, this.readValue((Class<Object>)field.getType(), metadata.elementType, child));
                }
                catch (ReflectionException ex2) {
                    throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex2);
                }
                catch (SerializationException ex3) {
                    ex3.addTrace(String.valueOf(field.getName()) + " (" + type.getName() + ")");
                    throw ex3;
                }
                catch (RuntimeException runtimeEx) {
                    final SerializationException ex4 = new SerializationException(runtimeEx);
                    ex4.addTrace(child.trace());
                    ex4.addTrace(String.valueOf(field.getName()) + " (" + type.getName() + ")");
                    throw ex4;
                }
            }
        }
    }
    
    protected boolean ignoreUnknownField(final Class type, final String fieldName) {
        return false;
    }
    
    public <T> T readValue(final String name, final Class<T> type, final JsonValue jsonMap) {
        return this.readValue(type, null, jsonMap.get(name));
    }
    
    public <T> T readValue(final String name, final Class<T> type, final T defaultValue, final JsonValue jsonMap) {
        final JsonValue jsonValue = jsonMap.get(name);
        if (jsonValue == null) {
            return defaultValue;
        }
        return this.readValue(type, null, jsonValue);
    }
    
    public <T> T readValue(final String name, final Class<T> type, final Class elementType, final JsonValue jsonMap) {
        return this.readValue(type, elementType, jsonMap.get(name));
    }
    
    public <T> T readValue(final String name, final Class<T> type, final Class elementType, final T defaultValue, final JsonValue jsonMap) {
        final JsonValue jsonValue = jsonMap.get(name);
        return this.readValue(type, elementType, defaultValue, jsonValue);
    }
    
    public <T> T readValue(final Class<T> type, final Class elementType, final T defaultValue, final JsonValue jsonData) {
        if (jsonData == null) {
            return defaultValue;
        }
        return this.readValue(type, elementType, jsonData);
    }
    
    public <T> T readValue(final Class<T> type, final JsonValue jsonData) {
        return this.readValue(type, null, jsonData);
    }
    
    public <T> T readValue(Class<T> type, Class elementType, JsonValue jsonData) {
        if (jsonData == null) {
            return null;
        }
        if (jsonData.isObject()) {
            final String className = (this.typeName == null) ? null : jsonData.getString(this.typeName, null);
            if (className != null) {
                type = (Class<T>)this.getClass(className);
                if (type == null) {
                    try {
                        type = (Class<T>)ClassReflection.forName(className);
                    }
                    catch (ReflectionException ex) {
                        throw new SerializationException(ex);
                    }
                }
            }
            if (type == null) {
                if (this.defaultSerializer != null) {
                    return this.defaultSerializer.read(this, jsonData, type);
                }
                return (T)jsonData;
            }
            else if (this.typeName != null && ClassReflection.isAssignableFrom(Collection.class, type)) {
                jsonData = jsonData.get("items");
                if (jsonData == null) {
                    throw new SerializationException("Unable to convert object to collection: " + jsonData + " (" + type.getName() + ")");
                }
            }
            else {
                final Serializer serializer = this.classToSerializer.get(type);
                if (serializer != null) {
                    return serializer.read(this, jsonData, type);
                }
                if (type == String.class || type == Integer.class || type == Boolean.class || type == Float.class || type == Long.class || type == Double.class || type == Short.class || type == Byte.class || type == Character.class || ClassReflection.isAssignableFrom(Enum.class, type)) {
                    return this.readValue("value", type, jsonData);
                }
                final Object object = this.newInstance(type);
                if (object instanceof Serializable) {
                    ((Serializable)object).read(this, jsonData);
                    return (T)object;
                }
                if (object instanceof ObjectMap) {
                    final ObjectMap result = (ObjectMap)object;
                    for (JsonValue child = jsonData.child; child != null; child = child.next) {
                        result.put(child.name, this.readValue((Class<Object>)elementType, null, child));
                    }
                    return (T)result;
                }
                if (object instanceof ArrayMap) {
                    final ArrayMap result2 = (ArrayMap)object;
                    for (JsonValue child = jsonData.child; child != null; child = child.next) {
                        result2.put(child.name, this.readValue((Class<Object>)elementType, null, child));
                    }
                    return (T)result2;
                }
                if (object instanceof Map) {
                    final Map result3 = (Map)object;
                    for (JsonValue child = jsonData.child; child != null; child = child.next) {
                        if (!child.name.equals(this.typeName)) {
                            result3.put(child.name, this.readValue((Class<Object>)elementType, null, child));
                        }
                    }
                    return (T)result3;
                }
                this.readFields(object, jsonData);
                return (T)object;
            }
        }
        if (type != null) {
            final Serializer serializer2 = this.classToSerializer.get(type);
            if (serializer2 != null) {
                return serializer2.read(this, jsonData, type);
            }
            if (ClassReflection.isAssignableFrom(Serializable.class, type)) {
                final Object object2 = this.newInstance(type);
                ((Serializable)object2).read(this, jsonData);
                return (T)object2;
            }
        }
        if (jsonData.isArray()) {
            if (type == null || type == Object.class) {
                type = (Class<T>)Array.class;
            }
            if (ClassReflection.isAssignableFrom(Array.class, type)) {
                final Array result4 = (Array)((type == Array.class) ? new Array() : this.newInstance(type));
                for (JsonValue child2 = jsonData.child; child2 != null; child2 = child2.next) {
                    result4.add(this.readValue((Class<Object>)elementType, null, child2));
                }
                return (T)result4;
            }
            if (ClassReflection.isAssignableFrom(Queue.class, type)) {
                final Queue result5 = (Queue)((type == Queue.class) ? new Queue() : this.newInstance(type));
                for (JsonValue child2 = jsonData.child; child2 != null; child2 = child2.next) {
                    result5.addLast(this.readValue((Class<Object>)elementType, null, child2));
                }
                return (T)result5;
            }
            if (ClassReflection.isAssignableFrom(Collection.class, type)) {
                final Collection result6 = type.isInterface() ? new ArrayList() : ((Collection)this.newInstance(type));
                for (JsonValue child2 = jsonData.child; child2 != null; child2 = child2.next) {
                    result6.add(this.readValue((Class<Object>)elementType, null, child2));
                }
                return (T)result6;
            }
            if (type.isArray()) {
                final Class componentType = type.getComponentType();
                if (elementType == null) {
                    elementType = componentType;
                }
                final Object result7 = ArrayReflection.newInstance(componentType, jsonData.size);
                int i = 0;
                for (JsonValue child3 = jsonData.child; child3 != null; child3 = child3.next) {
                    ArrayReflection.set(result7, i++, this.readValue((Class<Object>)elementType, null, child3));
                }
                return (T)result7;
            }
            throw new SerializationException("Unable to convert value to required type: " + jsonData + " (" + type.getName() + ")");
        }
        else {
            if (jsonData.isNumber()) {
                try {
                    if (type == null || type == Float.TYPE || type == Float.class) {
                        return (T)Float.valueOf(jsonData.asFloat());
                    }
                    if (type == Integer.TYPE || type == Integer.class) {
                        return (T)Integer.valueOf(jsonData.asInt());
                    }
                    if (type == Long.TYPE || type == Long.class) {
                        return (T)Long.valueOf(jsonData.asLong());
                    }
                    if (type == Double.TYPE || type == Double.class) {
                        return (T)Double.valueOf(jsonData.asDouble());
                    }
                    if (type == String.class) {
                        return (T)jsonData.asString();
                    }
                    if (type == Short.TYPE || type == Short.class) {
                        return (T)Short.valueOf(jsonData.asShort());
                    }
                    if (type == Byte.TYPE || type == Byte.class) {
                        return (T)Byte.valueOf(jsonData.asByte());
                    }
                }
                catch (NumberFormatException ex2) {}
                jsonData = new JsonValue(jsonData.asString());
            }
            if (jsonData.isBoolean()) {
                try {
                    if (type == null || type == Boolean.TYPE || type == Boolean.class) {
                        return (T)Boolean.valueOf(jsonData.asBoolean());
                    }
                }
                catch (NumberFormatException ex3) {}
                jsonData = new JsonValue(jsonData.asString());
            }
            if (!jsonData.isString()) {
                return null;
            }
            final String string = jsonData.asString();
            if (type == null || type == String.class) {
                return (T)string;
            }
            try {
                if (type == Integer.TYPE || type == Integer.class) {
                    return (T)Integer.valueOf(string);
                }
                if (type == Float.TYPE || type == Float.class) {
                    return (T)Float.valueOf(string);
                }
                if (type == Long.TYPE || type == Long.class) {
                    return (T)Long.valueOf(string);
                }
                if (type == Double.TYPE || type == Double.class) {
                    return (T)Double.valueOf(string);
                }
                if (type == Short.TYPE || type == Short.class) {
                    return (T)Short.valueOf(string);
                }
                if (type == Byte.TYPE || type == Byte.class) {
                    return (T)Byte.valueOf(string);
                }
            }
            catch (NumberFormatException ex4) {}
            if (type == Boolean.TYPE || type == Boolean.class) {
                return (T)Boolean.valueOf(string);
            }
            if (type == Character.TYPE || type == Character.class) {
                return (T)Character.valueOf(string.charAt(0));
            }
            if (ClassReflection.isAssignableFrom(Enum.class, type)) {
                final Enum[] constants = (Enum[])type.getEnumConstants();
                for (int i = 0, n = constants.length; i < n; ++i) {
                    final Enum e = constants[i];
                    if (string.equals(this.convertToString(e))) {
                        return (T)e;
                    }
                }
            }
            if (type == CharSequence.class) {
                return (T)string;
            }
            throw new SerializationException("Unable to convert value to required type: " + jsonData + " (" + type.getName() + ")");
        }
    }
    
    public void copyFields(final Object from, final Object to) {
        final ObjectMap<String, FieldMetadata> toFields = this.getFields(from.getClass());
        for (final ObjectMap.Entry<String, FieldMetadata> entry : this.getFields(from.getClass())) {
            final FieldMetadata toField = toFields.get(entry.key);
            final Field fromField = entry.value.field;
            if (toField == null) {
                throw new SerializationException("To object is missing field" + entry.key);
            }
            try {
                toField.field.set(to, fromField.get(from));
            }
            catch (ReflectionException ex) {
                throw new SerializationException("Error copying field: " + fromField.getName(), ex);
            }
        }
    }
    
    private String convertToString(final Enum e) {
        return this.enumNames ? e.name() : e.toString();
    }
    
    private String convertToString(final Object object) {
        if (object instanceof Enum) {
            return this.convertToString((Enum)object);
        }
        if (object instanceof Class) {
            return ((Class)object).getName();
        }
        return String.valueOf(object);
    }
    
    protected Object newInstance(Class type) {
        try {
            return ClassReflection.newInstance(type);
        }
        catch (Exception ex) {
            try {
                final Constructor constructor = ClassReflection.getDeclaredConstructor(type, new Class[0]);
                constructor.setAccessible(true);
                return constructor.newInstance(new Object[0]);
            }
            catch (SecurityException ex2) {}
            catch (ReflectionException ignored) {
                if (ClassReflection.isAssignableFrom(Enum.class, type)) {
                    if (type.getEnumConstants() == null) {
                        type = type.getSuperclass();
                    }
                    return type.getEnumConstants()[0];
                }
                if (type.isArray()) {
                    throw new SerializationException("Encountered JSON object when expected array of type: " + type.getName(), ex);
                }
                if (ClassReflection.isMemberClass(type) && !ClassReflection.isStaticClass(type)) {
                    throw new SerializationException("Class cannot be created (non-static member class): " + type.getName(), ex);
                }
                throw new SerializationException("Class cannot be created (missing no-arg constructor): " + type.getName(), ex);
            }
            catch (Exception privateConstructorException) {
                ex = privateConstructorException;
            }
            throw new SerializationException("Error constructing instance of class: " + type.getName(), ex);
        }
    }
    
    public String prettyPrint(final Object object) {
        return this.prettyPrint(object, 0);
    }
    
    public String prettyPrint(final String json) {
        return this.prettyPrint(json, 0);
    }
    
    public String prettyPrint(final Object object, final int singleLineColumns) {
        return this.prettyPrint(this.toJson(object), singleLineColumns);
    }
    
    public String prettyPrint(final String json, final int singleLineColumns) {
        return new JsonReader().parse(json).prettyPrint(this.outputType, singleLineColumns);
    }
    
    public String prettyPrint(final Object object, final JsonValue.PrettyPrintSettings settings) {
        return this.prettyPrint(this.toJson(object), settings);
    }
    
    public String prettyPrint(final String json, final JsonValue.PrettyPrintSettings settings) {
        return new JsonReader().parse(json).prettyPrint(settings);
    }
    
    private static class FieldMetadata
    {
        final Field field;
        Class elementType;
        
        public FieldMetadata(final Field field) {
            this.field = field;
            final int index = (ClassReflection.isAssignableFrom(ObjectMap.class, field.getType()) || ClassReflection.isAssignableFrom(Map.class, field.getType())) ? 1 : 0;
            this.elementType = field.getElementType(index);
        }
    }
    
    public abstract static class ReadOnlySerializer<T> implements Serializer<T>
    {
        @Override
        public void write(final Json json, final T object, final Class knownType) {
        }
        
        @Override
        public abstract T read(final Json p0, final JsonValue p1, final Class p2);
    }
    
    public interface Serializer<T>
    {
        void write(final Json p0, final T p1, final Class p2);
        
        T read(final Json p0, final JsonValue p1, final Class p2);
    }
    
    public interface Serializable
    {
        void write(final Json p0);
        
        void read(final Json p0, final JsonValue p1);
    }
}
