import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;
public class Resource {
    public Resource(){}
    public String fetchConfigProperties(String prop) throws IOException {
        FileInputStream loadProperties = new FileInputStream(System.getProperty("user.dir")+"//Config//config.properties");
        Properties globalProperties = new Properties();
        globalProperties.load(loadProperties);
        String propertyValue = globalProperties.getProperty(prop);
        return propertyValue;
    }

}
