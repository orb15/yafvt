package net.orb15.yafvt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class ConsoleBean implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ConsoleBean.class);

    @Override
    public void run(String... strings) throws Exception {

        LOG.debug("Command line application starting...");

    }
}
