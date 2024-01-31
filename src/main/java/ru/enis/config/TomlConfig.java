package ru.enis.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.io.File;
import java.lang.reflect.Field;
import ru.enis.config.annotations.Comment;
import ru.enis.config.annotations.Final;
import ru.enis.config.annotations.Ignore;
import ru.enis.config.annotations.Optional;
import ru.enis.config.annotations.Key;

public abstract class TomlConfig {

    public void reload(File file) throws Exception {
        Field[] fields = this.getClass().getFields();

        for (Field field :
            fields) {

            // Check for @Ignored
            if (field.getAnnotation(Ignore.class) == null) {
                CommentedFileConfig cfg = CommentedFileConfig.builder(file).build();
                cfg.load();

                String name;
                if (field.getAnnotation(Key.class) != null) {
                    name = field.getAnnotation(Key.class).value();
                } else {
                    name = field.getName();
                }

                Object o = cfg.get(name);
                // Если в конфиге поля нет или оно пустое
                if (o == null) {
                    // Если поле обязательное
                    if (field.getAnnotation(Optional.class) == null) {
                        cfg.set(name, field.get(this));
                        if (field.getAnnotation(Comment.class) != null) {
                            cfg.setComment(name, field.getAnnotation(Comment.class).value());
                        }
                    }
                }
                // Если в конфиге поле есть
                else {
                    if (field.getAnnotation(Final.class) != null) {
                        field.set(this, field.get(this));
                        cfg.set(name, field.get(this));
                    } else {
                        field.set(this, o);
                    }
                }
                cfg.save();
            }
        }
    }
}