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
package org.hisp.dhis.mobile.model;

import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Button;
import com.sun.lwuit.Font;

/**
 * @author Nguyen Kim Lai
 * 
 * @version Link.java 12:31:35 PM Feb 19, 2013 $
 */
public class LinkButton
    extends Button
{

    public LinkButton( String name )
    {
        this.getUnselectedStyle().setBorder( null );
        this.getSelectedStyle().setBorder( null );

        this.getUnselectedStyle().setBgTransparency( 100 );
        this.getSelectedStyle().setBgTransparency( 100 );

        this.getUnselectedStyle().setFont(
            Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM ) );
        this.getSelectedStyle().setFont(
            Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN | Font.STYLE_BOLD, Font.SIZE_MEDIUM ) );

        this.getUnselectedStyle().setBgColor( 0xffffff );
        this.getSelectedStyle().setBgColor( Text.HIGHLIGHT_COLOR() );
        this.setText( name );
    }
}
