/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.mobile.view;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.TextArea;

/**
 * @author Nguyen Kim Lai
 * 
 * @version AlertBoxView.java 10:50:00 AM Mar 21, 2013 $
 */
public class AlertBoxView
    extends Dialog
{
    String titleText;

    String message;

    TextArea textArea;

    public AlertBoxView( String message, String titleText )
    {
        super();
        this.setDialogType( Dialog.TYPE_INFO );
        this.titleText = titleText;
        this.message = message;
    }
    
    public synchronized void showView()
    {   
        this.removeAll();
        this.setScrollable( false );
        this.setTimeout( 3000 );
        this.setTitle( this.titleText );
        this.addComponent( this.getTextArea() );
        this.show();
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public TextArea getTextArea()
    {
        if ( textArea == null )
        {
            textArea = new TextArea( getMessage() );
            textArea.setFocusable(false);
        }
        else
        {
            textArea.setText( getMessage() );
        }
        return textArea;
    }

    public void setTextArea( TextArea textArea )
    {
        this.textArea = textArea;
    }

    public String getTitleText()
    {
        return titleText;
    }

    public void setTitleText( String titleText )
    {
        this.titleText = titleText;
    }

}
