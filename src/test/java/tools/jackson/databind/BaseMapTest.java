package tools.jackson.databind;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;

import tools.jackson.core.*;

import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.ser.std.StdScalarSerializer;
import tools.jackson.databind.type.TypeFactory;

public abstract class BaseMapTest
    extends BaseTest
{
    private final static Object SINGLETON_OBJECT = new Object();
    
    /*
    /**********************************************************************
    /* Shared helper classes
    /**********************************************************************
     */

    public static class BogusSchema implements FormatSchema {
        @Override
        public String getSchemaType() {
            return "TestFormat";
        }
    }

    /**
     * Simple wrapper around boolean types, usually to test value
     * conversions or wrapping
     */
    protected static class BooleanWrapper {
        public Boolean b;

        public BooleanWrapper() { }
        public BooleanWrapper(Boolean value) { b = value; }
    }

    protected static class IntWrapper {
        public int i;

        public IntWrapper() { }
        public IntWrapper(int value) { i = value; }
    }

    protected static class LongWrapper {
        public long l;

        public LongWrapper() { }
        public LongWrapper(long value) { l = value; }
    }

    protected static class FloatWrapper {
        public float f;

        public FloatWrapper() { }
        public FloatWrapper(float value) { f = value; }
    }

    protected static class DoubleWrapper {
        public double d;

        public DoubleWrapper() { }
        public DoubleWrapper(double value) { d = value; }
    }

    /**
     * Simple wrapper around String type, usually to test value
     * conversions or wrapping
     */
    protected static class StringWrapper {
        public String str;

        public StringWrapper() { }
        public StringWrapper(String value) {
            str = value;
        }
    }

    protected static class ObjectWrapper {
        final Object object;

        public ObjectWrapper(final Object object) {
            this.object = object;
        }
        public Object getObject() { return object; }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        static ObjectWrapper jsonValue(final Object object) {
            return new ObjectWrapper(object);
        }
    }

    protected static class ListWrapper<T>
    {
        public List<T> list;

        public ListWrapper(@SuppressWarnings("unchecked") T... values) {
            list = new ArrayList<T>();
            for (T value : values) {
                list.add(value);
            }
        }
    }

    protected static class MapWrapper<K,V>
    {
        public Map<K,V> map;

        public MapWrapper() { }
        public MapWrapper(Map<K,V> m) {
            map = m;
        }
        public MapWrapper(K key, V value) {
            map = new LinkedHashMap<>();
            map.put(key, value);
        }
    }
    
    protected static class ArrayWrapper<T>
    {
        public T[] array;

        public ArrayWrapper(T[] v) {
            array = v;
        }
    }
    
    /**
     * Enumeration type with sub-classes per value.
     */
    protected enum EnumWithSubClass {
        A { @Override public void foobar() { } }
        ,B { @Override public void foobar() { } }
        ;

        public abstract void foobar();
    }

    public enum ABC { A, B, C; }

    // since 2.8
    public static class Point {
        public int x, y;

        protected Point() { } // for deser
        public Point(int x0, int y0) {
            x = x0;
            y = y0;
        }
    
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) {
                return false;
            }
            Point other = (Point) o;
            return (other.x == x) && (other.y == y);
        }

        @Override
        public String toString() {
            return String.format("[x=%d, y=%d]", x, y);
        }
    }

    /*
    /**********************************************************************
    /* Shared serializers
    /**********************************************************************
     */

    public static class UpperCasingSerializer extends StdScalarSerializer<String>
    {
        public UpperCasingSerializer() { super(String.class); }

        @Override
        public void serialize(String value, JsonGenerator gen,
                SerializerProvider provider) {
            gen.writeString(value.toUpperCase());
        }
    }

    public static class LowerCasingDeserializer extends StdScalarDeserializer<String>
    {
        public LowerCasingDeserializer() { super(String.class); }

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) {
            return p.getText().toLowerCase();
        }
    }

    /*
    /**********************************************************************
    /* Construction
    /**********************************************************************
     */
    
    protected BaseMapTest() { super(); }

    /*
    /**********************************************************************
    /* Factory methods
    /**********************************************************************
     */

    // @since 2.7
    protected TypeFactory newTypeFactory() {
        // this is a work-around; no null modifier added
        return TypeFactory.defaultInstance().withModifier(null);
    }
    
    /*
    /**********************************************************************
    /* Additional assert methods
    /**********************************************************************
     */

    protected void assertEquals(int[] exp, int[] act)
    {
        assertArrayEquals(exp, act);
    }

    protected void assertEquals(byte[] exp, byte[] act)
    {
        assertArrayEquals(exp, act);
    }

    /**
     * Helper method for verifying 3 basic cookie cutter cases;
     * identity comparison (true), and against null (false),
     * or object of different type (false)
     */
    protected void assertStandardEquals(Object o)
    {
        assertTrue(o.equals(o));
        assertFalse(o.equals(null));
        assertFalse(o.equals(SINGLETON_OBJECT));
        // just for fun, let's also call hash code...
        o.hashCode();
    }

    /*
    /**********************************************************************
    /* Helper methods, serialization
    /**********************************************************************
     */

    @SuppressWarnings("unchecked")
    protected Map<String,Object> writeAndMap(ObjectMapper m, Object value)
        throws IOException
    {
        String str = m.writeValueAsString(value);
        return (Map<String,Object>) m.readValue(str, LinkedHashMap.class);
    }

    /*
    /**********************************************************************
    /* Helper methods, deserialization
    /**********************************************************************
     */
    
    protected <T> T readAndMapFromString(String input, Class<T> cls)
        throws IOException
    {
        return readAndMapFromString(objectMapper(), input, cls);
    }

    protected <T> T readAndMapFromString(ObjectMapper m, String input, Class<T> cls) throws IOException
    {
        return (T) m.readValue("\""+input+"\"", cls);
    }

    /*
    /**********************************************************
    /* Helper methods, other
    /**********************************************************
     */

    protected TimeZone getUTCTimeZone() {
        return TimeZone.getTimeZone("GMT");
    }

    protected byte[] utf8Bytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}