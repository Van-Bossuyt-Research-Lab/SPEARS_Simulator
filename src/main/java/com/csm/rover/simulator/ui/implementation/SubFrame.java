package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.platforms.sub.SubObject;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@FrameMarker(name="Credits Info")
class SubFrame extends EmbeddedFrame implements HyperlinkListener {

    private static final long serialVersionUID = 8884625122423593285L;

    private JPanel content;
    private JTextPane subTxt;

    private String internal;
    private String externalHeader;
    private String externalCredits;
    private SubObject sub;

    SubFrame() {
        setTest();
        initialize();
        setSize(
                subTxt.getPreferredSize().getWidth() < 500 ? (int) subTxt.getPreferredSize().getWidth() : 500,
                subTxt.getPreferredSize().getHeight() < 700 ? (int) subTxt.getPreferredSize().getHeight() : 700
        );
    }

    private void setTest(){
        internal = String.format("test subframe format, position:" + sub.getIDTag());
        externalHeader = "Written by Robin Coleman 10/16";
        externalCredits = "";
        addCredit("Icons from IconFinder sets:\n"+linkTo("https://www.iconfinder.com/iconsets/sem_labs_icon_pack")+"\n"+linkTo("https://www.iconfinder.com/iconsets/miu"));
        addCredit("Sound effects clips from freeSFX:\n"+linkTo("http://www.freesfx.co.uk"));
    }

    private void initialize(){
        setTitle("Sub");

        content = new JPanel();
        content.setLayout(new BorderLayout(0, 0));
        setContentPane(content);

        JScrollPane scroll = new JScrollPane();
        content.add(scroll, BorderLayout.CENTER);

        subTxt = new JTextPane();
        subTxt.setContentType("text/html");
        subTxt.setEditable(false);
        subTxt.setText(String.format("<HTML>%s\n\n%s%s</HTML>", internal, externalHeader, externalCredits).replace("\n", "<br>"));
        subTxt.addHyperlinkListener(this);
        scroll.setViewportView(subTxt);
    }

    private void addCredit(String credit){
        this.externalCredits += "\n\n" + credit;
    }

    private String linkTo(String link){
        try {
            URL url = new URL(link);
            return String.format("<a href=%s>%s</a>", url.toString(), url.toString());
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("\"" + link + "\" is not a url");
        }
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                Desktop.getDesktop().browse(event.getURL().toURI());
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}