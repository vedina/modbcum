package net.idea.modbcum.i.test;

import java.io.StringWriter;

import org.junit.Test;

import net.idea.modbcum.i.bucket.Bucket;

public class BucketTest {
	@Test
	public void test() throws Exception {
		Bucket item = new Bucket();
		item.setHeader(new String[] { "key", "key1", "key2" });
		item.put("key", "val\"ue");
		item.put("key1", "value1,value2");
		item.put("key2", 1);
		StringWriter w = new StringWriter();
		item.toCSV(w, ",");
		System.out.println(w);
	}
}
