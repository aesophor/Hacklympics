package com.hacklympics.student.code;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import com.hacklympics.student.code.lang.Language;
import com.hacklympics.utility.CodeAreaUtils;
import com.hacklympics.utility.Utils;

import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;

public class FileTab extends Tab {
	
	private final static Language DEFAULT_LANG = Language.JAVA;
    
	private Language currentLang;
    private final List<String> patches;
    
    private final AnchorPane anchorPane;
    private final VBox vbox;
    private final CodeArea codeArea;
    private File file;
    private boolean unsaved;

    public FileTab() {
        super("Untitled.java");
        
        currentLang = DEFAULT_LANG;
        
        patches = new ArrayList<>();
        
        codeArea = new CodeArea();
        codeArea.getStyleClass().add("code-area");
        codeArea.getStylesheets().add(currentLang.getCSSFilepath());
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.multiPlainChanges()
				.successionEnds(Duration.ofMillis(100))
				.subscribe(ignore -> {
					codeArea.setStyleSpans(0, CodeAreaUtils.computeHighlighting(currentLang, codeArea.getText()));
		});
        
        
        // Add sample code to the CodeArea.
        codeArea.replaceText(0, 0, currentLang.getSampleCode());
        
        // Whenever there's a change to the CodeArea, we compute the diff,
        // create a patch and add that patch to keystrokeHistory.
        // Later on we can send these patches to the teacher's client.
        // Make sure to mark current tab as unsaved as well.
        codeArea.textProperty().addListener((observable, original, revised) -> {
        	Patch patch = DiffUtils.diff(original, revised);
        	String sp = "";
        	try {
        		sp = Utils.serialize(patch);
        		addPatch(sp);
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				markAsUnsaved();
			}
        	
        	try {
				Patch p = (Patch) Utils.deserialize(sp);
				System.out.println(p.applyTo(original));
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PatchFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        });
        
        // Add the code area we just created into a VBox.
        vbox = new VBox();
        vbox.getStyleClass().add("code-vbox");
        vbox.getChildren().add(codeArea);

        // Add the vbox into the AnchorPane.
        anchorPane = new AnchorPane();
        anchorPane.getStyleClass().add("code-anchor");
        anchorPane.getChildren().add(vbox);
        setContent(anchorPane);
        
        getStyleClass().add("minimal-tab");
        markAsUnsaved();
    }
    

    /**
     * Opens the specified file in the tab.
     *
     * @throws IOException if an input or output exception occurred.
     */
    public void open(File file) throws IOException {
        // Associate the file with this tab, and set up the tab title.
        this.file = file;

        // Now read the content of file into the CodeArea.
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder content = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            content.append(line);
            content.append(System.lineSeparator());
            line = br.readLine();
        }

        codeArea.replaceText(content.toString());
        unsaved = false;
        br.close();
        
        setText(getFilename());
    }

    /**
     * Saves the content in the tab into a file.
     *
     * @throws IOException if an input or output exception occurred.
     */
    public void save() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(codeArea.getText());
        bw.flush();
        bw.close();

        setText(getFilename());
        unsaved = false;
    }
    
    
    /**
     * Marks current file as unsaved, prepending the title of this tab with a
     * "*" character.
     */
    private void markAsUnsaved() {
        if (unsaved == false) {
            unsaved = true;
            String title = getText();
            setText(String.format("* %s", title));
        }
    }

    /**
     * Gets whether this file has unsaved changes.
     *
     * @return whether this file is unsaved.
     */
    public boolean unsaved() {
        return unsaved;
    }
    
    public synchronized List<String> getPatches() {
    	return patches;
    }
    
    public synchronized void addPatch(String patch) {
    	patches.add(patch);
    }
    
    public synchronized void clearPatches() {
    	patches.clear();
    }

    /**
     * Gets the filename of the file opened in this tab.
     *
     * @return filename (with extension).
     */
    public String getFilename() {
        if (file == null) {
            return "Untitled";
        } else {
            String[] parts = file.getAbsolutePath().split("[/]");
            return parts[parts.length - 1];
        }
    }
    
    /**
     * Gets the extension of the file opened in this tab.
     * 
     * @return extension
     */
    public String getExtension() {
    	if (getFilename().contains(".")) {
    		return getFilename().split("[.]")[1];
    	} else {
    		return "";
    	}
    }

    /**
     * Gets the absolute path of the file opened in this tab.
     *
     * @return absolute path of the file.
     */
    public String getFilepath() {
        return (file == null) ? "Unsaved file" : file.getAbsolutePath();
    }

    /**
     * Gets the location of the file opened in this tab.
     *
     * @return the directory in which the file is saved.
     */
    public String getLocation() {
        return file.getAbsoluteFile().getParent();
    }
    
    public CodeArea getCodeArea() {
        return codeArea;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
