/*
           Jreepad - personal information manager.
           Copyright (C) 2004 Dan Stowell

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

The full license can be read online here:

           http://www.gnu.org/copyleft/gpl.html
*/

package jreepad.editor;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import jreepad.JreepadArticle;
import jreepad.JreepadPrefs;
import jreepad.JreepadView;

/**
 * The plain text editor pane.
 */
public class PlainTextEditor extends EditorPaneView implements CaretListener, UndoableEditListener
{
	private ContentChangeListener contentChangeListener = null;

	public PlainTextEditor(JreepadArticle article)
	{
		super("text/plain", article);
		editorPane.setEditable(true);

		if (getPrefs().wrapToWindow)
			editorPane.setEditorKit(new JPEditorKit());

		// Add a listener to make sure the editorpane's content is always stored
		// when it changes
		editorPane.addCaretListener(this);
		editorPane.getDocument().addUndoableEditListener(this);
	}

    public void insertText(String text)
	{
		Document doc = editorPane.getDocument();
		int here = editorPane.getCaretPosition();
		try
		{
			editorPane.setText(doc.getText(0, here) + text
					+ doc.getText(here, doc.getLength() - here));
			editorPane.setCaretPosition(here + text.length());
		}
		catch (BadLocationException e)
		{
			// Simply ignore this
		}
	}

    public void setContentChangeListener(ContentChangeListener listener)
	{
		contentChangeListener = listener;
	}

	private void notifyContentChangeListener()
	{
		if (contentChangeListener != null)
			contentChangeListener.contentChanged();
	}

	public String selectWordUnderCursor()
	{
		try
		{
			String text = editorPane.getText();
			int startpos = editorPane.getCaretPosition();
			int endpos = startpos;
			if (text.length() > 0)
			{
				// Select the character before/after the current position, and
				// grow it until we hit whitespace...
				while (startpos > 0 && !Character.isWhitespace(editorPane.getText(startpos - 1, 1).charAt(0)))
					startpos--;
				while (endpos < (text.length()) && !Character.isWhitespace(editorPane.getText(endpos, 1).charAt(0)))
					endpos++;
				if (endpos > startpos)
				{
					editorPane.setSelectionStart(startpos);
					editorPane.setSelectionEnd(endpos);
					return getSelectedText();
				}
			}
		}
		catch (BadLocationException e)
		{
		}
		return "";
	}

	public void caretUpdate(CaretEvent e)
	{
		if (editLocked)
			return; // i.e. we are deactivated while changing from node to node
		if (article.getArticleMode() != JreepadArticle.ARTICLEMODE_ORDINARY)
			return; // i.e. we are only relevant when in plain-text mode

		if (!editorPane.getText().equals(article.getContent()))
		{
			// System.out.println("UPDATE - I'd now overwrite node content with
			// editorpane content");
			article.setContent(editorPane.getText());
			notifyContentChangeListener();
		}
		else
		{
			// System.out.println(" No need to update content.");
		}
	}

	public static JreepadPrefs getPrefs()
	{
		return JreepadView.getPrefs();
	}

	// Code to ensure that the article word-wraps follows
	// - contributed by Michael Labhard based on code found on the web...
	private static class JPEditorKit extends StyledEditorKit
	{
		public ViewFactory getViewFactory()
		{
			return new JPRTFViewFactory();
		}
	}

	private static class JPRTFViewFactory implements ViewFactory
	{
		public View create(Element elem)
		{
			String kind = elem.getName();
			if (kind != null)
				if (kind.equals(AbstractDocument.ContentElementName))
				{
					return new LabelView(elem);
				}
				else if (kind.equals(AbstractDocument.ParagraphElementName))
				{
					return new JPParagraphView(elem);
				}
				else if (kind.equals(AbstractDocument.SectionElementName))
				{
					return new BoxView(elem, View.Y_AXIS);
				}
				else if (kind.equals(StyleConstants.ComponentElementName))
				{
					return new ComponentView(elem);
				}
				else if (kind.equals(StyleConstants.IconElementName))
				{
					return new IconView(elem);
				}
			// default to text display
			return new LabelView(elem);
		}
	}

	private static short paraRightMargin = 0;

	static class JPParagraphView extends javax.swing.text.ParagraphView
	{
		public JPParagraphView(Element e)
		{
			super(e);
			this.setInsets((short) 0, (short) 0, (short) 0, paraRightMargin);
		}
	}
	// Code to ensure that the article word-wraps ends here
	// - contributed by Michael Labhard

    //This one listens for edits that can be undone.
    public void undoableEditHappened(UndoableEditEvent e) {

        // System.out.println("Undoable event is " +
		// (e.getEdit().isSignificant()?"":"NOT ") + "significant");
        // System.out.println("Undoable event source: " + e.getEdit());

        // Remember the edit and update the menus.

        if(!editLocked) {
          // System.out.println("Storing undoable event for node " +
			// getCurrentNode().getTitle());
          // System.out.println(" Event is " +
			// e.getEdit().getPresentationName() );
          // if(getCurrentNode().lastEditStyle !=
			// e.getEdit().getPresentationName()){
          // System.out.println(" This is a SIGNIFICANT change.");
          // }
          // System.out.println(" Content: " + getCurrentNode().getContent());
          // System.out.println(" Node undoMgr: " + getCurrentNode().undoMgr);
          // Thread.currentThread().dumpStack();
          article.getUndoMgr().addEdit(e.getEdit());
        }
        // undoAction.updateUndoState();
        // redoAction.updateRedoState();
    }
}
