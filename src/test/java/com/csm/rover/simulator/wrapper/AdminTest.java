package com.csm.rover.simulator.wrapper;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class AdminTest {

    private File file;

    @Before
    public void clearAdmin(){
        Inject.field("singleton_instance").of(Admin.getInstance()).with(Optional.empty());
        file = new File("temp");
    }

    @After
    public void cleanFile(){
        if (file.exists()){
            file.delete();
        }
    }

    @Test
    public void testSingleton(){
        Admin admin1 = Admin.getInstance();
        Admin admin2 = Admin.getInstance();
        assert admin1 == admin2;
    }

    @Test
    public void testFileType() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getFileType = Admin.class.getDeclaredMethod("getFileType", File.class);
        getFileType.setAccessible(true);
        File test = new File("test.file.txt");
        Assert.assertEquals("txt", getFileType.invoke(Admin.getInstance(), test));
    }

    @Test
    public void testVerifyFileGood() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        Method verifyFile = Admin.class.getDeclaredMethod("verifyFile", File.class);
        verifyFile.setAccessible(true);
        file = new File("temp_test.cfg");
        file.createNewFile();

        assert (Boolean)verifyFile.invoke(Admin.getInstance(), file);
    }

    @Test
    public void testVerifyFileBad1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        Method verifyFile = Admin.class.getDeclaredMethod("verifyFile", File.class);
        verifyFile.setAccessible(true);
        file = new File("temp_test.cfg");

        assert !(Boolean)verifyFile.invoke(Admin.getInstance(), file);
    }

    @Test
    public void testVerifyFileBad2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        Method verifyFile = Admin.class.getDeclaredMethod("verifyFile", File.class);
        verifyFile.setAccessible(true);
        file = new File("temp_test.not");
        file.createNewFile();

        assert !(Boolean)verifyFile.invoke(Admin.getInstance(), file);
    }



}
