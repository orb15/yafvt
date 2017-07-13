package net.orb15.yafvt.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

@Component
public class PropertyFileLoader {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyFileLoader.class);

    private static final String CHARACTER_FILE_PREFIX = "characters/char.";
    private static final String ARENA_FILE_PREFIX = "arenas/arena.";


    public enum PropertyFileType {

        CHARACTER,
        ARENA;

    }

    public Optional<Properties> load(String propFileName, PropertyFileType type) {

        String prefix;
        Properties props = new Properties();
        InputStream input = null;

        switch(type) {

            case CHARACTER:
                prefix = CHARACTER_FILE_PREFIX;
                break;

            case ARENA:
                prefix = ARENA_FILE_PREFIX;
                break;

            default:
                throw new YafvtException("PropertyFileType: " + type + " is not supported");

        }

        try {

            input = new FileInputStream(prefix + propFileName + ".properties");
            props.load(input);

        } catch (Exception ex) {
            LOG.error("Can't find or load the properties file: {}", propFileName, ex);
            System.out.println("Unable to load file: " + propFileName);

            props = null;

        } finally {

            if (input != null) {
                try {

                    input.close();

                } catch (Exception ex) {
                    LOG.error("Can't close the properties file: {}", propFileName, ex);
                    props =  null;
                }
            }
        }

        return Optional.ofNullable(props);
    }

}
