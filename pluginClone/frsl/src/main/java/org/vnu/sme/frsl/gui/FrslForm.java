package org.vnu.sme.frsl.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.tzi.use.config.Options;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.util.CloseOnEscapeKeyListener;
import org.tzi.use.gui.util.ExtFileFilter;
import org.tzi.use.gui.util.GridBagHelper;
import org.tzi.use.main.ChangeEvent;
import org.tzi.use.main.ChangeListener;
import org.tzi.use.main.Session;
import org.vnu.sme.frsl.FrslLoader;

/**
 * FRSL-0.1
 * 
 * @author vnu-sme 
 * 
 */
@SuppressWarnings("serial")
public class FrslForm extends JDialog {
	private Session fSession;
	private MainWindow mainWindow;
	private JTextField frslFileName;
	private PrintWriter logWriter;
	private FrslLoader loader;
	

	private ChangeListener sessionChangeListener;

	public FrslForm(Session session, MainWindow parent) {
		super(parent, "Load FRSL specification");
		mainWindow = parent;
		fSession = session;
		sessionChangeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				closeDialog();
			}
		};
		fSession.addChangeListener(sessionChangeListener);

		logWriter = parent.logWriter();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// Label and text field for TGG rules
		frslFileName = new JTextField(35);
		JLabel labelTgg = new JLabel("FRSL specification:");
		labelTgg.setLabelFor(frslFileName);

		JButton btnPath3 = new JButton("Browse...");
		btnPath3.addActionListener(new ActionListener() {
			private JFileChooser fChooser;

			public void actionPerformed(ActionEvent e) {
				String path;
				if (fChooser == null) {
					path = Options.getLastDirectory().toString();
					fChooser = new JFileChooser(path);
					ExtFileFilter filter = new ExtFileFilter("uc", "FRSL specification");
					fChooser.setFileFilter(filter);
					fChooser.setDialogTitle("Open FRSL file");
				}
				int returnVal = fChooser.showOpenDialog(FrslForm.this);
				if (returnVal != JFileChooser.APPROVE_OPTION)
					return;

				path = fChooser.getCurrentDirectory().toString();
				Options.setLastDirectory(new File(path).toPath());

				frslFileName.setText(Paths.get(path, fChooser.getSelectedFile().getName()).toString());

			}
		});

		JButton btnParse = new JButton("Parse");
		btnParse.setMnemonic('P');
		btnParse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parseFRSL();
			}
		});
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});
		JButton btAgain = new JButton("Again");
		btAgain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Again();
			}
		});

		// layout content pane
		JComponent contentPane = (JComponent) getContentPane();
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		GridBagHelper gh = new GridBagHelper(contentPane);
		gh.add(frslFileName, 0, 4, 6, 1, 0.0, 0.0, GridBagConstraints.HORIZONTAL);
		gh.add(btnPath3, 7, 4, 1, 1, 0.0, 0.0, GridBagConstraints.HORIZONTAL);
		gh.add(btnParse, 0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.HORIZONTAL);
		gh.add(btAgain, 2, 6, 1, 1, 0.0, 0.0, GridBagConstraints.HORIZONTAL);
		gh.add(btnClose, 1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.HORIZONTAL);

		getRootPane().setDefaultButton(btnParse);
		pack();
		setLocationRelativeTo(parent);

		// Close dialog on escape key
		CloseOnEscapeKeyListener ekl = new CloseOnEscapeKeyListener(this);
		addKeyListener(ekl);
	}

	private void closeDialog() {
		fSession.removeChangeListener(sessionChangeListener);
		setVisible(false);
		dispose();
	}

	private void parseFRSL() {
		File f = new File(frslFileName.getText());
		if (f.exists()) {
			loader = new FrslLoader(fSession, frslFileName.getText(), logWriter, mainWindow);
			loader.run();
			closeDialog();
		} else {
			JOptionPane.showMessageDialog(mainWindow, "The frsl specification does not exist. Please try again.", "File error", JOptionPane.ERROR_MESSAGE);;
		}
	}

	private void Again () {
		
		loader.again();
	}
}
