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

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.LinkButton;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.model.Section;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Style;

/**
 * @author Nguyen Kim Lai
 * 
 * @version SectionListView.java 3:58:05 PM Mar 4, 2013 $
 */
public class SectionListView
    extends AbstractView
    implements ActionListener
{

    private NameBasedMIDlet namebasedMidlet;

    private Form sectionListForm;

    private Command backCommand;

    private Patient patient;

    private ProgramStage programStage;

    public SectionListView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.namebasedMidlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        System.gc();
        getSectionListForm();
        // clear previous data
        sectionListForm.removeAll();

        Style labelStyle = new Style();
        labelStyle.setBgColor( Text.HEADER_BG_COLOR() );

        Label lblSections = new Label( "Sections of " + programStage.getName() );
        lblSections.setUnselectedStyle( labelStyle );
        lblSections.setSelectedStyle( labelStyle );
        sectionListForm.addComponent( lblSections );
        prepareSection();

        Button btnSend = new Button( "Form Complete" );
        btnSend.addActionListener( new ActionListener()
        {

            public void actionPerformed( ActionEvent arg )
            {
                namebasedMidlet.getWaitingView().showView();
                ConnectionManager.setUrl( namebasedMidlet.getCurrentOrgUnit().getUploadProgramStageUrl()+"/"+patient.getId() );
                ConnectionManager.uploadProgramStage( programStage, null, patient, programStage );
            }
        } );
        btnSend.getUnselectedStyle().setAlignment( Component.CENTER );
        btnSend.getSelectedStyle().setAlignment( Component.CENTER );
        btnSend.getPressedStyle().setAlignment( Component.CENTER );
        btnSend.getUnselectedStyle().setFont(
            Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM ) );
        btnSend.getSelectedStyle()
            .setFont( Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM ) );
        btnSend.getPressedStyle()
            .setFont( Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM ) );
        sectionListForm.addComponent( btnSend );
    }

    public void showView()
    {
        prepareView();
        getSectionListForm().show();

    }

    public void prepareSection()
    {
        Vector sections = programStage.getSections();
        for ( int i = 0; i < sections.size(); i++ )
        {
            final Section section = (Section) sections.elementAt( i );
            LinkButton sectionLink = new LinkButton( section.getName() );
            sectionLink.addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent ae )
                {
                    namebasedMidlet.getTrackingDataEntryView().setPatient( patient );
                    namebasedMidlet.getTrackingDataEntryView().setProgramStage( programStage );
                    namebasedMidlet.getTrackingDataEntryView().setSection( section );
                    namebasedMidlet.getTrackingDataEntryView().setTitle( programStage.getName()+" - section: "+section.getName() );
                    namebasedMidlet.getTrackingDataEntryView().showView();
                }
            } );
            sectionListForm.addComponent( sectionLink );
        }
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.BACK() ) )
        {
            namebasedMidlet.getPersonDashboardView().showView();
        }

    }

    public Form getSectionListForm()
    {
        if ( sectionListForm == null )
        {
//            sectionListForm = new Form( patient.getName() );
            sectionListForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            sectionListForm.setScrollableY( true );
            sectionListForm.setTransitionOutAnimator( CommonTransitions.createSlide(
                CommonTransitions.SLIDE_HORIZONTAL, true, 200 ) );

            sectionListForm.addCommand( this.getBackCommand() );
            sectionListForm.addCommandListener( this );
        }
        return sectionListForm;
    }

    public void setSectionListForm( Form sectionListForm )
    {
        this.sectionListForm = sectionListForm;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public Command getBackCommand()
    {
        if ( backCommand == null )
        {
            backCommand = new Command( Text.BACK() );
        }
        return backCommand;
    }

    public void setBackCommand( Command backCommand )
    {
        this.backCommand = backCommand;
    }

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

}
