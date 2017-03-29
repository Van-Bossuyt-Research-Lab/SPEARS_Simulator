package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.objects.io.RunConfiguration;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.CancellationException;

import static org.mockito.Mockito.*;

public class StartUpTest {

    private Admin adminMock;

    @Before
    public void reset(){
        adminMock = mock(Admin.class);
        Inject.field("singleton_instance").of(Admin.getInstance()).with(Optional.of(adminMock));
    }

    @AfterClass
    public static void clearAdmin(){
        Inject.field("singleton_instance").of(Admin.getInstance()).with(Optional.empty());
    }

    @Test(timeout = 5000)
    public void testStart() throws Exception {
        when(adminMock.useSave()).thenReturn(true);
        when(adminMock.getRunConfigFile()).thenReturn(null);

        Admin.main(new String[0]);
        verify(adminMock).checkRegistries();
        verify(adminMock).checkFolders();
    }

    @Test(timeout = 5000)
    public void testUseSave() throws Exception {
        when(adminMock.useSave()).thenReturn(true);
        RunConfiguration cfg = mock(RunConfiguration.class);
        when(adminMock.getRunConfigFile()).thenReturn(cfg);

        Admin.main(new String[0]);
        verify(adminMock).startWithGUI(cfg);
    }

    @Test(timeout = 5000)
    public void testDontUseSave(){
        when(adminMock.useSave()).thenReturn(false);

        Admin.main(new String[0]);
        verify(adminMock).promptStartup();
    }

    @Test(timeout = 5000)
    public void testUseSaveCanceled() throws Exception {
        when(adminMock.useSave()).thenReturn(true);
        when(adminMock.getRunConfigFile()).thenThrow(CancellationException.class);

        Admin.main(new String[0]);
        verify(adminMock).promptStartup();
    }

    @Test(timeout = 5000)
    public void testUseSaveFailed() throws Exception {
        when(adminMock.useSave()).thenReturn(true);
        when(adminMock.getRunConfigFile()).thenThrow(new Exception("This is a test"));

        Admin.main(new String[0]);
        verify(adminMock).shutDownSimulator(2);
    }

    @Test(timeout = 5000)
    public void testCommandStart() throws Exception {
        File testCfg = new File("test.cfg");
        when(adminMock.verifyFile(testCfg)).thenReturn(true);
        RunConfiguration cfg = mock(RunConfiguration.class);
        when(adminMock.getRunConfigFile(testCfg)).thenReturn(cfg);

        Admin.main(new String[]{testCfg.getName()});
        verify(adminMock).beginSimulation(cfg);
    }

    @Test(timeout = 5000)
    public void testCommandFailed() throws Exception {
        File testCfg = new File("test.cfg");
        when(adminMock.verifyFile(testCfg)).thenReturn(true);
        RunConfiguration cfg = mock(RunConfiguration.class);
        when(adminMock.getRunConfigFile(testCfg)).thenThrow(Exception.class);

        Admin.main(new String[]{testCfg.getName()});
        verify(adminMock).shutDownSimulator(2);
    }

    @Test(timeout = 5000)
    public void testCommandBadFile(){
        File testCfg = new File("test.cfg");
        when(adminMock.verifyFile(testCfg)).thenReturn(false);

        Admin.main(new String[]{testCfg.getName()});
        verify(adminMock).shutDownSimulator(3);
    }

}
