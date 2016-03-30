package com.csm.rover.simulator.ui.frame;

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
public class CreditsFrame extends EmbeddedFrame implements HyperlinkListener {

	private static final long serialVersionUID = 8884625122423593285L;
	
	private JPanel content;
	private JTextPane creditsTxt;
	
	private String internal;
	private String externalHeader;
	private String externalCredits;
	
	public CreditsFrame() {
		setCredits();
		initialize();
		setSize(
                creditsTxt.getPreferredSize().getWidth() < 500 ? (int) creditsTxt.getPreferredSize().getWidth() : 500,
                creditsTxt.getPreferredSize().getHeight() < 700 ? (int) creditsTxt.getPreferredSize().getHeight() : 700
        );
	}
	
	private void setCredits(){
		internal = String.format("The SPEARS Project is run by:\n%s\n\nDevelopers: %s\nProject Supervisor: %s\nFaculty Advisor: %s", "Van Bossuyt Group\nDepartment of Mechanical Engineering\nColorado School of Mines", "Zac Mimlitz, Robin Coleman", "Adam Short", "Douglas Van Bossuyt Ph.D.");
		externalHeader = "SPEARS is made possible by the support of the following open source projects:";
		externalCredits = "";
		addCredit("Icons from IconFinder sets:\n"+linkTo("https://www.iconfinder.com/iconsets/sem_labs_icon_pack")+"\n"+linkTo("https://www.iconfinder.com/iconsets/miu"));
		addCredit("Sound effects clips from freeSFX:\n"+linkTo("http://www.freesfx.co.uk"));
	}
	
	private void initialize(){
		setTitle("Credits");
		
		content = new JPanel();
		content.setLayout(new BorderLayout(0, 0));
		setContentPane(content);
		
		JScrollPane scroll = new JScrollPane();
		content.add(scroll, BorderLayout.CENTER);
		
		creditsTxt = new JTextPane();
		creditsTxt.setContentType("text/html");
		creditsTxt.setEditable(false);
		creditsTxt.setText(String.format("<HTML>%s\n\n%s%s</HTML>", internal, externalHeader, externalCredits).replace("\n", "<br>"));
		creditsTxt.addHyperlinkListener(this);
		scroll.setViewportView(creditsTxt);
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
