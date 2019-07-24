package net.idea.modbcum.i.config;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

import junit.framework.Assert;

public class ConfigPropertiesTest {
	protected static final String ambitProperties = "ambit2/rest/config/ambit2.pref";
	protected static final String aaProperties = "ambit2/rest/config/config.properties";
	protected static final String overrideProperties = "ambit2/rest/config/override";
	protected static final String key4test = "custom.title";

	@Test
	public void test() throws Exception {
		ConfigProperties p = new ConfigProperties(null);
		Assert.assertEquals("TEST", p.getPropertyWithDefault(key4test, ambitProperties, "ERROR"));
	}

	@Test
	public void testOverride() throws Exception {
		String overridePath = this.getClass().getClassLoader().getResource(overrideProperties).getFile();
		Assert.assertNotNull(overridePath);
		ConfigProperties p = new ConfigProperties(new File(overridePath));
		Assert.assertEquals("TEST123", p.getPropertyWithDefault(key4test, ambitProperties, "ERROR"));
	}

	@Test
	public void testOverrideBadPath() throws Exception {
		String overridePath = "";
		Assert.assertNotNull(overridePath);
		ConfigProperties p = new ConfigProperties(new File(overridePath));
		Assert.assertEquals("TEST", p.getPropertyWithDefault(key4test, ambitProperties, "ERROR"));
	}

	@Test
	public void testPropertiesDefault() {
		Properties defaults = new Properties();
		defaults.put("test", "defaults");
		Assert.assertEquals("defaults", defaults.get("test"));
		Properties p = new Properties(defaults);
		Assert.assertEquals("defaults", p.getProperty("test"));
		Assert.assertNull(p.get("test"));
	}

	@Test
	public void testOverrideFileMissing() throws Exception {
		String overridePath = this.getClass().getClassLoader().getResource(overrideProperties).getFile();
		Assert.assertNotNull(overridePath);
		ConfigProperties p = new ConfigProperties(new File(overridePath));
		Assert.assertEquals("XXX", p.getPropertyWithDefault(key4test, aaProperties, "ERROR"));
	}

}
