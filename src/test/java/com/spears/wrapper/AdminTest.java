/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spears.wrapper;

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
