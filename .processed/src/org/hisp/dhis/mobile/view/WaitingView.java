package org.hisp.dhis.mobile.view;

/*
 * Copyright (c) 2004-2011, University of Oslo All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. * Neither the name of the HISP project nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Form;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.layouts.BoxLayout;

public class WaitingView
    extends AbstractView
{

    private Form waitForm;

    public WaitingView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
    }

    public void prepareView()
    {
        // TODO Auto-generated method stub
    }

    public void showView()
    {
        this.getWaitForm().show();
    }

    public Form getWaitForm()
    {
        if ( waitForm == null )
        {
            waitForm = new Form( Text.PLEASE_WAIT() );
            waitForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            waitForm.setTransitionOutAnimator( CommonTransitions.createSlide(
                CommonTransitions.SLIDE_HORIZONTAL, true, 400 ) );
        }
        return waitForm;
    }

    public void setWaitForm( Form waitForm )
    {
        this.waitForm = waitForm;
    }

}
