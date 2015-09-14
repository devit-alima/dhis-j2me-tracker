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

import java.util.Vector;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.List;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

/**
 * @author Nguyen Kim Lai
 * 
 * @version AnonymousOrgUnitSelectView.java 10:17:49 AM Apr 18, 2013 $
 */
public class OrgUnitSelectView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "OrgUnitSelectView";

    private Vector orgUnitVector;

    private Form orgUnitForm;

    private List orgUnitSelectList;

    private NameBasedMIDlet nameBasedMidlet;

    private String programType = "1,2";

    private int targetScreen;

    public OrgUnitSelectView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        nameBasedMidlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        getOrgUnitForm();
    }

    public void showView()
    {
        this.prepareView();
        this.getOrgUnitForm().show();
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getSource() == this.getOrgUnitSelectList() )
        {
            nameBasedMidlet.getWaitingView().showView();

            nameBasedMidlet.setCurrentOrgUnit( ((OrgUnit) getOrgUnitVector().elementAt(
                getOrgUnitSelectList().getSelectedIndex() )) );

            if ( targetScreen == nameBasedMidlet.FIND_INSTANCE_VIEW )
            {
                nameBasedMidlet.getFindBeneficiaryView().showView();
            }
            else
            {
                try
                {
                    if ( programType.equals( "3" ) )
                    {
                        nameBasedMidlet.getProgramSelectView().setTargetScreen( targetScreen );
                        nameBasedMidlet.getProgramSelectView().setPrograms( ProgramRecordStore.getProgramByType( 3 ) );
                    }
                    else
                    {
                        nameBasedMidlet.getProgramSelectView().setPrograms(
                            ProgramRecordStore.getNonAnonymousPrograms() );
                    }

                }
                catch ( Exception e )
                {
                    LogMan.log( "UI," + CLASS_TAG, e );
                    e.printStackTrace();
                }

                nameBasedMidlet.getProgramSelectView().setTargetScreen( targetScreen );
                nameBasedMidlet.getProgramSelectView().showView();
            }
        }
        else if ( ae.getCommand().getCommandName().equals( "Back" ) )
        {
            orgUnitForm = null;
            orgUnitVector = null;
            orgUnitSelectList = null;

            nameBasedMidlet.getTrackingMainMenuView().showView();
        }
    }

    public Form getOrgUnitForm()
    {
        if ( orgUnitForm == null )
        {
            orgUnitForm = new Form( "Select Org Unit" );
            orgUnitForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            orgUnitForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL,
                true, 200 ) );
            orgUnitForm.addCommandListener( this );
            orgUnitForm.addComponent( this.getOrgUnitSelectList() );
            orgUnitForm.addCommand( new Command( "Back" ) );
        }
        return orgUnitForm;
    }

    public void setOrgUnitForm( Form orgUnitForm )
    {
        this.orgUnitForm = orgUnitForm;
    }

    public List getOrgUnitSelectList()
    {
        if ( orgUnitSelectList == null )
        {
            orgUnitSelectList = new List();
            // Create a list model
            orgUnitVector = null;
            try
            {
                orgUnitVector = OrgUnitRecordStore.loadAllOrgUnit();
            }
            catch ( Exception e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
            for ( int i = 0; i < orgUnitVector.size(); i++ )
            {
                orgUnitSelectList.addItem( ((OrgUnit) orgUnitVector.elementAt( i )).getName() );
            }
            orgUnitSelectList.setSmoothScrolling( true );
            orgUnitSelectList.addActionListener( this );
        }
        return orgUnitSelectList;
    }

    public void setOrgUnitSelectList( List orgUnitSelectList )
    {
        this.orgUnitSelectList = orgUnitSelectList;
    }

    public Vector getOrgUnitVector()
    {
        return orgUnitVector;
    }

    public void setOrgUnitVector( Vector orgUnitVector )
    {
        this.orgUnitVector = orgUnitVector;
    }

    public String getProgramType()
    {
        return programType;
    }

    public void setProgramType( String programType )
    {
        this.programType = programType;
    }

    public int getTargetScreen()
    {
        return targetScreen;
    }

    public void setTargetScreen( int targetScreen )
    {
        this.targetScreen = targetScreen;
    }
}
