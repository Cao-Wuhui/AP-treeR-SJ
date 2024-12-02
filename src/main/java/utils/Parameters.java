//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DefaultStringifier;
import org.apache.hadoop.util.GenericsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parameters {
    private static final Logger log = LoggerFactory.getLogger(Parameters.class);
    private Map<String, String> params;

    public Parameters() {
        this.params = new HashMap();
    }

    public Parameters(String serializedString) throws IOException {
        this(parseParams(serializedString));
    }

    protected Parameters(Map<String, String> params) {
        this.params = new HashMap();
        this.params = params;
    }

    public String get(String key) {
        return (String)this.params.get(key);
    }

    public String get(String key, String defaultValue) {
        String ret = (String)this.params.get(key);
        return ret == null ? defaultValue : ret;
    }

    public void set(String key, String value) {
        this.params.put(key, value);
    }

    public int getInt(String key, int defaultValue) {
        String ret = (String)this.params.get(key);
        return ret == null ? defaultValue : Integer.parseInt(ret);
    }

    public float getFloat(String key, float defaultvalue) {
        String ret = (String)this.params.get(key);
        return ret == null ? defaultvalue : Float.parseFloat(ret);
    }

    public double getDouble(String key, double defaultValue) {
        String ret = (String)this.params.get(key);
        return ret == null ? defaultValue : Double.parseDouble(ret);
    }

    public String toString() {
        Configuration conf = new Configuration();
        conf.set("io.serializations", "org.apache.hadoop.io.serializer.JavaSerialization,org.apache.hadoop.io.serializer.WritableSerialization");
        DefaultStringifier<Map<String, String>> mapStringifier = new DefaultStringifier(conf, GenericsUtil.getClass(this.params));

        try {
            return mapStringifier.toString(this.params);
        } catch (IOException var4) {
            log.info("Encountered IOException while deserializing returning empty string", var4);
            return "";
        }
    }

    public String print() {
        return this.params.toString();
    }

    public static Map<String, String> parseParams(String serializedString) throws IOException {
        Configuration conf = new Configuration();
        conf.set("io.serializations", "org.apache.hadoop.io.serializer.JavaSerialization,org.apache.hadoop.io.serializer.WritableSerialization");
        Map<String, String> params = new HashMap();
        DefaultStringifier<Map<String, String>> mapStringifier = new DefaultStringifier(conf, GenericsUtil.getClass(params));
        return (Map)mapStringifier.fromString(serializedString);
    }
}
