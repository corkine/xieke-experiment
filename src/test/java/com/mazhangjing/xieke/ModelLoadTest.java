package com.mazhangjing.xieke;

import com.mazhangjing.xieke.model.Config;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ModelLoadTest {
    @Test public void t1() throws FileNotFoundException {
        Config load = new Yaml().loadAs(new FileReader("config.yml"),
                Config.class);
        System.out.println("load = " + load);
    }
}