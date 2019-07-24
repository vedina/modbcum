package net.idea.modbcum.i.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigProperties {
	
	protected String AMBIT_CONFIG_OVERRIDE_VAR = "AMBIT_CONFIG_OVERRIDE_VAR";
	public String getConfigOverrideVar() {
		return AMBIT_CONFIG_OVERRIDE_VAR;
	}

	public void setConfigOverrideVar(String configOverrideVar) {
		this.AMBIT_CONFIG_OVERRIDE_VAR = configOverrideVar;
	}

	protected ConcurrentHashMap<String, Properties> properties = new ConcurrentHashMap<String, Properties>();
	protected File overridePath = null;

	public File getConfigOverrideDir(String override_var) {
		try {
			String path = System.getenv(override_var);
			if (path != null) {
				File dir = new File(path);
				if (dir.exists() && dir.isDirectory())
					return dir;
			}
			return null;
		} catch (Exception x) {
			return null;
		}

	}

	public ConfigProperties() {
		this(null);
	}


	public ConfigProperties(File overridePath) {
		this.overridePath = overridePath;
	}

	public synchronized Properties getProperties(String config) {
		try {
			Properties p = properties.get(config);
			if (p == null) {
				// Load the internal config as defaults
				Properties defaults = new Properties();
				try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(config)) {
					defaults.load(in);
				}
				if (getConfigOverrideVar() !=null && defaults.get(getConfigOverrideVar()) != null) {
					File dir = getConfigOverrideDir(defaults.get(getConfigOverrideVar()).toString());
					if (dir != null && !"".equals(dir.getAbsolutePath()))
						overridePath = dir;
				}
				if (overridePath != null) {
					p = new Properties(defaults);
					String[] segments = config.split("/");

					File file = new File(overridePath, segments[segments.length - 1]);
					if (file.exists())
						try (InputStream in = new FileInputStream(file)) {
							p.load(in);
							//no overriding of the var
							p.remove(AMBIT_CONFIG_OVERRIDE_VAR);
						}
				} else
					p = defaults;
				properties.put(config, p);

			}
			return p;

		} catch (Exception x) {
			return null;
		}
	}

	protected synchronized String getProperty(String name, String config) {
		try {
			Properties p = getProperties(config);
			return p.getProperty(name);
		} catch (Exception x) {
			return null;
		}
	}

	public synchronized String getPropertyWithDefault(String name, String config, String defaultValue) {
		try {
			String value = getProperty(name, config);
			if (value == null)
				return defaultValue;
			else if (value != null && value.startsWith("${"))
				return defaultValue;
			else
				return value;
		} catch (Exception x) {
			return defaultValue;
		}
	}

	public synchronized Long getPropertyWithDefaultLong(String name, String config, Long defaultValue) {
		try {
			String value = getProperty(name, config);
			if (value == null)
				return defaultValue;
			else if (value != null && value.startsWith("${"))
				return defaultValue;
			else
				return Long.parseLong(value);
		} catch (Exception x) {
			return defaultValue;
		}
	}

	public synchronized boolean getBooleanPropertyWithDefault(String name, String config, boolean defaultValue) {
		try {
			String attach = getProperty(name, config);
			if (attach != null && attach.startsWith("${"))
				return defaultValue;
			return attach == null ? defaultValue : Boolean.parseBoolean(attach);
		} catch (Exception x) {
			return defaultValue;
		}
	}
}
