package ru.enis.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.enis.config.annotations.Final;
import ru.enis.config.annotations.Ignore;

public class WrapperTest {

    @Test
    void testConfig() throws Exception {
        Path configFile = Files.createTempFile("config", ".toml");
        this.processTempFile(configFile);

        Config config = new Config();
        config.reload(configFile.toFile());

        Assertions.assertEquals("\uD83D\uDD25 final value", config.finalField);
        Assertions.assertEquals("regular \"value\"", config.regularField);
        Assertions.assertEquals((float) Math.PI, config.regularFloatField);
        Assertions.assertEquals(Math.E, config.regularDoubleField);
        Assertions.assertEquals(RegularEnum.TRUE, config.enumField);
        Assertions.assertEquals(3, config.regularList.size());

    }

    private void processTempFile(Path path) {
        File file = path.toFile();
        if (!file.delete()) { // We don't need an empty temp file, we need only path.
            throw new IllegalStateException("File must be deleted.");
        }
        file.deleteOnExit();
    }

    public static class Config extends TomlConfig {
        @Ignore
        public static final Config IMP = new Config();

        @Final
        public String finalField = "\uD83D\uDD25 final value";

        public String regularField = "regular \"value\"";

        public float regularFloatField = (float) Math.PI;
        public double regularDoubleField = Math.E;

        public RegularEnum enumField = RegularEnum.TRUE;

        public List<String> regularList = Arrays.asList("123", "123", "456");
    }

    private enum RegularEnum {

        ENUM_VALUE_1,
        TRUE,
        FALSE,
    }


}
