package com.manparvesh.barfee;

import com.manparvesh.barfee.ui.MainUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author manparvesh
 */
@SpringBootApplication
@ComponentScan
public class BarfeeApplication {
    public static void main(String[] args) {
        // TODO start splash screen using SWT

        // starting spring boot application
        ConfigurableApplicationContext applicationContext = SpringApplication.run(BarfeeApplication.class, args);

        // show main GUI using SWT when application loads
        // if bean not present for UI, abort
        if (applicationContext.containsBeanDefinition("ui")) {
            applicationContext.getBean(MainUI.class).open();
        }
    }
}
